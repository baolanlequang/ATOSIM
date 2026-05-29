package org.palladiosimulator.blockchainsystems.threesim.creation.network.connectedsubgraphs;

import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetwork;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkCreationResult;

import java.util.HashMap;

public class ConnectedSubgraphNetworkCreationResult implements P2PNetworkCreationResult {

    private final P2PNetwork _createdNetwork;
    private final HashMap<String, String> _nodeIdToNodeTemplateIdMapping;

    public ConnectedSubgraphNetworkCreationResult(P2PNetwork createdNetwork,
            HashMap<String, String> nodeIdToNodeTemplateIdMapping) {
        _createdNetwork = createdNetwork;
        _nodeIdToNodeTemplateIdMapping = nodeIdToNodeTemplateIdMapping;
    }

    @Override
    public P2PNetwork getCreatedNetwork() { return _createdNetwork; }

    public HashMap<String, String> getNodeIdToNodeTemplateIdMapping() {
        return _nodeIdToNodeTemplateIdMapping;
    }
}
