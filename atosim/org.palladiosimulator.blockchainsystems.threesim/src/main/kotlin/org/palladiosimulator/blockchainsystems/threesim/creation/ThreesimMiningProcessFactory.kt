package org.palladiosimulator.blockchainsystems.threesim.creation

import org.palladiosimulator.blockchainsystems.core.mining.MiningProcessImpl
import org.palladiosimulator.blockchainsystems.core.system.abstractions.MiningProcess
import org.palladiosimulator.blockchainsystems.core.system.abstractions.MiningProcessFactory
import org.palladiosimulator.blockchainsystems.core.system.abstractions.ResourcePowerCalculator
import java.util.random.RandomGenerator

/**
 * Factory for creating a [MiningProcess] for a blockchain node in 3SIM.
 *
 * [meanBlockTime] is the global average block interval in milliseconds.
 * Each node receives a local mean arrival time scaled by its resource-power share.
 *
 * Example:
 * - global meanBlockTime = 600000.0 means 10 minutes
 * - global meanBlockTime = 60000.0 means 1 minute
 *
 * @author Davis Riedel
 */
class ThreesimMiningProcessFactory(
  private val meanBlockTime: Double,
  private val resourcePowerCalculator: ResourcePowerCalculator
) : MiningProcessFactory {

  override fun createMiningProcess(nodeId: String): MiningProcess {
    require(meanBlockTime > 0.0) {
      "meanBlockTime must be > 0, but was $meanBlockTime"
    }

    val nodeResourcePower = resourcePowerCalculator.getResourcePowerOfNode(nodeId)
      ?: throw IllegalArgumentException(
        "Node with ID $nodeId does not have a defined resource power."
      )

    require(nodeResourcePower > 0.0) {
      "Node with ID $nodeId has non-positive resource power: $nodeResourcePower"
    }

    val globalResourcePower = resourcePowerCalculator.calculateGlobalResourcePower()

    require(globalResourcePower > 0.0) {
      "Global resource power must be > 0, but was $globalResourcePower"
    }

    val nodeResourcePowerShare = nodeResourcePower / globalResourcePower

    require(nodeResourcePowerShare > 0.0) {
      "Node with ID $nodeId has non-positive resource power share: $nodeResourcePowerShare"
    }

    val nodeAverageBlockArrivalTimeMillis = meanBlockTime / nodeResourcePowerShare

    return MiningProcessImpl(
      nodeAverageBlockArrivalTimeMillis,
      RandomGenerator.of("Random")
    )
  }
}