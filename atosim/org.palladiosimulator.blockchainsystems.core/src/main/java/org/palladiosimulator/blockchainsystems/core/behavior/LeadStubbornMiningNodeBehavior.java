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
 * Stronger lead-stubborn mining behavior with explicit private-branch and contest-state tracking.
 *
 * Main idea:
 * - the attacker prefers to preserve a one-block private advantage instead of reacting as aggressively
 *   as the selfish miner in every case
 * - when honest progress arrives and the attacker has exactly one hidden block, the attacker stays stubborn
 *   and keeps mining privately rather than immediately revealing
 * - when the attacker has larger hidden advantage, it reveals only enough to maintain pressure
 *
 * State:
 * - privateChain: attacker-mined blocks that are still hidden / unpublished
 * - inContestState: true when the attacker has already revealed some private branch material and is now
 *   in an active public contest
 * - publishedInCurrentContest: number of attacker blocks already revealed in the current public contest
 *
 * Notes:
 * - It is intentionally conservative about transaction removal:
 *   only remove transactions after INCLUDED or FORKING append outcomes.
 */
public class LeadStubbornMiningNodeBehavior extends BlockchainNodeObject implements BlockchainSystemNodeBehavior {

    private final HonestBlockchainSystemNodeBehavior honest = new HonestBlockchainSystemNodeBehavior();

    /**
     * Hidden attacker blocks that have been mined but not yet published.
     * Index 0 is the next hidden block that would be published first.
     */
    private final List<Block> privateChain = new ArrayList<>();

    /**
     * True when the attacker has already revealed part of its private branch and is currently
     * managing an ongoing public contest.
     */
    private boolean inContestState = false;

    /**
     * Number of attacker blocks revealed in the current contest.
     * This is not the same thing as hidden lead.
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

        if (inContestState) {
            handleBlockWhileInContest(block, context);
            return;
        }

        int hiddenLead = hiddenLead();

        // No hidden advantage left -> behave honestly and reset.
        if (hiddenLead == 0) {
            adoptPublicBlockAndAbandonPrivateState(block, context);
            return;
        }

        // Lead-stubborn behavior:
        // with exactly one hidden block, do NOT reveal immediately.
        // Stay private and keep mining to try to extend the lead.
        if (hiddenLead == 1) {
            // Intentionally do nothing here: keep mining privately.
            return;
        }

        // With exactly two hidden blocks:
        // reveal one block to begin/continue controlling the public contest,
        // but preserve a one-block hidden advantage.
        if (hiddenLead == 2) {
            boolean published = publishOneHiddenBlock(context);
            if (published) {
                inContestState = true;
                publishedInCurrentContest = 1;
            }
            return;
        }

        // With more than two hidden blocks:
        // reveal one block to maintain pressure, but keep the remaining advantage private.
        boolean published = publishOneHiddenBlock(context);
        if (published) {
            inContestState = true;
            publishedInCurrentContest++;
        }
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        privateChain.add(block);

        // If the attacker mines during an active contest, it can strengthen its position.
        // For lead-stubborn behavior, do not always dump everything immediately;
        // only publish all if the hidden branch has grown beyond a minimal preserved lead.
        if (inContestState && hiddenLead() > 1) {
            publishAllHiddenBlocks(context);
            clearContestStateOnly();
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
        if (!privateChain.isEmpty()) {
            return privateChain.get(privateChain.size() - 1).getHash();
        }

        return honest.onPreviousBlockSelection(context);
    }

    /**
     * Contest-state handler.
     *
     * Lead-stubborn interpretation:
     * - if the attacker still has more than one hidden block, reveal one more block and keep contest pressure
     * - if the attacker has exactly one hidden block left, stay stubborn and keep it private
     * - if the attacker has no hidden blocks left, the contest is treated as lost locally and public progress is adopted
     */
    private void handleBlockWhileInContest(Block block, BlockchainSystemNodeContext context) {
        int hiddenLead = hiddenLead();

        if (hiddenLead > 1) {
            boolean published = publishOneHiddenBlock(context);
            if (published) {
                publishedInCurrentContest++;
            }
            return;
        }

        if (hiddenLead == 1) {
            // Preserve a one-block hidden advantage and keep mining privately.
            // This is the core stubborn behavior.
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

    private void clearContestStateOnly() {
        inContestState = false;
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