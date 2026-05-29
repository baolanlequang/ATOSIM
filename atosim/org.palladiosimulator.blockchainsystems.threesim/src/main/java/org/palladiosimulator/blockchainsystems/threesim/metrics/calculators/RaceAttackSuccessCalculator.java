package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.RaceAttackSuccess;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import java.util.List;

public class RaceAttackSuccessCalculator {
    private final boolean success;
    public RaceAttackSuccessCalculator(boolean success) { this.success = success; }
    public RaceAttackSuccess calculate() { return new RaceAttackSuccess(success); }

    public static AverageOutputMetric calculateAverage(List<RaceAttackSuccess> values) {
        double avg = values.stream().mapToDouble(v -> v.getValue()).average().orElse(0.0);
        double variance = values.stream().mapToDouble(v -> (v.getValue() - avg) * (v.getValue() - avg)).average().orElse(0.0);
        double std = Math.sqrt(variance);
        Double cv = avg == 0.0 ? null : std / avg;
        return new AverageOutputMetricImpl(RaceAttackSuccess.NAME, avg, null, std, cv);
    }
}
