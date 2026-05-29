package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;
public class ShannonEntropy implements OutputMetric<Double> {
    public static final String NAME = "ShannonEntropy";
    public static final String UNIT = "bits";
    private final double _value;
    public ShannonEntropy(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
