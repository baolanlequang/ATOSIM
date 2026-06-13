package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.AttackerBlockRewards;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import java.util.List;
import java.util.stream.Collectors;

public class AttackerBlockRewardsCalculator {
    private final int value;
    public AttackerBlockRewardsCalculator(int value) { this.value = value; }
    public AttackerBlockRewards calculate() { return new AttackerBlockRewards(value); }

    public static AverageOutputMetric calculateAverage(List<AttackerBlockRewards> measurements) {
        var result = AverageCalculator.calculate(measurements.stream().map(m -> (double) m.getValue()).collect(Collectors.toList()));
        return new AverageOutputMetricImpl(AttackerBlockRewards.NAME, result.getAverage(), AttackerBlockRewards.UNIT, result.getStandardDeviation(), result.getCoefficientOfVariation());
    }
}
