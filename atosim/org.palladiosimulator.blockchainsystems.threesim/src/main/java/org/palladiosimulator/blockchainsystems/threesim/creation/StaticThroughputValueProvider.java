package org.palladiosimulator.blockchainsystems.threesim.creation;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationContext;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationLifecycleAwareValueProvider;

public class StaticThroughputValueProvider implements SimulationLifecycleAwareValueProvider<Long> {

    private final long _throughput;

    public StaticThroughputValueProvider(long throughput) {
        _throughput = throughput;
    }

    @Override public Long getValue() { return _throughput; }
    @Override public void initialize(SimulationContext simulationContext) {}
    @Override public void cleanup() {}
}
