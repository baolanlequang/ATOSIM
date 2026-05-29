package org.palladiosimulator.blockchainsystems.threesim.metrics;

import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;

public class FaultTolerance implements OutputMetric<FaultToleranceValue> {

    public static final String NAME = "FaultTolerance";

    private final FaultToleranceValue _value;

    public FaultTolerance(FaultToleranceValue value) {
        _value = value;
    }

    @Override public FaultToleranceValue getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return null; }
}
