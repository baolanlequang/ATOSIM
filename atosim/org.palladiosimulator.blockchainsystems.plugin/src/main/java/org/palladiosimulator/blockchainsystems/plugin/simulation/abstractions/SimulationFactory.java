package org.palladiosimulator.blockchainsystems.plugin.simulation.abstractions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.Simulation;

public interface SimulationFactory {
    Simulation create(ILaunchConfiguration configuration, IProgressMonitor progressMonitor);
}
