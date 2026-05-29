package org.palladiosimulator.blockchainsystems.plugin.simulation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationResult;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationResultSerializer;
import org.palladiosimulator.blockchainsystems.plugin.Attributes;
import org.palladiosimulator.blockchainsystems.plugin.simulation.abstractions.SimulationFactory;

public class SimulationJob extends Job {

    private final ILaunchConfiguration _configuration;
    private final SimulationFactory _simulationFactory;
    private final SimulationResultSerializer _simulationResultSerializer;

    public SimulationJob(
            ILaunchConfiguration configuration,
            String jobName,
            SimulationFactory simulationFactory,
            SimulationResultSerializer simulationResultSerializer
    ) {
        super(jobName);
        _configuration = configuration;
        _simulationFactory = simulationFactory;
        _simulationResultSerializer = simulationResultSerializer;
    }

    @Override
    protected IStatus run(IProgressMonitor progressMonitor) {
        SimulationResult result = _simulationFactory.create(_configuration, progressMonitor).run();

        try {
            String outDir = _configuration.getAttribute(
                    Attributes.ArchitecturalModels.SIMULATION_RESULT_FILE_DIRECTORY,
                    Attributes.ArchitecturalModels.SIMULATION_RESULT_FILE_DIRECTORY_DEFAULT
            );
            new SimulationResultsWriter(_simulationResultSerializer).saveResultFile(result, outDir);
        } catch (CoreException e) {
            e.printStackTrace();
            return Status.OK_STATUS;
        }

        return Status.OK_STATUS;
    }
}
