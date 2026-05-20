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
 * Stronger Trail-Stubborn Mining behavior with explicit trailing-contest state.
 *
 * Main idea:
 * - the attacker prefers to stay behind the public branch longer than EqualForkStubborn
 *   and reacts more reluctantly than SelfishMining
 * - with only a small hidden advantage, it keeps mining privately instead of immediately
 *   releasing to match public progress
 * - with larger hidden advantage, it reveals blocks gradually to keep the contest alive
 *   without dumping the whole branch
 *
 * State:
 * - privateChain: attacker-mined blocks that are still hidden / unpublished
 * - inTrailingContest: true when the attacker is currently responding to public progress
 *   with controlled delayed publication
 * - publishedInCurrentContest: number of attacker blocks already revealed in the current contest
 *
 * Notes:
 * - Transactions are removed only after INCLUDED or FORKING outcomes.
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
     * True when the attacker is currently in a trailing public contest.
     */
    private boolean inTrailingContest = false;

    /**
     * Number of attacker blocks already revealed in the current contest.
     * This is separate from the number of still-hidden blocks.
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

        if (inTrailingContest) {
            handleBlockWhileInTrailingContest(block, context);
            return;
        }

        int hiddenLead = hiddenLead();

        // No hidden advantage -> behave honestly.
        if (hiddenLead == 0) {
            adoptPublicBlockAndAbandonPrivateState(block, context);
            return;
        }

        // Trail-stubborn behavior:
        // with exactly one hidden block, keep trailing and do not reveal yet.
        if (hiddenLead == 1) {
            return;
        }

        // With exactly two hidden blocks, reveal one to begin a delayed contest,
        // but keep one block hidden rather than trying to match aggressively.
        if (hiddenLead == 2) {
            boolean published = publishOneHiddenBlock(context);
            if (published) {
                inTrailingContest = true;
                publishedInCurrentContest = 1;
            }
            return;
        }

        // With more than two hidden blocks, reveal one and keep the rest hidden.
        boolean published = publishOneHiddenBlock(context);
        if (published) {
            inTrailingContest = true;
            publishedInCurrentContest++;
        }
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        privateChain.add(block);

        // Trail-stubborn is intentionally reluctant to dump the whole hidden branch.
        // Even during contest, mining a block does not automatically trigger publish-all.
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
     * Trailing-contest handler.
     *
     * Interpretation:
     * - if more than one hidden block remains, reveal one block and keep trailing
     * - if exactly one hidden block remains, keep it hidden and continue trailing
     * - if no hidden blocks remain, abandon the private strategy and adopt public progress
     */
    private void handleBlockWhileInTrailingContest(Block block, BlockchainSystemNodeContext context) {
        int hiddenLead = hiddenLead();

        if (hiddenLead > 1) {
            boolean published = publishOneHiddenBlock(context);
            if (published) {
                publishedInCurrentContest++;
            }
            return;
        }

        if (hiddenLead == 1) {
            // Preserve the final hidden block and continue trailing.
            return;
        }

        adoptPublicBlockAndAbandonPrivateState(block, context);
    }

    /**
     * Append a public block and fully abandon the no-longer-viable private state.
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
        inTrailingContest = false;
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