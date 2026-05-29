package org.palladiosimulator.blockchainsystems.threesim.creation;

import org.palladiosimulator.blockchainsystems.core.system.BlockchainSystem;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;

public class BlockchainSystemWithParameters {

    private final BlockchainSystem system;
    private final ThreesimSimulationParameters effectiveParameters;

    public BlockchainSystemWithParameters(BlockchainSystem system, ThreesimSimulationParameters effectiveParameters) {
        this.system = system;
        this.effectiveParameters = effectiveParameters;
    }

    public BlockchainSystem getSystem() { return system; }
    public ThreesimSimulationParameters getEffectiveParameters() { return effectiveParameters; }
}
