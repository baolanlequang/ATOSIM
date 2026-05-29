package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.NakamotoCoefficient;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetricCalculator;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NakamotoCoefficientCalculator implements OutputMetricCalculator<NakamotoCoefficient> {
    private final Collection<Double> hashingPowerPerNode;
    private final double threshold;

    public NakamotoCoefficientCalculator(Collection<Double> hashingPowerPerNode, double threshold) {
        if (threshold < 0.0 || threshold > 100.0) throw new IllegalArgumentException("Threshold must be in the range 0.0 to 100.0");
        this.hashingPowerPerNode = hashingPowerPerNode;
        this.threshold = threshold;
    }

    @Override
    public NakamotoCoefficient calculate() {
        double total = hashingPowerPerNode.stream().mapToDouble(Double::doubleValue).sum();
        if (total == 0.0) return new NakamotoCoefficient(0);
        List<Double> sorted = hashingPowerPerNode.stream().sorted((a, b) -> Double.compare(b, a)).collect(Collectors.toList());
        double cumulative = 0;
        for (int i = 0; i < sorted.size(); i++) {
            cumulative += sorted.get(i);
            if (cumulative / total >= threshold / 100.0) return new NakamotoCoefficient(i + 1);
        }
        return new NakamotoCoefficient(sorted.size());
    }

    public static AverageOutputMetric calculateAverage(List<NakamotoCoefficient> measurements) {
        var result = AverageCalculator.calculate(measurements.stream().map(m -> (double) m.getValue()).collect(Collectors.toList()));
        return new AverageOutputMetricImpl(NakamotoCoefficient.NAME, Math.round(result.getAverage()), NakamotoCoefficient.UNIT, result.getStandardDeviation(), result.getCoefficientOfVariation());
    }
}
