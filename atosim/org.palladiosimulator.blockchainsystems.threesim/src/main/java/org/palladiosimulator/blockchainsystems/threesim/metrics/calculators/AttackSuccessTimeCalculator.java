package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.AttackSuccessTime;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import java.util.List;
import java.util.stream.Collectors;

public class AttackSuccessTimeCalculator {
    private final Long successTime;

    public AttackSuccessTimeCalculator(Long successTime) { this.successTime = successTime; }

    public AttackSuccessTime calculate() {
        return new AttackSuccessTime(successTime != null ? successTime.doubleValue() : -1.0);
    }

    public static AverageOutputMetric calculateAverage(List<AttackSuccessTime> values) {
        List<AttackSuccessTime> valid = values.stream().filter(v -> v.getValue() >= 0).collect(Collectors.toList());
        if (valid.isEmpty()) return new AverageOutputMetricImpl(AttackSuccessTime.NAME, -1.0, AttackSuccessTime.UNIT, 0.0, null);
        double avg = valid.stream().mapToDouble(AttackSuccessTime::getValue).average().orElse(0.0);
        double variance = valid.stream().mapToDouble(v -> (v.getValue() - avg) * (v.getValue() - avg)).average().orElse(0.0);
        double std = Math.sqrt(variance);
        Double cv = avg == 0.0 ? null : std / avg;
        return new AverageOutputMetricImpl(AttackSuccessTime.NAME, avg, AttackSuccessTime.UNIT, std, cv);
    }
}
