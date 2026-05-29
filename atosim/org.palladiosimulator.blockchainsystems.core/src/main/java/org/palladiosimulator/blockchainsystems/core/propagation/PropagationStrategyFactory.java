package org.palladiosimulator.blockchainsystems.core.propagation;

public interface PropagationStrategyFactory<E extends Propagatable> {
    PropagationStrategy<E> createPropagationStrategy();
}
