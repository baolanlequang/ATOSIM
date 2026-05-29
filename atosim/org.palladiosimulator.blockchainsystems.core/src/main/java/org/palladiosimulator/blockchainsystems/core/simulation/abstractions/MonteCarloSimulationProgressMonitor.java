package org.palladiosimulator.blockchainsystems.core.simulation.abstractions;

public interface MonteCarloSimulationProgressMonitor {
    void onSimulationStarted(int numberOfSimulationRounds);
    void onSimulationRoundFinished();
    void onSimulationFinished();
}
