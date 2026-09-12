package org.palladiosimulator.blockchainsystems.threesim.creation;

import org.palladiosimulator.blockchainsystems.core.system.BlockchainSystem;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;

public class BlockchainSystemWithParameters {

    private final BlockchainSystem system;
    private final ThreesimSimulationParameters effectiveParameters;
    private final TopologyDeterminismInfo topologyDeterminismInfo;

    public BlockchainSystemWithParameters(BlockchainSystem system, ThreesimSimulationParameters effectiveParameters,
            TopologyDeterminismInfo topologyDeterminismInfo) {
        this.system = system;
        this.effectiveParameters = effectiveParameters;
        this.topologyDeterminismInfo = topologyDeterminismInfo;
    }

    public BlockchainSystem getSystem() { return system; }
    public ThreesimSimulationParameters getEffectiveParameters() { return effectiveParameters; }
    public TopologyDeterminismInfo getTopologyDeterminismInfo() { return topologyDeterminismInfo; }
}
