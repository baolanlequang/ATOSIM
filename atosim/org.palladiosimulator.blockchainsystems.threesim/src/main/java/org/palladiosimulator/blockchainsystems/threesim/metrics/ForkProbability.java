package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;
public class ForkProbability implements OutputMetric<Double> {
    public static final String NAME = "ForkProbability";
    public static final String UNIT = null;
    private final double _value;
    public ForkProbability(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
