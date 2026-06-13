package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;
public class TotalBlockRewards implements OutputMetric<Integer> {
    public static final String NAME = "Total Block Rewards";
    public static final String UNIT = "blocks";
    private final int _value;
    public TotalBlockRewards(int value) { _value = value; }
    @Override public Integer getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
