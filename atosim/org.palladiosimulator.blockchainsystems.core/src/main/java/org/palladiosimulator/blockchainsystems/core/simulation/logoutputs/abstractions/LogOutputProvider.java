package org.palladiosimulator.blockchainsystems.core.simulation.logoutputs.abstractions;

import org.palladiosimulator.blockchainsystems.core.tracing.TraceEventLogOutput;

import java.util.Set;

public interface LogOutputProvider {
    Set<TraceEventLogOutput> getLogOutputs();
}
