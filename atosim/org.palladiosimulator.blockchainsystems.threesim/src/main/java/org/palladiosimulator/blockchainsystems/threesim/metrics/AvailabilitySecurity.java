package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;
public class AvailabilitySecurity implements OutputMetric<Double> {
    public static final String NAME = "AvailabilitySecurity";
    public static final String UNIT = "%";
    private final double _value;
    public AvailabilitySecurity(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
