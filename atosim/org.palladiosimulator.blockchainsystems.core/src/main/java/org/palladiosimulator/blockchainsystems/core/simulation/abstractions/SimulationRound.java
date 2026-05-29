package org.palladiosimulator.blockchainsystems.core.simulation.abstractions;

import org.palladiosimulator.blockchainsystems.core.clock.SimulationClock;
import org.palladiosimulator.blockchainsystems.core.common.SimulationContextImpl;
import org.palladiosimulator.blockchainsystems.core.eventcoordination.EventCoordinatorImpl;
import org.palladiosimulator.blockchainsystems.core.monitoring.abstractions.SimulationMonitor;
import org.palladiosimulator.blockchainsystems.core.system.BlockchainSystem;
import org.palladiosimulator.blockchainsystems.core.tracing.TraceEventLogOutput;
import org.palladiosimulator.blockchainsystems.core.tracing.TraceEventLoggerContainerImpl;

import java.util.Set;

public abstract class SimulationRound<M extends SimulationMonitor, R extends SimulationRoundResult> {

    protected final BlockchainSystem blockchainSystem;
    protected final Set<TraceEventLogOutput> logOutputs;
    protected final M monitor;

    protected final SimulationClock clock = new SimulationClock();
    protected final TraceEventLoggerContainerImpl traceEventLoggerContainer = new TraceEventLoggerContainerImpl();

    protected final EventCoordinatorImpl eventCoordinator;
    protected final SimulationContextImpl context;

    public SimulationRound(BlockchainSystem blockchainSystem, Set<TraceEventLogOutput> logOutputs, M monitor) {
        this.blockchainSystem = blockchainSystem;
        this.logOutputs = logOutputs;
        this.monitor = monitor;

        eventCoordinator = new EventCoordinatorImpl(clock, monitor);
        context = new SimulationContextImpl(eventCoordinator, clock, traceEventLoggerContainer);

        setUpTraceEventSubscribers();
    }

    private void setUpTraceEventSubscribers() {
        traceEventLoggerContainer.addSubscriber(monitor);
        for (TraceEventLogOutput logOutput : logOutputs) {
            traceEventLoggerContainer.addSubscriber(logOutput);
        }
    }

    public void initialize() {
        for (TraceEventLogOutput logOutput : logOutputs) logOutput.initialize();
        monitor.initialize(blockchainSystem);
        blockchainSystem.initialize(context);
    }

    public void cleanup() {
        for (TraceEventLogOutput logOutput : logOutputs) logOutput.cleanUp();
    }

    public R run() {
        initialize();
        eventCoordinator.processEvents();
        cleanup();
        return createSimulationRoundResult(clock.getCurrentTime());
    }

    public abstract R createSimulationRoundResult(long finalSystemTime);
}
