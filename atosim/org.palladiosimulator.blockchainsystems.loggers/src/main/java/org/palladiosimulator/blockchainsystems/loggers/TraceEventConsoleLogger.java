package org.palladiosimulator.blockchainsystems.loggers;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogOrigin;
import org.palladiosimulator.blockchainsystems.loggers.abstractions.AbstractJsonLogger;

public class TraceEventConsoleLogger extends AbstractJsonLogger {

    @Override
    public void onTraceEventOccurred(TraceEvent traceEvent, TraceEventLogOrigin logOrigin) {
        // System.out.println(getEventFormat(traceEvent, logOrigin));
    }

    @Override
    public void initialize() {
    }

    @Override
    public void cleanUp() {
    }
}
