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
 * Race-attack behavior with explicit arming, trigger, contest, and reset states.
 *
 * Main idea:
 * - the attacker first mines a private conflicting block and withholds it (armed state)
 * - when an honest sibling on the same parent appears, the attacker releases its hidden block
 *   to trigger a public race
 * - if the attacker mines again during the contest, it releases the remaining hidden blocks
 *   aggressively to strengthen its branch
 * - if honest public progress moves away from the contested parent and the attacker has no
 *   remaining hidden advantage, the attacker abandons the attempt and adopts the public view
 *
 * This gives the race attack real strategic depth instead of modeling it as honest mining plus
 * only transaction-gossip timing manipulation.
 */
public class RaceMiningNodeBehavior extends BlockchainNodeObject implements BlockchainSystemNodeBehavior {

    private final HonestBlockchainSystemNodeBehavior honest = new HonestBlockchainSystemNodeBehavior();

    /**
     * Hidden attacker blocks that have been mined but not yet published.
     * Index 0 is the next block that would be revealed first.
     */
    private final List<Block> privateChain = new ArrayList<>();

    /**
     * True once the attacker has revealed a competing block and is in an active public race.
     */
    private boolean inContestState = false;

    /**
     * Parent hash of the contested sibling set.
     * Example: if attacker and honest blocks both build on X, then contestedParentHash = X.
     */
    private String contestedParentHash = null;

    /**
     * Number of attacker blocks already revealed in the current contest.
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

        // No private attack is currently armed -> behave publicly/honestly.
        if (privateChain.isEmpty()) {
            adoptPublicBlock(block, context);
            return;
        }

        // The armed attack is triggered when an honest sibling on the same parent appears.
        if (isTriggeringSibling(block)) {
            boolean published = publishOneHiddenBlock(context);
            if (published) {
                inContestState = true;
                contestedParentHash = block.getPreviousHash();
                publishedInCurrentContest = 1;
            }
            return;
        }

        // Public progress on some other path invalidates the currently armed race attempt.
        // The attacker abandons and resynchronizes with the public chain.
        adoptPublicBlockAndAbandonPrivateState(block, context);
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        privateChain.add(block);

        // If the attacker mines during an active race, strengthen the attacker branch immediately.
        if (inContestState) {
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
        // While the attack is armed or the race is ongoing, keep extending the newest hidden attacker tip.
        if (!privateChain.isEmpty()) {
            return privateChain.get(privateChain.size() - 1).getHash();
        }

        return honest.onPreviousBlockSelection(context);
    }

    /**
     * Contest-state handler.
     *
     * Policy:
     * - if the attacker still has hidden blocks left, reveal them to push the attacker branch harder
     * - if no hidden blocks remain, treat the contest as locally lost and adopt honest public progress
     */
    private void handleBlockWhileInContest(Block block, BlockchainSystemNodeContext context) {
        if (hiddenLead() > 0) {
            publishAllHiddenBlocks(context);
            clearContestStateOnly();
            return;
        }

        adoptPublicBlockAndAbandonPrivateState(block, context);
    }

    /**
     * Trigger condition for a race contest:
     * - the attacker already has a hidden block
     * - the public network now reveals another block on the same parent
     */
    private boolean isTriggeringSibling(Block block) {
        if (privateChain.isEmpty()) {
            return false;
        }

        Block firstPrivate = privateChain.get(0);
        String privateParentHash = firstPrivate.getPreviousHash();
        String publicParentHash = block.getPreviousHash();

        if (privateParentHash == null || publicParentHash == null) {
            return false;
        }

        return privateParentHash.equals(publicParentHash)
                && !firstPrivate.getHash().equals(block.getHash());
    }

    /**
     * Append a public block and abandon the current private race attempt.
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
     * Append a public block without touching private race state.
     * Used only when no race attempt is currently armed.
     */
    private void adoptPublicBlock(Block block, BlockchainSystemNodeContext context) {
        AppendOutcome outcome = BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(block, context);

        if (outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING) {
            context.getTrxMemPool().removeTransactions(block.getTransactions());
            context.getMiningProcess().restartMining();
            context.getBlockPropagationStrategy().distribute(block);
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
        contestedParentHash = null;
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