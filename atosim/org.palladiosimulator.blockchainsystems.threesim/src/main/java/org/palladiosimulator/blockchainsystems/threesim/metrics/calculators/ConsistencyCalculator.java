package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.Consistency;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetricCalculator;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ConsistencyCalculator implements OutputMetricCalculator<Consistency> {
    private final Collection<long[]> blockProposalTimeAndConfirmationTimePerConfirmedBlock;

    public ConsistencyCalculator(Collection<long[]> blockProposalTimeAndConfirmationTimePerConfirmedBlock) {
        this.blockProposalTimeAndConfirmationTimePerConfirmedBlock = blockProposalTimeAndConfirmationTimePerConfirmedBlock;
    }

    @Override
    public Consistency calculate() {
        int n = blockProposalTimeAndConfirmationTimePerConfirmedBlock.size();
        if (n == 0) return new Consistency(0.0);
        double sum = 0;
        for (long[] pair : blockProposalTimeAndConfirmationTimePerConfirmedBlock) {
            sum += (pair[1] - pair[0]);
        }
        return new Consistency((1.0 / n) * sum);
    }

    public static AverageOutputMetric calculateAverage(List<Consistency> measurements) {
        var result = AverageCalculator.calculate(measurements.stream().map(m -> m.getValue()).collect(Collectors.toList()));
        return new AverageOutputMetricImpl(Consistency.NAME, result.getAverage(), Consistency.UNIT, result.getStandardDeviation(), result.getCoefficientOfVariation());
    }
}
