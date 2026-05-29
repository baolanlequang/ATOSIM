package org.palladiosimulator.blockchainsystems.core.transaction;

import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TrxMemPool;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TrxMemPoolFactory;

public class TrxMemPoolFactoryImpl implements TrxMemPoolFactory {

    @Override
    public TrxMemPool createEmptyTransactionMemPool(String nodeId) {
        return new TrxMemPoolImpl(nodeId);
    }
}
