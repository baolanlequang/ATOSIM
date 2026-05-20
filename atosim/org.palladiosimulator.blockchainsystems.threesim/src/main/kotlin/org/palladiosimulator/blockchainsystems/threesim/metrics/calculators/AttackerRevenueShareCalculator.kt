package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators

import org.palladiosimulator.blockchainsystems.threesim.metrics.AttackerRevenueShare
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl
import kotlin.math.sqrt

class AttackerRevenueShareCalculator(
    private val attackerRewards: Double,
    private val totalRewards: Double
) {

    fun calculate(): AttackerRevenueShare {

        val value =
            if (totalRewards == 0.0) 0.0
            else (attackerRewards / totalRewards) * 100.0

        return AttackerRevenueShare(value)
    }

    companion object {

        fun calculateAverage(values: List<AttackerRevenueShare>): AverageOutputMetric {

            val avg = values.map { it.value }.average()

            val variance = values
                .map { (it.value - avg) * (it.value - avg) }
                .average()

            val std = sqrt(variance)

            val cv = if (avg == 0.0) null else std / avg

            return AverageOutputMetricImpl(
                name = AttackerRevenueShare.NAME,
                average = avg,
                unit = AttackerRevenueShare.UNIT,
                standardDeviation = std,
                coefficientOfVariation = cv
            )
        }
    }
}