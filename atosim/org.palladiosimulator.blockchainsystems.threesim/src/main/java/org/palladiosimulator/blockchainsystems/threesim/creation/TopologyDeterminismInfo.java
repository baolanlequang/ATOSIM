package org.palladiosimulator.blockchainsystems.threesim.creation;

import java.util.List;
import java.util.Map;

/**
 * Per-replication determinism metadata: the root seed and realized topology this specific
 * (configId, replicationId) produced, plus enough of the resulting structure (attacker indices,
 * adjacency) to verify reproducibility without re-deriving it from the raw network. Not part of
 * the full item-11 output schema (block-size summaries etc. are a separate, later item) -- this
 * exists to make part A/B's determinism checkable. replicationId is item 11's explicit per-round
 * replication identifier (previously only implicit via array position in simulationRoundResults)
 * -- it rides along here rather than through a new field/pipeline of its own, since this record
 * already flows end-to-end from createBlockchainSystem (where replicationId originates) to the
 * JSON output without ever being dropped. meanDegree/minDegree/maxDegree are item 2's realized
 * degree statistics, derived from nodeAdjacency (one node-count sample per entry) rather than
 * plumbed separately -- for the same reason: nodeAdjacency already flows through unmodified, and
 * is only ever populated for topology types that assign a stable node index (see
 * ThreesimBlockchainSystemFactory.getNodeIdToIndexMapping); 0/0/0.0 when it isn't.
 */
public record TopologyDeterminismInfo(
        long rootSeed,
        long replicationId,
        String topologyId,
        List<Integer> attackerNodeIndices,
        Map<Integer, List<Integer>> nodeAdjacency,
        double meanDegree,
        int minDegree,
        int maxDegree
) {
    public static TopologyDeterminismInfo empty(long rootSeed, long replicationId) {
        return new TopologyDeterminismInfo(rootSeed, replicationId, null, List.of(), Map.of(), 0.0, 0, 0);
    }
}
