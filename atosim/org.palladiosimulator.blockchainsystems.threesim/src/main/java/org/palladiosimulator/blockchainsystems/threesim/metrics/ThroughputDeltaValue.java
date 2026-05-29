package org.palladiosimulator.blockchainsystems.threesim.metrics;

public class ThroughputDeltaValue {
    public static final String UNIT = "transactions/min";

    private final double value;
    private final String unit;

    private ThroughputDeltaValue(double value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public static ThroughputDeltaValue of(double value) {
        return new ThroughputDeltaValue(value, UNIT);
    }

    public double getValue() { return value; }
    public String getUnit() { return unit; }
}
