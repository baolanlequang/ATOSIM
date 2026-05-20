package org.palladiosimulator.blockchainsystems.threesim.metrics

import kotlinx.serialization.Serializable
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric

@Serializable
class FinneyAttackSuccess(
    private val succeeded: Boolean
) : OutputMetric<Int> {

    companion object {
        const val NAME = "Finney Attack Success"
    }

    override val value: Int
        get() = if (succeeded) 1 else 0

    override val name: String = NAME
    override val unit: String? = null
}