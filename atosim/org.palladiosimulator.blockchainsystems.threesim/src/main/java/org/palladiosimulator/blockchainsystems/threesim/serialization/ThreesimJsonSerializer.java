package org.palladiosimulator.blockchainsystems.threesim.serialization;

import org.palladiosimulator.blockchainsystems.core.blockchain.ChainReorganizedTraceEvent;
import org.palladiosimulator.blockchainsystems.core.simulation.MonteCarloSimulationParameters;
import org.palladiosimulator.blockchainsystems.core.simulation.SingleSimulationParameters;
import org.palladiosimulator.blockchainsystems.threesim.metrics.AvailabilityScalability;
import org.palladiosimulator.blockchainsystems.threesim.metrics.AvailabilitySecurity;
import org.palladiosimulator.blockchainsystems.threesim.metrics.FaultTolerance;
import org.palladiosimulator.blockchainsystems.threesim.metrics.FaultToleranceAverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.GeographicalDiversity;
import org.palladiosimulator.blockchainsystems.threesim.metrics.GiniCoefficient;
import org.palladiosimulator.blockchainsystems.threesim.metrics.HerfindahlHirschmanIndex;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.OutputMetricsSet;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ChainReorganizationOccurrence;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimAverageSimulationRoundResult;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimMonteCarloSimulationResult;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimSimulationRoundResult;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimSingleSimulationResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hand-written JSON serializer replacing kotlinx.serialization for 3SIM simulation results.
 */
public class ThreesimJsonSerializer {

    // Metrics computed by the simulation but excluded from the output JSON.
    private static final Set<String> EXCLUDED_METRIC_NAMES = Set.of(
            GeographicalDiversity.NAME, FaultTolerance.NAME, GiniCoefficient.NAME, HerfindahlHirschmanIndex.NAME,
            AvailabilityScalability.NAME, AvailabilitySecurity.NAME
    );

