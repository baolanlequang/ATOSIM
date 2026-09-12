package org.palladiosimulator.blockchainsystems.threesim.behavior;

import org.palladiosimulator.blockchainsystems.bscm.transactions.TransactionPropertiesSpecification;
import org.palladiosimulator.blockchainsystems.bscm.transactions.TransactionPropertiesSpecificationValue;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TransactionSelectionProcess;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TransactionSelectionProcessFactory;
import org.palladiosimulator.blockchainsystems.threesim.creation.TransactionPropertiesValueProviderAdapter;
import org.palladiosimulator.blockchainsystems.threesim.simulation.DeterministicSeeds;
import org.palladiosimulator.blockchainsystems.threesim.simulation.TransactionGenerationMode;

public class ThreesimTransactionSelectionProcessFactory implements TransactionSelectionProcessFactory {

    private final int _maxBlockSize;
    private final TransactionGenerationMode _mode;
    private final TransactionPropertiesSpecification _transactionPropertiesSpecification;
    private final long _rootSeed;

    public ThreesimTransactionSelectionProcessFactory(int maxBlockSize) {
        this(maxBlockSize, TransactionGenerationMode.MEMPOOL, null, 0L);
    }

    public ThreesimTransactionSelectionProcessFactory(
            int maxBlockSize,
            TransactionGenerationMode mode,
            TransactionPropertiesSpecification transactionPropertiesSpecification,
            long rootSeed) {
        _maxBlockSize = maxBlockSize;
        _mode = mode != null ? mode : TransactionGenerationMode.MEMPOOL;
        _transactionPropertiesSpecification = transactionPropertiesSpecification;
        _rootSeed = rootSeed;
    }

    @Override
    public TransactionSelectionProcess createTransactionSelectionProcess(String nodeId) {
        return switch (_mode) {
            // Each node needs its own seeded generator here (site tag includes nodeId), not one
            // shared across nodes -- with DETERMINISTIC_FULL_BLOCK, selectTransactionsForBlock is
            // called by every node's event-driven block creation, so a single shared generator's
            // draw order (and therefore every downstream simulation outcome) would depend on
            // event execution order instead of the seed.
            // Item 7: floor of the finite discrete size distribution's support, so the selector
            // knows when no further draw could possibly fit (see
            // DeterministicFullBlockTransactionSelectionProcess's stopping-rule comment).
            case DETERMINISTIC_FULL_BLOCK -> new DeterministicFullBlockTransactionSelectionProcess(
                    _maxBlockSize,
                    TransactionPropertiesValueProviderAdapter.create(
                            _transactionPropertiesSpecification,
                            DeterministicSeeds.seededGenerator(_rootSeed, "transactionSelectionProperties:" + nodeId)),
                    _transactionPropertiesSpecification.getValues().stream()
                            .mapToInt(TransactionPropertiesSpecificationValue::getSize)
                            .min().orElse(Integer.MAX_VALUE),
                    DeterministicSeeds.seededGenerator(_rootSeed, "transactionId:" + nodeId));
            case MEMPOOL -> new ThreesimTransactionSelectionProcess(_maxBlockSize);
        };
    }
}
