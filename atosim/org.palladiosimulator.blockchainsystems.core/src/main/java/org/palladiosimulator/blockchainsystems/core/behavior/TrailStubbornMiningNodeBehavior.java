package org.palladiosimulator.blockchainsystems.core.behavior;

import org.jetbrains.annotations.NotNull;
import org.palladiosimulator.blockchainsystems.core.block.BlockBroadcastTraceEvent;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.Blockchain;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeContext;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

/**
 * Trail-stubborn mining, T_j-stubborn (Nayak et al., "Stubborn Mining", Section 3.2 and Table 3).
 *
 * Definition followed (Nayak et al.): "When Alice's private chain falls behind the public chain,
 * she may decide to continue mining on it anyway, in the hope of catching up. [...] if Alice is
 * T_j-stubborn then she accepts the public blockchain once her private chain falls j+1 behind.
 * Thus a T_j-stubborn strategy is characterized by a beta edge from lead = -j to lead = 0.
 * Finally, when Alice catches up from lead = -1, the state machine transitions to lead = 0''"
 * -- a state in which a fork exists and the revealed portions of both forks are equal in length.
 * Table 3 lists acceptance ("Behind by x, x >= 0 and Bob finds a block: do not accept till x = j")
 * as T_j's only deviation from selfish mining, so every reveal decision follows selfish mining.
 *
 * The lead is Delta = height(own tip) - height(public tip), computed from block heights, never
 * counted (AD_trail_check section 5.1). The public tip height is the largest height of any block
 * this node has validated from others or published itself. Repeated copies of a block cannot
 * change it. The trailing deficit stored in {@link AttackForkState} is max(0, -Delta), recomputed
 * after every event, so the monitor's views ({@link UnpublishedBlockCounter}) stay correct.
 *
 * Decisions on a public block (validated block from another node):
 * - it extends my own tip and nothing is hidden: my branch has been adopted by others -> accept it;
 * - it does not raise the public height: no public progress -> keep the view connected (no reaction);
 * - Delta < 0 after it (behind): keep mining on my own tip (start trailing on the tip I was mining
 *   on if I was synced); accept the public chain once -Delta > j (give-up: reset in every case);
 * - Delta >= 0 with hidden blocks (ahead), selfish rules stated in heights: Delta = 0 -> reveal the
 *   hidden blocks up to the public height (equal-length fork, contest); Delta = 1 -> reveal all
 *   (override); Delta >= 2 -> reveal up to the public height (keep the rest).
 * Decisions on an own block:
 * - trailing and Delta still < 0: keep it private;
 * - trailing and Delta = 0 (caught up, 0''): reveal the private branch at once, contest open;
 * - contest open and nothing hidden (0' / 0''): reveal it -- the selfish win on the next own block;
 * - otherwise keep it private.
 */
