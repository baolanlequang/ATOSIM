package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators

import org.palladiosimulator.blockchainsystems.threesim.metrics.FinneyAttackSuccess
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl
import kotlin.math.sqrt

class FinneyAttackSuccessCalculator(
    private val success: Boolean
) {

    fun calculate(): FinneyAttackSuccess {
        return FinneyAttackSuccess(success)
    }

    companion object {

        fun calculateAverage(values: List<FinneyAttackSuccess>): AverageOutputMetric {

            val avg = values.map { it.value }.average()

            val variance = values
                .map { (it.value - avg) * (it.value - avg) }
                .average()

            val std = sqrt(variance)

            val cv = if (avg == 0.0) null else std / avg

            return AverageOutputMetricImpl(
                name = FinneyAttackSuccess.NAME,
                average = avg,
                unit = null,
                standardDeviation = std,
                coefficientOfVariation = cv
            )
        }
    }
}