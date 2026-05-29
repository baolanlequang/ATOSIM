package org.palladiosimulator.blockchainsystems.threesim.metrics;

import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;

public class FaultToleranceAverageOutputMetricItem {

    private final AverageOutputMetricImpl throughputDelta;
    private final AverageOutputMetricImpl confirmationLatencyDelta;

    public FaultToleranceAverageOutputMetricItem(AverageOutputMetricImpl throughputDelta,
            AverageOutputMetricImpl confirmationLatencyDelta) {
        this.throughputDelta = throughputDelta;
        this.confirmationLatencyDelta = confirmationLatencyDelta;
    }

    public AverageOutputMetricImpl getThroughputDelta() { return throughputDelta; }
    public AverageOutputMetricImpl getConfirmationLatencyDelta() { return confirmationLatencyDelta; }
}
