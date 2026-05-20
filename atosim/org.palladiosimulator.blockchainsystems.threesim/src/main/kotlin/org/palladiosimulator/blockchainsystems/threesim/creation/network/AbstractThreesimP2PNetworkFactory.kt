package org.palladiosimulator.blockchainsystems.threesim.creation.network

import org.palladiosimulator.blockchainsystems.bscm.linkallocation.DynamicLinkLatencySpecification
import org.palladiosimulator.blockchainsystems.bscm.linkallocation.DynamicLinkThroughputSpecification
import org.palladiosimulator.blockchainsystems.bscm.linkallocation.LinkLatencySpecification
import org.palladiosimulator.blockchainsystems.bscm.linkallocation.LinkThroughputSpecification
import org.palladiosimulator.blockchainsystems.bscm.linkallocation.StaticLinkLatencySpecification
import org.palladiosimulator.blockchainsystems.bscm.linkallocation.StaticLinkThroughputSpecification
import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationLifecycleAwareValueProvider
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkFactory
import org.palladiosimulator.blockchainsystems.threesim.creation.LatencyValueProviderAdapter
import org.palladiosimulator.blockchainsystems.threesim.creation.StaticLatencyValueProvider
import org.palladiosimulator.blockchainsystems.threesim.creation.StaticThroughputValueProvider
import org.palladiosimulator.blockchainsystems.threesim.creation.ThroughputValueProviderAdapter
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters
import java.util.random.RandomGenerator

/**
 * Abstract factory for creating a P2P network in 3SIM. Stores common methods for explicit and connected subgraphs network factories.
 *
 * @author Davis Riedel
 */
abstract class AbstractThreesimP2PNetworkFactory(
  protected val simulationParameters: ThreesimSimulationParameters
) : P2PNetworkFactory {

  protected fun createLatencyValueProvider(
    latencySpecification: LinkLatencySpecification
  ): SimulationLifecycleAwareValueProvider<Long> {
    if (simulationParameters.propagationDelay > 0) {
      return StaticLatencyValueProvider(
        (simulationParameters.propagationDelay * 1000.0).toLong()
      )
    }
    return when (latencySpecification) {
      is StaticLinkLatencySpecification -> {
        StaticLatencyValueProvider(latencySpecification.latency)
      }

      is DynamicLinkLatencySpecification -> {
        LatencyValueProviderAdapter.create(
          latencySpecification,
          RandomGenerator.of("Random")
        )
      }

      else -> {
        throw IllegalArgumentException(
          "Unsupported latency specification type: ${latencySpecification::class.java.name}"
        )
      }
    }
  }

  protected fun createThroughputValueProvider(
    throughputSpecification: LinkThroughputSpecification
  ): SimulationLifecycleAwareValueProvider<Long> {
    if (simulationParameters.networkBandwidth > 0.0) {
      val throughputBitsPerSecond =
        (simulationParameters.networkBandwidth * 1_000_000.0).toLong()

      return StaticThroughputValueProvider(throughputBitsPerSecond)
    }
    return when (throughputSpecification) {
      is StaticLinkThroughputSpecification -> {
        StaticThroughputValueProvider(throughputSpecification.throughput)
      }

      is DynamicLinkThroughputSpecification -> {
        ThroughputValueProviderAdapter.create(
          throughputSpecification,
          RandomGenerator.of("Random")
        )
      }

      else -> {
        throw IllegalArgumentException(
          "Unsupported throughput specification type: ${throughputSpecification::class.java.name}"
        )
      }
    }
  }

}