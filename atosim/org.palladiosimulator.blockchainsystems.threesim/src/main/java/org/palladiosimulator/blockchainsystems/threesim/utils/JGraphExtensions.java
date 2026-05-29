package org.palladiosimulator.blockchainsystems.threesim.utils;

import org.jgrapht.graph.AbstractBaseGraph;
import org.palladiosimulator.blockchainsystems.core.network.P2PLink;
import org.palladiosimulator.blockchainsystems.core.network.P2PNode;

import java.util.function.BiFunction;

public class JGraphExtensions {

    public static <V, E> void addBidirectionalEdge(
            AbstractBaseGraph<V, E> graph,
            V firstVertex,
            V secondVertex,
            BiFunction<V, V, E> getElement
    ) {
        graph.addEdge(firstVertex, secondVertex, getElement.apply(firstVertex, secondVertex));
        graph.addEdge(secondVertex, firstVertex, getElement.apply(secondVertex, firstVertex));
    }

    public static void addBidirectionalEdge(
            AbstractBaseGraph<P2PNode, P2PLink> graph,
            P2PNode firstVertex,
            P2PNode secondVertex,
            BiFunction<P2PNode, P2PNode, P2PLink> getElement
    ) {
        graph.addEdge(firstVertex, secondVertex, getElement.apply(firstVertex, secondVertex));
        graph.addEdge(secondVertex, firstVertex, getElement.apply(secondVertex, firstVertex));
    }
}
