package org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions;

public interface OutputMetricCalculator<M extends OutputMetric<?>> {
    M calculate();
}
