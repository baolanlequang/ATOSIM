package org.palladiosimulator.blockchainsystems.threesim.creation.network.explicit;

import org.palladiosimulator.blockchainsystems.bscm.nodeallocation.NodeAllocation;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.ExplicitNetworkTopology;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.Node;
import org.palladiosimulator.blockchainsystems.threesim.creation.abstractions.NodeAllocationResolver;

import java.util.HashMap;
import java.util.Map;

public class ExplicitNetworkNodeAllocationResolver implements NodeAllocationResolver {

    private final Map<String, Node> _nodeIdToNodeMappings = new HashMap<>();

    public ExplicitNetworkNodeAllocationResolver(ExplicitNetworkTopology networkTopology) {
        for (Node node : networkTopology.getNodes()) {
            _nodeIdToNodeMappings.put(node.getId(), node);
        }
    }

    @Override
    public NodeAllocation getNodeAllocation(String nodeId) {
        Node node = _nodeIdToNodeMappings.get(nodeId);
        return node != null ? node.getAllocation() : null;
    }
}
