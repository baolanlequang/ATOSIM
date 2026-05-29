package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.AttackerRevenueShare;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import java.util.List;
import java.util.stream.Collectors;

public class AttackerRevenueShareCalculator {
    private final double attackerRewards;
    private final double totalRewards;

    public AttackerRevenueShareCalculator(double attackerRewards, double totalRewards) {
        this.attackerRewards = attackerRewards;
        this.totalRewards = totalRewards;
    }

    public AttackerRevenueShare calculate() {
        double value = totalRewards == 0.0 ? 0.0 : (attackerRewards / totalRewards) * 100.0;
        return new AttackerRevenueShare(value);
    }

    public static AverageOutputMetric calculateAverage(List<AttackerRevenueShare> values) {
        double avg = values.stream().mapToDouble(AttackerRevenueShare::getValue).average().orElse(0.0);
        double variance = values.stream().mapToDouble(v -> (v.getValue() - avg) * (v.getValue() - avg)).average().orElse(0.0);
        double std = Math.sqrt(variance);
        Double cv = avg == 0.0 ? null : std / avg;
        return new AverageOutputMetricImpl(AttackerRevenueShare.NAME, avg, AttackerRevenueShare.UNIT, std, cv);
    }
}
