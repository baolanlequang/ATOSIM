package org.palladiosimulator.blockchainsystems.threesim.creation.network.explicit;

import org.palladiosimulator.blockchainsystems.bscm.blockchainsystemComponentRepository.MiningProcessComponent;
import org.palladiosimulator.blockchainsystems.bscm.nodeallocation.NodeAllocationContext;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.ExplicitNetworkTopology;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.Node;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.ResourcePowerCalculator;

import java.util.HashMap;
import java.util.Map;

public class ExplicitNetworkResourcePowerCalculator implements ResourcePowerCalculator {

    private final Map<String, Double> _resourcePowerPerNode;
    private final double _globalResourcePower;

    public ExplicitNetworkResourcePowerCalculator(ExplicitNetworkTopology networkTopology) {
        _resourcePowerPerNode = new HashMap<>();
        double total = 0;
        for (Node node : networkTopology.getNodes()) {
            double power = getResourcePowerOfNode(node);
            _resourcePowerPerNode.put(node.getId(), power);
            total += power;
        }
        _globalResourcePower = total;
    }

    @Override
    public double calculateGlobalResourcePower() { return _globalResourcePower; }

    @Override
    public Double getResourcePowerOfNode(String nodeId) {
        return _resourcePowerPerNode.get(nodeId);
    }

    private double getResourcePowerOfNode(Node node) {
        double sum = 0;
        for (NodeAllocationContext ctx : node.getAllocation().getAllocationContexts()) {
            if (ctx.getAssemblyContext().getEncapsulatedComponent() instanceof MiningProcessComponent) {
                sum += ctx.getResourceContainer().getResourcePower();
            }
        }
        return sum;
    }
}
