package org.palladiosimulator.blockchainsystems.threesim.simulation.results;

import org.palladiosimulator.blockchainsystems.threesim.metrics.*;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.calculators.*;

import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ThreesimAverageSimulationRoundResult {

    private final List<AverageOutputMetric> results;

    private ThreesimAverageSimulationRoundResult(List<AverageOutputMetric> results) {
        this.results = results;
    }

    public List<AverageOutputMetric> getResults() { return results; }

    @SuppressWarnings("unchecked")
    public static ThreesimAverageSimulationRoundResult fromSimulationRoundResults(
            List<ThreesimSimulationRoundResult> simulationRoundResults) {

        Map<String, List<OutputMetric<?>>> grouped = simulationRoundResults.stream()
                .flatMap(r -> r.getOutputMetrics().stream())
                .collect(Collectors.groupingBy(OutputMetric::getName));

        List<AverageOutputMetric> averages = new ArrayList<>();
        for (Map.Entry<String, List<OutputMetric<?>>> entry : grouped.entrySet()) {
            AverageOutputMetric avg = switch (entry.getKey()) {
                case AvailabilityScalability.NAME -> AvailabilityScalabilityCalculator.calculateAverage(
                        (List<AvailabilityScalability>) (List<?>) entry.getValue());
                case AvailabilitySecurity.NAME -> AvailabilitySecurityCalculator.calculateAverage(
                        (List<AvailabilitySecurity>) (List<?>) entry.getValue());
                case Consistency.NAME -> ConsistencyCalculator.calculateAverage(
                        (List<Consistency>) (List<?>) entry.getValue());
                case FaultTolerance.NAME -> FaultToleranceCalculator.calculateAverage(
                        (List<FaultTolerance>) (List<?>) entry.getValue());
                case GeographicalDiversity.NAME -> GeographicalDiversityCalculator.calculateAverage(
                        (List<GeographicalDiversity>) (List<?>) entry.getValue());
                case GiniCoefficient.NAME -> GiniCoefficientCalculator.calculateAverage(
                        (List<GiniCoefficient>) (List<?>) entry.getValue());
                case HerfindahlHirschmanIndex.NAME -> HerfindahlHirschmanIndexCalculator.calculateAverage(
                        (List<HerfindahlHirschmanIndex>) (List<?>) entry.getValue());
                case NakamotoCoefficient.NAME -> NakamotoCoefficientCalculator.calculateAverage(
                        (List<NakamotoCoefficient>) (List<?>) entry.getValue());
                case Reliability.NAME -> ReliabilityCalculator.calculateAverage(
                        (List<Reliability>) (List<?>) entry.getValue());
                case ShannonEntropy.NAME -> ShannonEntropyCalculator.calculateAverage(
                        (List<ShannonEntropy>) (List<?>) entry.getValue());
                case StaleBlockRate.NAME -> StaleBlockRateCalculator.calculateAverage(
                        (List<StaleBlockRate>) (List<?>) entry.getValue());
                case TransactionThroughput.NAME -> TransactionThroughputCalculator.calculateAverage(
                        (List<TransactionThroughput>) (List<?>) entry.getValue());
                case AttackerRevenueShare.NAME -> AttackerRevenueShareCalculator.calculateAverage(
                        (List<AttackerRevenueShare>) (List<?>) entry.getValue());
                case FinneyAttackSuccess.NAME -> FinneyAttackSuccessCalculator.calculateAverage(
                        (List<FinneyAttackSuccess>) (List<?>) entry.getValue());
                case RaceAttackSuccess.NAME -> RaceAttackSuccessCalculator.calculateAverage(
                        (List<RaceAttackSuccess>) (List<?>) entry.getValue());
                case ForkProbability.NAME -> ForkProbabilityCalculator.calculateAverage(
                        (List<ForkProbability>) (List<?>) entry.getValue());
                case DoubleSpendSuccessProbability.NAME -> DoubleSpendSuccessProbabilityCalculator.calculateAverage(
                        (List<DoubleSpendSuccessProbability>) (List<?>) entry.getValue());
                case AttackSuccessTime.NAME -> AttackSuccessTimeCalculator.calculateAverage(
                        (List<AttackSuccessTime>) (List<?>) entry.getValue());
                default -> null;
            };
            if (avg != null) averages.add(avg);
        }
        return new ThreesimAverageSimulationRoundResult(averages);
    }
}
