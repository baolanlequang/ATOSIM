package org.palladiosimulator.blockchainsystems.threesim.metrics

import kotlinx.serialization.Serializable
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric

@Serializable
class DoubleSpendSuccessProbability (
    override val value: Double
) : OutputMetric<Double> {

    companion object {
        const val NAME = "DoubleSpendSuccessProbability"
        val UNIT: String? = null
    }

    override val name: String = NAME
    override val unit: String? = UNIT
}