package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.Reliability;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetricCalculator;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import java.util.List;
import java.util.stream.Collectors;

public class ReliabilityCalculator implements OutputMetricCalculator<Reliability> {
    private final double timespan;
    private final double meanTimeBetweenFailures;

    public ReliabilityCalculator(double timespan, double meanTimeBetweenFailures) {
        this.timespan = timespan;
        this.meanTimeBetweenFailures = meanTimeBetweenFailures;
    }

    @Override
    public Reliability calculate() {
        if (meanTimeBetweenFailures == -1.0) return new Reliability(0.0);
        if (meanTimeBetweenFailures == 0.0)
            throw new IllegalStateException("Mean time between failures must not be zero when calculating reliability.");
        double timespanInMs = timespan * 3_600_000.0;
        return new Reliability(Math.exp(-1.0 * timespanInMs / meanTimeBetweenFailures));
    }

    public static AverageOutputMetric calculateAverage(List<Reliability> measurements) {
        var result = AverageCalculator.calculate(measurements.stream().map(m -> m.getValue()).collect(Collectors.toList()));
        return new AverageOutputMetricImpl(Reliability.NAME, result.getAverage(), Reliability.UNIT, result.getStandardDeviation(), result.getCoefficientOfVariation());
    }
}
