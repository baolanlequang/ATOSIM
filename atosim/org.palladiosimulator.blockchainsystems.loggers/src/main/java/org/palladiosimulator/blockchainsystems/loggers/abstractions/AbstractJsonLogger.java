package org.palladiosimulator.blockchainsystems.loggers.abstractions;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.blockchain.ChainReorganizedTraceEvent;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogOrigin;
import org.palladiosimulator.blockchainsystems.core.tracing.TraceEventLogOutput;

public abstract class AbstractJsonLogger implements TraceEventLogOutput {

    protected String getEventFormat(TraceEvent traceEvent, TraceEventLogOrigin logOrigin) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"traceEvent\":{\"occurrenceTime\":").append(traceEvent.getOccurrenceTime())
          .append(",\"eventType\":\"").append(escapeJson(traceEvent.getEventType())).append("\"");
        if (traceEvent instanceof ChainReorganizedTraceEvent e) {
            sb.append(",\"payload\":").append(chainReorganizedPayload(e));
        }
        sb.append("},\"logOrigin\":{\"id\":\"").append(escapeJson(logOrigin.getId()))
          .append("\",\"name\":\"").append(escapeJson(logOrigin.getName())).append("\"}}");
        return sb.toString();
    }

    // Reorg-depth analysis needs heights (D = h(old tip) - h(common ancestor)), which the base
    // {occurrenceTime, eventType} envelope above doesn't carry for any event, so this one type
    // is special-cased rather than generically reflecting every TraceEvent subtype's fields.
    // Block hashes are omitted (height/originId alone are what analysis needs) to keep this
    // payload small -- it's emitted once per reorg, which can be very frequent under attack.
    private static String chainReorganizedPayload(ChainReorganizedTraceEvent e) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"newCanonicalTip\":");
        appendHeight(sb, e.getNewCanonicalTipHeight());
        sb.append(",\"commonAncestor\":");
        appendHeight(sb, e.getCommonAncestorHeight());
        sb.append(",\"oldCanonicalTips\":[");
        boolean first = true;
        for (ChainReorganizedTraceEvent.OldCanonicalTip tip : e.getOldCanonicalTips()) {
            if (!first) sb.append(",");
            appendHeight(sb, tip.getHeight());
            first = false;
        }
        sb.append("],\"replacingChainBlocks\":[");
        boolean firstBlock = true;
        for (ChainReorganizedTraceEvent.ChainBlock cb : e.getReplacingChainBlocks()) {
            if (!firstBlock) sb.append(",");
            appendChainBlock(sb, cb.getBlock(), cb.getHeight());
            firstBlock = false;
        }
        sb.append("]}");
        return sb.toString();
    }

    private static void appendHeight(StringBuilder sb, long height) {
        sb.append("{\"height\":").append(height).append("}");
    }

    // Includes originId (unlike appendHeight) so consumers can attribute each replacing
    // block to the node that mined it, e.g. to count attacker-mined blocks in a reorg.
    private static void appendChainBlock(StringBuilder sb, Block block, long height) {
        sb.append("{\"height\":").append(height)
          .append(",\"originId\":\"").append(escapeJson(block.getOriginId())).append("\"}");
    }

    private static String escapeJson(String value) {
        if (value == null) return "";
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default   -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
