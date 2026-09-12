package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;

/** Colleague review item 6: median of the same per-block full-network-coverage propagation
 * times as PropagationTimeMean -- see that class's doc. -1.0 if none completed this round. */
public class PropagationTimeMedian implements OutputMetric<Double> {
    public static final String NAME = "Propagation Time (Median)";
    public static final String UNIT = "ms";
    private final double _value;
    public PropagationTimeMedian(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
