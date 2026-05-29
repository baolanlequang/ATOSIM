package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.ShannonEntropy;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetricCalculator;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ShannonEntropyCalculator implements OutputMetricCalculator<ShannonEntropy> {
    private final double k;
    private final Collection<Integer> blocksProposedPerNode;

    public ShannonEntropyCalculator(double k, Collection<Integer> blocksProposedPerNode) {
        this.k = k;
        this.blocksProposedPerNode = blocksProposedPerNode;
    }

    @Override
    public ShannonEntropy calculate() {
        int total = blocksProposedPerNode.stream().mapToInt(Integer::intValue).sum();
        if (total == 0) return new ShannonEntropy(0.0);
        double sum = 0;
        for (int count : blocksProposedPerNode) {
            if (count == 0) continue;
            double b = (double) count / total;
            sum += b * (Math.log(b) / Math.log(2.0));
        }
        return new ShannonEntropy(-1 * k * sum);
    }

    public static AverageOutputMetric calculateAverage(List<ShannonEntropy> measurements) {
        var result = AverageCalculator.calculate(measurements.stream().map(m -> m.getValue()).collect(Collectors.toList()));
        return new AverageOutputMetricImpl(ShannonEntropy.NAME, result.getAverage(), ShannonEntropy.UNIT, result.getStandardDeviation(), result.getCoefficientOfVariation());
    }
}
