package org.palladiosimulator.blockchainsystems.atosim;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;

import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainSystem;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.ConnectedSubgraphsNetworkTopology;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.ExplicitNetworkTopology;

import org.palladiosimulator.blockchainsystems.core.simulation.MonteCarloSimulationParameters;
import org.palladiosimulator.blockchainsystems.core.simulation.SingleSimulationParameters;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.Simulation;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationParameters;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationResult;

import org.palladiosimulator.blockchainsystems.plugin.logging.LogOutputProviderImpl;
import org.palladiosimulator.blockchainsystems.plugin.simulation.MonteCarloSimulationProgressMonitorAdapter;

import org.palladiosimulator.blockchainsystems.threesim.creation.ThreesimBlockchainSystemFactory;
import org.palladiosimulator.blockchainsystems.threesim.creation.network.connectedsubgraphs.ConnectedSubgraphNetworkBlockchainSystemFactory;
import org.palladiosimulator.blockchainsystems.threesim.creation.network.explicit.ExplicitNetworkBlockchainSystemFactory;
import org.palladiosimulator.blockchainsystems.threesim.simulation.AttackType;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimMonteCarloSimulation;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSingleSimulation;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;

public class ATOSIMSimulationFactory implements Simulation {

    private final Simulation simulation;

    public ATOSIMSimulationFactory(
            SimulationParameters simulationParameters,
            Map<String, String> configuration) {

        ThreesimSimulationParameters threesimSimulationParameters =
                getThreesimSimulationParametersFromConfiguration(configuration);

        ThreesimBlockchainSystemFactory blockchainSystemFactory =
                createBlockchainSystemFactory(simulationParameters, configuration);

        int maxAllowedBlockchainLength =
                Math.toIntExact(simulationParameters.getMaxAllowedBlockchainLength());

        LogOutputProviderImpl logOutputProvider;
        try {
            logOutputProvider = LogOutputProviderImpl.fromLaunchConfiguration(null);
        } catch (CoreException e) {
            throw new RuntimeException("Failed to build log output provider", e);
        }

        if (simulationParameters instanceof MonteCarloSimulationParameters) {
            MonteCarloSimulationProgressMonitorAdapter progressMonitor =
                    new MonteCarloSimulationProgressMonitorAdapter(null);

            this.simulation =
                    new ThreesimMonteCarloSimulation(
                            progressMonitor,
                            blockchainSystemFactory,
                            logOutputProvider,
                            maxAllowedBlockchainLength,
                            (MonteCarloSimulationParameters) simulationParameters,
                            threesimSimulationParameters
                    );
        } else {
            this.simulation =
                    new ThreesimSingleSimulation(
                            blockchainSystemFactory,
                            logOutputProvider,
                            maxAllowedBlockchainLength,
                            (SingleSimulationParameters) simulationParameters,
                            threesimSimulationParameters
                    );
        }
    }

    @Override
    public SimulationResult run() {
        return simulation.run();
    }

    private ThreesimSimulationParameters getThreesimSimulationParametersFromConfiguration(
            Map<String, String> configuration) {

        double failureThroughputThreshold =
                Double.parseDouble(configuration.getOrDefault("failureThroughputThreshold", "1.0"));
        double shannonEntropyK =
                Double.parseDouble(configuration.getOrDefault("shannonEntropyK", "1.0"));
        double nakamotoCoefficientThreshold =
                Double.parseDouble(configuration.getOrDefault("nakamotoCoefficientThreshold", "50.0"));
        double reliabilityObservationTimespan =
                Double.parseDouble(configuration.getOrDefault("reliabilityObservationTimespan", "24.0"));
        double blockInterval =
                Double.parseDouble(configuration.getOrDefault("block_creation_interval", "600.0"));
        int maxBlockSize =
                Integer.parseInt(configuration.getOrDefault("max_block_size", "1000000"));
        double propagationDelay =
                Double.parseDouble(configuration.getOrDefault("propagation_delay", "0.0"));
        int nodeDegree =
                Integer.parseInt(configuration.getOrDefault("node_degree", "8"));
        double attackerHashPower =
                Double.parseDouble(configuration.getOrDefault("attacker_hash_power", "0.0"));
        double gamma =
                Double.parseDouble(configuration.getOrDefault("tie_breaking_parameter", "0.0"));
        long deltaA =
                Long.parseLong(configuration.getOrDefault("transaction_delay", "0"));
        long deltaB =
                Long.parseLong(configuration.getOrDefault("transaction_acceleration", "0"));
        int numberOfAttackers =
                Integer.parseInt(configuration.getOrDefault("number_of_attackers", "0"));
        AttackType attackType = numberOfAttackers > 0 ? AttackType.SELFISH_MINING : AttackType.NONE;

        return new ThreesimSimulationParameters(
                failureThroughputThreshold,
                shannonEntropyK,
                nakamotoCoefficientThreshold,
                reliabilityObservationTimespan,
                attackType,
                false,
                AttackType.NONE,
                Collections.emptySet(),
                attackerHashPower,
                gamma,
                deltaA,
                deltaB,
                6,
                blockInterval,
                propagationDelay,
                nodeDegree,
                maxBlockSize,
                100.0
        );
    }

    private ThreesimBlockchainSystemFactory createBlockchainSystemFactory(
            SimulationParameters simulationParameters,
            Map<String, String> configuration) {

        BlockchainSystemModelLoader loader =
                new BlockchainSystemModelLoader();

        BlockchainSystem designBlockchainSystem =
                loader.load(
                        simulationParameters.getBlockchainSystemModelFilePath(),
                        configuration);  

        var networkTopology =
                designBlockchainSystem.getNetwork().getTopology();

        if (networkTopology instanceof ConnectedSubgraphsNetworkTopology) {
            return new ConnectedSubgraphNetworkBlockchainSystemFactory(
                    designBlockchainSystem,
                    (ConnectedSubgraphsNetworkTopology) networkTopology);
        }

        if (networkTopology instanceof ExplicitNetworkTopology) {
            return new ExplicitNetworkBlockchainSystemFactory(
                    designBlockchainSystem,
                    (ExplicitNetworkTopology) networkTopology);
        }

        throw new IllegalStateException(
                "Unsupported network topology: "
                        + networkTopology.getClass().getName());
    }
}
