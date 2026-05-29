package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;
public class HerfindahlHirschmanIndex implements OutputMetric<Double> {
    public static final String NAME = "HerfindahlHirschmanIndex";
    private final double _value;
    public HerfindahlHirschmanIndex(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return null; }
}
