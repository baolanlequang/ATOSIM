package org.palladiosimulator.blockchainsystems.threesim.creation.network.connectedsubgraphs;

import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetwork;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkCreationResult;

import java.util.HashMap;

public class ConnectedSubgraphNetworkCreationResult implements P2PNetworkCreationResult {

    private final P2PNetwork _createdNetwork;
    private final HashMap<String, String> _nodeIdToNodeTemplateIdMapping;
    private final HashMap<String, Integer> _nodeIdToIndex;
    private final String _topologyId;

    public ConnectedSubgraphNetworkCreationResult(P2PNetwork createdNetwork,
            HashMap<String, String> nodeIdToNodeTemplateIdMapping,
            HashMap<String, Integer> nodeIdToIndex,
            String topologyId) {
        _createdNetwork = createdNetwork;
        _nodeIdToNodeTemplateIdMapping = nodeIdToNodeTemplateIdMapping;
        _nodeIdToIndex = nodeIdToIndex;
        _topologyId = topologyId;
    }

    @Override
    public P2PNetwork getCreatedNetwork() { return _createdNetwork; }

    public HashMap<String, String> getNodeIdToNodeTemplateIdMapping() {
        return _nodeIdToNodeTemplateIdMapping;
    }

    /** Stable 0..N-1 index per node's endpoint id, assigned in deterministic creation order. */
    public HashMap<String, Integer> getNodeIdToIndex() { return _nodeIdToIndex; }

    /** Identifier for this replication's realized topology (a function of its root seed). */
    public String getTopologyId() { return _topologyId; }
}
