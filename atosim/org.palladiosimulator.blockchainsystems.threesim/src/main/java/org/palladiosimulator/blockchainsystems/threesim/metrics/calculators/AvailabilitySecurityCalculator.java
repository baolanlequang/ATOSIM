package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.AvailabilitySecurity;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetricCalculator;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import java.util.List;
import java.util.stream.Collectors;

public class AvailabilitySecurityCalculator implements OutputMetricCalculator<AvailabilitySecurity> {
    private final double meanTimeBetweenFailures;
    private final double meanTimeToRepair;

    public AvailabilitySecurityCalculator(double meanTimeBetweenFailures, double meanTimeToRepair) {
        this.meanTimeBetweenFailures = meanTimeBetweenFailures;
        this.meanTimeToRepair = meanTimeToRepair;
    }

    @Override
    public AvailabilitySecurity calculate() {
        if (meanTimeBetweenFailures == 0.0 || meanTimeToRepair == 0.0)
            throw new IllegalStateException("Mean time between failures and mean time to repair must not be zero when calculating AvailabilitySecurity.");
        if (meanTimeBetweenFailures == -1.0 || meanTimeToRepair == -1.0)
            return new AvailabilitySecurity(1.0);
        return new AvailabilitySecurity(meanTimeBetweenFailures / (meanTimeBetweenFailures + meanTimeToRepair));
    }

    public static AverageOutputMetric calculateAverage(List<AvailabilitySecurity> measurements) {
        var result = AverageCalculator.calculate(measurements.stream().map(m -> m.getValue()).collect(Collectors.toList()));
        return new AverageOutputMetricImpl(AvailabilitySecurity.NAME, result.getAverage(), AvailabilitySecurity.UNIT, result.getStandardDeviation(), result.getCoefficientOfVariation());
    }
}
