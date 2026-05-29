package org.palladiosimulator.blockchainsystems.core.simulation.termination.abstractions;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;

public interface NodeTerminationState {
    void onTraceEventOccurred(TraceEvent traceEvent);
}
