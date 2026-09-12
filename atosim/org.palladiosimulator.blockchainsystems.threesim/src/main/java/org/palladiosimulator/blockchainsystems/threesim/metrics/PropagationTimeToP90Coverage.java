package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;

/** Mean, across every block mined this round that reached at least 90% of nodes before the
 * round ended, of (time that 90th-percentile-of-nodes coverage was first reached - block's mined
 * time). Distinct from PropagationTimeP95 (a percentile ACROSS blocks of the 100%-coverage time)
 * -- this is a per-block coverage-fraction threshold, not a distributional percentile. Added
 * because requiring literally 100% of nodes (PropagationTimeMean/Median/P95's basis) structurally
 * never completes on high-diameter, low-degree topologies within a round's time budget -- see
 * ThreesimSimulationMonitor's recordBlockPropagation doc. -1.0 if no block reached 90% coverage
 * this round. */
public class PropagationTimeToP90Coverage implements OutputMetric<Double> {
    public static final String NAME = "Propagation Time (Mean, to 90% Coverage)";
    public static final String UNIT = "ms";
    private final double _value;
    public PropagationTimeToP90Coverage(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
