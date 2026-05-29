package org.palladiosimulator.blockchainsystems.threesim.creation.abstractions;

import org.palladiosimulator.blockchainsystems.bscm.nodeallocation.NodeAllocation;

public interface NodeAllocationResolver {
    NodeAllocation getNodeAllocation(String nodeId);
}
