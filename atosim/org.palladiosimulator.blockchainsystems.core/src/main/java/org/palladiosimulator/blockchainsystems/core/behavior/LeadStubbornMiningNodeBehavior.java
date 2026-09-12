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
 * Lead-stubborn mining, a.k.a. "L-stubborn" (Nayak et al., "Stubborn Mining", Section 3.1).
 *
 * L-stubborn's entire deviation from selfish mining is a single rule: whenever the public
 * chain catches up to any hidden lead of 1 or more, reveal exactly ONE block (matching the
 * public chain's new length) instead of selfish mining's behavior of revealing everything at
 * lead=2 or nothing at lead&gt;2. This keeps a tied fork alive with whatever hidden material
 * remains, rather than winning outright or silently conceding ground. Every other transition
 * (lead=0 adopt; own-mining behavior; the lead=0' win-on-own-block case) is unchanged from
 * selfish mining.
 *
 * State (item 9: now held in the shared {@link AttackForkState}, see the `state` field below --
 * semantics unchanged from when these were local fields):
 * - privateChain: attacker-mined blocks that are still hidden / unpublished
 * - ownTipHash: hash of the attacker's own most recently authored block, published or not
 * - inContestState: true when the attacker has revealed part of its private branch and is
 *   currently in a tied public contest
 *
 * Known anomaly, preserved as-is by item 9 (not adjudicated as bug or intentional -- a decision-
 * logic change is explicitly out of scope for this refactor): unlike Selfish/Trail/Equal-fork's
 * inTieState/inEqualForkContest, inContestState is never read to gate onBlockValidated's
 * branching -- L-stubborn reveals exactly one block on every hiddenLead&gt;=1 case regardless of
 * whether a contest is already active. It IS read once, in onBlockMined's wasWinningRace check
 * below, just never used to change how a newly-validated block gets handled.
 */
public class LeadStubbornMiningNodeBehavior extends BlockchainNodeObject
        implements BlockchainSystemNodeBehavior, BlockHashSeedable {

    private final HonestBlockchainSystemNodeBehavior honest = new HonestBlockchainSystemNodeBehavior();
    private RandomGenerator _blockHashGenerator;

    @Override
    public void setBlockHashGenerator(RandomGenerator generator) {
        _blockHashGenerator = generator;
    }

    /**
     * Item 9: shared private-branch/fork state -- replaces the privateChain/ownTipHash/
     * inContestState/publishedInCurrentContest fields this class used to hold directly.
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

            int hiddenLead = hiddenLead();

            // No hidden advantage -> behave honestly and reset.
            if (hiddenLead == 0) {
                adoptPublicBlockAndAbandonPrivateState(block, context);
                return;
            }

            // L-stubborn's defining rule: for ANY hidden lead >= 1, reveal exactly one block to
            // match the public chain's new length and keep the tied contest alive, instead of
            // selfish mining's reveal-all-at-2 / reveal-nothing-beyond-2 behavior.
            boolean published = publishOneHiddenBlock(context);
            if (published) {
                state.setInContest(true);
                state.incrementPublishedInCurrentContest();
            }
        } finally {
            AttackForkState.logPhaseTransitionIfChanged(getSimulationContext(), getTraceEventLogger(), state, phaseBefore);
        }
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        AttackPhase phaseBefore = AttackForkState.computePhase(state);
        try {
            // Selfish mining's own-mining rule is unchanged by L-stubborn: only the special case of
            // winning an already-tied race outright (state 0', no remaining hidden material) reveals;
            // otherwise Alice always keeps mining privately without revealing.
            boolean wasWinningRace = state.isInContest() && state.getPrivateChain().isEmpty();

            state.recordMinedBlock(block);

            if (wasWinningRace) {
                publishAllHiddenBlocks(context);
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
