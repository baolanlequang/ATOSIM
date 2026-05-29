package org.palladiosimulator.blockchainsystems.threesim.simulation;

import org.palladiosimulator.blockchainsystems.core.simulation.SingleSimulationParameters;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SingleSimulation;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SingleSimulationResult;
import org.palladiosimulator.blockchainsystems.core.simulation.logoutputs.abstractions.LogOutputProvider;
import org.palladiosimulator.blockchainsystems.threesim.creation.ThreesimBlockchainSystemFactory;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimSimulationRoundResult;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimSingleSimulationResult;

public class ThreesimSingleSimulation implements SingleSimulation {

    private final ThreesimBlockchainSystemFactory _blockchainSystemFactory;
    private final LogOutputProvider _logOutputProvider;
    private final long _maxAllowedBlockchainLength;
    private final SingleSimulationParameters _simulationParameters;
    private final ThreesimSimulationParameters _threesimSimulationParameters;

    public ThreesimSingleSimulation(
            ThreesimBlockchainSystemFactory blockchainSystemFactory,
            LogOutputProvider logOutputProvider,
            long maxAllowedBlockchainLength,
            SingleSimulationParameters simulationParameters,
            ThreesimSimulationParameters threesimSimulationParameters) {
        _blockchainSystemFactory = blockchainSystemFactory;
        _logOutputProvider = logOutputProvider;
        _maxAllowedBlockchainLength = maxAllowedBlockchainLength;
        _simulationParameters = simulationParameters;
        _threesimSimulationParameters = threesimSimulationParameters;
    }

    @Override
    public SingleSimulationResult run() {
        ThreesimSimulationRoundResult result = new ThreesimSimulationRound(
                _blockchainSystemFactory,
                _logOutputProvider.getLogOutputs(),
                _maxAllowedBlockchainLength,
                _threesimSimulationParameters
        ).run();

        return new ThreesimSingleSimulationResult(
                _simulationParameters,
                _threesimSimulationParameters,
                result);
    }
}
