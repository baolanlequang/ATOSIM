package org.palladiosimulator.blockchainsystems.core.simulation;

import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationParameters;

public class MonteCarloSimulationParameters implements SimulationParameters {

    private final long _maxAllowedBlockchainLength;
    private final int _numberOfMonteCarloRounds;
    private final String _blockchainSystemModelFilePath;

    public MonteCarloSimulationParameters(
            long maxAllowedBlockchainLength,
            int numberOfMonteCarloRounds,
            String blockchainSystemModelFilePath
    ) {
        _maxAllowedBlockchainLength = maxAllowedBlockchainLength;
        _numberOfMonteCarloRounds = numberOfMonteCarloRounds;
        _blockchainSystemModelFilePath = blockchainSystemModelFilePath;
    }

    @Override public SimulationType getSimulationType() { return SimulationType.MonteCarlo; }
    @Override public long getMaxAllowedBlockchainLength() { return _maxAllowedBlockchainLength; }
    @Override public String getBlockchainSystemModelFilePath() { return _blockchainSystemModelFilePath; }
    public int getNumberOfMonteCarloRounds() { return _numberOfMonteCarloRounds; }
}
