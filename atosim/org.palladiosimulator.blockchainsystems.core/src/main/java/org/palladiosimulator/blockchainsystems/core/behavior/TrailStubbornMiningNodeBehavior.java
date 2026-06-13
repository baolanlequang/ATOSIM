package org.palladiosimulator.blockchainsystems.core.behavior;

import org.jetbrains.annotations.NotNull;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeContext;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
 * State:
 * - privateChain: attacker-mined blocks that are still hidden / unpublished (used both while
 *   ahead and while trying to catch up from behind)
 * - ownTipHash: hash of the attacker's own most recently authored block, published or not
 * - inTieState: true when the attacker has revealed part of its private branch and is
 *   currently in a tied public contest
 * - deficit: how many blocks behind the public chain the attacker currently trails (0 when
 *   not trailing)
 */
public class TrailStubbornMiningNodeBehavior extends BlockchainNodeObject
        implements BlockchainSystemNodeBehavior {

    private final HonestBlockchainSystemNodeBehavior honest = new HonestBlockchainSystemNodeBehavior();

    /**
     * Hidden attacker blocks that have been mined but not yet published.
     * Index 0 is the next hidden block that would be revealed first.
     */
    private final List<Block> privateChain = new ArrayList<>();

    /**
     * Hash of the attacker's own most recently authored block, published or not.
     * Kept separate from privateChain because privateChain empties out the moment a
     * block is published, even though the attacker must keep mining on top of that same
     * block rather than falling back to an ambiguous public tip.
     */
    private String ownTipHash = null;

    /**
     * True when the attacker is currently in a tied public contest.
     */
    private boolean inTieState = false;

    /**
     * Number of attacker blocks already revealed in the current contest.
     */
    private int publishedInCurrentContest = 0;

    /**
     * How many blocks behind the public chain the attacker currently trails.
     * 0 means not trailing (privateChain, if non-empty, represents a hidden lead instead).
     */
    private int deficit = 0;

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
        if (!isValid) {
            return;
        }

        if (deficit > 0) {
            handleBlockWhileTrailing(block, context);
            return;
        }

        if (inTieState) {
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
                inTieState = true;
                publishedInCurrentContest = 1;
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
    }

    /**
     * Already trailing: one more public block extends the deficit. Give up only once behind
     * by more than j; otherwise keep the local view connected (without reacting to it) so a
     * later give-up can still resolve this block's ancestry.
     */
    private void handleBlockWhileTrailing(Block block, BlockchainSystemNodeContext context) {
        deficit++;

        if (deficit > j) {
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
        if (ownTipHash == null) {
            ownTipHash = block.getPreviousHash();
        }
        deficit = 1;
        BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(block, context);
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        if (deficit > 0) {
            // Mining while trailing catches up by one block. Reaching deficit=0 this way lands
            // at the paper's "0''" state, which behaves like a plain hidden lead of 1 since
            // nothing has been revealed - no special-casing needed beyond the decrement.
            privateChain.add(block);
            ownTipHash = block.getHash();
            deficit--;
            return;
        }

        // Selfish mining's own-mining rule is unchanged: only winning an already-tied race
        // outright (state 0', no remaining hidden material) reveals.
        boolean wasWinningRace = inTieState && privateChain.isEmpty();

        privateChain.add(block);
        ownTipHash = block.getHash();

        if (wasWinningRace) {
            publishAllHiddenBlocks(context);
            clearTieStateOnly();
        }
    }

    @Override
    public Block onCreatingBlock(long blockMinedAt, String previousBlockHash, BlockchainSystemNodeContext context) {
        var selection = context.getTransactionSelectionProcess().selectTransactionsForBlock(context);

        return context.getBlockFactory().createBlock(
                UUID.randomUUID().toString(),
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
        if (ownTipHash != null) {
            return ownTipHash;
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
        if (privateChain.isEmpty()) {
            return false;
        }

        Block publish = privateChain.get(0);

        AppendOutcome outcome = BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(publish, context);

        if (outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING) {
            privateChain.remove(0);
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
        while (!privateChain.isEmpty()) {
            int sizeBefore = privateChain.size();
            boolean published = publishOneHiddenBlock(context);

            if (!published || privateChain.size() == sizeBefore) {
                break;
            }
        }
    }

    private int hiddenLead() {
        return privateChain.size();
    }

    private void clearTieStateOnly() {
        inTieState = false;
        publishedInCurrentContest = 0;
    }

    private void resetPrivateState() {
        privateChain.clear();
        ownTipHash = null;
        deficit = 0;
        clearTieStateOnly();
    }

    @Override
    public void dispatchEvent(Event event) {
        // no-op
    }
}
