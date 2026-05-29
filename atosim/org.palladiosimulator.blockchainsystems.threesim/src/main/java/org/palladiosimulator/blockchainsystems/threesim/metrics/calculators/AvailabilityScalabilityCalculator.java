package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.AvailabilityScalability;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetricCalculator;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import java.util.List;
import java.util.stream.Collectors;

public class AvailabilityScalabilityCalculator implements OutputMetricCalculator<AvailabilityScalability> {
    private final int numberOfConfirmedTransactions;
    private final int numberOfTransactions;

    public AvailabilityScalabilityCalculator(int numberOfConfirmedTransactions, int numberOfTransactions) {
        this.numberOfConfirmedTransactions = numberOfConfirmedTransactions;
        this.numberOfTransactions = numberOfTransactions;
    }

    @Override
    public AvailabilityScalability calculate() {
        if (numberOfTransactions <= 0) return new AvailabilityScalability(0.0);
        return new AvailabilityScalability((double) numberOfConfirmedTransactions / numberOfTransactions);
    }

    public static AverageOutputMetric calculateAverage(List<AvailabilityScalability> measurements) {
        var result = AverageCalculator.calculate(measurements.stream().map(m -> m.getValue()).collect(Collectors.toList()));
        return new AverageOutputMetricImpl(AvailabilityScalability.NAME, result.getAverage(), AvailabilityScalability.UNIT, result.getStandardDeviation(), result.getCoefficientOfVariation());
    }
}
