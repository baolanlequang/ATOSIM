package org.palladiosimulator.blockchainsystems.core.network;

import org.jgrapht.Graph;
import org.palladiosimulator.blockchainsystems.core.common.BlockchainSimulationObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.Message;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.NodeP2PNetworkInterface;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetwork;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkEndpoint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class P2PNetworkImpl extends BlockchainSimulationObject implements P2PNetwork {

    private final Graph<P2PNode, P2PLink> _networkGraph;

    private P2PNetworkImpl(String id, Graph<P2PNode, P2PLink> networkGraph, String name) {
        super(id, name);
        _networkGraph = networkGraph;
    }

    @Override
    public void dispatchEvent(Event event) {
    }

    @Override
    public void multicast(NodeP2PNetworkInterface sendingNetworkInterface, Message content) {
        for (P2PLink link : _networkGraph.outgoingEdgesOf((P2PNode) sendingNetworkInterface)) {
            link.send(content);
        }
    }

    @Override
    protected void onInitialize() {
        for (P2PNode node : _networkGraph.vertexSet()) {
            node.initialize(getSimulationContext());
        }
        for (P2PLink link : _networkGraph.edgeSet()) {
            link.initialize(getSimulationContext());
        }
    }

    @Override
    protected void onCleanup() {
        for (P2PLink link : _networkGraph.edgeSet()) {
            link.cleanup();
        }
        for (P2PNode node : _networkGraph.vertexSet()) {
            node.cleanup();
        }
    }

    @Override
    public Set<NodeP2PNetworkInterface> getNodes() {
        return Collections.unmodifiableSet(_networkGraph.vertexSet());
    }

    @Override
    public void send(NodeP2PNetworkInterface sendingNetworkInterface, NodeP2PNetworkInterface receivingNetworkInterface, Message content) {
        P2PLink link = _networkGraph.getEdge((P2PNode) sendingNetworkInterface, (P2PNode) receivingNetworkInterface);
        link.send(content);
    }

    @Override
    public Set<P2PNetworkEndpoint> getNeighbors(NodeP2PNetworkInterface networkInterface) {
        return _networkGraph.outgoingEdgesOf((P2PNode) networkInterface)
                .stream()
                .map(link -> (P2PNetworkEndpoint) link.getToNode())
                .collect(Collectors.toCollection(HashSet::new));
    }

    public static P2PNetworkImpl create(Graph<P2PNode, P2PLink> networkGraph) {
        String id = UUID.randomUUID().toString();
        return new P2PNetworkImpl(id, networkGraph, "P2PNetwork_" + networkGraph.hashCode());
    }
}
