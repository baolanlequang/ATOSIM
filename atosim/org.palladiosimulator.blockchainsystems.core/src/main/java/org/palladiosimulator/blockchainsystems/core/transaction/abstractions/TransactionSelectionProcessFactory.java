package org.palladiosimulator.blockchainsystems.core.transaction.abstractions;

public interface TransactionSelectionProcessFactory {
    TransactionSelectionProcess createTransactionSelectionProcess(String nodeId);
}
