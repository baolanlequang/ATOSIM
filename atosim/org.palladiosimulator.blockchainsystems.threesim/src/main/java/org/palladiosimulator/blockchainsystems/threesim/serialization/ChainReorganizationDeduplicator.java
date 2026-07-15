package org.palladiosimulator.blockchainsystems.threesim.serialization;

import org.palladiosimulator.blockchainsystems.core.blockchain.ChainReorganizedTraceEvent;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ChainReorganizationOccurrence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The same attacker reveal is independently logged once per node whose local view reorganizes
 * because of it -- every node keeps its own local chain state, and propagation delay means
 * different nodes notice at different times -- so raw per-node occurrences are collapsed down to
 * one entry per distinct reveal, keyed on the resulting branch (new-tip/common-ancestor heights
 * plus the replacing blocks). Shared by both the JSON and CSV chainReorganizations output so the
 * dedup logic isn't duplicated between them.
 */
final class ChainReorganizationDeduplicator {

    private ChainReorganizationDeduplicator() {
    }

    static List<ChainReorganizedTraceEvent> distinctEvents(List<ChainReorganizationOccurrence> occurrences) {
        Map<String, ChainReorganizedTraceEvent> distinct = new LinkedHashMap<>();
        for (ChainReorganizationOccurrence occ : occurrences) {
            ChainReorganizedTraceEvent e = occ.getEvent();
            distinct.putIfAbsent(key(e), e);
        }
        return new ArrayList<>(distinct.values());
    }

    // Two occurrences are the same underlying reveal iff they describe the same resulting
    // branch: same new-tip/common-ancestor heights and the same set of replacing blocks (by
    // height + miner). Different nodes observing the same reveal produce identical keys.
    private static String key(ChainReorganizedTraceEvent e) {
        List<String> oldTips = new ArrayList<>();
        for (ChainReorganizedTraceEvent.OldCanonicalTip tip : e.getOldCanonicalTips()) {
            oldTips.add(String.valueOf(tip.getHeight()));
        }
        Collections.sort(oldTips);

        List<String> replacingBlocks = new ArrayList<>();
        for (ChainReorganizedTraceEvent.ChainBlock cb : e.getReplacingChainBlocks()) {
            replacingBlocks.add(cb.getHeight() + ":" + cb.getBlock().getOriginId());
        }
        Collections.sort(replacingBlocks);

        return e.getNewCanonicalTipHeight() + "|" + e.getCommonAncestorHeight()
                + "|" + String.join(",", oldTips) + "|" + String.join(",", replacingBlocks);
    }
}
