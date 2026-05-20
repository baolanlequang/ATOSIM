package org.palladiosimulator.blockchainsystems.threesim_plugin

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.*
import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainSystem
import org.palladiosimulator.blockchainsystems.threesim.simulation.AttackType
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters

object AttackModelMapper {

    fun applyAttackScenario(
        base: ThreesimSimulationParameters,
        blockchainSystem: BlockchainSystem,
        scenario: AttackScenario
    ): ThreesimSimulationParameters {

        val attackType = mapAttackType(scenario.attack)

        val attackerNodeIds = scenario.attackers
            .mapNotNull { it.linkedNodeSystem?.id }
            .toSet()

        val attackerHashPower =
            scenario.attackers.sumOf { it.powerShare }.coerceIn(0.0, 1.0)

        val (deltaA, deltaB) =
            if (scenario.attack is RaceAttack) {
                val ra = scenario.attack as RaceAttack
                ra.transactionADelay to ra.transactionBAcceleration
            } else {
                0L to 0L
            }

        // IMPORTANT: create a NEW instance (no copy())
        return ThreesimSimulationParameters(
            failureThroughputThreshold = base.failureThroughputThreshold,
            shannonEntropyK = base.shannonEntropyK,
            nakamotoCoefficientThreshold = base.nakamotoCoefficientThreshold,
            reliabilityObservationTimespan = base.reliabilityObservationTimespan,

            attackType = attackType,
            attackerNodeIds = attackerNodeIds,
            attackerHashPower = attackerHashPower,
            gamma = base.gamma, // still from launch config / default
            deltaA = deltaA,
            deltaB = deltaB
        )
    }

    private fun mapAttackType(attack: Attack): AttackType =
        when (attack) {
            is SelfishMiningAttack -> AttackType.SELFISH_MINING
            is LeadStubbornAttack -> AttackType.LEAD_STUBBORN_MINING
            is EqualForkStubbornAttack -> AttackType.EQUAL_FORK_STUBBORN_MINING
            is TrailStubbornAttack -> AttackType.TRAIL_STUBBORN_MINING
            is FinneyAttack -> AttackType.FINNEY
            is RaceAttack -> AttackType.RACE
            is MajorityAttack -> AttackType.MAJORITY
            else -> AttackType.NONE
        }
}
