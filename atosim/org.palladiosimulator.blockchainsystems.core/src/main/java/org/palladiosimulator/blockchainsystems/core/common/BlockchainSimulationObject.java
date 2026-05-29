package org.palladiosimulator.blockchainsystems.core.common;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.EventDispatchable;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationContext;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Traceable;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogOrigin;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogger;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLoggerContainer;

public abstract class BlockchainSimulationObject implements EventDispatchable, Traceable, TraceEventLogOrigin {

    private final String _id;
    private final String _name;
    private SimulationContext _simulationContext;
    private TraceEventLogger _traceEventLogger;

    protected BlockchainSimulationObject(String id, String name) {
        _id = id;
        _name = name;
    }

    @Override
    public String getId() {
        return _id;
    }

    @Override
    public String getName() {
        return _name;
    }

    protected void onInitialize() {
    }

    @Override
    public void initialize(SimulationContext simulationContext) {
        _simulationContext = simulationContext;
        initializeLogger(this);
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
