package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators

import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt
import org.palladiosimulator.blockchainsystems.threesim.metrics.DoubleSpendSuccessProbability
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl

class DoubleSpendSuccessProbabilityCalculator(
    private val attackerHashPower: Double,
    private val confirmationDepth: Int
) {

    fun calculate(): DoubleSpendSuccessProbability {

        val q = attackerHashPower
        val p = 1 - q

        if (q >= p) {
            return DoubleSpendSuccessProbability(1.0)
        }

        val lambda = confirmationDepth * (q / p)

        var sum = 0.0

        for (k in 0..confirmationDepth) {

            val poisson = poisson(lambda, k)

            val term = 1 - (q / p).pow((confirmationDepth - k).toDouble())

            sum += poisson * term
        }

        val probability = 1 - sum

        return DoubleSpendSuccessProbability(probability)
    }

    private fun poisson(lambda: Double, k: Int): Double {
        return exp(-lambda) * lambda.pow(k.toDouble()) / factorial(k)
    }

    private fun factorial(n: Int): Double {
        var result = 1.0
        for (i in 2..n) result *= i
        return result
    }

    companion object {

        fun calculateAverage(values: List<DoubleSpendSuccessProbability>): AverageOutputMetric {

            val avg = values.map { it.value }.average()

            val variance = values
                .map { (it.value - avg) * (it.value - avg) }
                .average()

            val std = sqrt(variance)

            val cv = if (avg == 0.0) null else std / avg

            return AverageOutputMetricImpl(
                name = DoubleSpendSuccessProbability.NAME,
                average = avg,
                unit = DoubleSpendSuccessProbability.UNIT,
                standardDeviation = std,
                coefficientOfVariation = cv
            )
        }
    }
}