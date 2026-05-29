package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.ForkProbability;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import java.util.List;
import java.util.stream.Collectors;

public class ForkProbabilityCalculator {
    private final double lambdaH;
    private final double delta;

    public ForkProbabilityCalculator(double lambdaH, double delta) {
        this.lambdaH = lambdaH;
        this.delta = delta;
    }

    public ForkProbability calculate() {
        return new ForkProbability(1 - Math.exp(-lambdaH * delta));
    }

    public static AverageOutputMetric calculateAverage(List<ForkProbability> values) {
        double avg = values.stream().mapToDouble(ForkProbability::getValue).average().orElse(0.0);
        double variance = values.stream().mapToDouble(v -> (v.getValue() - avg) * (v.getValue() - avg)).average().orElse(0.0);
        double std = Math.sqrt(variance);
        Double cv = avg == 0.0 ? null : std / avg;
        return new AverageOutputMetricImpl(ForkProbability.NAME, avg, ForkProbability.UNIT, std, cv);
    }
}
