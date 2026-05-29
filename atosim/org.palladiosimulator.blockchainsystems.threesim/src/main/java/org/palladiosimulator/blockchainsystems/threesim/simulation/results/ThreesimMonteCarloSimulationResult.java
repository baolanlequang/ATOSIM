package org.palladiosimulator.blockchainsystems.threesim.simulation.results;

import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.MonteCarloSimulationResult;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationParameters;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;

import java.util.List;

public class ThreesimMonteCarloSimulationResult implements MonteCarloSimulationResult {

    private final SimulationParameters _simulationParameters;
    private final ThreesimSimulationParameters _threesimSimulationParameters;
    private final ThreesimSimulationRoundResult _generalResults;
    private final List<ThreesimSimulationRoundResult> _simulationRoundResults;
    private final ThreesimAverageSimulationRoundResult _averageSimulationRoundResult;

    public ThreesimMonteCarloSimulationResult(
            SimulationParameters simulationParameters,
            ThreesimSimulationParameters threesimSimulationParameters,
            ThreesimSimulationRoundResult generalResults,
            List<ThreesimSimulationRoundResult> simulationRoundResults) {
        _simulationParameters = simulationParameters;
        _threesimSimulationParameters = threesimSimulationParameters;
        _generalResults = generalResults;
        _simulationRoundResults = simulationRoundResults;
        _averageSimulationRoundResult =
                ThreesimAverageSimulationRoundResult.fromSimulationRoundResults(simulationRoundResults);
    }

    public SimulationParameters getSimulationParameters() { return _simulationParameters; }
    public ThreesimSimulationParameters getThreesimSimulationParameters() { return _threesimSimulationParameters; }
    public ThreesimSimulationRoundResult getGeneralResults() { return _generalResults; }
    public List<ThreesimSimulationRoundResult> getSimulationRoundResults() { return _simulationRoundResults; }
    public ThreesimAverageSimulationRoundResult getAverageSimulationRoundResult() { return _averageSimulationRoundResult; }
}
