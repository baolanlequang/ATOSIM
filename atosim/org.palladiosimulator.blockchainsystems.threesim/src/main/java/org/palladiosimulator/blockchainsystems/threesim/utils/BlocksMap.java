package org.palladiosimulator.blockchainsystems.threesim.utils;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlocksMap {

    private static class Entry {
        final Block block;
        final Set<String> nodeIds = new HashSet<>();

        Entry(Block block) { this.block = block; }
    }

    private final int _threshold;
    private final Map<String, Entry> _blocks = new HashMap<>();
    private final Map<String, Long> _timestamps = new HashMap<>();

    public BlocksMap(int threshold) {
        _threshold = threshold;
    }

    public void addNodeToBlock(Block block, String nodeId, long timestamp) {
        Entry entry = _blocks.computeIfAbsent(block.getHash(), k -> new Entry(block));
        boolean wasValid = entry.nodeIds.size() >= _threshold;
        boolean added = entry.nodeIds.add(nodeId);
        boolean isValid = entry.nodeIds.size() >= _threshold;
        if (added && !wasValid && isValid) {
            _timestamps.putIfAbsent(block.getHash(), timestamp);
        }
    }

    public void removeNodeFromBlock(String blockHash, String nodeId) {
        Entry entry = _blocks.get(blockHash);
        if (entry == null) return;
        entry.nodeIds.remove(nodeId);
        if (entry.nodeIds.isEmpty()) {
            _blocks.remove(blockHash);
            _timestamps.remove(blockHash);
            return;
        }
        if (entry.nodeIds.size() < _threshold) {
            _timestamps.remove(blockHash);
        }
    }

    public int getNumberOfValidBlocks() {
        return _timestamps.size();
    }

    public List<Map.Entry<Block, Long>> getValidBlocks() {
        List<Map.Entry<Block, Long>> result = new ArrayList<>();
        for (Map.Entry<String, Entry> e : _blocks.entrySet()) {
            Long ts = _timestamps.get(e.getKey());
            if (ts != null) {
                result.add(new AbstractMap.SimpleEntry<>(e.getValue().block, ts));
            }
        }
        return result;
    }

    public void clear() {
        _blocks.clear();
        _timestamps.clear();
    }
}
