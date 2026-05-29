package org.palladiosimulator.blockchainsystems.core.common.abstractions;

public interface SimulationLifecycleAware {
    void initialize(SimulationContext simulationContext);
    void cleanup();
}
