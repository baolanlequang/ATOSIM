package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators

import org.palladiosimulator.blockchainsystems.threesim.metrics.ForkProbability
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl
import kotlin.math.exp
import kotlin.math.sqrt

class ForkProbabilityCalculator(
    private val lambdaH: Double,
    private val delta: Double
) {

    fun calculate(): ForkProbability {

        val pFork = 1 - exp(-lambdaH * delta)

        return ForkProbability(pFork)
    }

    companion object {

        fun calculateAverage(values: List<ForkProbability>): AverageOutputMetric {

            val avg = values.map { it.value }.average()

            val variance = values
                .map { (it.value - avg) * (it.value - avg) }
                .average()

            val std = sqrt(variance)

            val cv = if (avg == 0.0) null else std / avg

            return AverageOutputMetricImpl(
                name = ForkProbability.NAME,
                average = avg,
                unit = ForkProbability.UNIT,
                standardDeviation = std,
                coefficientOfVariation = cv
            )
        }
    }
}