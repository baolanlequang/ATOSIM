package org.palladiosimulator.blockchainsystems.threesim.serialization;

import org.palladiosimulator.blockchainsystems.core.simulation.MonteCarloSimulationParameters;
import org.palladiosimulator.blockchainsystems.core.simulation.SingleSimulationParameters;
import org.palladiosimulator.blockchainsystems.threesim.metrics.FaultTolerance;
import org.palladiosimulator.blockchainsystems.threesim.metrics.FaultToleranceAverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.OutputMetricsSet;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimAverageSimulationRoundResult;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimMonteCarloSimulationResult;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimSimulationRoundResult;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimSingleSimulationResult;

import java.util.List;

/**
 * Hand-written JSON serializer replacing kotlinx.serialization for 3SIM simulation results.
 */
public class ThreesimJsonSerializer {

    public static String toJson(ThreesimSingleSimulationResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        appendSimulationParameters(sb, result.getSimulationParameters(), "  ");
        sb.append(",\n");
        appendThreesimParameters(sb, result.getThreesimSimulationParameters(), "  ");
        sb.append(",\n");
        sb.append("  \"simulationRoundResult\": ");
        appendMetricsArray(sb, result.getSimulationRoundResult().getOutputMetrics(), "  ");
        sb.append("\n}");
        return sb.toString();
    }

    public static String toJson(ThreesimMonteCarloSimulationResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        appendSimulationParameters(sb, result.getSimulationParameters(), "  ");
        sb.append(",\n");
        appendThreesimParameters(sb, result.getThreesimSimulationParameters(), "  ");
        sb.append(",\n");
        sb.append("  \"generalResults\": ");
        appendMetricsArray(sb, result.getGeneralResults().getOutputMetrics(), "  ");
        sb.append(",\n");
        sb.append("  \"simulationRoundResults\": [\n");
        List<ThreesimSimulationRoundResult> rounds = result.getSimulationRoundResults();
        for (int i = 0; i < rounds.size(); i++) {
            sb.append("    ");
            appendMetricsArray(sb, rounds.get(i).getOutputMetrics(), "    ");
            if (i < rounds.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ],\n");
        sb.append("  \"averageSimulationRoundResult\": ");
        appendAveragesArray(sb, result.getAverageSimulationRoundResult(), "  ");
        sb.append("\n}");
        return sb.toString();
    }

    private static void appendSimulationParameters(StringBuilder sb, Object params, String indent) {
        if (params instanceof MonteCarloSimulationParameters p) {
            sb.append(indent).append("\"simulationParameters\": {\n");
            sb.append(indent).append("  \"maxAllowedBlockchainLength\": ").append(p.getMaxAllowedBlockchainLength()).append(",\n");
            sb.append(indent).append("  \"numberOfMonteCarloRounds\": ").append(p.getNumberOfMonteCarloRounds()).append(",\n");
            sb.append(indent).append("  \"blockchainSystemModelFilePath\": ").append(jsonStr(p.getBlockchainSystemModelFilePath())).append("\n");
            sb.append(indent).append("}");
        } else if (params instanceof SingleSimulationParameters p) {
            sb.append(indent).append("\"simulationParameters\": {\n");
            sb.append(indent).append("  \"maxAllowedBlockchainLength\": ").append(p.getMaxAllowedBlockchainLength()).append(",\n");
            sb.append(indent).append("  \"blockchainSystemModelFilePath\": ").append(jsonStr(p.getBlockchainSystemModelFilePath())).append("\n");
            sb.append(indent).append("}");
        } else {
            sb.append(indent).append("\"simulationParameters\": {}");
        }
    }

    private static void appendThreesimParameters(StringBuilder sb, ThreesimSimulationParameters p, String indent) {
        sb.append(indent).append("\"threesimSimulationParameters\": {\n");
        sb.append(indent).append("  \"failureThroughputThreshold\": ").append(p.getFailureThroughputThreshold()).append(",\n");
        sb.append(indent).append("  \"shannonEntropyK\": ").append(p.getShannonEntropyK()).append(",\n");
        sb.append(indent).append("  \"nakamotoCoefficientThreshold\": ").append(p.getNakamotoCoefficientThreshold()).append(",\n");
        sb.append(indent).append("  \"reliabilityObservationTimespan\": ").append(p.getReliabilityObservationTimespan()).append(",\n");
        sb.append(indent).append("  \"attackType\": ").append(jsonStr(p.getAttackType().name())).append(",\n");
        sb.append(indent).append("  \"attackerHashPower\": ").append(p.getAttackerHashPower()).append(",\n");
        sb.append(indent).append("  \"gamma\": ").append(p.getGamma()).append(",\n");
        sb.append(indent).append("  \"deltaA\": ").append(p.getDeltaA()).append(",\n");
        sb.append(indent).append("  \"deltaB\": ").append(p.getDeltaB()).append(",\n");
        sb.append(indent).append("  \"confirmationDepth\": ").append(p.getConfirmationDepth()).append(",\n");
        sb.append(indent).append("  \"blockInterval\": ").append(p.getBlockInterval()).append(",\n");
        sb.append(indent).append("  \"propagationDelay\": ").append(p.getPropagationDelay()).append(",\n");
        sb.append(indent).append("  \"nodeDegree\": ").append(p.getNodeDegree()).append(",\n");
        sb.append(indent).append("  \"maxBlockSize\": ").append(p.getMaxBlockSize()).append(",\n");
        sb.append(indent).append("  \"networkBandwidth\": ").append(p.getNetworkBandwidth()).append("\n");
        sb.append(indent).append("}");
    }

    private static void appendMetricsArray(StringBuilder sb, OutputMetricsSet metrics, String indent) {
        sb.append("[\n");
        Object[] arr = metrics.toArray();
        for (int i = 0; i < arr.length; i++) {
            OutputMetric<?> m = (OutputMetric<?>) arr[i];
            sb.append(indent).append("  ");
            appendMetric(sb, m);
            if (i < arr.length - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append(indent).append("]");
    }

    @SuppressWarnings("unchecked")
    private static void appendMetric(StringBuilder sb, OutputMetric<?> m) {
        sb.append("{\"name\": ").append(jsonStr(m.getName())).append(", \"value\": ");
        Object val = m.getValue();
        if (val instanceof Double d) {
            sb.append(d);
        } else if (val instanceof Integer i) {
            sb.append(i);
        } else if (m instanceof FaultTolerance ft) {
            var ftv = ft.getValue();
            sb.append("{\"throughputDelta\": {\"value\": ").append(ftv.getThroughputDelta().getValue())
              .append(", \"unit\": ").append(jsonStr(ftv.getThroughputDelta().getUnit()))
              .append("}, \"confirmationLatencyDelta\": {\"value\": ").append(ftv.getConfirmationLatencyDelta().getValue())
              .append(", \"unit\": ").append(jsonStr(ftv.getConfirmationLatencyDelta().getUnit()))
              .append("}}");
        } else {
            sb.append(jsonStr(val != null ? val.toString() : "null"));
        }
        if (m.getUnit() != null) sb.append(", \"unit\": ").append(jsonStr(m.getUnit()));
        sb.append("}");
    }

    private static void appendAveragesArray(StringBuilder sb, ThreesimAverageSimulationRoundResult avg, String indent) {
        sb.append("[\n");
        List<AverageOutputMetric> results = avg.getResults();
        for (int i = 0; i < results.size(); i++) {
            sb.append(indent).append("  ");
            appendAverage(sb, results.get(i));
            if (i < results.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append(indent).append("]");
    }

    private static void appendAverage(StringBuilder sb, AverageOutputMetric avg) {
        if (avg instanceof AverageOutputMetricImpl a) {
            sb.append("{\"name\": ").append(jsonStr(a.getName()))
              .append(", \"average\": ").append(a.getAverage());
            if (a.getUnit() != null) sb.append(", \"unit\": ").append(jsonStr(a.getUnit()));
            sb.append(", \"standardDeviation\": ").append(a.getStandardDeviation());
            if (a.getCoefficientOfVariation() != null)
                sb.append(", \"coefficientOfVariation\": ").append(a.getCoefficientOfVariation());
            sb.append("}");
        } else if (avg instanceof FaultToleranceAverageOutputMetric fa) {
            var item = fa.getAverage();
            sb.append("{\"name\": ").append(jsonStr(fa.getName())).append(", \"average\": {");
            sb.append("\"throughputDelta\": ");
            appendAvgImpl(sb, item.getThroughputDelta());
            sb.append(", \"confirmationLatencyDelta\": ");
            appendAvgImpl(sb, item.getConfirmationLatencyDelta());
            sb.append("}}");
        } else {
            sb.append("{}");
        }
    }

    private static void appendAvgImpl(StringBuilder sb, AverageOutputMetricImpl a) {
        sb.append("{\"name\": ").append(jsonStr(a.getName()))
          .append(", \"average\": ").append(a.getAverage());
        if (a.getUnit() != null) sb.append(", \"unit\": ").append(jsonStr(a.getUnit()));
        sb.append(", \"standardDeviation\": ").append(a.getStandardDeviation());
        if (a.getCoefficientOfVariation() != null)
            sb.append(", \"coefficientOfVariation\": ").append(a.getCoefficientOfVariation());
        sb.append("}");
    }

    private static String jsonStr(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}
