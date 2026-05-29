package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.TransactionThroughput;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetricCalculator;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionThroughputCalculator implements OutputMetricCalculator<TransactionThroughput> {
    private final int numberOfConfirmedTransactions;
    private final long observationTime;

    public TransactionThroughputCalculator(int numberOfConfirmedTransactions, long observationTime) {
        this.numberOfConfirmedTransactions = numberOfConfirmedTransactions;
        this.observationTime = observationTime;
    }

    @Override
    public TransactionThroughput calculate() {
        if (observationTime <= 0 || numberOfConfirmedTransactions <= 0) return new TransactionThroughput(0.0);
        return new TransactionThroughput(((double) numberOfConfirmedTransactions / observationTime) * 60 * 1000);
    }

    public static AverageOutputMetric calculateAverage(List<TransactionThroughput> measurements) {
        var result = AverageCalculator.calculate(measurements.stream().map(m -> m.getValue()).collect(Collectors.toList()));
        return new AverageOutputMetricImpl(TransactionThroughput.NAME, result.getAverage(), TransactionThroughput.UNIT, result.getStandardDeviation(), result.getCoefficientOfVariation());
    }
}
