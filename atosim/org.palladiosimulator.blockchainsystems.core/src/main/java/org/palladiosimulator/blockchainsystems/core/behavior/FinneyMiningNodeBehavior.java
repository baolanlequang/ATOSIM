package org.palladiosimulator.blockchainsystems.core.behavior;

import org.jetbrains.annotations.NotNull;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeContext;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

import java.util.UUID;

/**
 * Stronger Finney-mining behavior with explicit armed/released/invalidated state.
 *
 * Main idea:
 * - mine one private block in advance and keep it hidden
 * - wait for a transaction-trigger event while the private block remains strategically usable
 * - release the hidden block once the attack is armed and triggered
 * - invalidate and abandon the attack if public progress makes the hidden block no longer viable
 *
 * Notes:
 * - It intentionally removes transactions only after INCLUDED or FORKING outcomes.
 */
public class FinneyMiningNodeBehavior extends BlockchainNodeObject implements BlockchainSystemNodeBehavior {

    private final HonestBlockchainSystemNodeBehavior honest = new HonestBlockchainSystemNodeBehavior();

    /**
     * The single hidden pre-mined block used for the Finney attack.
     * Null means no private attack block is currently armed.
     */
    private Block privateBlock = null;

    /**
     * True once the attacker has an armed private block and is waiting for a trigger transaction.
     */
    private boolean attackArmed = false;

    /**
     * True once the currently armed private block has already been released.
     */
    private boolean attackReleased = false;

    /**
     * True once the currently armed attack became strategically unusable because public progress
     * invalidated the hidden block before release.
     */
    private boolean attackInvalidated = false;

    @Override
    public void onNodeInitialized(BlockchainSystemNodeContext context) {
        resetAttackState();
        context.getMiningProcess().startMining();
    }

    @Override
    public void onTransactionReceived(Transaction transaction, BlockchainSystemNodeContext context) {
        context.getTrxMemPool().storeTransaction(transaction);
        context.getTransactionPropagationStrategy().distribute(transaction);

        // Finney trigger:
        // if a private pre-mined block is armed and still valid, release it now.
        if (attackArmed && !attackReleased && !attackInvalidated && privateBlock != null) {
            releasePrivateBlock(context);
        }
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

        // If we still have a hidden Finney block armed but not yet released,
        // public progress may invalidate it strategically.
        if (attackArmed && !attackReleased && !attackInvalidated && privateBlock != null) {
            // In this simplified version, any meaningful public progress is treated as invalidating
            // the hidden pre-mined block before it can be profitably released.
            invalidateArmedAttack();
        }

        AppendOutcome outcome = BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(block, context);

        if (outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING) {
            context.getTrxMemPool().removeTransactions(block.getTransactions());
            context.getMiningProcess().restartMining();
            context.getBlockPropagationStrategy().distribute(block);
        }
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        // If no attack is currently armed, use the first mined block as the hidden Finney block.
        if (!attackArmed && privateBlock == null) {
            privateBlock = block;
            attackArmed = true;
            attackReleased = false;
            attackInvalidated = false;
            return;
        }

        // If the current Finney attack was already released or invalidated,
        // start behaving normally for subsequent mined blocks.
        AppendOutcome outcome = BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(block, context);

        if (outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING) {
            context.getTrxMemPool().removeTransactions(block.getTransactions());
            context.getBlockPropagationStrategy().distribute(block);
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
        // While the attack is armed and the private block is still hidden,
        // keep mining on top of that private block.
        if (attackArmed && !attackReleased && !attackInvalidated && privateBlock != null) {
            return privateBlock.getHash();
        }

        return honest.onPreviousBlockSelection(context);
    }

    /**
     * Release the hidden Finney block into the public chain.
     */
    private void releasePrivateBlock(BlockchainSystemNodeContext context) {
        if (privateBlock == null || attackReleased || attackInvalidated) {
            return;
        }

        AppendOutcome outcome = BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(privateBlock, context);

        if (outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING) {
            attackReleased = true;
            attackArmed = false;

            context.getTrxMemPool().removeTransactions(privateBlock.getTransactions());
            context.getMiningProcess().restartMining();
            context.getBlockPropagationStrategy().distribute(privateBlock);

            // Attack block was consumed successfully.
            privateBlock = null;
        }
    }

    /**
     * Abandon the currently armed hidden block because it is no longer strategically usable.
     */
    private void invalidateArmedAttack() {
        attackInvalidated = true;
        attackArmed = false;
        attackReleased = false;
        privateBlock = null;
    }

    private void resetAttackState() {
        privateBlock = null;
        attackArmed = false;
        attackReleased = false;
        attackInvalidated = false;
    }

    @Override
    public void dispatchEvent(Event event) {
        // no-op
    }
}