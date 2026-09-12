package org.palladiosimulator.blockchainsystems.threesim.creation.network.connectedsubgraphs;

import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;
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
import org.palladiosimulator.blockchainsystems.threesim.simulation.DeterministicSeeds;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;
import org.palladiosimulator.blockchainsystems.threesim.utils.JGraphExtensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class ConnectedSubgraphP2PNetworkFactory extends AbstractThreesimP2PNetworkFactory {

    // Item 2 (D6-confirmed ranges): N (total node count across all subgraphs) and d (target
    // node degree) validity bounds. Enforced here rather than at ThreesimSimulationParameters
    // construction because that class has no node-count field at all -- N only ever exists as a
    // concept once the topology's NodeTemplates are summed, which happens here.
    private static final int MIN_NODE_COUNT = 20;
    private static final int MAX_NODE_COUNT = 1000;
    private static final int MIN_NODE_DEGREE = 2;
    private static final int MAX_NODE_DEGREE = 8;

    private final ConnectedSubgraphsNetworkTopology _topology;

    public ConnectedSubgraphP2PNetworkFactory(ConnectedSubgraphsNetworkTopology topology,
            ThreesimSimulationParameters simulationParameters, long rootSeed) {
        super(simulationParameters, rootSeed);
        _topology = topology;
    }

    @Override
    public P2PNetworkCreationResult createP2PNetwork() {
        int totalNodeCount = 0;
        for (SubgraphSpecification subgraphSpec : _topology.getSubgraphs()) {
            for (SubgraphNodeTemplate template : subgraphSpec.getNodeTemplates()) {
                totalNodeCount += template.getNumberOfNodeOccurences();
            }
        }
        if (totalNodeCount < MIN_NODE_COUNT || totalNodeCount > MAX_NODE_COUNT) {
            throw new IllegalStateException(
                    "validator_count (N=" + totalNodeCount + ") must be in [" + MIN_NODE_COUNT + "," + MAX_NODE_COUNT + "]");
        }

        HashMap<String, String> nodeIdToTemplateId = new HashMap<>();
        // LinkedHashSet, not HashSet: iteration order here must match node-creation order (below)
        // so that both index assignment and the adjacency-construction loops further down are
        // deterministic given the same seed, instead of depending on P2PNode's identity hashCode
        // (which varies run to run since P2PNode has no custom equals/hashCode).
        Map<String, Set<P2PNode>> subgraphNodes = new LinkedHashMap<>();
        Map<String, Set<P2PNode>> subgraphProxies = new LinkedHashMap<>();
        Map<String, SubgraphSpecification> subgraphSpecs = new LinkedHashMap<>();
        HashMap<String, Integer> nodeIdToIndex = new HashMap<>();

        int nextIndex = 0;
        for (SubgraphSpecification subgraphSpec : _topology.getSubgraphs()) {
            String sgId = subgraphSpec.getId();
            subgraphNodes.put(sgId, new LinkedHashSet<>());
            subgraphProxies.put(sgId, new LinkedHashSet<>());
            subgraphSpecs.put(sgId, subgraphSpec);

            for (SubgraphNodeTemplate template : subgraphSpec.getNodeTemplates()) {
                for (int i = 0; i < template.getNumberOfNodeOccurences(); i++) {
                    // UUID identity is left unseeded on purpose (see part A's task scope: the
                    // index below, not the UUID, is the reproducible identity); only the order in
                    // which nodes are created -- and therefore which index each one gets -- must
                    // be deterministic, and it already is: this loop's structure (subgraphs list,
                    // node templates list, occurrence count) is entirely EMF-model-order-driven.
                    String p2pNodeId = UUID.randomUUID().toString();
                    P2PNode node = new P2PNode(p2pNodeId);
                    subgraphNodes.get(sgId).add(node);
                    if (template.isIsSubgraphProxy()) subgraphProxies.get(sgId).add(node);
                    nodeIdToTemplateId.put(p2pNodeId, template.getId());
                    nodeIdToIndex.put(p2pNodeId, nextIndex++);
                }
            }
        }

        SimpleDirectedGraph<P2PNode, P2PLink> networkGraph = new SimpleDirectedGraph<>(P2PLink.class);

        for (Map.Entry<String, Set<P2PNode>> entry : subgraphNodes.entrySet()) {
            String sgId = entry.getKey();
            Set<P2PNode> nodes = entry.getValue();
            SubgraphSpecification spec = subgraphSpecs.get(sgId);
            CounterMap<P2PNode> initialDegrees = new CounterMap<>();

            int effectiveDegree = simulationParameters.getNodeDegree() > 0
                    ? simulationParameters.getNodeDegree() : spec.getConnectivity();
            if (effectiveDegree < MIN_NODE_DEGREE || effectiveDegree > MAX_NODE_DEGREE) {
                throw new IllegalStateException(
                        "node_degree (d=" + effectiveDegree + ") must be in [" + MIN_NODE_DEGREE + "," + MAX_NODE_DEGREE
                                + "] for subgraph " + sgId);
            }
            if (effectiveDegree >= nodes.size()) {
                throw new IllegalStateException(
                        "node_degree (d=" + effectiveDegree + ") must be less than the node count (N=" + nodes.size()
                                + ") of subgraph " + sgId);
            }

            for (P2PNode node : nodes) {
                networkGraph.addVertex(node);
                int degree = simulationParameters.getNodeDegree() > 0
                        ? simulationParameters.getNodeDegree() : spec.getConnectivity();
                initialDegrees.put(node, degree);
            }

            var latency = createLatencyValueProvider(
                    spec.getLinkAllocation().getLatencySpecification(), "latency:subgraph:" + sgId);
            var throughput = createThroughputValueProvider(
                    spec.getLinkAllocation().getThroughputSpecification(), "throughput:subgraph:" + sgId);

            List<P2PNode> nodeList = new ArrayList<>(nodes);
            for (int i = 0; i + 1 < nodeList.size(); i++) {
                P2PNode a = nodeList.get(i), b = nodeList.get(i + 1);
                JGraphExtensions.addBidirectionalEdge(networkGraph, a, b,
                        (from, to) -> new P2PLink(latency, throughput, from, to));
                initialDegrees.decrement(a);
                initialDegrees.decrement(b);
            }

            P2PNode[] arr = nodes.toArray(new P2PNode[0]);
            Random rng = new Random(DeterministicSeeds.deriveSeed(rootSeed, "topologyWiring:" + sgId));

            // Explicit needy-candidate pool, actively maintained (not a per-attempt filter over
            // all nodes): sampling blindly from `arr` and rejecting already-satisfied candidates
            // (the previous fix) wastes most draws once the pool of still-needy nodes shrinks,
            // which is exactly what caused that fix's undershoot (item 2 follow-up measurement).
            // Seeded from `arr`'s order (not initialDegrees.getKeys(), which is HashMap-backed and
            // therefore NOT run-to-run deterministic for P2PNode -- it has no custom
            // equals/hashCode, see the UUID comment above) so sampling stays reproducible.
            List<P2PNode> needyPool = new ArrayList<>();
            for (P2PNode node : arr) {
                if (initialDegrees.get(node) > 0) needyPool.add(node);
            }

            for (P2PNode current : arr) {
                // `current` must never be its own candidate; removing it up front for the duration
                // of its own turn is simpler than filtering it out of every draw, and correct: by
                // the time this loop moves on to the next `current`, initialDegrees.get(current) is
                // guaranteed 0 (the while-loop condition below), so it must never re-enter the pool.
                needyPool.remove(current);
                while (initialDegrees.get(current) > 0) {
                    // No "already at max possible degree" short-circuit here: the random-search
                    // loop below already terminates safely with selected==null in that case (every
                    // candidate is necessarily already connected to `current`, so the search always
                    // exhausts its attempts and falls through) -- this used to double as a silent
                    // d>=N cap, which item 2 replaces with the explicit upfront rejection above.
                    //
                    // Exhaustive shuffle-scan, not bounded random draws with replacement: sampling
                    // needyPool.size() times *with replacement* does not guarantee every distinct
                    // needy candidate gets tried (coupon-collector gap -- roughly 1-1/e, ~63%,
                    // coverage per that many draws), which was confirmed by measurement to still
                    // trigger the give-up fallback far more often than genuine exhaustion accounts
                    // for. Shuffling a copy once and scanning it guarantees every still-needy
                    // candidate is checked before giving up, so selected==null now means what it's
                    // supposed to: current is already connected to every remaining needy node.
                    List<P2PNode> shuffledCandidates = new ArrayList<>(needyPool);
                    Collections.shuffle(shuffledCandidates, rng);
                    P2PNode selected = null;
                    for (P2PNode candidate : shuffledCandidates) {
                        if (!networkGraph.containsEdge(current, candidate)) {
                            selected = candidate;
                            break;
                        }
                    }
                    if (selected == null) { initialDegrees.decrement(current); continue; }
                    JGraphExtensions.addBidirectionalEdge(networkGraph, current, selected,
                            (from, to) -> new P2PLink(latency, throughput, from, to));
                    initialDegrees.decrement(current);
                    initialDegrees.decrement(selected);
                    if (initialDegrees.get(selected) == 0) needyPool.remove(selected);
                }
            }
        }

        for (SubgraphLink subgraphLink : _topology.getSubgraphLinks()) {
            SubgraphSpecification sg1 = subgraphLink.getConnectedSubgraphs().get(0);
            SubgraphSpecification sg2 = subgraphLink.getConnectedSubgraphs().get(1);
            Set<P2PNode> proxies1 = subgraphProxies.getOrDefault(sg1.getId(), Set.of());
            Set<P2PNode> proxies2 = subgraphProxies.getOrDefault(sg2.getId(), Set.of());

            var latency = createLatencyValueProvider(
                    subgraphLink.getAllocation().getLatencySpecification(), "latency:link:" + subgraphLink.getId());
            var throughput = createThroughputValueProvider(
                    subgraphLink.getAllocation().getThroughputSpecification(), "throughput:link:" + subgraphLink.getId());

            for (P2PNode p1 : proxies1) {
                for (P2PNode p2 : proxies2) {
                    JGraphExtensions.addBidirectionalEdge(networkGraph, p1, p2,
                            (from, to) -> new P2PLink(latency, throughput, from, to));
                }
            }
        }

        // Item 2 assertions: the realized graph must actually have the node count the topology
        // declared, and every node must be able to reach every other node in both directions.
        // Strong connectivity, not mere weak/undirected connectivity, is the correct check here:
        // since every edge in this graph is added exclusively via addBidirectionalEdge (both
        // directions always added together, never a one-way edge), weak and strong connectivity
        // coincide for graphs this factory can produce -- but checking strong connectivity
        // directly tests the bilateral-reachability requirement itself, rather than relying on
        // that structural invariant to imply it.
        if (networkGraph.vertexSet().size() != totalNodeCount) {
            throw new IllegalStateException(
                    "Constructed P2P network has " + networkGraph.vertexSet().size()
                            + " nodes, expected " + totalNodeCount);
        }
        if (!new GabowStrongConnectivityInspector<>(networkGraph).isStronglyConnected()) {
            throw new IllegalStateException(
                    "Constructed P2P network is not strongly connected (bilateral reachability violated), rootSeed=" + rootSeed);
        }

        P2PNetworkImpl networkImpl = P2PNetworkImpl.create(networkGraph,
                DeterministicSeeds.randomHexId(DeterministicSeeds.seededGenerator(rootSeed, "p2pNetworkId")));
        for (P2PNode node : networkGraph.vertexSet()) node.initNetwork(networkImpl);

        String topologyId = "topo-" + Long.toHexString(rootSeed);
        return new ConnectedSubgraphNetworkCreationResult(networkImpl, nodeIdToTemplateId, nodeIdToIndex, topologyId);
    }
}
