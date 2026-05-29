package org.palladiosimulator.blockchainsystems.threesim_plugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.ConnectedSubgraphsNetworkTopology;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.ExplicitNetworkTopology;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.NetworkTopology;
import org.palladiosimulator.blockchainsystems.core.simulation.MonteCarloSimulationParameters;
import org.palladiosimulator.blockchainsystems.core.simulation.SimulationType;
import org.palladiosimulator.blockchainsystems.core.simulation.SingleSimulationParameters;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.Simulation;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationParameters;
import org.palladiosimulator.blockchainsystems.plugin.Attributes;
import org.palladiosimulator.blockchainsystems.plugin.logging.LogOutputProviderImpl;
import org.palladiosimulator.blockchainsystems.plugin.simulation.MonteCarloSimulationProgressMonitorAdapter;
import org.palladiosimulator.blockchainsystems.plugin.simulation.abstractions.SimulationFactory;
import org.palladiosimulator.blockchainsystems.threesim.creation.ThreesimBlockchainSystemFactory;
import org.palladiosimulator.blockchainsystems.threesim.creation.network.connectedsubgraphs.ConnectedSubgraphNetworkBlockchainSystemFactory;
import org.palladiosimulator.blockchainsystems.threesim.creation.network.explicit.ExplicitNetworkBlockchainSystemFactory;
import org.palladiosimulator.blockchainsystems.threesim.simulation.AttackType;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimMonteCarloSimulation;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSingleSimulation;

import java.util.Collections;

public class ThreesimSimulationFactory implements SimulationFactory {

    private final SimulationParameters _simulationParameters;

    public ThreesimSimulationFactory(SimulationParameters simulationParameters) {
        _simulationParameters = simulationParameters;
    }

    @Override
    public Simulation create(ILaunchConfiguration configuration, IProgressMonitor progressMonitor) {
        ThreesimSimulationParameters threesimParams = getThreesimParams(configuration);
        LogOutputProviderImpl logOutputProvider;
        try {
            logOutputProvider = LogOutputProviderImpl.fromLaunchConfiguration(configuration);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build log output provider", e);
        }

        return switch (_simulationParameters.getSimulationType()) {
            case Single -> new ThreesimSingleSimulation(
                    createBlockchainSystemFactory(),
                    logOutputProvider,
                    _simulationParameters.getMaxAllowedBlockchainLength(),
                    (SingleSimulationParameters) _simulationParameters,
                    threesimParams);
            case MonteCarlo -> new ThreesimMonteCarloSimulation(
                    new MonteCarloSimulationProgressMonitorAdapter(progressMonitor),
                    createBlockchainSystemFactory(),
                    logOutputProvider,
                    _simulationParameters.getMaxAllowedBlockchainLength(),
                    (MonteCarloSimulationParameters) _simulationParameters,
                    threesimParams);
        };
    }

