package org.palladiosimulator.blockchainsystems.core.stochastics;

import java.util.random.RandomGenerator;

public class PoissonProcess {

    private final double _mean;
    private final RandomGenerator _randomGenerator;

    public PoissonProcess(double mean, RandomGenerator randomGenerator) {
        _mean = mean;
        _randomGenerator = randomGenerator;
    }

    public double getMean() {
        return _mean;
    }

    public long nextPointDistance() {
        double nextPointDistance = -Math.log(1.0 - _randomGenerator.nextDouble()) / _mean;
        return Math.round(nextPointDistance);
    }
}
