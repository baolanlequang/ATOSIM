package org.palladiosimulator.blockchainsystems.core.behavior;

import org.jetbrains.annotations.NotNull;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeContext;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

import java.util.random.RandomGenerator;

/**
 * Stronger selfish-mining behavior with explicit private-branch and tie-state tracking.
 *
 * Main state (item 9: now held in the shared {@link AttackForkState}, see the `state` field
 * below -- semantics unchanged from when these were local fields):
 * - privateChain: attacker-mined blocks that are still hidden / unpublished
 * - publishedInCurrentTie: number of attacker blocks from the current private branch
 *   that have already been revealed to the public during the current tie episode
 * - inTieState: true when the attacker has intentionally revealed blocks to create or
 *   continue a public tie/race and is waiting to see how the network evolves
 *
 * Policy summary:
 * - hidden lead = 0: adopt honest progress
 * - hidden lead = 1 and honest block arrives: publish one hidden block and enter tie
 * - hidden lead = 2 and honest block arrives: publish all hidden blocks to override
 * - hidden lead > 2 and honest block arrives: publish one hidden block and keep advantage
 * - if attacker mines while in tie: publish all remaining hidden blocks immediately
 * - if honest network advances while in tie and attacker has nothing hidden left: abandon tie and adopt
 *
 * Notes:
 * - This still remains a simplified selfish-mining policy.
 * - It is intentionally conservative about transaction removal:
 *   only remove transactions after INCLUDED or FORKING append outcomes.
 */
