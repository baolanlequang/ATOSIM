package org.palladiosimulator.blockchainsystems.threesim.creation

import org.palladiosimulator.blockchainsystems.core.system.abstractions.ResourcePowerCalculator

class AttackAwareResourcePowerCalculator(
    private val delegate: ResourcePowerCalculator,
    private val attackerNodeIds: Set<String>,
    private val attackerHashPower: Double
) : ResourcePowerCalculator {

    init {
        require(attackerHashPower in 0.0..1.0) {
            "attackerHashPower must be in [0,1]"
        }
    }

    private val totalBasePower: Double by lazy {
        delegate.calculateGlobalResourcePower()
    }

    private val attackerBasePower: Double by lazy {
        var sum = 0.0
        for (id in attackerNodeIds) {
            sum += delegate.getResourcePowerOfNode(id) ?: 0.0
        }
        sum
    }

    private val honestBasePower: Double by lazy {
        totalBasePower - attackerBasePower
    }

    override fun getResourcePowerOfNode(nodeId: String): Double? {
        val basePower = delegate.getResourcePowerOfNode(nodeId) ?: return null
        val isAttacker = attackerNodeIds.contains(nodeId)

        // No total power in system
        if (totalBasePower == 0.0) {
            return 0.0
        }

        // Edge case: target attacker share = 0
        if (attackerHashPower == 0.0) {
            return if (isAttacker) 0.0 else {
                if (honestBasePower == 0.0) 0.0 else basePower * (totalBasePower / honestBasePower)
            }
        }

        // Edge case: target attacker share = 1
        if (attackerHashPower == 1.0) {
            return if (isAttacker) {
                if (attackerBasePower == 0.0) 0.0 else basePower * (totalBasePower / attackerBasePower)
            } else 0.0
        }

        // Impossible / inconsistent cases
        if (attackerHashPower > 0.0 && attackerBasePower == 0.0) {
            error("Configured attackerHashPower=$attackerHashPower but attacker nodes have zero base resource power")
        }

        if (attackerHashPower < 1.0 && honestBasePower == 0.0) {
            error("Configured attackerHashPower=$attackerHashPower but honest nodes have zero base resource power")
        }

        val attackerScale = (attackerHashPower * totalBasePower) / attackerBasePower
        val honestScale = ((1.0 - attackerHashPower) * totalBasePower) / honestBasePower

        return if (isAttacker) {
            basePower * attackerScale
        } else {
            basePower * honestScale
        }
    }

    override fun calculateGlobalResourcePower(): Double {
        // Preserved by construction
        return totalBasePower
    }
}