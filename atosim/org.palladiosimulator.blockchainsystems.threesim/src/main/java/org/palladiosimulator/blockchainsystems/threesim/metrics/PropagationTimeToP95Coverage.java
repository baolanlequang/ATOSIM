package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;

/** Mean, across every block mined this round that reached at least 95% of nodes before the
 * round ended, of (time that 95th-percentile-of-nodes coverage was first reached - block's mined
 * time). Distinct from PropagationTimeP95 (a percentile ACROSS blocks of the 100%-coverage time)
 * -- this is a per-block coverage-fraction threshold, not a distributional percentile. See
 * PropagationTimeToP90Coverage's doc for the full rationale. -1.0 if no block reached 95%
 * coverage this round. */
public class PropagationTimeToP95Coverage implements OutputMetric<Double> {
    public static final String NAME = "Propagation Time (Mean, to 95% Coverage)";
    public static final String UNIT = "ms";
    private final double _value;
    public PropagationTimeToP95Coverage(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
