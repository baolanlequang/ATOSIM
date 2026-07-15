package org.palladiosimulator.blockchainsystems.core.blockchain;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;

import java.util.List;
import java.util.Set;

public class ChainReorganizedTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "ChainReorganizedTraceEvent";

    private final long _occurrenceTime;
    private final Block _newCanonicalTip;
    private final long _newCanonicalTipHeight;
    private final Block _commonAncestor;
    private final long _commonAncestorHeight;
    private final Set<OldCanonicalTip> _oldCanonicalTips;
    private final List<ChainBlock> _replacingChainBlocks;

    public ChainReorganizedTraceEvent(
            long occurrenceTime,
            Block newCanonicalTip,
            long newCanonicalTipHeight,
            Block commonAncestor,
            long commonAncestorHeight,
            Set<OldCanonicalTip> oldCanonicalTips,
            List<ChainBlock> replacingChainBlocks
    ) {
        _occurrenceTime = occurrenceTime;
        _newCanonicalTip = newCanonicalTip;
        _newCanonicalTipHeight = newCanonicalTipHeight;
        _commonAncestor = commonAncestor;
        _commonAncestorHeight = commonAncestorHeight;
        _oldCanonicalTips = oldCanonicalTips;
        _replacingChainBlocks = replacingChainBlocks;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public Block getNewCanonicalTip() { return _newCanonicalTip; }
    public long getNewCanonicalTipHeight() { return _newCanonicalTipHeight; }
    public Block getCommonAncestor() { return _commonAncestor; }
    public long getCommonAncestorHeight() { return _commonAncestorHeight; }
    public Set<OldCanonicalTip> getOldCanonicalTips() { return _oldCanonicalTips; }

    /**
     * The blocks on the new canonical branch between the common ancestor (exclusive) and the new
     * canonical tip (inclusive), ordered oldest-to-newest. Each block's {@code getOriginId()}
     * identifies who mined it, so callers can determine how many replacing blocks were
     * attacker-mined (e.g. a multi-block selfish-mining reveal) rather than just the reorg depth.
     */
    public List<ChainBlock> getReplacingChainBlocks() { return _replacingChainBlocks; }

    /**
     * An old canonical tip together with its height, so that reorganization depth
     * (height of old tip - height of common ancestor) can be derived downstream.
     */
    public static class OldCanonicalTip {
        private final Block _block;
        private final long _height;

        public OldCanonicalTip(Block block, long height) {
            _block = block;
            _height = height;
        }

        public Block getBlock() { return _block; }
        public long getHeight() { return _height; }
    }

    /** A block on a chain branch together with its height. */
    public static class ChainBlock {
        private final Block _block;
        private final long _height;

        public ChainBlock(Block block, long height) {
            _block = block;
            _height = height;
        }

        public Block getBlock() { return _block; }
        public long getHeight() { return _height; }
    }
}
