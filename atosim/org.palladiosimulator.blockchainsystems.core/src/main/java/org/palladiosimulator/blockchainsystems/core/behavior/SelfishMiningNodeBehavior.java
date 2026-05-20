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
 * Stronger selfish-mining behavior with explicit private-branch and tie-state tracking.
 *
 * Main state:
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
public class SelfishMiningNodeBehavior extends BlockchainNodeObject implements BlockchainSystemNodeBehavior {

    private final HonestBlockchainSystemNodeBehavior honest = new HonestBlockchainSystemNodeBehavior();

    /**
     * Hidden attacker blocks that have been mined but not yet published.
     * Index 0 is the next hidden block that would be published first.
     */
    private final List<Block> privateChain = new ArrayList<>();

    /**
     * Number of attacker blocks from the current private branch that were already
     * revealed to the public during the current tie/race episode.
     *
     * This is not the same thing as hidden lead. It lets us distinguish:
     * - how many blocks remain hidden
     * - whether we already revealed some private advantage into the public view
     */
    private int publishedInCurrentTie = 0;

    /**
     * True when the selfish miner is currently in the public tie/race phase.
     * This is the tie phase inside selfish mining, not the separate "race attack".
     */
    private boolean inTieState = false;

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

        if (inTieState) {
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
                inTieState = true;
                publishedInCurrentTie = 1;
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
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        privateChain.add(block);

        // If the attacker mines during a tie, that usually means the selfish miner can
        // immediately reveal remaining hidden blocks and try to secure the win.
        if (inTieState) {
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
        // Mine on top of the newest hidden attacker block if any exist.
        if (!privateChain.isEmpty()) {
            return privateChain.get(privateChain.size() - 1).getHash();
        }

        // Otherwise mine on the public tip according to the honest behavior.
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

            // Safety guard against infinite loops if append fails and no progress is made.
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
        publishedInCurrentTie = 0;
    }

    private void resetPrivateState() {
        privateChain.clear();
        clearTieStateOnly();
    }

    @Override
    public void dispatchEvent(Event event) {
        // no-op
    }
}