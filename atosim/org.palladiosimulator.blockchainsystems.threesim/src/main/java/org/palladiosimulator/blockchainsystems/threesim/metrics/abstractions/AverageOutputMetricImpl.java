package org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions;

public class AverageOutputMetricImpl implements AverageOutputMetric {

    private final String name;
    private final double average;
    private final String unit;
    private final double standardDeviation;
    private final Double coefficientOfVariation;

    public AverageOutputMetricImpl(String name, double average, String unit,
            double standardDeviation, Double coefficientOfVariation) {
        this.name = name;
        this.average = average;
        this.unit = unit;
        this.standardDeviation = standardDeviation;
        this.coefficientOfVariation = coefficientOfVariation;
    }

    public String getName() { return name; }
    public double getAverage() { return average; }
    public String getUnit() { return unit; }
    public double getStandardDeviation() { return standardDeviation; }
    public Double getCoefficientOfVariation() { return coefficientOfVariation; }
}
