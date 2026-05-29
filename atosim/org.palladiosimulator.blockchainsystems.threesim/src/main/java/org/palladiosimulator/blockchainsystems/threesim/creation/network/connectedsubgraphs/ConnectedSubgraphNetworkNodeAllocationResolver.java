package org.palladiosimulator.blockchainsystems.threesim.creation.network.connectedsubgraphs;

import org.palladiosimulator.blockchainsystems.bscm.nodeallocation.NodeAllocation;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.ConnectedSubgraphsNetworkTopology;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.SubgraphNodeTemplate;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.SubgraphSpecification;
import org.palladiosimulator.blockchainsystems.threesim.creation.abstractions.NodeAllocationResolver;

import java.util.HashMap;
import java.util.Map;

public class ConnectedSubgraphNetworkNodeAllocationResolver implements NodeAllocationResolver {

    private final Map<String, SubgraphNodeTemplate> _nodeIdToNodeTemplatesMappings = new HashMap<>();

    public ConnectedSubgraphNetworkNodeAllocationResolver(
            ConnectedSubgraphsNetworkTopology connectedSubgraphsTopology,
            HashMap<String, String> nodeIdToNodeTemplateIdMapping) {

        Map<String, SubgraphNodeTemplate> nodeTemplatesByIds = new HashMap<>();
        for (SubgraphSpecification subgraph : connectedSubgraphsTopology.getSubgraphs()) {
            for (SubgraphNodeTemplate template : subgraph.getNodeTemplates()) {
                nodeTemplatesByIds.put(template.getId(), template);
            }
        }

        for (Map.Entry<String, String> entry : nodeIdToNodeTemplateIdMapping.entrySet()) {
            SubgraphNodeTemplate template = nodeTemplatesByIds.get(entry.getValue());
            if (template != null) {
                _nodeIdToNodeTemplatesMappings.put(entry.getKey(), template);
            }
        }
    }

    @Override
    public NodeAllocation getNodeAllocation(String nodeId) {
        SubgraphNodeTemplate template = _nodeIdToNodeTemplatesMappings.get(nodeId);
        return template != null ? template.getAllocation() : null;
    }
}
