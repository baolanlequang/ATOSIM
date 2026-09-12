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
 * Equal-fork stubborn mining, a.k.a. "F-stubborn" (Nayak et al., "Stubborn Mining", Section 3.2).
 *
 * F-stubborn's entire deviation from selfish mining is a single rule: when Alice wins a tied
 * race (state lead=0', i.e. she has no remaining hidden material and then mines the next block
 * herself), instead of revealing that block to win outright, she CONCEALS it and keeps mining
 * on it privately, landing back at a plain hidden lead of 1. Every other transition (lead=0
 * adopt; lead=1 reveal-one-to-tie; lead=2 reveal-all-to-win; lead&gt;2 reveal-one-to-pressure) is
 * unchanged from selfish mining.
 *
 * State (item 9: now held in the shared {@link AttackForkState}, see the `state` field below --
 * semantics unchanged from when these were local fields):
 * - privateChain: attacker-mined blocks that are still hidden / unpublished
 * - ownTipHash: hash of the attacker's own most recently authored block, published or not
 * - inEqualForkContest: true when the attacker has revealed part of its private branch and is
 *   currently in a tied public contest
 */
public class EqualForkStubbornMiningNodeBehavior extends BlockchainNodeObject
        implements BlockchainSystemNodeBehavior, BlockHashSeedable {

    private final HonestBlockchainSystemNodeBehavior honest = new HonestBlockchainSystemNodeBehavior();
    private RandomGenerator _blockHashGenerator;

    @Override
    public void setBlockHashGenerator(RandomGenerator generator) {
        _blockHashGenerator = generator;
    }

    /**
     * Item 9: shared private-branch/fork state -- replaces the privateChain/ownTipHash/
     * inEqualForkContest/publishedInCurrentContest fields this class used to hold directly.
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
        AttackPhase phaseBefore = AttackForkState.computePhase(state);
        try {
            if (!isValid) {
                return;
            }

            if (state.isInContest()) {
                handleBlockWhileInEqualForkContest(block, context);
                return;
            }

            int hiddenLead = hiddenLead();

            // No hidden branch -> behave honestly.
            if (hiddenLead == 0) {
                adoptPublicBlockAndAbandonPrivateState(block, context);
                return;
            }

            // lead=1: reveal one block to force a tie (unchanged from selfish mining).
            if (hiddenLead == 1) {
                boolean published = publishOneHiddenBlock(context);
                if (published) {
                    state.setInContest(true);
                    state.setPublishedInCurrentContest(1);
                }
                return;
            }

            // lead=2: reveal everything and win outright (unchanged from selfish mining).
            if (hiddenLead == 2) {
                publishAllHiddenBlocks(context);
                clearContestStateOnly();
                return;
            }

            // lead>2: reveal one block to keep pressure while preserving the rest (unchanged).
            boolean published = publishOneHiddenBlock(context);
            if (published) {
                clearContestStateOnly();
            }
        } finally {
            AttackForkState.logPhaseTransitionIfChanged(getSimulationContext(), getTraceEventLogger(), state, phaseBefore);
        }
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        AttackPhase phaseBefore = AttackForkState.computePhase(state);
        try {
            // F-stubborn's defining rule: winning an already-tied race outright (state 0', no
            // remaining hidden material) is NOT revealed. Conceal the new block and keep mining
            // on it privately instead, landing at a plain hidden lead of 1.
            boolean wasWinningRace = state.isInContest() && state.getPrivateChain().isEmpty();

            state.recordMinedBlock(block);

            if (wasWinningRace) {
                clearContestStateOnly();
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
        // Always continue mining on top of my own last-authored block, published or not.
        // Falling back to the generic public-tip lookup here would pick arbitrarily between
        // my own tip and a tied honest tip during a contest.
        if (state.getOwnTipHash() != null) {
            return state.getOwnTipHash();
        }

        return honest.onPreviousBlockSelection(context);
    }

    /**
     * Contest-state handler (unchanged from selfish mining's tie handling): reveal everything
     * and win once any hidden material remains; otherwise the contest is lost, adopt.
     */
    private void handleBlockWhileInEqualForkContest(Block block, BlockchainSystemNodeContext context) {
        if (hiddenLead() > 0) {
            publishAllHiddenBlocks(context);
            clearContestStateOnly();
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

            if (!published || state.getPrivateChain().size() == sizeBefore) {
                break;
            }
        }
    }

    private int hiddenLead() {
        return state.hiddenLead();
    }

    private void clearContestStateOnly() {
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
