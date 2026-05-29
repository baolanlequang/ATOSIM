package org.palladiosimulator.blockchainsystems.core.simulation.abstractions;

import org.palladiosimulator.blockchainsystems.core.simulation.SimulationType;

public interface SimulationParameters {
    SimulationType getSimulationType();
    long getMaxAllowedBlockchainLength();
    String getBlockchainSystemModelFilePath();
}
