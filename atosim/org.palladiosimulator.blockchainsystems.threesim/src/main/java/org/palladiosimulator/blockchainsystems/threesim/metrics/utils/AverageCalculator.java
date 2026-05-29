package org.palladiosimulator.blockchainsystems.threesim.metrics.utils;

import java.util.List;

public class AverageCalculator {

    public static final AverageCalculator INSTANCE = new AverageCalculator();

    public static AverageCalculatorResult calculate(List<Double> values) {
        if (values.isEmpty()) {
            return new AverageCalculatorResult(0.0, 0.0, null);
        }
        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = values.stream().mapToDouble(v -> (v - mean) * (v - mean)).average().orElse(0.0);
        double standardDeviation = Math.sqrt(variance);
        Double coefficientOfVariation = mean != 0.0 ? standardDeviation / mean : null;
        return new AverageCalculatorResult(mean, standardDeviation, coefficientOfVariation);
    }
}
