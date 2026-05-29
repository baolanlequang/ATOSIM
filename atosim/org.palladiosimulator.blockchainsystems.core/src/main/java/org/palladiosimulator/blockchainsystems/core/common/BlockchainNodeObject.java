package org.palladiosimulator.blockchainsystems.core.common;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.EventDispatchable;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationContext;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Traceable;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogOrigin;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogger;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLoggerContainer;

public abstract class BlockchainNodeObject implements EventDispatchable, Traceable {

    private SimulationContext _simulationContext;
    private TraceEventLogger _traceEventLogger;

    protected void onInitialize() {
    }

    @Override
    public void initialize(SimulationContext simulationContext) {
        _simulationContext = simulationContext;
        onInitialize();
    }

    protected void onCleanup() {
    }

    @Override
    public void cleanup() {
        onCleanup();
    }

    @Override
    public void initializeLogger(TraceEventLogOrigin logOrigin) {
        TraceEventLoggerContainer loggerContainer = _simulationContext.getTraceEventLoggerContainer();
        loggerContainer.createTraceEventLogger(logOrigin);
        _traceEventLogger = loggerContainer.getLogger(logOrigin);
    }

    protected SimulationContext getSimulationContext() {
        return _simulationContext;
    }

    protected TraceEventLogger getTraceEventLogger() {
        return _traceEventLogger;
    }
}
