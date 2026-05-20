package org.palladiosimulator.blockchainsystems.core.orphanblockpool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.OrphanBlockPool;

public class OrphanBlockPoolImpl extends BlockchainNodeObject implements OrphanBlockPool {

    private final HashMap<String, Set<Block>> orphanBlocksByPreviousHash;
    private final HashMap<String, Block> orphanBlocksByHash;

    public OrphanBlockPoolImpl() {
        this.orphanBlocksByPreviousHash = new HashMap<>();
        this.orphanBlocksByHash = new HashMap<>();
    }

    @Override
    public Set<Block> getBlocksByPreviousBlockHash(String previousBlockHash) {
        Set<Block> blocks = orphanBlocksByPreviousHash.get(previousBlockHash);
        if (blocks == null || blocks.isEmpty()) {
            return Collections.emptySet();
        }

        // Defensive copy so callers cannot mutate internal state.
        return Collections.unmodifiableSet(new HashSet<>(blocks));
    }

    @Override
    public void storeBlock(Block block) {
        if (orphanBlocksByHash.containsKey(block.getHash())) {
            return;
        }

        orphanBlocksByPreviousHash
                .computeIfAbsent(block.getPreviousHash(), ignored -> new HashSet<>())
                .add(block);

        orphanBlocksByHash.put(block.getHash(), block);
        logBlockStoredEvent(block);
    }

    @Override
    public boolean hasBlockWithHash(String hash) {
        return orphanBlocksByHash.containsKey(hash);
    }

    @Override
    public Set<Block> takeBlocksByPreviousBlockHash(String previousBlockHash) {
        Set<Block> blocks = orphanBlocksByPreviousHash.remove(previousBlockHash);
        if (blocks == null || blocks.isEmpty()) {
            return Collections.emptySet();
        }

        for (Block block : blocks) {
            orphanBlocksByHash.remove(block.getHash());
        }

        return new HashSet<>(blocks);
    }

    @Override
    public void removeBlock(String hash) {
        Block block = orphanBlocksByHash.remove(hash);
        if (block == null) {
            return;
        }

        Set<Block> siblings = orphanBlocksByPreviousHash.get(block.getPreviousHash());
        if (siblings != null) {
            siblings.removeIf(candidate -> candidate.getHash().equals(hash));
            if (siblings.isEmpty()) {
                orphanBlocksByPreviousHash.remove(block.getPreviousHash());
            }
        }
    }

    private void logBlockStoredEvent(Block block) {
        if (!getTraceEventLogger().isEventTypeEnabled(BlockStoredInOrphanPoolTraceEvent.EVENT_TYPE)) {
            return;
        }

        BlockStoredInOrphanPoolTraceEvent event = new BlockStoredInOrphanPoolTraceEvent(
                getSimulationContext().getSystemClock().getCurrentTime(),
                block
        );

        getTraceEventLogger().logEvent(event);
    }

    @Override
    public void dispatchEvent(Event event) {
        // Intentionally empty: orphan pool is passive storage only.
    }
}