package org.palladiosimulator.blockchainsystems.threesim.behavior;

import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.ValueProvider;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeContext;
import org.palladiosimulator.blockchainsystems.core.transaction.TransactionFactoryImpl;
import org.palladiosimulator.blockchainsystems.core.transaction.TransactionProperties;
import org.palladiosimulator.blockchainsystems.core.transaction.TransactionSelectionResult;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TransactionSelectionProcess;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Fills every block to {@code maxBlockSize} with freshly-manufactured transactions drawn from
 * the model's transaction-properties distribution, independent of any mempool or arrival
 * process -- the sustained full-block condition the draft's methodology assumes.
 */
public class DeterministicFullBlockTransactionSelectionProcess extends BlockchainNodeObject
        implements TransactionSelectionProcess {

    private final int _maxBlockSize;
    private final ValueProvider<TransactionProperties> _transactionPropertiesProvider;
    private final TransactionFactoryImpl _transactionFactory = new TransactionFactoryImpl();

    public DeterministicFullBlockTransactionSelectionProcess(
            int maxBlockSize, ValueProvider<TransactionProperties> transactionPropertiesProvider) {
        _maxBlockSize = maxBlockSize;
        _transactionPropertiesProvider = transactionPropertiesProvider;
    }

    @Override
    public TransactionSelectionResult selectTransactionsForBlock(BlockchainSystemNodeContext context) {
        long creationTime = getSimulationContext().getSystemClock().getCurrentTime();
        int currentBlockSize = 0;
        Set<Transaction> selected = new HashSet<>();
        while (true) {
            TransactionProperties properties = _transactionPropertiesProvider.getValue();
            int newSize = currentBlockSize + properties.getSize();
            if (newSize > _maxBlockSize) break;
            selected.add(_transactionFactory.createTransaction(
                    UUID.randomUUID().toString(),
                    properties.getSize(),
                    creationTime,
                    context.getId(),
                    context.getId(),
                    context.getId(),
                    properties.getAmount(),
                    properties.getFee()));
            currentBlockSize = newSize;
        }
        return new TransactionSelectionResult(selected, currentBlockSize);
    }

    @Override
    public void dispatchEvent(Event event) {
    }
}
