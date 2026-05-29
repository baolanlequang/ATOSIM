package org.palladiosimulator.blockchainsystems.loggers.abstractions;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogOrigin;
import org.palladiosimulator.blockchainsystems.core.tracing.TraceEventLogOutput;

public abstract class AbstractJsonLogger implements TraceEventLogOutput {

    protected String getEventFormat(TraceEvent traceEvent, TraceEventLogOrigin logOrigin) {
        return "{\"traceEvent\":{\"occurrenceTime\":" + traceEvent.getOccurrenceTime() +
                ",\"eventType\":\"" + escapeJson(traceEvent.getEventType()) + "\"}," +
                "\"logOrigin\":{\"id\":\"" + escapeJson(logOrigin.getId()) +
                "\",\"name\":\"" + escapeJson(logOrigin.getName()) + "\"}}";
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
