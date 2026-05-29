package org.palladiosimulator.blockchainsystems.core.simulation;

import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationParameters;

public class SingleSimulationParameters implements SimulationParameters {

    private final long _maxAllowedBlockchainLength;
    private final String _blockchainSystemModelFilePath;

    public SingleSimulationParameters(long maxAllowedBlockchainLength, String blockchainSystemModelFilePath) {
        _maxAllowedBlockchainLength = maxAllowedBlockchainLength;
        _blockchainSystemModelFilePath = blockchainSystemModelFilePath;
    }

    @Override public SimulationType getSimulationType() { return SimulationType.Single; }
    @Override public long getMaxAllowedBlockchainLength() { return _maxAllowedBlockchainLength; }
    @Override public String getBlockchainSystemModelFilePath() { return _blockchainSystemModelFilePath; }
}
