package org.palladiosimulator.blockchainsystems.core.simulation;

import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationParameters;

public class MonteCarloSimulationParameters implements SimulationParameters {

    private final long _maxAllowedBlockchainLength;
    private final int _numberOfMonteCarloRounds;
    private final int _monteCarloParallelism;
    private final String _blockchainSystemModelFilePath;

    /**
     * @param monteCarloParallelism Maximum number of Monte Carlo rounds executed concurrently.
     *                              {@code 0} or less means "use the default ForkJoinPool common pool"
     *                              (i.e. unbounded, one task per available processor).
     */
    public MonteCarloSimulationParameters(
            long maxAllowedBlockchainLength,
            int numberOfMonteCarloRounds,
            int monteCarloParallelism,
            String blockchainSystemModelFilePath
    ) {
        _maxAllowedBlockchainLength = maxAllowedBlockchainLength;
        _numberOfMonteCarloRounds = numberOfMonteCarloRounds;
        _monteCarloParallelism = monteCarloParallelism;
        _blockchainSystemModelFilePath = blockchainSystemModelFilePath;
    }

    public MonteCarloSimulationParameters(
            long maxAllowedBlockchainLength,
            int numberOfMonteCarloRounds,
            String blockchainSystemModelFilePath
    ) {
        this(maxAllowedBlockchainLength, numberOfMonteCarloRounds, 0, blockchainSystemModelFilePath);
    }

    @Override public SimulationType getSimulationType() { return SimulationType.MonteCarlo; }
    @Override public long getMaxAllowedBlockchainLength() { return _maxAllowedBlockchainLength; }
    @Override public String getBlockchainSystemModelFilePath() { return _blockchainSystemModelFilePath; }
    public int getNumberOfMonteCarloRounds() { return _numberOfMonteCarloRounds; }
    public int getMonteCarloParallelism() { return _monteCarloParallelism; }
}
