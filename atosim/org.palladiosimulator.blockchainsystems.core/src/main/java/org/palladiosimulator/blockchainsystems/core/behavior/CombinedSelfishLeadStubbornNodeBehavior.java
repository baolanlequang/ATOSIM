package org.palladiosimulator.blockchainsystems.core.behavior;

import org.jetbrains.annotations.NotNull;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeContext;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

public class CombinedSelfishLeadStubbornNodeBehavior extends BlockchainNodeObject implements BlockchainSystemNodeBehavior {

    private final SelfishMiningNodeBehavior selfish = new SelfishMiningNodeBehavior();
    private final LeadStubbornMiningNodeBehavior leadStubborn = new LeadStubbornMiningNodeBehavior();

    @Override
    public void onNodeInitialized(BlockchainSystemNodeContext context) {
        selfish.onNodeInitialized(context);
    }

    @Override
    public void onTransactionReceived(Transaction transaction, BlockchainSystemNodeContext context) {
        leadStubborn.onTransactionReceived(transaction, context);
    }

    @Override
    public void onBlockReceived(Block block, BlockchainSystemNodeContext context) {
        selfish.onBlockReceived(block, context);
    }

    @Override
    public void onBlockValidated(Block block, boolean isValid, BlockchainSystemNodeContext context) {
        selfish.onBlockValidated(block, isValid, context);
        if (isValid) {
            leadStubborn.onBlockValidated(block, true, context);
        }
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        selfish.onBlockMined(block, context);
        leadStubborn.onBlockMined(block, context);
    }

    @Override
    public Block onCreatingBlock(long blockMinedAt, String previousBlockHash, BlockchainSystemNodeContext context) {
        return selfish.onCreatingBlock(blockMinedAt, previousBlockHash, context);
    }

    @NotNull
    @Override
    public String onPreviousBlockSelection(BlockchainSystemNodeContext context) {
        return selfish.onPreviousBlockSelection(context);
    }

    @Override
    public void dispatchEvent(Event event) {
        selfish.dispatchEvent(event);
        leadStubborn.dispatchEvent(event);
    }
}
