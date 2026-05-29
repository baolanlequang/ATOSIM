package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.GiniCoefficient;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetricCalculator;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import java.util.List;
import java.util.stream.Collectors;

public class GiniCoefficientCalculator implements OutputMetricCalculator<GiniCoefficient> {
    private final List<Double> tokensHeldPerNode;

    public GiniCoefficientCalculator(List<Double> tokensHeldPerNode) {
        this.tokensHeldPerNode = tokensHeldPerNode;
    }

    @Override
    public GiniCoefficient calculate() {
        int n = tokensHeldPerNode.size();
        double total = tokensHeldPerNode.stream().mapToDouble(Double::doubleValue).sum();
        if (n == 0 || total == 0.0) return new GiniCoefficient(0.0);
        double sum = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                sum += Math.abs(tokensHeldPerNode.get(i) - tokensHeldPerNode.get(j));
        return new GiniCoefficient(sum / (2 * n * total));
    }

    public static AverageOutputMetric calculateAverage(List<GiniCoefficient> measurements) {
        var result = AverageCalculator.calculate(measurements.stream().map(m -> m.getValue()).collect(Collectors.toList()));
        return new AverageOutputMetricImpl(GiniCoefficient.NAME, result.getAverage(), null, result.getStandardDeviation(), result.getCoefficientOfVariation());
    }
}
