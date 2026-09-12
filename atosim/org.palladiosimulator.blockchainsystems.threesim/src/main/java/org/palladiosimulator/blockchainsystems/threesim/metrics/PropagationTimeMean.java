package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;

/** Colleague review item 6: mean, across every block mined this round that reached every node
 * before the round ended, of (last node's first appearance of that block - block's mined time).
 * -1.0 if no block completed full-network coverage this round (see ThreesimSimulationMonitor's
 * propagation-time tracking doc for why incomplete blocks are excluded, not zero-filled). */
public class PropagationTimeMean implements OutputMetric<Double> {
    public static final String NAME = "Propagation Time (Mean)";
    public static final String UNIT = "ms";
    private final double _value;
    public PropagationTimeMean(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
