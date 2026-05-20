package org.palladiosimulator.blockchainsystems.threesim.behavior

import org.palladiosimulator.blockchainsystems.core.behavior.CombinedSelfishFinneyNodeBehavior
import org.palladiosimulator.blockchainsystems.core.behavior.CombinedSelfishRaceNodeBehavior
import org.palladiosimulator.blockchainsystems.core.behavior.EqualForkStubbornMiningNodeBehavior
import org.palladiosimulator.blockchainsystems.core.behavior.FinneyMiningNodeBehavior
import org.palladiosimulator.blockchainsystems.core.behavior.GammaAwareHonestBlockchainSystemNodeBehavior
import org.palladiosimulator.blockchainsystems.core.behavior.HonestBlockchainSystemNodeBehavior
import org.palladiosimulator.blockchainsystems.core.behavior.LeadStubbornMiningNodeBehavior
import org.palladiosimulator.blockchainsystems.core.behavior.RaceMiningNodeBehavior
import org.palladiosimulator.blockchainsystems.core.behavior.SelfishMiningNodeBehavior
import org.palladiosimulator.blockchainsystems.core.behavior.TrailStubbornMiningNodeBehavior
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeBehavior
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeBehaviorFactory
import org.palladiosimulator.blockchainsystems.threesim.simulation.AttackType
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters

/**
 * Factory for creating a [BlockchainSystemNodeBehavior] for the Threesim blockchain system.
 *
 * This factory creates an instance of [HonestBlockchainSystemNodeBehavior], which represents a node that behaves honestly
 * in the blockchain system.
 *
 * @author Davis Riedel
 */
class ThreesimBlockchainSystemNodeBehaviorFactory(
  private val simulationParameters: ThreesimSimulationParameters
) : BlockchainSystemNodeBehaviorFactory {

  override fun create(nodeId: String): BlockchainSystemNodeBehavior {
    val isAttacker = simulationParameters.attackerNodeIds.contains(nodeId)

    if (isAttacker) {
      when (simulationParameters.attackType) {

        AttackType.RACE ->
          return RaceMiningNodeBehavior()

        AttackType.MAJORITY ->
          return HonestBlockchainSystemNodeBehavior()

        AttackType.SELFISH_MINING ->
          return SelfishMiningNodeBehavior()

        AttackType.LEAD_STUBBORN_MINING ->
          return LeadStubbornMiningNodeBehavior()

        AttackType.EQUAL_FORK_STUBBORN_MINING ->
          return EqualForkStubbornMiningNodeBehavior()

        AttackType.TRAIL_STUBBORN_MINING ->
          return TrailStubbornMiningNodeBehavior()

        AttackType.FINNEY ->
          return FinneyMiningNodeBehavior()

        AttackType.COMBINED_SELFISH_RACE ->
          return CombinedSelfishRaceNodeBehavior()

        AttackType.COMBINED_SELFISH_FINNEY ->
          return CombinedSelfishFinneyNodeBehavior()

        else ->
          return HonestBlockchainSystemNodeBehavior()
      }
    }

    // Honest nodes
    when (simulationParameters.attackType) {
      AttackType.SELFISH_MINING,
      AttackType.LEAD_STUBBORN_MINING,
      AttackType.EQUAL_FORK_STUBBORN_MINING,
      AttackType.TRAIL_STUBBORN_MINING,
      AttackType.RACE,
      AttackType.COMBINED_SELFISH_RACE,
      AttackType.COMBINED_SELFISH_FINNEY,
      AttackType.MAJORITY ->
        return GammaAwareHonestBlockchainSystemNodeBehavior(
          simulationParameters.attackerNodeIds,
          simulationParameters.gamma
        )

      else ->
        return HonestBlockchainSystemNodeBehavior()
    }
  }

}