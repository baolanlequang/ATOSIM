package org.palladiosimulator.blockchainsystems.threesim_plugin

import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.debug.core.ILaunchConfiguration
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.ConnectedSubgraphsNetworkTopology
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.ExplicitNetworkTopology
import org.palladiosimulator.blockchainsystems.core.simulation.MonteCarloSimulationParameters
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.Simulation
import org.palladiosimulator.blockchainsystems.core.simulation.SimulationType
import org.palladiosimulator.blockchainsystems.core.simulation.SingleSimulationParameters
import org.palladiosimulator.blockchainsystems.plugin.logging.LogOutputProviderImpl
import org.palladiosimulator.blockchainsystems.plugin.simulation.MonteCarloSimulationProgressMonitorAdapter
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationParameters
import org.palladiosimulator.blockchainsystems.plugin.simulation.abstractions.SimulationFactory
import org.palladiosimulator.blockchainsystems.threesim.creation.ThreesimBlockchainSystemFactory
import org.palladiosimulator.blockchainsystems.threesim.creation.network.connectedsubgraphs.ConnectedSubgraphNetworkBlockchainSystemFactory
import org.palladiosimulator.blockchainsystems.threesim.creation.network.explicit.ExplicitNetworkBlockchainSystemFactory
import org.palladiosimulator.blockchainsystems.threesim.serialization.ThreesimSerializers
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimMonteCarloSimulation
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSingleSimulation
import org.palladiosimulator.blockchainsystems.plugin.Attributes
import org.palladiosimulator.blockchainsystems.threesim.simulation.AttackType

/**
 * Factory for creating instances of [Simulation] for the 3SIM blockchain simulator.
 *
 * @author Davis Riedel
 */
