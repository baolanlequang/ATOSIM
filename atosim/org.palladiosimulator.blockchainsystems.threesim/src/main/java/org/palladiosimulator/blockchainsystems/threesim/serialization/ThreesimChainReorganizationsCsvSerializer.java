package org.palladiosimulator.blockchainsystems.threesim.serialization;

import org.palladiosimulator.blockchainsystems.core.blockchain.ChainReorganizedTraceEvent;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ChainReorganizationOccurrence;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimMonteCarloSimulationResult;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimSimulationRoundResult;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimSingleSimulationResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CSV serializer for the distinct chain-reorganization events (attacker reveals) observed per
 * round, for attacker-attribution (which blocks in the replacing chain were attacker-mined).
 * Only used when explicitly requested (see the writeChainReorganizationsAsCsv config flag) --
 * by default this data is embedded directly in the result JSON, see ThreesimJsonSerializer.
 *
 * Columns: round, newCanonicalTipHeight/commonAncestorHeight, oldCanonicalTipHeights (old
 * canonical tip heights, ';'-separated), replacingChainBlocks (replacing blocks as
 * 'height:internedOriginId' pairs, '|'-separated).
 */
public class ThreesimChainReorganizationsCsvSerializer {

    private static final String HEADER =
            "round,newCanonicalTipHeight,commonAncestorHeight,oldCanonicalTipHeights,replacingChainBlocks\n";

    public static String toCsv(ThreesimSingleSimulationResult result) {
        StringBuilder sb = new StringBuilder(HEADER);
        appendRoundRows(sb, 0, result.getSimulationRoundResult().getChainReorganizations());
        return sb.toString();
    }

    public static String toCsv(ThreesimMonteCarloSimulationResult result) {
        StringBuilder sb = new StringBuilder(HEADER);
        List<ThreesimSimulationRoundResult> rounds = result.getSimulationRoundResults();
        for (int i = 0; i < rounds.size(); i++) {
            appendRoundRows(sb, i, rounds.get(i).getChainReorganizations());
        }
        return sb.toString();
    }

    private static void appendRoundRows(StringBuilder sb, int round, List<ChainReorganizationOccurrence> occurrences) {
        Map<String, Integer> nodeIndex = new HashMap<>();
        for (ChainReorganizedTraceEvent e : ChainReorganizationDeduplicator.distinctEvents(occurrences)) {
            sb.append(round).append(',')
              .append(e.getNewCanonicalTipHeight()).append(',')
              .append(e.getCommonAncestorHeight()).append(',');
            appendOldTips(sb, e.getOldCanonicalTips());
            sb.append(',');
            appendReplacingBlocks(sb, e.getReplacingChainBlocks(), nodeIndex);
            sb.append('\n');
        }
    }

    private static int internNodeId(Map<String, Integer> nodeIndex, String id) {
        return nodeIndex.computeIfAbsent(id, k -> nodeIndex.size());
    }

    private static void appendOldTips(StringBuilder sb, Set<ChainReorganizedTraceEvent.OldCanonicalTip> tips) {
        boolean first = true;
        for (ChainReorganizedTraceEvent.OldCanonicalTip tip : tips) {
            if (!first) sb.append(';');
            sb.append(tip.getHeight());
            first = false;
        }
    }

    private static void appendReplacingBlocks(
            StringBuilder sb, List<ChainReorganizedTraceEvent.ChainBlock> blocks, Map<String, Integer> nodeIndex) {
        for (int i = 0; i < blocks.size(); i++) {
            if (i > 0) sb.append('|');
            ChainReorganizedTraceEvent.ChainBlock cb = blocks.get(i);
            sb.append(cb.getHeight()).append(':').append(internNodeId(nodeIndex, cb.getBlock().getOriginId()));
        }
    }
}
