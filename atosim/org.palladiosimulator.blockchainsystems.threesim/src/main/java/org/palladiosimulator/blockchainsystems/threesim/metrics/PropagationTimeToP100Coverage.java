package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;

/** Mean, across every block mined this round that reached 100% of nodes before the round ended,
 * of (time full coverage was reached - block's mined time). Numerically identical to
 * PropagationTimeMean -- kept alongside PropagationTimeToP90Coverage/P95Coverage under a
 * consistent naming scheme so all three coverage thresholds are directly comparable side by
 * side, not because this differs from the pre-existing metric. -1.0 if no block reached 100%
 * coverage this round (kept alongside on purpose -- see the colleague's own request that 100%
 * remain available, not be replaced). */
public class PropagationTimeToP100Coverage implements OutputMetric<Double> {
    public static final String NAME = "Propagation Time (Mean, to 100% Coverage)";
    public static final String UNIT = "ms";
    private final double _value;
    public PropagationTimeToP100Coverage(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
