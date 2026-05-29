package org.palladiosimulator.blockchainsystems.core.tracing;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogOrigin;

public interface TraceEventSubscriber {
    void onTraceEventOccurred(TraceEvent event, TraceEventLogOrigin logOrigin);
}
