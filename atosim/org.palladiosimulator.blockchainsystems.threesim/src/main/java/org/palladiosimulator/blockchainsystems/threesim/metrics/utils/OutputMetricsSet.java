package org.palladiosimulator.blockchainsystems.threesim.metrics.utils;

import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;

import java.util.Comparator;
import java.util.TreeSet;

public class OutputMetricsSet extends TreeSet<OutputMetric<?>> {

    public OutputMetricsSet() {
        super(Comparator.comparing(OutputMetric::getName));
    }

    public static OutputMetricsSet from(OutputMetric<?>... metrics) {
        OutputMetricsSet set = new OutputMetricsSet();
        for (OutputMetric<?> m : metrics) set.add(m);
        return set;
    }
}
