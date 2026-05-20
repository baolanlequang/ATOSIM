package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators

import kotlin.math.sqrt
import org.palladiosimulator.blockchainsystems.threesim.metrics.AttackSuccessTime
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl

class AttackSuccessTimeCalculator(
    private val successTime: Long?
) {

    fun calculate(): AttackSuccessTime {
        return AttackSuccessTime(successTime?.toDouble() ?: -1.0)
    }

    companion object {

        fun calculateAverage(values: List<AttackSuccessTime>): AverageOutputMetric {

            val validValues = values.filter { it.value >= 0 }

            if (validValues.isEmpty()) {
                return AverageOutputMetricImpl(
                    name = AttackSuccessTime.NAME,
                    average = -1.0,
                    unit = AttackSuccessTime.UNIT,
                    standardDeviation = 0.0,
                    coefficientOfVariation = null
                )
            }

            val avg = validValues.map { it.value }.average()

            val variance = validValues
                .map { (it.value - avg) * (it.value - avg) }
                .average()

            val std = sqrt(variance)

            val cv = if (avg == 0.0) null else std / avg

            return AverageOutputMetricImpl(
                name = AttackSuccessTime.NAME,
                average = avg,
                unit = AttackSuccessTime.UNIT,
                standardDeviation = std,
                coefficientOfVariation = cv
            )
        }
    }
}