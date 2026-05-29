package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;
public class NakamotoCoefficient implements OutputMetric<Integer> {
    public static final String NAME = "NakamotoCoefficient";
    public static final String UNIT = "validating nodes";
    private final int _value;
    public NakamotoCoefficient(int value) { _value = value; }
    @Override public Integer getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
