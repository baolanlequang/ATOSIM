package org.palladiosimulator.blockchainsystems.core.common.abstractions;

public interface Traceable extends SimulationLifecycleAware {
    void initializeLogger(TraceEventLogOrigin logOrigin);
}
