package org.palladiosimulator.blockchainsystems.core.common.abstractions;

public interface TraceEventLoggerContainer {
    void createTraceEventLogger(TraceEventLogOrigin logOrigin);
    TraceEventLogger getLogger(TraceEventLogOrigin logOrigin);
}
