package org.palladiosimulator.blockchainsystems.core.propagation.transaction;

import org.palladiosimulator.blockchainsystems.core.propagation.PropagationStrategy;
import org.palladiosimulator.blockchainsystems.core.propagation.PropagationStrategyFactory;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

import java.util.function.Supplier;

public class TransactionPropagationStrategyFactoryImpl implements PropagationStrategyFactory<Transaction> {

    private final Supplier<PropagationStrategy<Transaction>> _strategySupplier;

    public TransactionPropagationStrategyFactoryImpl(Supplier<PropagationStrategy<Transaction>> strategySupplier) {
        _strategySupplier = strategySupplier;
    }

    @Override
    public PropagationStrategy<Transaction> createPropagationStrategy() {
        return _strategySupplier.get();
    }
}
