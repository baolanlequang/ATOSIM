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
import org.palladiosimulator.blockchainsystems.threesim.simulation.DeterministicSeeds;

import java.util.HashSet;
import java.util.Set;
import java.util.random.RandomGenerator;

/**
 * Fills every block to {@code maxBlockSize} with freshly-manufactured transactions drawn from
 * the model's transaction-properties distribution, independent of any mempool or arrival
 * process -- the sustained full-block condition the draft's methodology assumes.
 */
public class DeterministicFullBlockTransactionSelectionProcess extends BlockchainNodeObject
        implements TransactionSelectionProcess {

    private final int _maxBlockSize;
    private final ValueProvider<TransactionProperties> _transactionPropertiesProvider;
    private final int _minTransactionSize;
    private final RandomGenerator _idGenerator;
    private final TransactionFactoryImpl _transactionFactory = new TransactionFactoryImpl();

    public DeterministicFullBlockTransactionSelectionProcess(
            int maxBlockSize, ValueProvider<TransactionProperties> transactionPropertiesProvider,
            int minTransactionSize, RandomGenerator idGenerator) {
        _maxBlockSize = maxBlockSize;
        _transactionPropertiesProvider = transactionPropertiesProvider;
        _minTransactionSize = minTransactionSize;
        _idGenerator = idGenerator;
    }

    @Override
    public TransactionSelectionResult selectTransactionsForBlock(BlockchainSystemNodeContext context) {
        long creationTime = getSimulationContext().getSystemClock().getCurrentTime();
        int currentBlockSize = 0;
        Set<Transaction> selected = new HashSet<>();
        // Item 7: stop only once remaining capacity is below the smallest transaction size the
        // distribution can ever produce (_minTransactionSize, the floor of the finite discrete
        // support in TransactionPropertiesSpecification -- see
        // ThreesimTransactionSelectionProcessFactory, where it's computed from the same spec used
        // to build _transactionPropertiesProvider). A single draw that doesn't fit is discarded
        // and retried -- it does not end the block -- since "continue selecting transactions that
        // fit" means the packing process itself only gives up once no future draw could possibly
        // fit, not the first time one random draw happens not to. The pre-draw capacity check
        // (rather than checking after drawing and discarding) also avoids wasting a draw -- and
        // perturbing the per-node seeded RNG stream -- once no draw could ever succeed anyway.
        while (_maxBlockSize - currentBlockSize >= _minTransactionSize) {
            TransactionProperties properties = _transactionPropertiesProvider.getValue();
            int newSize = currentBlockSize + properties.getSize();
            if (newSize > _maxBlockSize) continue;
            selected.add(_transactionFactory.createTransaction(
                    DeterministicSeeds.randomHexId(_idGenerator),
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
