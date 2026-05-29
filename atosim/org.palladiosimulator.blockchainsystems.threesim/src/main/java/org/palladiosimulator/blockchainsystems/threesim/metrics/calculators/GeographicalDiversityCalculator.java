package org.palladiosimulator.blockchainsystems.threesim.metrics.calculators;

import org.palladiosimulator.blockchainsystems.threesim.metrics.GeographicalDiversity;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetric;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.AverageOutputMetricImpl;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetricCalculator;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.AverageCalculator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GeographicalDiversityCalculator implements OutputMetricCalculator<GeographicalDiversity> {
    private final int numberOfNodes;
    private final int numberOfRegions;
    private final Collection<Integer> numberOfNodesPerRegion;

    public GeographicalDiversityCalculator(int numberOfNodes, int numberOfRegions, Collection<Integer> numberOfNodesPerRegion) {
        this.numberOfNodes = numberOfNodes;
        this.numberOfRegions = numberOfRegions;
        this.numberOfNodesPerRegion = numberOfNodesPerRegion;
    }

    @Override
    public GeographicalDiversity calculate() {
        double gTarget = calculateGAux(numberOfNodesPerRegion.stream().map(Integer::doubleValue).collect(Collectors.toList()));
        double gExcl = calculateGAux(List.of((double) numberOfNodes));
        List<Double> equal = new ArrayList<>();
        for (int i = 0; i < numberOfRegions; i++) equal.add((double) numberOfNodes / numberOfRegions);
        double gEqual = calculateGAux(equal);
        return new GeographicalDiversity((gExcl - gTarget - gEqual) / (gExcl - gEqual));
    }

    private double calculateGAux(List<Double> nodesPerRegion) {
        double r = numberOfRegions;
        long regionsWithNodes = nodesPerRegion.stream().filter(v -> v > 0).count();
        double a = Math.log(r) / Math.log(regionsWithNodes + 1.0);
        double b = Math.log(r) / Math.log(r + 1.0);
        double c = Math.log(r) / Math.log(2.0);
        double first = 2.0 - (a - b) / (c - b);
        double mu = (double) numberOfNodes / r;
        double sum = 0;
        for (int i = 0; i < numberOfRegions; i++) {
            double n = (i < nodesPerRegion.size()) ? nodesPerRegion.get(i) : 0.0;
            sum += Math.pow(n - mu, 2);
        }
        double second = Math.sqrt(sum / r);
        return first * second;
    }

    public static AverageOutputMetric calculateAverage(List<GeographicalDiversity> measurements) {
        var result = AverageCalculator.calculate(measurements.stream().map(m -> m.getValue()).collect(Collectors.toList()));
        return new AverageOutputMetricImpl(GeographicalDiversity.NAME, result.getAverage(), GeographicalDiversity.UNIT, result.getStandardDeviation(), result.getCoefficientOfVariation());
    }
}
