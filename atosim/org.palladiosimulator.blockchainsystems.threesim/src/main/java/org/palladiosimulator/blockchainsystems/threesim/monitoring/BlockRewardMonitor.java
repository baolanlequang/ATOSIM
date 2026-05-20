package org.palladiosimulator.blockchainsystems.threesim.monitoring;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Tracks block rewards per runtime node ID.
 *
 * Used to compute attacker revenue share.
 */
public class BlockRewardMonitor {

    private final Map<String, Integer> rewardsPerNode = new HashMap<>();
    private final Set<String> rewardedBlockHashes = new HashSet<>();

    /**
     * Record a confirmed block for a runtime node.
     * A block reward is counted at most once per unique block hash.
     */
    public void recordBlockReward(Block block) {
        if (block.getOriginId() == null || block.getHash() == null) {
            return;
        }

        if (!rewardedBlockHashes.add(block.getHash())) {
            return;
        }

        rewardsPerNode.merge(block.getOriginId(), 1, Integer::sum);
    }

    public int getTotalRewards() {
        return rewardsPerNode.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    public int getRewardsForNode(String nodeId) {
        return rewardsPerNode.getOrDefault(nodeId, 0);
    }

    public Map<String, Integer> getAllRewards() {
        return rewardsPerNode;
    }
}