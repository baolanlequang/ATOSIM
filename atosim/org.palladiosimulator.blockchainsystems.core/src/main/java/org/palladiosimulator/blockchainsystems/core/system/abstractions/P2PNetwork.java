package org.palladiosimulator.blockchainsystems.core.system.abstractions;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationLifecycleAware;

import java.util.Set;

public interface P2PNetwork extends SimulationLifecycleAware {
    Set<NodeP2PNetworkInterface> getNodes();
    Set<P2PNetworkEndpoint> getNeighbors(NodeP2PNetworkInterface networkInterface);
    void multicast(NodeP2PNetworkInterface sendingNetworkInterface, Message content);
    void send(NodeP2PNetworkInterface sendingNetworkInterface, NodeP2PNetworkInterface receivingNetworkInterface, Message content);
}
