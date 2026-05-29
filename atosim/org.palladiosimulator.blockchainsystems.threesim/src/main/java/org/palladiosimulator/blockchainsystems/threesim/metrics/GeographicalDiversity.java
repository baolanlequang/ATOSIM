package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;
public class GeographicalDiversity implements OutputMetric<Double> {
    public static final String NAME = "GeographicalDiversity";
    public static final String UNIT = "%";
    private final double _value;
    public GeographicalDiversity(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
