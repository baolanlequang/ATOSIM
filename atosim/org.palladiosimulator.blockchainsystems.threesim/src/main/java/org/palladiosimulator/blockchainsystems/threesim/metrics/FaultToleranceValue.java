package org.palladiosimulator.blockchainsystems.threesim.metrics;

public class FaultToleranceValue {

    private final ThroughputDeltaValue throughputDelta;
    private final ConfirmationLatencyDeltaValue confirmationLatencyDelta;

    private FaultToleranceValue(ThroughputDeltaValue throughputDelta,
            ConfirmationLatencyDeltaValue confirmationLatencyDelta) {
        this.throughputDelta = throughputDelta;
        this.confirmationLatencyDelta = confirmationLatencyDelta;
    }

    public static FaultToleranceValue of(double throughputDelta, double confirmationLatencyDelta) {
        return new FaultToleranceValue(
                ThroughputDeltaValue.of(throughputDelta),
                ConfirmationLatencyDeltaValue.of(confirmationLatencyDelta)
        );
    }

    public ThroughputDeltaValue getThroughputDelta() { return throughputDelta; }
    public ConfirmationLatencyDeltaValue getConfirmationLatencyDelta() { return confirmationLatencyDelta; }
}
