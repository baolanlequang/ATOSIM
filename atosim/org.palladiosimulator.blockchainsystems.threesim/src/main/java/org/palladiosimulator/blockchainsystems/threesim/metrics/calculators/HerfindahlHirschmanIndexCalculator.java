package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.HerfindahlHirschmanIndex;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetricCalculator;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class HerfindahlHirschmanIndexCalculator implements OutputMetricCalculator<HerfindahlHirschmanIndex> {
    private final Collection<Double> tokensHeldPerNode;

    public HerfindahlHirschmanIndexCalculator(Collection<Double> tokensHeldPerNode) {
        this.tokensHeldPerNode = tokensHeldPerNode;
    }

    @Override
    public HerfindahlHirschmanIndex calculate() {
        double n = tokensHeldPerNode.size();
        double total = tokensHeldPerNode.stream().mapToDouble(Double::doubleValue).sum();
        if (n == 0 || total == 0.0) return new HerfindahlHirschmanIndex(0.0);
        double hhi = tokensHeldPerNode.stream().mapToDouble(v -> Math.pow(v / total, 2)).sum();
        double hhiNorm = (hhi - 1.0 / n) / (1.0 - 1.0 / n);
        return new HerfindahlHirschmanIndex(hhiNorm);
    }

    public static AverageOutputMetric calculateAverage(List<HerfindahlHirschmanIndex> measurements) {
        var result = AverageCalculator.calculate(measurements.stream().map(m -> m.getValue()).collect(Collectors.toList()));
        return new AverageOutputMetricImpl(HerfindahlHirschmanIndex.NAME, result.getAverage(), null, result.getStandardDeviation(), result.getCoefficientOfVariation());
    }
}
