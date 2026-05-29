package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.StaleBlockRate;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetricCalculator;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import java.util.List;
import java.util.stream.Collectors;

public class StaleBlockRateCalculator implements OutputMetricCalculator<StaleBlockRate> {
    private final int numberOfStaleBlocks;
    private final int numberOfConfirmedBlocks;

    public StaleBlockRateCalculator(int numberOfStaleBlocks, int numberOfConfirmedBlocks) {
        this.numberOfStaleBlocks = numberOfStaleBlocks;
        this.numberOfConfirmedBlocks = numberOfConfirmedBlocks;
    }

    @Override
    public StaleBlockRate calculate() {
        if (numberOfConfirmedBlocks <= 0) return new StaleBlockRate(0.0);
        return new StaleBlockRate((double) numberOfStaleBlocks / numberOfConfirmedBlocks);
    }

    public static AverageOutputMetric calculateAverage(List<StaleBlockRate> measurements) {
        var result = AverageCalculator.calculate(measurements.stream().map(m -> m.getValue()).collect(Collectors.toList()));
        return new AverageOutputMetricImpl(StaleBlockRate.NAME, result.getAverage(), StaleBlockRate.UNIT, result.getStandardDeviation(), result.getCoefficientOfVariation());
    }
}
