package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;

/** Colleague review item 6: median of the same per-contest fork durations as ForkDurationMean --
 * see that class's doc. -1.0 if no reorg resolved this round. */
public class ForkDurationMedian implements OutputMetric<Double> {
    public static final String NAME = "Fork Duration (Median)";
    public static final String UNIT = "ms";
    private final double _value;
    public ForkDurationMedian(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
