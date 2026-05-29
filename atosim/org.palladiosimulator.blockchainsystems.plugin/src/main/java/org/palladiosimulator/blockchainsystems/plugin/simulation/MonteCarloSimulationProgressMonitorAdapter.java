package org.palladiosimulator.blockchainsystems.plugin.simulation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.MonteCarloSimulationProgressMonitor;

public class MonteCarloSimulationProgressMonitorAdapter implements MonteCarloSimulationProgressMonitor {

    private final IProgressMonitor _progressMonitor;

    public MonteCarloSimulationProgressMonitorAdapter(IProgressMonitor progressMonitor) {
        _progressMonitor = progressMonitor;
    }

    @Override
    public void onSimulationStarted(int numberOfSimulationRounds) {
    	if (_progressMonitor != null) {
    		_progressMonitor.beginTask("Running Monte-Carlo Simulation", numberOfSimulationRounds);
    	} else {
    		System.out.println("Running Monte-Carlo Simulation with " + numberOfSimulationRounds + " simulation round(s)");
    	}
    }

    @Override
    public synchronized void onSimulationRoundFinished() {
    	if (_progressMonitor != null) {
    		_progressMonitor.worked(1);
    	}
    }

    @Override
    public void onSimulationFinished() {
    	if (_progressMonitor != null) {
    		_progressMonitor.done();
    	} else {
    		System.out.println("Monte-Carlo Simulation finished");
    	}
    }
}
