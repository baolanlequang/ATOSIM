package org.palladiosimulator.blockchainsystems.threesim.metrics;

import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;

public class FinneyAttackSuccess implements OutputMetric<Integer> {

    public static final String NAME = "Finney Attack Success";

    private final boolean _succeeded;

    public FinneyAttackSuccess(boolean succeeded) {
        _succeeded = succeeded;
    }

    @Override public Integer getValue() { return _succeeded ? 1 : 0; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return null; }
}
