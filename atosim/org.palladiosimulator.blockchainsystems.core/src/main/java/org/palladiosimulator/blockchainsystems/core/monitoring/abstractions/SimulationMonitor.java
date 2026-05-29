package org.palladiosimulator.blockchainsystems.core.monitoring.abstractions;

import org.palladiosimulator.blockchainsystems.core.eventcoordination.TerminationCondition;
import org.palladiosimulator.blockchainsystems.core.system.BlockchainSystem;
import org.palladiosimulator.blockchainsystems.core.tracing.TraceEventSubscriber;

public interface SimulationMonitor extends TraceEventSubscriber, TerminationCondition {
    void initialize(BlockchainSystem blockchainSystem);
}
