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
 * Trail-stubborn mining, a.k.a. "Tj-stubborn" (Nayak et al., "Stubborn Mining", Section 3.2).
 *
 * Tj-stubborn's entire deviation from selfish mining concerns falling BEHIND the public chain.
 * Ordinarily, the moment the attacker's hidden lead reaches 0 and the public chain pulls ahead,
 * the attacker gives up immediately and adopts. A Tj-stubborn attacker instead keeps mining
 * privately on her own last-known tip even while trailing, hoping to catch back up, and only
 * gives up once she falls j+1 blocks behind (i.e. once deficit exceeds j; the default j=1
 * matches the paper's T1-stubborn, abandoning once behind by 2 or more). If she catches up
 * exactly (deficit reaches 0 again), this lands at the paper's double-primed "0''" state, which
 * behaves identically to a plain hidden lead of 1 since nothing has been revealed. Every
 * ahead-side transition (lead=1 reveal-one-to-tie; lead=2 reveal-all-to-win; lead&gt;2
 * reveal-one-to-pressure; the lead=0' win-on-own-block case) is unchanged from selfish mining.
 *
 * State (item 9: now held in the shared {@link AttackForkState}, see the `state` field below --
 * semantics unchanged from when these were local fields):
 * - privateChain: attacker-mined blocks that are still hidden / unpublished (used both while
 *   ahead and while trying to catch up from behind)
 * - ownTipHash: hash of the attacker's own most recently authored block, published or not
 * - inTieState: true when the attacker has revealed part of its private branch and is
 *   currently in a tied public contest
 * - deficit: how many blocks behind the public chain the attacker currently trails (0 when
 *   not trailing)
 */
public class TrailStubbornMiningNodeBehavior extends BlockchainNodeObject
        implements BlockchainSystemNodeBehavior, BlockHashSeedable {

    private final HonestBlockchainSystemNodeBehavior honest = new HonestBlockchainSystemNodeBehavior();
    private RandomGenerator _blockHashGenerator;

    @Override
    public void setBlockHashGenerator(RandomGenerator generator) {
        _blockHashGenerator = generator;
    }

    /**
     * Item 9: shared private-branch/fork state -- replaces the privateChain/ownTipHash/
     * inTieState/publishedInCurrentContest/deficit fields this class used to hold directly.
     */
    private final AttackForkState state = new AttackForkState();

    /**
     * Threshold j: the attacker gives up once deficit exceeds j (Delta &lt;= -(j+1)).
     * Defaults to 1, matching the paper's T1-stubborn (abandon once behind by 2 or more).
     */
    private final int j;

    public TrailStubbornMiningNodeBehavior() {
        this(1);
    }

    public TrailStubbornMiningNodeBehavior(int j) {
        this.j = j;
    }

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

            if (state.getDeficit() > 0) {
                handleBlockWhileTrailing(block, context);
                return;
            }

            if (state.isInContest()) {
                handleBlockWhileInTie(block, context);
                return;
            }

            int hiddenLead = hiddenLead();

            // lead=0: instead of adopting immediately (selfish mining), start trailing - keep
            // mining on my own current tip rather than switching to this new one.
            if (hiddenLead == 0) {
                beginTrailing(block, context);
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
                clearTieStateOnly();
                return;
            }

            // lead>2: reveal one block to keep pressure while preserving the rest (unchanged).
            boolean published = publishOneHiddenBlock(context);
            if (published) {
                clearTieStateOnly();
            }
        } finally {
            AttackForkState.logPhaseTransitionIfChanged(getSimulationContext(), getTraceEventLogger(), state, phaseBefore);
        }
    }

    /**
     * Already trailing: one more public block extends the deficit. Give up only once behind
     * by more than j; otherwise keep the local view connected (without reacting to it) so a
     * later give-up can still resolve this block's ancestry.
     */
    private void handleBlockWhileTrailing(Block block, BlockchainSystemNodeContext context) {
        state.incrementDeficit();

        if (state.getDeficit() > j) {
            adoptPublicBlockAndAbandonPrivateState(block, context);
            return;
        }

        BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(block, context);
    }

    /**
     * Tied contest handler (unchanged from selfish mining's win case): reveal everything and
     * win once any hidden material remains. If nothing remains, a Tj-stubborn attacker begins
     * trailing instead of adopting immediately.
     */
    private void handleBlockWhileInTie(Block block, BlockchainSystemNodeContext context) {
        if (hiddenLead() > 0) {
            publishAllHiddenBlocks(context);
            clearTieStateOnly();
            return;
        }

        clearTieStateOnly();
        beginTrailing(block, context);
    }

    /**
     * Start trailing: keep mining on whatever my own tip was before this block, rather than
     * adopting it, while still recording the block so the local view stays connected.
     */
    private void beginTrailing(Block block, BlockchainSystemNodeContext context) {
        if (state.getOwnTipHash() == null) {
            state.setOwnTipHash(block.getPreviousHash());
        }
        state.setDeficit(1);
        BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(block, context);
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        AttackPhase phaseBefore = AttackForkState.computePhase(state);
        try {
            if (state.getDeficit() > 0) {
                // Mining while trailing catches up by one block. Reaching deficit=0 this way lands
                // at the paper's "0''" state, which behaves like a plain hidden lead of 1 since
                // nothing has been revealed - no special-casing needed beyond the decrement.
                state.recordMinedBlock(block);
                state.decrementDeficit();
                return;
            }

            // Selfish mining's own-mining rule is unchanged: only winning an already-tied race
            // outright (state 0', no remaining hidden material) reveals.
            boolean wasWinningRace = state.isInContest() && state.getPrivateChain().isEmpty();

            state.recordMinedBlock(block);

            if (wasWinningRace) {
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
        // Always continue mining on top of my own last-authored block, published or not -
        // this holds whether I'm ahead (extending hidden material) or trailing (trying to
        // catch back up to the tip I refused to adopt).
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
