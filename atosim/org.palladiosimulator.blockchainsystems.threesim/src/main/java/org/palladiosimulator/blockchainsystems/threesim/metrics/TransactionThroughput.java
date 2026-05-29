package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;
public class TransactionThroughput implements OutputMetric<Double> {
    public static final String NAME = "TransactionThroughput";
    public static final String UNIT = "transactions/min";
    private final double _value;
    public TransactionThroughput(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
