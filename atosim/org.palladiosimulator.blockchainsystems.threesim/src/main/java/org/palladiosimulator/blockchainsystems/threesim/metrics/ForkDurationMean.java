package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;

/** Colleague review item 6: mean, across every distinct resolved contest this round, of (the
 * reorg's own occurrence time - the mined time of the first block of its replacing branch) -- see
 * ThreesimSimulationMonitor.getForkDurations()'s doc for why this is "distinct contest", not
 * "every node-local reorg observation". -1.0 if no reorg resolved this round. */
public class ForkDurationMean implements OutputMetric<Double> {
    public static final String NAME = "Fork Duration (Mean)";
    public static final String UNIT = "ms";
    private final double _value;
    public ForkDurationMean(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