    private ThreesimSimulationParameters getThreesimParams(ILaunchConfiguration configuration) {
        try {
            String combinedAttackMode = configuration.getAttribute(
                    ThreesimAttributes.COMBINED_ATTACK_MODE,
                    ThreesimAttributes.COMBINED_ATTACK_MODE_DEFAULT);

            boolean combinedAttackEnabled =
                    !ThreesimAttributes.COMBINED_ATTACK_MODE_NONE.equals(combinedAttackMode);

            AttackType secondaryAttackType = switch (combinedAttackMode) {
                case ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_RACE -> AttackType.RACE;
                case ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_FINNEY -> AttackType.FINNEY;
                default -> AttackType.NONE;
            };

            ThreesimSimulationParameters baseParams = new ThreesimSimulationParameters(
                    Double.parseDouble(configuration.getAttribute(ThreesimAttributes.FAILURE_THROUGHPUT_THRESHOLD, ThreesimAttributes.FAILURE_THROUGHPUT_THRESHOLD_DEFAULT)),
                    Double.parseDouble(configuration.getAttribute(ThreesimAttributes.SHANNON_ENTROPY_K, ThreesimAttributes.SHANNON_ENTROPY_K_DEFAULT)),
                    Double.parseDouble(configuration.getAttribute(ThreesimAttributes.NAKAMOTO_COEFFICIENT_THRESHOLD, ThreesimAttributes.NAKAMOTO_COEFFICIENT_THRESHOLD_DEFAULT)),
                    Double.parseDouble(configuration.getAttribute(ThreesimAttributes.RELIABILITY_OBSERVATION_TIMESPAN, ThreesimAttributes.RELIABILITY_OBSERVATION_TIMESPAN_DEFAULT)),
                    AttackType.NONE,
                    combinedAttackEnabled,
                    secondaryAttackType,
                    Collections.emptySet(),
                    0.0,
                    0.0,
                    Long.parseLong(configuration.getAttribute(ThreesimAttributes.TRANSACTION_A_DELAY, ThreesimAttributes.TRANSACTION_A_DELAY_DEFAULT)),
                    Long.parseLong(configuration.getAttribute(ThreesimAttributes.TRANSACTION_B_ACCELERATION, ThreesimAttributes.TRANSACTION_B_ACCELERATION_DEFAULT)),
                    6,
                    Double.parseDouble(configuration.getAttribute(ThreesimAttributes.BLOCK_INTERVAL, ThreesimAttributes.BLOCK_INTERVAL_DEFAULT)),
                    Double.parseDouble(configuration.getAttribute(ThreesimAttributes.PROPAGATION_DELAY, ThreesimAttributes.PROPAGATION_DELAY_DEFAULT)),
                    Integer.parseInt(configuration.getAttribute(ThreesimAttributes.NODE_DEGREE, ThreesimAttributes.NODE_DEGREE_DEFAULT)),
                    Integer.parseInt(configuration.getAttribute(ThreesimAttributes.MAX_BLOCK_SIZE, ThreesimAttributes.MAX_BLOCK_SIZE_DEFAULT)),
                    Double.parseDouble(configuration.getAttribute(ThreesimAttributes.NETWORK_BANDWIDTH, ThreesimAttributes.NETWORK_BANDWIDTH_DEFAULT))
            );

            String attackModelPath = configuration.getAttribute(
                    Attributes.ArchitecturalModels.ATTACK_MODEL_FILE_PATH_ATTRIBUTE,
                    Attributes.ArchitecturalModels.ATTACK_MODEL_FILE_PATH_ATTRIBUTE_DEFAULT);

            if (attackModelPath == null || attackModelPath.isBlank()) {
                return baseParams;
            }

            BlockchainSystemModelLoader.LoadedModels loaded = new BlockchainSystemModelLoader()
                    .load(_simulationParameters.getBlockchainSystemModelFilePath(), attackModelPath);

            if (loaded.getAttackScenario() == null) return baseParams;

            return AttackModelMapper.applyAttackScenario(
                    baseParams, loaded.getBlockchainSystem(), loaded.getAttackScenario());

        } catch (Exception e) {
            throw new RuntimeException("Failed to build 3SIM simulation parameters", e);
        }
    }

    private ThreesimBlockchainSystemFactory createBlockchainSystemFactory() {
        org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainSystem designSystem =
                new BlockchainSystemModelLoader()
                        .load(_simulationParameters.getBlockchainSystemModelFilePath());

        NetworkTopology topology = designSystem.getNetwork().getTopology();
        if (topology instanceof ExplicitNetworkTopology e) {
            return new ExplicitNetworkBlockchainSystemFactory(designSystem, e);
        }
        if (topology instanceof ConnectedSubgraphsNetworkTopology c) {
            return new ConnectedSubgraphNetworkBlockchainSystemFactory(designSystem, c);
        }
        throw new IllegalArgumentException(
                "Unknown network topology type: " + topology.getClass().getSimpleName());
    }
}
