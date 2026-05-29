package org.palladiosimulator.blockchainsystems.threesim.metrics;

public class ConfirmationLatencyDeltaValue {
    public static final String UNIT = "ms";

    private final double value;
    private final String unit;

    private ConfirmationLatencyDeltaValue(double value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public static ConfirmationLatencyDeltaValue of(double value) {
        return new ConfirmationLatencyDeltaValue(value, UNIT);
    }

    public double getValue() { return value; }
    public String getUnit() { return unit; }
}
