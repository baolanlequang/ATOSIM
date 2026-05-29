package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.DoubleSpendSuccessProbability;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import java.util.List;

public class DoubleSpendSuccessProbabilityCalculator {
    private final double attackerHashPower;
    private final int confirmationDepth;

    public DoubleSpendSuccessProbabilityCalculator(double attackerHashPower, int confirmationDepth) {
        this.attackerHashPower = attackerHashPower;
        this.confirmationDepth = confirmationDepth;
    }

    public DoubleSpendSuccessProbability calculate() {
        double q = attackerHashPower;
        double p = 1 - q;
        if (q >= p) return new DoubleSpendSuccessProbability(1.0);
        double lambda = confirmationDepth * (q / p);
        double sum = 0;
        for (int k = 0; k <= confirmationDepth; k++) {
            double poisson = Math.exp(-lambda) * Math.pow(lambda, k) / factorial(k);
            double term = 1 - Math.pow(q / p, confirmationDepth - k);
            sum += poisson * term;
        }
        return new DoubleSpendSuccessProbability(1 - sum);
    }

    private double factorial(int n) {
        double r = 1.0;
        for (int i = 2; i <= n; i++) r *= i;
        return r;
    }

    public static AverageOutputMetric calculateAverage(List<DoubleSpendSuccessProbability> values) {
        double avg = values.stream().mapToDouble(DoubleSpendSuccessProbability::getValue).average().orElse(0.0);
        double variance = values.stream().mapToDouble(v -> (v.getValue() - avg) * (v.getValue() - avg)).average().orElse(0.0);
        double std = Math.sqrt(variance);
        Double cv = avg == 0.0 ? null : std / avg;
        return new AverageOutputMetricImpl(DoubleSpendSuccessProbability.NAME, avg, DoubleSpendSuccessProbability.UNIT, std, cv);
    }
}
