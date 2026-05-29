package org.palladiosimulator.blockchainsystems.core.transaction.abstractions;

public interface TrxMemPoolFactory {
    TrxMemPool createEmptyTransactionMemPool(String nodeId);
}
