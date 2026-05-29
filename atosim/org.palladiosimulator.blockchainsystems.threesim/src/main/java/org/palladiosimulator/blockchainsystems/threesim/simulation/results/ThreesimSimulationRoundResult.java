package org.palladiosimulator.blockchainsystems.threesim.simulation.results;

import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationRoundResult;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.OutputMetricsSet;

public class ThreesimSimulationRoundResult implements SimulationRoundResult {

    private final OutputMetricsSet outputMetrics;

    public ThreesimSimulationRoundResult(OutputMetricsSet outputMetrics) {
        this.outputMetrics = outputMetrics;
    }

    public OutputMetricsSet getOutputMetrics() { return outputMetrics; }
}