class ThreesimSimulationFactory(
  private val simulationParameters: SimulationParameters
) : SimulationFactory {
  override fun create(
    configuration: ILaunchConfiguration,
    progressMonitor: IProgressMonitor
  ): Simulation {
    val threesimSimulationParameters = getThreesimSimulationParametersFromLaunchConfiguration(configuration)

    val logOutputProvider = LogOutputProviderImpl.fromLaunchConfiguration(
      ThreesimSerializers.json,
      configuration,
    )

    return when (simulationParameters.simulationType) {
      SimulationType.Single -> {
        ThreesimSingleSimulation(
          createBlockchainSystemFactory(),
          logOutputProvider,
          simulationParameters.maxAllowedBlockchainLength,
          simulationParameters as SingleSimulationParameters,
          threesimSimulationParameters,
        )
      }

      SimulationType.MonteCarlo -> {
        ThreesimMonteCarloSimulation(
          MonteCarloSimulationProgressMonitorAdapter(progressMonitor),
          createBlockchainSystemFactory(),
          logOutputProvider,
          simulationParameters.maxAllowedBlockchainLength,
          simulationParameters as MonteCarloSimulationParameters,
          threesimSimulationParameters,
        )
      }
    }
  }

  private fun getThreesimSimulationParametersFromLaunchConfiguration(
    configuration: ILaunchConfiguration
  ): ThreesimSimulationParameters {

    val combinedAttackMode = configuration.getAttribute(
      ThreesimAttributes.COMBINED_ATTACK_MODE,
      ThreesimAttributes.COMBINED_ATTACK_MODE_DEFAULT
    )

    val combinedAttackEnabled =
      combinedAttackMode != ThreesimAttributes.COMBINED_ATTACK_MODE_NONE

    val secondaryAttackType =
      when (combinedAttackMode) {
        ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_RACE -> AttackType.RACE
        ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_FINNEY -> AttackType.FINNEY
        else -> AttackType.NONE
      }

    val baseParams = ThreesimSimulationParameters(
      failureThroughputThreshold = configuration.getAttribute(
        ThreesimAttributes.FAILURE_THROUGHPUT_THRESHOLD,
        ThreesimAttributes.FAILURE_THROUGHPUT_THRESHOLD_DEFAULT
      ).toDouble(),
      shannonEntropyK = configuration.getAttribute(
        ThreesimAttributes.SHANNON_ENTROPY_K,
        ThreesimAttributes.SHANNON_ENTROPY_K_DEFAULT
      ).toDouble(),
      nakamotoCoefficientThreshold = configuration.getAttribute(
        ThreesimAttributes.NAKAMOTO_COEFFICIENT_THRESHOLD,
        ThreesimAttributes.NAKAMOTO_COEFFICIENT_THRESHOLD_DEFAULT
      ).toDouble(),
      reliabilityObservationTimespan = configuration.getAttribute(
        ThreesimAttributes.RELIABILITY_OBSERVATION_TIMESPAN,
        ThreesimAttributes.RELIABILITY_OBSERVATION_TIMESPAN_DEFAULT
      ).toDouble(),

      attackType = AttackType.NONE,
      combinedAttackEnabled = combinedAttackEnabled,
      secondaryAttackType = secondaryAttackType,

      deltaA = configuration.getAttribute(
        ThreesimAttributes.TRANSACTION_A_DELAY,
        ThreesimAttributes.TRANSACTION_A_DELAY_DEFAULT
      ).toLong(),
      deltaB = configuration.getAttribute(
        ThreesimAttributes.TRANSACTION_B_ACCELERATION,
        ThreesimAttributes.TRANSACTION_B_ACCELERATION_DEFAULT
      ).toLong(),

      blockInterval = configuration.getAttribute(
        ThreesimAttributes.BLOCK_INTERVAL,
        ThreesimAttributes.BLOCK_INTERVAL_DEFAULT
      ).toDouble(),
      propagationDelay = configuration.getAttribute(
        ThreesimAttributes.PROPAGATION_DELAY,
        ThreesimAttributes.PROPAGATION_DELAY_DEFAULT
      ).toDouble(),
      nodeDegree = configuration.getAttribute(
        ThreesimAttributes.NODE_DEGREE,
        ThreesimAttributes.NODE_DEGREE_DEFAULT
      ).toInt(),
      maxBlockSize = configuration.getAttribute(
        ThreesimAttributes.MAX_BLOCK_SIZE,
        ThreesimAttributes.MAX_BLOCK_SIZE_DEFAULT
      ).toInt(),
      networkBandwidth = configuration.getAttribute(
        ThreesimAttributes.NETWORK_BANDWIDTH,
        ThreesimAttributes.NETWORK_BANDWIDTH_DEFAULT
      ).toDouble()
    )

    //  NEW: read attack model path
    val attackModelPath =
      configuration.getAttribute(
        Attributes.ArchitecturalModels.ATTACK_MODEL_FILE_PATH_ATTRIBUTE,
        Attributes.ArchitecturalModels.ATTACK_MODEL_FILE_PATH_ATTRIBUTE_DEFAULT
      )

    // No attack model → honest simulation
    if (attackModelPath.isBlank()) {
      return baseParams
    }

    // Load BOTH models together
    val loader = BlockchainSystemModelLoader()
    val loadedModels =
      loader.load(simulationParameters.blockchainSystemModelFilePath, attackModelPath)

    val attackScenario = loadedModels.attackScenario ?: return baseParams

    //  Map attackmodel → runtime parameters
    return AttackModelMapper.applyAttackScenario(
      baseParams,
      loadedModels.blockchainSystem,
      attackScenario
    )
  }


  private fun createBlockchainSystemFactory(): ThreesimBlockchainSystemFactory {
    val designModelLoader = BlockchainSystemModelLoader()
    val designBlockchainSystem = designModelLoader.load(simulationParameters.blockchainSystemModelFilePath)

    return when (val networkTopology = designBlockchainSystem.network.topology) {
      is ExplicitNetworkTopology -> {
        ExplicitNetworkBlockchainSystemFactory(
          designBlockchainSystem,
          networkTopology
        )
      }

      is ConnectedSubgraphsNetworkTopology -> {
        ConnectedSubgraphNetworkBlockchainSystemFactory(
          designBlockchainSystem,
          networkTopology
        )
      }

      else -> throw IllegalArgumentException("Unknown network topology type: ${networkTopology::class.java.simpleName}")
    }
  }

}
