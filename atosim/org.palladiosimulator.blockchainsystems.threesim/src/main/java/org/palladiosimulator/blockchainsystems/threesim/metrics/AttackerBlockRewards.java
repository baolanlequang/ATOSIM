package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;
public class AttackerBlockRewards implements OutputMetric<Integer> {
    public static final String NAME = "Attacker Block Rewards";
    public static final String UNIT = "blocks";
    private final int _value;
    public AttackerBlockRewards(int value) { _value = value; }
    @Override public Integer getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
