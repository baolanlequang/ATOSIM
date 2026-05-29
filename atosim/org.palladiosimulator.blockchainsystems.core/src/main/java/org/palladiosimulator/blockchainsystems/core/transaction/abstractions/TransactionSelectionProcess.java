package org.palladiosimulator.blockchainsystems.core.transaction.abstractions;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.Traceable;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeContext;
import org.palladiosimulator.blockchainsystems.core.transaction.TransactionSelectionResult;

public interface TransactionSelectionProcess extends Traceable {
    TransactionSelectionResult selectTransactionsForBlock(BlockchainSystemNodeContext context);
}
