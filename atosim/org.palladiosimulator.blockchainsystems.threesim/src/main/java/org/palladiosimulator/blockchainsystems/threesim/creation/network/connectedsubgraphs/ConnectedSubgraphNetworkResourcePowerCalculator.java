package org.palladiosimulator.blockchainsystems.threesim.creation.network.connectedsubgraphs;

import org.palladiosimulator.blockchainsystems.bscm.blockchainsystemComponentRepository.MiningProcessComponent;
import org.palladiosimulator.blockchainsystems.bscm.nodeallocation.NodeAllocation;
import org.palladiosimulator.blockchainsystems.bscm.nodeallocation.NodeAllocationContext;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.ConnectedSubgraphsNetworkTopology;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.SubgraphNodeTemplate;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.SubgraphSpecification;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.ResourcePowerCalculator;

import java.util.HashMap;
import java.util.Map;

public class ConnectedSubgraphNetworkResourcePowerCalculator implements ResourcePowerCalculator {

    private final Map<String, Double> _resourcePowerPerNodeTemplate = new HashMap<>();
    private final double _globalResourcePower;
    private final Map<String, String> _nodeIdToNodeTemplateIdMapping;

    public ConnectedSubgraphNetworkResourcePowerCalculator(
            ConnectedSubgraphsNetworkTopology topology,
            Map<String, String> nodeIdToNodeTemplateIdMapping) {
        _nodeIdToNodeTemplateIdMapping = nodeIdToNodeTemplateIdMapping;

        double total = 0;
        for (SubgraphSpecification subgraph : topology.getSubgraphs()) {
            for (SubgraphNodeTemplate template : subgraph.getNodeTemplates()) {
                double power = getResourcePowerOfAllocation(template.getAllocation());
                _resourcePowerPerNodeTemplate.put(template.getId(), power);
                total += template.getNumberOfNodeOccurences() * power;
            }
        }
        _globalResourcePower = total;
    }

    private double getResourcePowerOfAllocation(NodeAllocation allocation) {
        double sum = 0;
        for (NodeAllocationContext ctx : allocation.getAllocationContexts()) {
            if (ctx.getAssemblyContext().getEncapsulatedComponent() instanceof MiningProcessComponent) {
                sum += ctx.getResourceContainer().getResourcePower();
            }
        }
        return sum;
    }

    @Override
    public double calculateGlobalResourcePower() { return _globalResourcePower; }

    @Override
    public Double getResourcePowerOfNode(String nodeId) {
        String templateId = _nodeIdToNodeTemplateIdMapping.get(nodeId);
        return templateId != null ? _resourcePowerPerNodeTemplate.get(templateId) : null;
    }
}
