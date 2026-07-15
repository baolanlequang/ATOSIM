package org.palladiosimulator.blockchainsystems.threesim.simulation.results;

import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationRoundResult;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.OutputMetricsSet;

import java.util.List;

public class ThreesimSimulationRoundResult implements SimulationRoundResult {

    private final OutputMetricsSet outputMetrics;
    private final List<ChainReorganizationOccurrence> chainReorganizations;

    public ThreesimSimulationRoundResult(OutputMetricsSet outputMetrics, List<ChainReorganizationOccurrence> chainReorganizations) {
        this.outputMetrics = outputMetrics;
        this.chainReorganizations = chainReorganizations;
    }

    public OutputMetricsSet getOutputMetrics() { return outputMetrics; }
    public List<ChainReorganizationOccurrence> getChainReorganizations() { return chainReorganizations; }
}
