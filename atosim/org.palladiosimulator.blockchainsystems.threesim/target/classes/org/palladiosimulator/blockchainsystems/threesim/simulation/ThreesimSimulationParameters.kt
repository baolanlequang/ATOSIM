package org.palladiosimulator.blockchainsystems.threesim.simulation

import kotlinx.serialization.Serializable

/**
 * Configurable parameters for the 3SIM simulation.
 *
 * @author Davis Riedel
 */
@Serializable
class ThreesimSimulationParameters(
  val failureThroughputThreshold: Double, // trx / s
  val shannonEntropyK: Double, // 0.0..1.0
  val nakamotoCoefficientThreshold: Double, // 0.0 .. 100.0 %
  val reliabilityObservationTimespan: Double, // hours

  //Attack Parameters
  val attackType: AttackType = AttackType.NONE,
  val combinedAttackEnabled: Boolean = false,
  val secondaryAttackType: AttackType = AttackType.NONE,

  //ID of attacker nodes
  val attackerNodeIds: Set<String> = emptySet<String>(),

  //Attacker hash power share
  val attackerHashPower: Double = 0.0, //0.0 .. 1.0

  //Gamma parameter for selfish/stubborn mining
  val gamma: Double = 0.0, // 0.0 .. 1.0

  // Race attack timing
  val deltaA: Long = 0L, //victim tx delay in ms
  val deltaB: Long = 0L, // attacker tx acceleration in ms
  val confirmationDepth: Int = 6, // double spend parameter
  val blockInterval: Double = 6000.0, // milliseconds
  val propagationDelay: Double = 0.0, //milliseconds
  val nodeDegree: Int = 8, // number of neighbors per node
  val maxBlockSize: Int = 1_000_000, // bytes
  val networkBandwidth: Double = 100.0// mbps
)