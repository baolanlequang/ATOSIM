package org.palladiosimulator.blockchainsystems.threesim.creation.network.connectedsubgraphs;

import org.jgrapht.graph.SimpleDirectedGraph;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.ConnectedSubgraphsNetworkTopology;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.SubgraphLink;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.SubgraphNodeTemplate;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.SubgraphSpecification;
import org.palladiosimulator.blockchainsystems.core.network.P2PLink;
import org.palladiosimulator.blockchainsystems.core.network.P2PNetworkImpl;
import org.palladiosimulator.blockchainsystems.core.network.P2PNode;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkCreationResult;
import org.palladiosimulator.blockchainsystems.core.utils.CounterMap;
import org.palladiosimulator.blockchainsystems.threesim.creation.network.AbstractThreesimP2PNetworkFactory;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;
import org.palladiosimulator.blockchainsystems.threesim.utils.JGraphExtensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class ConnectedSubgraphP2PNetworkFactory extends AbstractThreesimP2PNetworkFactory {

    private final ConnectedSubgraphsNetworkTopology _topology;

    public ConnectedSubgraphP2PNetworkFactory(ConnectedSubgraphsNetworkTopology topology,
            ThreesimSimulationParameters simulationParameters) {
        super(simulationParameters);
        _topology = topology;
    }

    @Override
    public P2PNetworkCreationResult createP2PNetwork() {
        HashMap<String, String> nodeIdToTemplateId = new HashMap<>();
        Map<String, Set<P2PNode>> subgraphNodes = new HashMap<>();
        Map<String, Set<P2PNode>> subgraphProxies = new HashMap<>();
        Map<String, SubgraphSpecification> subgraphSpecs = new HashMap<>();

        for (SubgraphSpecification subgraphSpec : _topology.getSubgraphs()) {
            String sgId = subgraphSpec.getId();
            subgraphNodes.put(sgId, new HashSet<>());
            subgraphProxies.put(sgId, new HashSet<>());
            subgraphSpecs.put(sgId, subgraphSpec);

            for (SubgraphNodeTemplate template : subgraphSpec.getNodeTemplates()) {
                for (int i = 0; i < template.getNumberOfNodeOccurences(); i++) {
                    String p2pNodeId = UUID.randomUUID().toString();
                    P2PNode node = new P2PNode(p2pNodeId);
                    subgraphNodes.get(sgId).add(node);
                    if (template.isIsSubgraphProxy()) subgraphProxies.get(sgId).add(node);
                    nodeIdToTemplateId.put(p2pNodeId, template.getId());
                }
            }
        }

        SimpleDirectedGraph<P2PNode, P2PLink> networkGraph = new SimpleDirectedGraph<>(P2PLink.class);

        for (Map.Entry<String, Set<P2PNode>> entry : subgraphNodes.entrySet()) {
            String sgId = entry.getKey();
            Set<P2PNode> nodes = entry.getValue();
            SubgraphSpecification spec = subgraphSpecs.get(sgId);
            CounterMap<P2PNode> initialDegrees = new CounterMap<>();

            for (P2PNode node : nodes) {
                networkGraph.addVertex(node);
                int degree = simulationParameters.getNodeDegree() > 0
                        ? simulationParameters.getNodeDegree() : spec.getConnectivity();
                initialDegrees.put(node, degree);
            }

            var latency = createLatencyValueProvider(spec.getLinkAllocation().getLatencySpecification());
            var throughput = createThroughputValueProvider(spec.getLinkAllocation().getThroughputSpecification());

            List<P2PNode> nodeList = new ArrayList<>(nodes);
            for (int i = 0; i + 1 < nodeList.size(); i++) {
                P2PNode a = nodeList.get(i), b = nodeList.get(i + 1);
                JGraphExtensions.addBidirectionalEdge(networkGraph, a, b,
                        (from, to) -> new P2PLink(latency, throughput, from, to));
                initialDegrees.decrement(a);
                initialDegrees.decrement(b);
            }

            P2PNode[] arr = nodes.toArray(new P2PNode[0]);
            Random rng = new Random();
            for (P2PNode current : arr) {
                while (initialDegrees.get(current) > 0) {
                    if (networkGraph.outDegreeOf(current) >= arr.length - 1) {
                        initialDegrees.decrement(current);
                        continue;
                    }
                    P2PNode selected = null;
                    for (int attempt = 0; attempt < arr.length; attempt++) {
                        P2PNode candidate = arr[rng.nextInt(arr.length)];
                        if (candidate != current && !networkGraph.containsEdge(current, candidate)) {
                            selected = candidate;
                            break;
                        }
                    }
                    if (selected == null) { initialDegrees.decrement(current); continue; }
                    JGraphExtensions.addBidirectionalEdge(networkGraph, current, selected,
                            (from, to) -> new P2PLink(latency, throughput, from, to));
                    initialDegrees.decrement(current);
                    initialDegrees.decrement(selected);
                }
            }
        }

        for (SubgraphLink subgraphLink : _topology.getSubgraphLinks()) {
            SubgraphSpecification sg1 = subgraphLink.getConnectedSubgraphs().get(0);
            SubgraphSpecification sg2 = subgraphLink.getConnectedSubgraphs().get(1);
            Set<P2PNode> proxies1 = subgraphProxies.getOrDefault(sg1.getId(), new HashSet<>());
            Set<P2PNode> proxies2 = subgraphProxies.getOrDefault(sg2.getId(), new HashSet<>());

            var latency = createLatencyValueProvider(subgraphLink.getAllocation().getLatencySpecification());
            var throughput = createThroughputValueProvider(subgraphLink.getAllocation().getThroughputSpecification());

            for (P2PNode p1 : proxies1) {
                for (P2PNode p2 : proxies2) {
                    JGraphExtensions.addBidirectionalEdge(networkGraph, p1, p2,
                            (from, to) -> new P2PLink(latency, throughput, from, to));
                }
            }
        }

        P2PNetworkImpl networkImpl = P2PNetworkImpl.create(networkGraph);
        for (P2PNode node : networkGraph.vertexSet()) node.initNetwork(networkImpl);

        return new ConnectedSubgraphNetworkCreationResult(networkImpl, nodeIdToTemplateId);
    }
}
