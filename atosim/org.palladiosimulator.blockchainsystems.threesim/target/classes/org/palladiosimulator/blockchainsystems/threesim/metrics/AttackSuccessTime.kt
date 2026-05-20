package org.palladiosimulator.blockchainsystems.threesim.metrics

import kotlinx.serialization.Serializable
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric

@Serializable
class AttackSuccessTime(
    override val value: Double
) : OutputMetric<Double> {

    companion object {
        const val NAME = "AttackSuccessTime"
        const val UNIT = "ms"
    }

    override val name: String = NAME
    override val unit: String? = UNIT
}