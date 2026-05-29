package org.palladiosimulator.blockchainsystems.threesim.simulation.results;

import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationParameters;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SingleSimulationResult;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;

public class ThreesimSingleSimulationResult implements SingleSimulationResult {

    private final SimulationParameters _simulationParameters;
    private final ThreesimSimulationParameters _threesimSimulationParameters;
    private final ThreesimSimulationRoundResult _simulationRoundResult;

    public ThreesimSingleSimulationResult(
            SimulationParameters simulationParameters,
            ThreesimSimulationParameters threesimSimulationParameters,
            ThreesimSimulationRoundResult simulationRoundResult) {
        _simulationParameters = simulationParameters;
        _threesimSimulationParameters = threesimSimulationParameters;
        _simulationRoundResult = simulationRoundResult;
    }

    public SimulationParameters getSimulationParameters() { return _simulationParameters; }
    public ThreesimSimulationParameters getThreesimSimulationParameters() { return _threesimSimulationParameters; }
    public ThreesimSimulationRoundResult getSimulationRoundResult() { return _simulationRoundResult; }
}
