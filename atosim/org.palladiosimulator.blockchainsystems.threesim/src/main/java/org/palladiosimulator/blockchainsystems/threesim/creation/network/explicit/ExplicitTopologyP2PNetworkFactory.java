package org.palladiosimulator.blockchainsystems.threesim.creation.network.explicit;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.BidirectionalLink;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.ExplicitNetworkTopology;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.Link;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.Node;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.UnidirectionalLink;
import org.palladiosimulator.blockchainsystems.core.network.P2PLink;
import org.palladiosimulator.blockchainsystems.core.network.P2PNetworkImpl;
import org.palladiosimulator.blockchainsystems.core.network.P2PNode;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkCreationResult;
import org.palladiosimulator.blockchainsystems.threesim.creation.network.AbstractThreesimP2PNetworkFactory;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExplicitTopologyP2PNetworkFactory extends AbstractThreesimP2PNetworkFactory {

    private final ExplicitNetworkTopology _topology;

    public ExplicitTopologyP2PNetworkFactory(ExplicitNetworkTopology topology,
            ThreesimSimulationParameters simulationParameters) {
        super(simulationParameters);
        _topology = topology;
    }

    @Override
    public P2PNetworkCreationResult createP2PNetwork() {
        Graph<P2PNode, P2PLink> networkGraph = new SimpleDirectedGraph<>(P2PLink.class);

        Map<String, P2PNode> p2pNodeMappings = new HashMap<>();
        for (Node node : _topology.getNodes()) {
            P2PNode p2pNode = new P2PNode(node.getId());
            networkGraph.addVertex(p2pNode);
            p2pNodeMappings.put(node.getId(), p2pNode);
        }

        for (Link designLink : _topology.getLinks()) {
            var latencyProvider = createLatencyValueProvider(designLink.getAllocation().getLatencySpecification());
            var throughputProvider = createThroughputValueProvider(designLink.getAllocation().getThroughputSpecification());

            List<String[]> pairs = new ArrayList<>();
            if (designLink instanceof UnidirectionalLink u) {
                pairs.add(new String[]{u.getFromNode().getId(), u.getToNode().getId()});
            } else if (designLink instanceof BidirectionalLink b) {
                if (b.getConnectedNodes().size() != 2)
                    throw new IllegalArgumentException("Bidirectional link must connect exactly two nodes.");
                pairs.add(new String[]{b.getConnectedNodes().get(0).getId(), b.getConnectedNodes().get(1).getId()});
                pairs.add(new String[]{b.getConnectedNodes().get(1).getId(), b.getConnectedNodes().get(0).getId()});
            } else {
                throw new IllegalArgumentException("Unsupported link type: " + designLink.getClass().getName());
            }

            for (String[] pair : pairs) {
                P2PNode from = p2pNodeMappings.get(pair[0]);
                P2PNode to = p2pNodeMappings.get(pair[1]);
                if (from != null && to != null) {
                    networkGraph.addEdge(from, to, new P2PLink(latencyProvider, throughputProvider, from, to));
                }
            }
        }

        P2PNetworkImpl networkImpl = P2PNetworkImpl.create(networkGraph);
        for (P2PNode node : p2pNodeMappings.values()) node.initNetwork(networkImpl);

        return new ExplicitP2PNetworkCreationResult(networkImpl);
    }
}