public class SelfishMiningNodeBehavior extends BlockchainNodeObject
        implements BlockchainSystemNodeBehavior, BlockHashSeedable {

    private final HonestBlockchainSystemNodeBehavior honest = new HonestBlockchainSystemNodeBehavior();
    private RandomGenerator _blockHashGenerator;

    @Override
    public void setBlockHashGenerator(RandomGenerator generator) {
        _blockHashGenerator = generator;
    }

    /**
     * Item 9: shared private-branch/fork state -- replaces the privateChain/ownTipHash/
     * publishedInCurrentTie/inTieState fields this class used to hold directly. See
     * {@link AttackForkState}'s class doc for what each checklist concept maps to.
     */
    private final AttackForkState state = new AttackForkState();

    @Override
    public void onNodeInitialized(BlockchainSystemNodeContext context) {
        resetPrivateState();
        context.getMiningProcess().startMining();
    }

    @Override
    public void onTransactionReceived(Transaction transaction, BlockchainSystemNodeContext context) {
        context.getTrxMemPool().storeTransaction(transaction);
        context.getTransactionPropagationStrategy().distribute(transaction);
    }

    @Override
    public void onBlockReceived(Block block, BlockchainSystemNodeContext context) {
        if (context.getBlockchain().hasBlockWithHash(block.getHash())
                || context.getOrphanBlockPool().hasBlockWithHash(block.getHash())) {
            return;
        }

        context.getBlockValidator().validateBlock(block);
    }

    @Override
    public void onBlockValidated(Block block, boolean isValid, BlockchainSystemNodeContext context) {
        // Item 9: phase captured before/after via try-finally so every exit path (including the
        // early `return`s below, unchanged) still logs a transition if the phase actually moved --
        // this wrapper adds no new branch to the logic itself.
        AttackPhase phaseBefore = AttackForkState.computePhase(state);
        try {
            if (!isValid) {
                return;
            }

            if (state.isInContest()) {
                handleBlockWhileInTie(block, context);
                return;
            }

            int hiddenLead = hiddenLead();

            // No hidden advantage left -> behave honestly.
            if (hiddenLead == 0) {
                adoptPublicBlockAndAbandonPrivateState(block, context);
                return;
            }

            // Exactly one hidden block:
            // reveal it to create a public tie, then wait to see who wins the next block.
            if (hiddenLead == 1) {
                boolean published = publishOneHiddenBlock(context);
                if (published) {
                    state.setInContest(true);
                    state.setPublishedInCurrentContest(1);
                }
                return;
            }

            // Exactly two hidden blocks:
            // reveal all to override the public chain with a longer attacker branch.
            if (hiddenLead == 2) {
                publishAllHiddenBlocks(context);
                clearTieStateOnly();
                return;
            }

            // hiddenLead > 2:
            // reveal one block to keep pressure on the public chain,
            // but preserve the remaining hidden advantage.
            boolean published = publishOneHiddenBlock(context);
            if (published) {
                // We intentionally do NOT enter tie state here by default.
                // This branch models the selfish miner maintaining a reduced but still positive hidden lead.
                clearTieStateOnly();
            }
        } finally {
            AttackForkState.logPhaseTransitionIfChanged(getSimulationContext(), getTraceEventLogger(), state, phaseBefore);
        }
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        AttackPhase phaseBefore = AttackForkState.computePhase(state);
        try {
            state.recordMinedBlock(block);

            // If the attacker mines during a tie, that usually means the selfish miner can
            // immediately reveal remaining hidden blocks and try to secure the win.
            if (state.isInContest()) {
                publishAllHiddenBlocks(context);
                clearTieStateOnly();
            }
        } finally {
            AttackForkState.logPhaseTransitionIfChanged(getSimulationContext(), getTraceEventLogger(), state, phaseBefore);
        }
    }

    @Override
    public Block onCreatingBlock(long blockMinedAt, String previousBlockHash, BlockchainSystemNodeContext context) {
        var selection = context.getTransactionSelectionProcess().selectTransactionsForBlock(context);

        return context.getBlockFactory().createBlock(
                String.format("%016x%016x", _blockHashGenerator.nextLong(), _blockHashGenerator.nextLong()),
                previousBlockHash,
                context.getId(),
                blockMinedAt,
                selection.getTotalSize(),
                selection.getTransactions()
        );
    }

    @NotNull
    @Override
    public String onPreviousBlockSelection(BlockchainSystemNodeContext context) {
        // Always continue mining on top of my own last-authored block, published or not
        // (Algorithm 1's "mine at the head of the private chain"). Falling back to the
        // generic public-tip lookup here would pick arbitrarily between my own tip and a
        // tied honest tip during a race.
        if (state.getOwnTipHash() != null) {
            return state.getOwnTipHash();
        }

        return honest.onPreviousBlockSelection(context);
    }

    /**
     * Tie-state handler.
     *
     * Interpretation:
     * - If we still have hidden blocks left, reveal them now to try to win the tie.
     * - If we have no hidden blocks left, we treat the tie as lost locally and adopt.
     *
     * This is still a simplification. A more advanced version could inspect ancestry
     * or explicitly model gamma-driven tie resolution.
     */
    private void handleBlockWhileInTie(Block block, BlockchainSystemNodeContext context) {
        if (hiddenLead() > 0) {
            publishAllHiddenBlocks(context);
            clearTieStateOnly();
            return;
        }

        adoptPublicBlockAndAbandonPrivateState(block, context);
    }

    /**
     * Append a public block and fully abandon any no-longer-viable private strategy state.
     */
    private void adoptPublicBlockAndAbandonPrivateState(Block block, BlockchainSystemNodeContext context) {
        AppendOutcome outcome = BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(block, context);

        if (outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING) {
            context.getTrxMemPool().removeTransactions(block.getTransactions());
            context.getMiningProcess().restartMining();
            context.getBlockPropagationStrategy().distribute(block);

            // Once we meaningfully adopt public progress, the prior hidden branch
            // is no longer treated as our active private advantage.
            resetPrivateState();
        }
    }

    /**
     * Publish the oldest hidden attacker block.
     *
     * @return true iff the block was meaningfully appended and removed from hidden state
     */
    private boolean publishOneHiddenBlock(BlockchainSystemNodeContext context) {
        Block publish = state.peekNextToPublish();
        if (publish == null) {
            return false;
        }

        AppendOutcome outcome = BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(publish, context);

        if (outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING) {
            state.removeFirstPending();
            context.getTrxMemPool().removeTransactions(publish.getTransactions());
            context.getMiningProcess().restartMining();
            context.getBlockPropagationStrategy().distribute(publish);
            return true;
        }

        return false;
    }

    /**
     * Publish all remaining hidden attacker blocks in order.
     */
    private void publishAllHiddenBlocks(BlockchainSystemNodeContext context) {
        while (!state.getPrivateChain().isEmpty()) {
            int sizeBefore = state.getPrivateChain().size();
            boolean published = publishOneHiddenBlock(context);

            // Safety guard against infinite loops if append fails and no progress is made.
            if (!published || state.getPrivateChain().size() == sizeBefore) {
                break;
            }
        }
    }

    private int hiddenLead() {
        return state.hiddenLead();
    }

    private void clearTieStateOnly() {
        state.clearContestOnly();
    }

    private void resetPrivateState() {
        state.reset();
    }

    @Override
    public void dispatchEvent(Event event) {
        // no-op
    }
}
