package org.palladiosimulator.blockchainsystems.threesim.creation;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationContext;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationLifecycleAwareValueProvider;

public class StaticLatencyValueProvider implements SimulationLifecycleAwareValueProvider<Long> {

    private final long _latency;

    public StaticLatencyValueProvider(long latency) {
        _latency = latency;
    }

    @Override public Long getValue() { return _latency; }
    @Override public void initialize(SimulationContext simulationContext) {}
    @Override public void cleanup() {}
}
