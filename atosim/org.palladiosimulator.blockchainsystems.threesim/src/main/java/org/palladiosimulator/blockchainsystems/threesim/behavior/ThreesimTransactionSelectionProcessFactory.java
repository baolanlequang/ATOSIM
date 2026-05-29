package org.palladiosimulator.blockchainsystems.threesim.behavior;

import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TransactionSelectionProcess;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TransactionSelectionProcessFactory;

public class ThreesimTransactionSelectionProcessFactory implements TransactionSelectionProcessFactory {

    private final int _maxBlockSize;

    public ThreesimTransactionSelectionProcessFactory(int maxBlockSize) {
        _maxBlockSize = maxBlockSize;
    }

    @Override
    public TransactionSelectionProcess createTransactionSelectionProcess(String nodeId) {
        return new ThreesimTransactionSelectionProcess(_maxBlockSize);
    }
}
