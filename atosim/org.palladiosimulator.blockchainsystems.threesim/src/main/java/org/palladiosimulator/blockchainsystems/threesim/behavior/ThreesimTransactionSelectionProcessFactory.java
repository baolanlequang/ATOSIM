package org.palladiosimulator.blockchainsystems.threesim.behavior;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.ValueProvider;
import org.palladiosimulator.blockchainsystems.core.transaction.TransactionProperties;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TransactionSelectionProcess;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TransactionSelectionProcessFactory;
import org.palladiosimulator.blockchainsystems.threesim.simulation.TransactionGenerationMode;

public class ThreesimTransactionSelectionProcessFactory implements TransactionSelectionProcessFactory {

    private final int _maxBlockSize;
    private final TransactionGenerationMode _mode;
    private final ValueProvider<TransactionProperties> _transactionPropertiesProvider;

    public ThreesimTransactionSelectionProcessFactory(int maxBlockSize) {
        this(maxBlockSize, TransactionGenerationMode.MEMPOOL, null);
    }

    public ThreesimTransactionSelectionProcessFactory(
            int maxBlockSize,
            TransactionGenerationMode mode,
            ValueProvider<TransactionProperties> transactionPropertiesProvider) {
        _maxBlockSize = maxBlockSize;
        _mode = mode != null ? mode : TransactionGenerationMode.MEMPOOL;
        _transactionPropertiesProvider = transactionPropertiesProvider;
    }

    @Override
    public TransactionSelectionProcess createTransactionSelectionProcess(String nodeId) {
        return switch (_mode) {
            case DETERMINISTIC_FULL_BLOCK -> new DeterministicFullBlockTransactionSelectionProcess(
                    _maxBlockSize, _transactionPropertiesProvider);
            case MEMPOOL -> new ThreesimTransactionSelectionProcess(_maxBlockSize);
        };
    }
}
