package org.palladiosimulator.blockchainsystems.threesim.simulation

import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationRound
import org.palladiosimulator.blockchainsystems.core.simulation.termination.LongestChainExceededMaxLengthCondition
import org.palladiosimulator.blockchainsystems.core.tracing.TraceEventLogOutput
import org.palladiosimulator.blockchainsystems.threesim.creation.ThreesimBlockchainSystemFactory
import org.palladiosimulator.blockchainsystems.threesim.creation.BlockchainSystemWithParameters
import org.palladiosimulator.blockchainsystems.threesim.monitoring.ThreesimSimulationMonitor
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.*

class ThreesimSimulationRound(
  blockchainSystemFactory: ThreesimBlockchainSystemFactory,
  logOutputs: Set<TraceEventLogOutput>,
  maxAllowedBlockchainLength: Long,
  originalParameters: ThreesimSimulationParameters,

  //  IMPORTANT: build the blockchain system ONCE and reuse it
  private val result: BlockchainSystemWithParameters =
    blockchainSystemFactory.createBlockchainSystem(originalParameters),

  ) : SimulationRound<ThreesimSimulationMonitor, ThreesimSimulationRoundResult>(
  result.system,
  logOutputs,
  monitor = ThreesimSimulationMonitor(
    LongestChainExceededMaxLengthCondition(maxAllowedBlockchainLength),
    originalParameters.failureThroughputThreshold,
    result.effectiveParameters
  )
) {

  private val effectiveParameters = result.effectiveParameters

  override fun createSimulationRoundResult(finalSystemTime: Long): ThreesimSimulationRoundResult {
    return ThreesimSimulationRoundResultFactory(
      effectiveParameters,
      monitor,
      finalSystemTime
    ).createSimulationRoundResult()
  }
}