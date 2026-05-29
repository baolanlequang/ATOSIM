package org.palladiosimulator.blockchainsystems.threesim_plugin;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.palladiosimulator.blockchainsystems.core.simulation.MonteCarloSimulationParameters;
import org.palladiosimulator.blockchainsystems.core.simulation.SimulationType;
import org.palladiosimulator.blockchainsystems.core.simulation.SingleSimulationParameters;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationParameters;
import org.palladiosimulator.blockchainsystems.plugin.Attributes;
import org.palladiosimulator.blockchainsystems.plugin.PluginLaunch;
import org.palladiosimulator.blockchainsystems.plugin.simulation.SimulationJob;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimSimulationResultSerializer;

public class ThreesimPluginLaunch extends PluginLaunch {

    @Override
    protected void launchSimulationJob(ILaunchConfiguration configuration) {
        SimulationParameters simulationParameters = getSimulationParametersFromLaunchConfiguration(configuration);

        String configurationName = configuration.getName();
        String simulationTypeName = simulationParameters.getSimulationType() == SimulationType.Single
                ? "Single" : "Monte-Carlo";

        SimulationJob job = new SimulationJob(
                configuration,
                "3SIM " + simulationTypeName + " Simulation: " + configurationName,
                new ThreesimSimulationFactory(simulationParameters),
                new ThreesimSimulationResultSerializer()
        );
        job.schedule();
    }

    private SimulationParameters getSimulationParametersFromLaunchConfiguration(
            ILaunchConfiguration configuration) {
        try {
            SimulationType simulationType = SimulationType.valueOf(
                    configuration.getAttribute(
                            Attributes.SimulationType.SIMULATION_TYPE_ATTRIBUTE,
                            Attributes.SimulationType.SIMULATION_TYPE_ATTRIBUTE_DEFAULT));
            long maxLength = Long.parseLong(configuration.getAttribute(
                    Attributes.SimulationTermination.MAX_BLOCKCHAIN_LENGTH_ATTRIBUTE,
                    Attributes.SimulationTermination.MAX_BLOCKCHAIN_LENGTH_ATTRIBUTE_DEFAULT));
            String modelPath = configuration.getAttribute(
                    Attributes.ArchitecturalModels.BLOCKCHAIN_SYSTEM_MODEL_FILE_PATH_ATTRIBUTE,
                    Attributes.ArchitecturalModels.BLOCKCHAIN_SYSTEM_MODEL_FILE_PATH_ATTRIBUTE_DEFAULT);

            return switch (simulationType) {
                case Single -> new SingleSimulationParameters(maxLength, modelPath);
                case MonteCarlo -> new MonteCarloSimulationParameters(maxLength,
                        Integer.parseInt(configuration.getAttribute(
                                Attributes.SimulationType.NUMBER_OF_MONTE_CARLO_SIMULATION_ROUNDS,
                                Attributes.SimulationType.NUMBER_OF_MONTE_CARLO_SIMULATION_ROUNDS_DEFAULT)),
                        modelPath);
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to read simulation parameters from launch configuration", e);
        }
    }
}
