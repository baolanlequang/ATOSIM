package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators

import org.palladiosimulator.blockchainsystems.threesim.metrics.RaceAttackSuccess
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl
import kotlin.math.sqrt

class RaceAttackSuccessCalculator(
    private val success: Boolean
) {

    fun calculate(): RaceAttackSuccess {
        return RaceAttackSuccess(success)
    }

    companion object {

        fun calculateAverage(values: List<RaceAttackSuccess>): AverageOutputMetric {

            val avg = values.map { it.value }.average()

            val variance = values
                .map { (it.value - avg) * (it.value - avg) }
                .average()

            val std = sqrt(variance)

            val cv = if (avg == 0.0) null else std / avg

            return AverageOutputMetricImpl(
                name = RaceAttackSuccess.NAME,
                average = avg,
                unit = null,
                standardDeviation = std,
                coefficientOfVariation = cv
            )
        }
    }
}