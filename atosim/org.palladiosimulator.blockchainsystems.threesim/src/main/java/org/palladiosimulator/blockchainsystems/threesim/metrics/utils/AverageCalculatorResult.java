package org.palladiosimulator.blockchainsystems.threesim.metrics.utils;

public class AverageCalculatorResult {

    private final double average;
    private final double standardDeviation;
    private final Double coefficientOfVariation;

    public AverageCalculatorResult(double average, double standardDeviation, Double coefficientOfVariation) {
        this.average = average;
        this.standardDeviation = standardDeviation;
        this.coefficientOfVariation = coefficientOfVariation;
    }

    public double getAverage() { return average; }
    public double getStandardDeviation() { return standardDeviation; }
    public Double getCoefficientOfVariation() { return coefficientOfVariation; }
}
