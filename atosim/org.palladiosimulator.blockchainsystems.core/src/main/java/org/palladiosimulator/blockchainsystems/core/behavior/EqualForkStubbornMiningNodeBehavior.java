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
 * Stronger Equal-Fork Stubborn Mining behavior with explicit contest-state tracking.
 *
 * Main idea:
 * - if the attacker has no hidden advantage, behave honestly
 * - if the attacker has hidden blocks and honest progress appears, reveal just enough
 *   to keep parity with the public branch instead of aggressively dumping everything
 * - once in an equal-fork contest, continue releasing one block at a time while hidden
 *   inventory remains, trying to sustain the parity contest
 *
 * State:
 * - privateChain: attacker-mined blocks that are still hidden / unpublished
 * - inEqualForkContest: true when the attacker has already revealed blocks to maintain
 *   parity with the public branch
 * - publishedInCurrentContest: number of attacker blocks revealed in the current contest
 *
 * Notes:
 * - It intentionally removes transactions only after INCLUDED or FORKING outcomes.
 */
public class EqualForkStubbornMiningNodeBehavior extends BlockchainNodeObject implements BlockchainSystemNodeBehavior {

    private final HonestBlockchainSystemNodeBehavior honest = new HonestBlockchainSystemNodeBehavior();

    /**
     * Hidden attacker blocks that have been mined but not yet published.
     * Index 0 is the next hidden block to reveal.
     */
    private final List<Block> privateChain = new ArrayList<>();

    /**
     * True when the attacker is currently engaged in an equal-fork public contest.
     */
    private boolean inEqualForkContest = false;

    /**
     * Number of attacker blocks already revealed in the current equal-fork contest.
     */
    private int publishedInCurrentContest = 0;

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

        if (inEqualForkContest) {
            handleBlockWhileInEqualForkContest(block, context);
            return;
        }

        int hiddenLead = hiddenLead();

        // No hidden branch -> behave honestly.
        if (hiddenLead == 0) {
            adoptPublicBlockAndAbandonPrivateState(block, context);
            return;
        }

        // Equal-fork behavior:
        // whenever honest progress arrives and we have hidden blocks,
        // reveal exactly one block to try to match public progress.
        boolean published = publishOneHiddenBlock(context);
        if (published) {
            inEqualForkContest = true;
            publishedInCurrentContest = 1;
        }
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        privateChain.add(block);

        // In equal-fork stubborn mining, mining during an active contest does not immediately
        // imply dumping the whole hidden branch. The attacker prefers to keep controlled parity.
        // So we do not auto-publish-all here.
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
        if (!privateChain.isEmpty()) {
            return privateChain.get(privateChain.size() - 1).getHash();
        }

        return honest.onPreviousBlockSelection(context);
    }

    /**
     * In an equal-fork contest, keep revealing one block at a time while hidden inventory exists,
     * attempting to preserve parity with the public branch.
     *
     * If no hidden blocks remain, the contest is treated as lost locally and the public block is adopted.
     */
    private void handleBlockWhileInEqualForkContest(Block block, BlockchainSystemNodeContext context) {
        if (hiddenLead() > 0) {
            boolean published = publishOneHiddenBlock(context);
            if (published) {
                publishedInCurrentContest++;
            }
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

    private int hiddenLead() {
        return privateChain.size();
    }

    private void clearContestStateOnly() {
        inEqualForkContest = false;
        publishedInCurrentContest = 0;
    }

    private void resetPrivateState() {
        privateChain.clear();
        clearContestStateOnly();
    }

    @Override
    public void dispatchEvent(Event event) {
        // no-op
    }
}