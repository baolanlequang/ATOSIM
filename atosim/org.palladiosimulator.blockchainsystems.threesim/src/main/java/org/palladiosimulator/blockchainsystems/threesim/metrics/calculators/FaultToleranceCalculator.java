package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.FaultTolerance;
import org.palladiosimulator.blockchainsystems.threesim.metrics.FaultToleranceAverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.FaultToleranceValue;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetricCalculator;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import java.util.List;
import java.util.stream.Collectors;

public class FaultToleranceCalculator implements OutputMetricCalculator<FaultTolerance> {
    private final double averageThroughputWithoutFailures;
    private final double averageThroughputWithFailures;
    private final double averageConfirmationLatencyWithoutFailures;
    private final double averageConfirmationLatencyWithFailures;

    public FaultToleranceCalculator(double averageThroughputWithoutFailures, double averageThroughputWithFailures,
            double averageConfirmationLatencyWithoutFailures, double averageConfirmationLatencyWithFailures) {
        this.averageThroughputWithoutFailures = averageThroughputWithoutFailures;
        this.averageThroughputWithFailures = averageThroughputWithFailures;
        this.averageConfirmationLatencyWithoutFailures = averageConfirmationLatencyWithoutFailures;
        this.averageConfirmationLatencyWithFailures = averageConfirmationLatencyWithFailures;
    }

    @Override
    public FaultTolerance calculate() {
        if (averageThroughputWithFailures == -1.0 || averageConfirmationLatencyWithFailures == -1.0
                || averageConfirmationLatencyWithoutFailures == -1.0 || averageThroughputWithoutFailures == -1.0)
            return new FaultTolerance(FaultToleranceValue.of(0.0, 0.0));
        if (averageThroughputWithoutFailures < 0 || averageThroughputWithFailures < 0
                || averageConfirmationLatencyWithoutFailures < 0 || averageConfirmationLatencyWithFailures < 0)
            throw new IllegalStateException("Average throughput and average confirmation latency must not be negative when calculating fault tolerance.");
        double td = Math.abs(averageThroughputWithoutFailures - averageThroughputWithFailures);
        double cd = Math.abs(averageConfirmationLatencyWithoutFailures - averageConfirmationLatencyWithFailures);
        return new FaultTolerance(FaultToleranceValue.of(td, cd));
    }

    public static FaultToleranceAverageOutputMetric calculateAverage(List<FaultTolerance> measurements) {
        return FaultToleranceAverageOutputMetric.of(
            AverageCalculator.calculate(measurements.stream().map(m -> m.getValue().getThroughputDelta().getValue()).collect(Collectors.toList())),
            AverageCalculator.calculate(measurements.stream().map(m -> m.getValue().getConfirmationLatencyDelta().getValue()).collect(Collectors.toList()))
        );
    }
}
