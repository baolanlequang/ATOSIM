package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;

/** Item 7: mean transaction count across every block mined this round. */
public class BlockTransactionCount implements OutputMetric<Double> {
    public static final String NAME = "Mean Block Transaction Count";
    public static final String UNIT = "transactions";
    private final double _value;
    public BlockTransactionCount(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
