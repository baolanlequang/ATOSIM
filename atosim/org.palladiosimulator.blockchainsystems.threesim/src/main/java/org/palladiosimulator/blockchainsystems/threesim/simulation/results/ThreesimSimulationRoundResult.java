package org.palladiosimulator.blockchainsystems.threesim.simulation.results;

import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationRoundResult;
import org.palladiosimulator.blockchainsystems.threesim.creation.TopologyDeterminismInfo;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.OutputMetricsSet;

import java.util.List;

public class ThreesimSimulationRoundResult implements SimulationRoundResult {

    private final OutputMetricsSet outputMetrics;
    private final List<ChainReorganizationOccurrence> chainReorganizations;
    private final TopologyDeterminismInfo topologyDeterminismInfo;
    private final EpisodeStatus episodeStatus;
    // The specific recorded reorg occurrence that decides chainReorganizationDepths /
    // attackerCausedChainReorganizationDepths (see ThreesimSimulationMonitor.
    // getDecisiveAttackerReorg()) -- null whenever episodeStatus isn't SUCCESS, since there is
    // then no decisive attacker-caused occurrence to point at.
    private final ChainReorganizationOccurrence decisiveAttackerReorg;

    public ThreesimSimulationRoundResult(OutputMetricsSet outputMetrics, List<ChainReorganizationOccurrence> chainReorganizations) {
        this(outputMetrics, chainReorganizations, null, null, null);
    }

    public ThreesimSimulationRoundResult(OutputMetricsSet outputMetrics, List<ChainReorganizationOccurrence> chainReorganizations,
            TopologyDeterminismInfo topologyDeterminismInfo) {
        this(outputMetrics, chainReorganizations, topologyDeterminismInfo, null, null);
    }

    public ThreesimSimulationRoundResult(OutputMetricsSet outputMetrics, List<ChainReorganizationOccurrence> chainReorganizations,
            TopologyDeterminismInfo topologyDeterminismInfo, EpisodeStatus episodeStatus) {
        this(outputMetrics, chainReorganizations, topologyDeterminismInfo, episodeStatus, null);
    }

    public ThreesimSimulationRoundResult(OutputMetricsSet outputMetrics, List<ChainReorganizationOccurrence> chainReorganizations,
            TopologyDeterminismInfo topologyDeterminismInfo, EpisodeStatus episodeStatus,
            ChainReorganizationOccurrence decisiveAttackerReorg) {
        this.outputMetrics = outputMetrics;
        this.chainReorganizations = chainReorganizations;
        this.topologyDeterminismInfo = topologyDeterminismInfo;
        this.episodeStatus = episodeStatus;
        this.decisiveAttackerReorg = decisiveAttackerReorg;
    }

    public OutputMetricsSet getOutputMetrics() { return outputMetrics; }
    public List<ChainReorganizationOccurrence> getChainReorganizations() { return chainReorganizations; }
    public TopologyDeterminismInfo getTopologyDeterminismInfo() { return topologyDeterminismInfo; }
    public EpisodeStatus getEpisodeStatus() { return episodeStatus; }
    public ChainReorganizationOccurrence getDecisiveAttackerReorg() { return decisiveAttackerReorg; }
}
