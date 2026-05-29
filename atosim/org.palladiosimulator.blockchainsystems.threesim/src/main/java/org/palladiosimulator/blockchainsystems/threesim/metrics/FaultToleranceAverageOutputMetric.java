package org.palladiosimulator.blockchainsystems.threesim.metrics;

import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculatorResult;

public class FaultToleranceAverageOutputMetric implements AverageOutputMetric {

    private final String name;
    private final FaultToleranceAverageOutputMetricItem average;

    private FaultToleranceAverageOutputMetric(String name, FaultToleranceAverageOutputMetricItem average) {
        this.name = name;
        this.average = average;
    }

    public static FaultToleranceAverageOutputMetric of(AverageCalculatorResult throughputDelta,
            AverageCalculatorResult confirmationLatencyDelta) {
        return new FaultToleranceAverageOutputMetric(
                FaultTolerance.NAME,
                new FaultToleranceAverageOutputMetricItem(
                        new AverageOutputMetricImpl("throughputDelta", throughputDelta.getAverage(),
                                ThroughputDeltaValue.UNIT, throughputDelta.getStandardDeviation(),
                                throughputDelta.getCoefficientOfVariation()),
                        new AverageOutputMetricImpl("confirmationLatencyDelta",
                                confirmationLatencyDelta.getAverage(), ConfirmationLatencyDeltaValue.UNIT,
                                confirmationLatencyDelta.getStandardDeviation(),
                                confirmationLatencyDelta.getCoefficientOfVariation())
                )
        );
    }

    public String getName() { return name; }
    public FaultToleranceAverageOutputMetricItem getAverage() { return average; }
}
