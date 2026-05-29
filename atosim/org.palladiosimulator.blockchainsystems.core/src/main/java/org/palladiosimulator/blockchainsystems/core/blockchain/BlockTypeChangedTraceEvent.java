package org.palladiosimulator.blockchainsystems.core.blockchain;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockType;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;

public class BlockTypeChangedTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "BlockTypeChangedTraceEvent";

    private final long _occurrenceTime;
    private final Block _block;
    private final BlockType _oldBlockType;
    private final BlockType _newBlockType;

    public BlockTypeChangedTraceEvent(long occurrenceTime, Block block, BlockType oldBlockType, BlockType newBlockType) {
        _occurrenceTime = occurrenceTime;
        _block = block;
        _oldBlockType = oldBlockType;
        _newBlockType = newBlockType;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public Block getBlock() { return _block; }
    public BlockType getOldBlockType() { return _oldBlockType; }
    public BlockType getNewBlockType() { return _newBlockType; }
}
