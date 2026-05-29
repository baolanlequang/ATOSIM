package org.palladiosimulator.blockchainsystems.core.system.abstractions;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Traceable;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

public interface BlockchainSystemNodeBehavior extends Traceable {
    void onNodeInitialized(BlockchainSystemNodeContext context);
    void onTransactionReceived(Transaction transaction, BlockchainSystemNodeContext context);
    void onBlockReceived(Block block, BlockchainSystemNodeContext context);
    void onBlockValidated(Block block, boolean isValid, BlockchainSystemNodeContext context);
    void onBlockMined(Block block, BlockchainSystemNodeContext context);
    Block onCreatingBlock(long blockMinedAt, String previousBlockHash, BlockchainSystemNodeContext context);
    String onPreviousBlockSelection(BlockchainSystemNodeContext context);
}
