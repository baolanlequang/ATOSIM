package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.TotalBlockRewards;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import java.util.List;
import java.util.stream.Collectors;

public class TotalBlockRewardsCalculator {
    private final int value;
    public TotalBlockRewardsCalculator(int value) { this.value = value; }
    public TotalBlockRewards calculate() { return new TotalBlockRewards(value); }

    public static AverageOutputMetric calculateAverage(List<TotalBlockRewards> measurements) {
        var result = AverageCalculator.calculate(measurements.stream().map(m -> (double) m.getValue()).collect(Collectors.toList()));
        return new AverageOutputMetricImpl(TotalBlockRewards.NAME, result.getAverage(), TotalBlockRewards.UNIT, result.getStandardDeviation(), result.getCoefficientOfVariation());
    }
}