public class TrailStubbornMiningNodeBehavior extends BlockchainNodeObject
        implements BlockchainSystemNodeBehavior, BlockHashSeedable, UnpublishedBlockCounter {

    private final HonestBlockchainSystemNodeBehavior honest = new HonestBlockchainSystemNodeBehavior();
    private RandomGenerator _blockHashGenerator;

    @Override
    public void setBlockHashGenerator(RandomGenerator generator) {
        _blockHashGenerator = generator;
    }

    /** Shared private-branch/fork state (item 9). deficit holds the computed max(0, -Delta). */
    private final AttackForkState state = new AttackForkState();

    /**
     * Threshold j: the attacker accepts the public chain once she falls j+1 behind (-Delta > j).
     * Defaults to 1, the paper's T_1-stubborn.
     */
    private final int j;

    // Heights (genesis = 1, as BlockchainImpl positions) of every block this node mined or
    // validated, and their parents, so heights are known even for public blocks the attacker did
    // not store while it held a hidden lead.
    private final Map<String, Long> heights = new HashMap<>();
    private final Map<String, String> parents = new HashMap<>();
    private long publicTipHeight = 1;
    // AD 5.5: hidden blocks dropped because they could no longer be published.
    private long droppedUnpublishable = 0;

    public TrailStubbornMiningNodeBehavior() {
        this(1);
    }

    public TrailStubbornMiningNodeBehavior(int j) {
        this.j = j;
    }

    @Override
    public void onNodeInitialized(BlockchainSystemNodeContext context) {
        resetPrivateState();
        heights.clear();
        parents.clear();
        droppedUnpublishable = 0;
        Blockchain chain = context.getBlockchain();
        publicTipHeight = chain.getPositionOfBlock(chain.getPreferredTipOfLongestChains());
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

            // Delta before this block: the base I am mining on, measured before it is appended.
            long ownHeight = ownHeight(context);
            long height = recordHeight(block, context);
            boolean raisesPublic = height > publicTipHeight;
            if (raisesPublic) publicTipHeight = height;

            // Others have extended my own published block and nothing is hidden: my branch is the
            // public chain now -> accept it. Only for an own tip I mined myself: when trailing
            // starts from the synced state the own tip is a public block, which every later public
            // block descends from -- that is trailing, not adoption.
            if (state.getPrivateChain().isEmpty() && state.getOwnTipHash() != null
                    && isOwnBlock(state.getOwnTipHash(), context)
                    && descendsFrom(block, state.getOwnTipHash(), context)) {
                acceptPublicChain(block, context);
                return;
            }

            if (!raisesPublic) {
                // No public progress (an equal-or-lower block): keep the local view connected
                // when not holding a lead; no strategy reaction.
                if (state.getPrivateChain().isEmpty()) appendWithoutReaction(block, context);
                updateDeficit(context);
                return;
            }

            long delta = ownHeight - publicTipHeight;

            if (delta < 0) {
                // Behind. T_j: "Do not accept till x = j" -- keep mining on my own tip; once the
                // private chain falls j+1 behind (-Delta > j), accept the public chain.
                if (state.getOwnTipHash() == null) {
                    // Synced until now: trail on the tip I was mining on (never set the deficit
                    // without checking it, AD 5.2).
                    state.setOwnTipHash(context.getBlockchain().getPreferredTipOfLongestChains().getHash());
                }
                state.clearContestOnly();
                if (-delta > j) {
                    acceptPublicChain(block, context);
                    return;
                }
                appendWithoutReaction(block, context);
                state.setDeficit((int) -delta);
                return;
            }

            if (!state.getPrivateChain().isEmpty()) {
                // Ahead: selfish-mining reveal rules, stated in heights instead of hidden counts.
                if (delta == 0) {
                    publishUpToHeight(publicTipHeight, context);   // equal-length fork
                    state.setInContest(true);
                    state.incrementPublishedInCurrentContest();
                } else if (delta == 1) {
                    publishAllHiddenBlocks(context);                // override
                    state.clearContestOnly();
                } else {
                    publishUpToHeight(publicTipHeight, context);   // keep the rest hidden
                    state.clearContestOnly();
                }
                updateDeficit(context);
                return;
            }

            // Delta >= 0 with nothing hidden: my published tip is at least as high; keep the view.
            appendWithoutReaction(block, context);
            updateDeficit(context);
        } finally {
            AttackForkState.logPhaseTransitionIfChanged(getSimulationContext(), getTraceEventLogger(), state, phaseBefore);
        }
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        AttackPhase phaseBefore = AttackForkState.computePhase(state);
        try {
            long height = recordHeight(block, context);
            boolean wasTrailing = state.getDeficit() > 0;
            boolean wasWinningRace = state.isInContest() && state.getPrivateChain().isEmpty();

            state.recordMinedBlock(block);
            long delta = height - publicTipHeight;

            if (wasTrailing) {
                if (delta < 0) {
                    state.setDeficit((int) -delta);   // still behind: keep it private
                    return;
                }
                // Caught up (lead = 0''): reveal the private branch now, so that both forks are
                // public and of equal length; the contest is open.
                publishCatchUpBranch(context);
                state.setInContest(true);
                state.setDeficit(0);
                return;
            }

            if (wasWinningRace) {
                // lead = 0' or 0'' and Alice mines: reveal (selfish rule; Table 3 lists no T_j
                // deviation for reveals).
                publishAllHiddenBlocks(context);
                state.clearContestOnly();
            }
            updateDeficit(context);
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
        // Mine on my own last-authored block, published or not, while I have one (ahead, in a
        // contest, or trailing); otherwise on the public chain.
        if (state.getOwnTipHash() != null) {
            return state.getOwnTipHash();
        }

        return honest.onPreviousBlockSelection(context);
    }

    // ---------------------------------------------------------------- heights and Delta

    private long ownHeight(BlockchainSystemNodeContext context) {
        if (state.getOwnTipHash() != null) {
            long h = heightOf(state.getOwnTipHash(), context);
            if (h > 0) return h;
        }
        Blockchain chain = context.getBlockchain();
        return chain.getPositionOfBlock(chain.getPreferredTipOfLongestChains());
    }

    /** Height of a known block, or -1 if unknown. */
    private long heightOf(String hash, BlockchainSystemNodeContext context) {
        if (hash == null) return -1;
        Long h = heights.get(hash);
        if (h != null) return h;
        Blockchain chain = context.getBlockchain();
        Block b = chain.getBlock(hash);
        return b != null ? chain.getPositionOfBlock(b) : -1;
    }

    /**
     * Records the block's height as parent height + 1. If the parent is unknown (not mined or
     * validated here and not in the chain), the block is taken to extend the current public tip.
     */
    private long recordHeight(Block block, BlockchainSystemNodeContext context) {
        Long known = heights.get(block.getHash());
        if (known != null) return known;
        long parentHeight = heightOf(block.getPreviousHash(), context);
        long h = parentHeight > 0 ? parentHeight + 1 : publicTipHeight + 1;
        heights.put(block.getHash(), h);
        if (block.getPreviousHash() != null) parents.put(block.getHash(), block.getPreviousHash());
        return h;
    }

    /** True iff the block was mined by this node (published own blocks are in this node's chain). */
    private boolean isOwnBlock(String hash, BlockchainSystemNodeContext context) {
        Block b = context.getBlockchain().getBlock(hash);
        return b != null && context.getId().equals(b.getOriginId());
    }

    /** True iff ancestorHash is an ancestor of block (walking parents down to the ancestor's height). */
    private boolean descendsFrom(Block block, String ancestorHash, BlockchainSystemNodeContext context) {
        long ancestorHeight = heightOf(ancestorHash, context);
        if (ancestorHeight <= 0) return false;
        String current = block.getPreviousHash();
        while (current != null) {
            if (current.equals(ancestorHash)) return true;
            long h = heightOf(current, context);
            if (h >= 0 && h <= ancestorHeight) return false;
            String next = parents.get(current);
            if (next == null) {
                Block b = context.getBlockchain().getBlock(current);
                next = b != null ? b.getPreviousHash() : null;
            }
            current = next;
        }
        return false;
    }

    /** deficit = max(0, -Delta) while I have an own tip; 0 when synced. Recomputed, never counted. */
    private void updateDeficit(BlockchainSystemNodeContext context) {
        if (state.getOwnTipHash() == null) {
            state.setDeficit(0);
            return;
        }
        long delta = ownHeight(context) - publicTipHeight;
        state.setDeficit((int) Math.max(0, -delta));
    }

    // ---------------------------------------------------------------- actions

    /**
     * Give-up / acceptance (AD 5.4): append the public block and abandon the private state in every
     * case -- also when the block is an orphan -- so the attacker never stays on an abandoned tip.
     */
    private void acceptPublicChain(Block block, BlockchainSystemNodeContext context) {
        AppendOutcome outcome = BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(block, context);
        if (outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING) {
            context.getTrxMemPool().removeTransactions(block.getTransactions());
            context.getBlockPropagationStrategy().distribute(block);
        }
        resetPrivateState();
        context.getMiningProcess().restartMining();
    }

    /** Store a public block to keep the local view connected, without any strategy reaction. */
    private void appendWithoutReaction(Block block, BlockchainSystemNodeContext context) {
        AppendOutcome outcome = BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(block, context);
        if (outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING) {
            context.getTrxMemPool().removeTransactions(block.getTransactions());
            context.getBlockPropagationStrategy().distribute(block);
            if (state.getOwnTipHash() == null) context.getMiningProcess().restartMining();
        }
    }

    /**
     * Catch-up reveal (lead = 0''): publish every hidden block in order. Blocks below the public
     * height append as stale locally (expected for j >= 2: they are the ancestors of the tip); the
     * last one lands at the public height and creates the equal-length fork.
     */
    private void publishCatchUpBranch(BlockchainSystemNodeContext context) {
        while (!state.getPrivateChain().isEmpty()) {
            boolean last = state.getPrivateChain().size() == 1;
            if (!publishOneHiddenBlock(context, !last)) break;
        }
    }

    /** Publish hidden blocks, oldest first, while the oldest lies at or below the given height. */
    private void publishUpToHeight(long maxHeight, BlockchainSystemNodeContext context) {
        while (!state.getPrivateChain().isEmpty()) {
            Block next = state.peekNextToPublish();
            if (heightOf(next.getHash(), context) > maxHeight) break;
            if (!publishOneHiddenBlock(context, false)) break;
        }
    }

    /** Publish all remaining hidden attacker blocks in order. */
    private void publishAllHiddenBlocks(BlockchainSystemNodeContext context) {
        while (!state.getPrivateChain().isEmpty()) {
            if (!publishOneHiddenBlock(context, false)) break;
        }
    }

    /**
     * Publish the oldest hidden attacker block.
     *
     * @param staleAllowed true when a locally stale append is expected (catch-up ancestors)
     * @return true iff the block left the hidden suffix by being published
     */
    private boolean publishOneHiddenBlock(BlockchainSystemNodeContext context, boolean staleAllowed) {
        Block publish = state.peekNextToPublish();
        if (publish == null) {
            return false;
        }

        AppendOutcome outcome = BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(publish, context);

        if (outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING
                || (staleAllowed && outcome == AppendOutcome.STALE)) {
            state.removeFirstPending();
            context.getTrxMemPool().removeTransactions(publish.getTransactions());
            context.getMiningProcess().restartMining();
            context.getBlockPropagationStrategy().distribute(publish);
            publicTipHeight = Math.max(publicTipHeight, heightOf(publish.getHash(), context));
            if (getTraceEventLogger() != null && getSimulationContext() != null
                    && getTraceEventLogger().isEventTypeEnabled(BlockBroadcastTraceEvent.EVENT_TYPE)) {
                getTraceEventLogger().logEvent(new BlockBroadcastTraceEvent(
                        getSimulationContext().getSystemClock().getCurrentTime(), publish));
            }
            return true;
        }

        if (outcome == AppendOutcome.STALE || outcome == AppendOutcome.ALREADY_APPENDED) {
            // Defensive (AD 5.5): the block lies below this node's chain and can never become
            // public -- drop it from the hidden suffix instead of keeping it forever, and count it.
            state.removeFirstPending();
            droppedUnpublishable++;
            return true;
        }

        return false;
    }

    private void resetPrivateState() {
        state.reset();
    }

    // UnpublishedBlockCounter: read-only views of `state` for the simulation monitor (end rule,
    // diagnostics). No decision in this class reads them.
    @Override
    public int getUnpublishedBlockCount() { return state.getUnpublishedSuffix().size(); }

    @Override
    public long getBranchStartTime() { return state.getBranchStartTime(); }

    @Override
    public boolean hasOpenContest() { return state.isInContest(); }

    @Override
    public int getTrailingDeficit() { return state.getDeficit(); }

    @Override
    public long getDroppedUnpublishableBlockCount() { return droppedUnpublishable; }

    @Override
    public void dispatchEvent(Event event) {
        // no-op
    }
}
