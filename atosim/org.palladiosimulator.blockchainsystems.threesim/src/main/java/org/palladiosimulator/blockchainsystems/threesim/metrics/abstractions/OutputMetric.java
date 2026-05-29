package org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions;

public interface OutputMetric<T> {
    T getValue();
    String getName();
    String getUnit();
}
