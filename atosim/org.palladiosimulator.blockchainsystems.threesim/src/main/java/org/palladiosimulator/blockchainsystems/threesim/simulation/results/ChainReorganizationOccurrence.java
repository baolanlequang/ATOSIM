package org.palladiosimulator.blockchainsystems.threesim.simulation.results;

import org.palladiosimulator.blockchainsystems.core.blockchain.ChainReorganizedTraceEvent;

/**
 * A chain reorganization as observed by one node's local blockchain view during a simulation round.
 */
public class ChainReorganizationOccurrence {

    private final String nodeId;
    private final ChainReorganizedTraceEvent event;

    public ChainReorganizationOccurrence(String nodeId, ChainReorganizedTraceEvent event) {
        this.nodeId = nodeId;
        this.event = event;
    }

    public String getNodeId() { return nodeId; }
    public ChainReorganizedTraceEvent getEvent() { return event; }
}
