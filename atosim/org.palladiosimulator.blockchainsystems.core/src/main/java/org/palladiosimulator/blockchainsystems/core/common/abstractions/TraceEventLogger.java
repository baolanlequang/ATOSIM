package org.palladiosimulator.blockchainsystems.core.common.abstractions;

public interface TraceEventLogger {
    TraceEventLogOrigin getLogOrigin();
    void logEvent(TraceEvent traceEvent);
    boolean isEventTypeEnabled(String eventType);
}
