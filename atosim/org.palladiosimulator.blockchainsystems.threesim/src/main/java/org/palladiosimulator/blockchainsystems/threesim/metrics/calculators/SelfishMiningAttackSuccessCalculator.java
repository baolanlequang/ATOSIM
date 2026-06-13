package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.SelfishMiningAttackSuccess;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import java.util.List;

public class SelfishMiningAttackSuccessCalculator {
    private final boolean success;
    public SelfishMiningAttackSuccessCalculator(boolean success) { this.success = success; }
    public SelfishMiningAttackSuccess calculate() { return new SelfishMiningAttackSuccess(success); }

    public static AverageOutputMetric calculateAverage(List<SelfishMiningAttackSuccess> values) {
        long roundsWon = values.stream().filter(v -> v.getValue() == 1).count();
        long roundsSystemWon = values.size() - roundsWon;
        double avg = roundsSystemWon == 0
                ? (roundsWon == 0 ? 0.0 : Double.POSITIVE_INFINITY)
                : (double) roundsWon / roundsSystemWon;
        double variance = values.stream().mapToDouble(v -> (v.getValue() - avg) * (v.getValue() - avg)).average().orElse(0.0);
        double std = Math.sqrt(variance);
        Double cv = avg == 0.0 ? null : std / avg;
        return new AverageOutputMetricImpl(SelfishMiningAttackSuccess.NAME, avg, null, std, cv);
    }
}