    public static String toJson(ThreesimSingleSimulationResult result, boolean includeChainReorganizations) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        appendSimulationParameters(sb, result.getSimulationParameters(), "  ");
        sb.append(",\n");
        appendThreesimParameters(sb, result.getThreesimSimulationParameters(), "  ");
        sb.append(",\n");
        sb.append("  \"simulationRoundResult\": ");
        appendMetricsArray(sb, result.getSimulationRoundResult().getOutputMetrics(), "  ");
        sb.append(",\n");
        sb.append("  \"chainReorganizationDepth\": ")
          .append(finalReorgDepth(result.getSimulationRoundResult().getChainReorganizations()));
        sb.append(",\n");
        sb.append("  \"attackerCausedChainReorganizationDepth\": ")
          .append(finalAttackerCausedReorgDepth(result.getSimulationRoundResult().getChainReorganizations()));
        if (includeChainReorganizations) {
            sb.append(",\n");
            sb.append("  \"chainReorganizations\": ");
            appendChainReorganizationsArray(sb, result.getSimulationRoundResult().getChainReorganizations(), "  ");
        }
        sb.append("\n}");
        return sb.toString();
    }

    public static String toJson(ThreesimMonteCarloSimulationResult result, boolean includeChainReorganizations) {
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
        sb.append("  \"chainReorganizationDepths\": [");
        for (int i = 0; i < rounds.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(finalReorgDepth(rounds.get(i).getChainReorganizations()));
        }
        sb.append("],\n");
        sb.append("  \"attackerCausedChainReorganizationDepths\": [");
        for (int i = 0; i < rounds.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(finalAttackerCausedReorgDepth(rounds.get(i).getChainReorganizations()));
        }
        sb.append("]");
        if (includeChainReorganizations) {
            sb.append(",\n");
            sb.append("  \"chainReorganizations\": [\n");
            for (int i = 0; i < rounds.size(); i++) {
                sb.append("    ");
                appendChainReorganizationsArray(sb, rounds.get(i).getChainReorganizations(), "    ");
                if (i < rounds.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ]");
        }
        sb.append(",\n");
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
        sb.append(indent).append("  \"nodeDegree\": ").append(p.getNodeDegree()).append(",\n");
        sb.append(indent).append("  \"maxBlockSize\": ").append(p.getMaxBlockSize()).append(",\n");
        sb.append(indent).append("  \"networkBandwidth\": ").append(p.getNetworkBandwidth()).append("\n");
        sb.append(indent).append("}");
    }

    // D_r for this round: the depth of the LAST occurrence observed before the round
    // terminated -- quiescence reached (0.5 x block_creation_interval with no further reorg,
    // see ThreesimSimulationMonitor.shouldTerminate()), or the cap-without-quiescence/no-reorg
    // case, where the list is already empty by the time it gets here (cleared by the monitor).
    // Deliberately NOT the deepest occurrence across the round: a deeper transient excursion
    // earlier on can later be fully orphaned by a shallower, final resolution (or vice versa),
    // so taking the max would report a possibly-superseded snapshot instead of the converged
    // outcome. 0 if no reorg occurred (or none survived to quiescence).
    private static long finalReorgDepth(List<ChainReorganizationOccurrence> occurrences) {
        if (occurrences.isEmpty()) return 0;
        ChainReorganizedTraceEvent last = occurrences.get(occurrences.size() - 1).getEvent();
        return last.getNewCanonicalTipHeight() - last.getCommonAncestorHeight();
    }

    // Same as finalReorgDepth, but only non-zero when that SAME last (converged) occurrence's
    // winning branch originated from the attacker (see ChainReorganizationOccurrence.
    // isAttackerCaused / branch-lineage attribution in ThreesimSimulationMonitor.
    // isAttackerCaused) -- 0 whenever the final outcome was not attacker-caused, even if some
    // earlier, since-superseded occurrence in this same round was.
    private static long finalAttackerCausedReorgDepth(List<ChainReorganizationOccurrence> occurrences) {
        if (occurrences.isEmpty()) return 0;
        ChainReorganizationOccurrence last = occurrences.get(occurrences.size() - 1);
        if (!last.isAttackerCaused()) return 0;
        ChainReorganizedTraceEvent e = last.getEvent();
        return e.getNewCanonicalTipHeight() - e.getCommonAncestorHeight();
    }

    // Only emitted when includeChainReorganizations is set (see toJson overloads) -- distinct
    // reveals within the round (see ChainReorganizationDeduplicator), not raw per-node
    // occurrences, and originId is interned to a small int local to this array since node
    // identities are recreated fresh each Monte Carlo round.
    private static void appendChainReorganizationsArray(StringBuilder sb, List<ChainReorganizationOccurrence> occurrences, String indent) {
        List<ChainReorganizedTraceEvent> events = ChainReorganizationDeduplicator.distinctEvents(occurrences);
        Map<String, Integer> nodeIndex = new HashMap<>();
        sb.append("[\n");
        for (int i = 0; i < events.size(); i++) {
            sb.append(indent).append("  ");
            appendChainReorganization(sb, events.get(i), nodeIndex);
            if (i < events.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append(indent).append("]");
    }

    private static int internNodeId(Map<String, Integer> nodeIndex, String id) {
        return nodeIndex.computeIfAbsent(id, k -> nodeIndex.size());
    }

    private static void appendChainReorganization(StringBuilder sb, ChainReorganizedTraceEvent e, Map<String, Integer> nodeIndex) {
        sb.append("{\"newCanonicalTipHeight\": ").append(e.getNewCanonicalTipHeight())
          .append(", \"commonAncestorHeight\": ").append(e.getCommonAncestorHeight())
          .append(", \"oldCanonicalTipHeights\": [");
        boolean first = true;
        for (ChainReorganizedTraceEvent.OldCanonicalTip tip : e.getOldCanonicalTips()) {
            if (!first) sb.append(",");
            sb.append(tip.getHeight());
            first = false;
        }
        sb.append("], \"replacingChainBlocks\": [");
        boolean firstBlock = true;
        for (ChainReorganizedTraceEvent.ChainBlock cb : e.getReplacingChainBlocks()) {
            if (!firstBlock) sb.append(",");
            sb.append("{\"height\": ").append(cb.getHeight())
              .append(", \"originId\": ").append(internNodeId(nodeIndex, cb.getBlock().getOriginId()))
              .append("}");
            firstBlock = false;
        }
        sb.append("]}");
    }

    private static void appendMetricsArray(StringBuilder sb, OutputMetricsSet metrics, String indent) {
        sb.append("[\n");
        List<OutputMetric<?>> arr = new ArrayList<>();
        for (Object o : metrics.toArray()) {
            OutputMetric<?> m = (OutputMetric<?>) o;
            if (!EXCLUDED_METRIC_NAMES.contains(m.getName())) arr.add(m);
        }
        for (int i = 0; i < arr.size(); i++) {
            sb.append(indent).append("  ");
            appendMetric(sb, arr.get(i));
            if (i < arr.size() - 1) sb.append(",");
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
        List<AverageOutputMetric> results = new ArrayList<>();
        for (AverageOutputMetric r : avg.getResults()) {
            if (!EXCLUDED_METRIC_NAMES.contains(averageMetricName(r))) results.add(r);
        }
        for (int i = 0; i < results.size(); i++) {
            sb.append(indent).append("  ");
            appendAverage(sb, results.get(i));
            if (i < results.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append(indent).append("]");
    }

    private static String averageMetricName(AverageOutputMetric avg) {
        if (avg instanceof AverageOutputMetricImpl a) return a.getName();
        if (avg instanceof FaultToleranceAverageOutputMetric fa) return fa.getName();
        return null;
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
