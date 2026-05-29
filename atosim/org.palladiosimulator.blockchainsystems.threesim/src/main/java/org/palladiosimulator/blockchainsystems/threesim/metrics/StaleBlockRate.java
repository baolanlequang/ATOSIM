package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;
public class StaleBlockRate implements OutputMetric<Double> {
    public static final String NAME = "StaleBlockRate";
    public static final String UNIT = "ratio";
    private final double _value;
    public StaleBlockRate(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
