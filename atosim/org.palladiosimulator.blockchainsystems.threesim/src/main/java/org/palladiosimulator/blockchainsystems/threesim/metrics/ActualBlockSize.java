package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;

/** Item 7: mean actual packed size (bytes) across every block mined this round -- distinct from
 * the configured maxBlockSize cap already reported in threesimSimulationParameters. */
public class ActualBlockSize implements OutputMetric<Double> {
    public static final String NAME = "Mean Actual Block Size";
    public static final String UNIT = "bytes";
    private final double _value;
    public ActualBlockSize(double value) { _value = value; }
    @Override public Double getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
