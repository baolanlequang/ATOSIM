package org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions;

import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculatorResult;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AverageOutputMetricCalculator<M extends OutputMetric<?>> {

    protected abstract double getValue(M metric);
    protected abstract AverageOutputMetric createResult(AverageCalculatorResult result);

    public AverageOutputMetric calculateAverage(List<M> measurements) {
        List<Double> values = measurements.stream()
                .map(this::getValue)
                .collect(Collectors.toList());
        AverageCalculatorResult result = AverageCalculator.calculate(values);
        return createResult(result);
    }
}
