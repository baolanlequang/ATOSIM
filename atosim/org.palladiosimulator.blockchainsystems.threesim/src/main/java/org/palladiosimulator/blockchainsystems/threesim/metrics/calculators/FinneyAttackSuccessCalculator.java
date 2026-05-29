package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.FinneyAttackSuccess;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import java.util.List;

public class FinneyAttackSuccessCalculator {
    private final boolean success;
    public FinneyAttackSuccessCalculator(boolean success) { this.success = success; }
    public FinneyAttackSuccess calculate() { return new FinneyAttackSuccess(success); }

    public static AverageOutputMetric calculateAverage(List<FinneyAttackSuccess> values) {
        double avg = values.stream().mapToDouble(v -> v.getValue()).average().orElse(0.0);
        double variance = values.stream().mapToDouble(v -> (v.getValue() - avg) * (v.getValue() - avg)).average().orElse(0.0);
        double std = Math.sqrt(variance);
        Double cv = avg == 0.0 ? null : std / avg;
        return new AverageOutputMetricImpl(FinneyAttackSuccess.NAME, avg, null, std, cv);
    }
}
