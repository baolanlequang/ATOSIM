package org.palladiosimulator.blockchainsystems.core.blockchain;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockType;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;

public class BlockAppendedTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "BlockAppendedTraceEvent";

    private final long _occurrenceTime;
    private final Block _appendedBlock;
    private final long _blockPosition;
    private final Block _previousBlock;
    private final BlockType _appendedBlockType;

    public BlockAppendedTraceEvent(
            long occurrenceTime,
            Block appendedBlock,
            long blockPosition,
            Block previousBlock,
            BlockType appendedBlockType
    ) {
        _occurrenceTime = occurrenceTime;
        _appendedBlock = appendedBlock;
        _blockPosition = blockPosition;
        _previousBlock = previousBlock;
        _appendedBlockType = appendedBlockType;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public Block getAppendedBlock() { return _appendedBlock; }
    public long getBlockPosition() { return _blockPosition; }
    public Block getPreviousBlock() { return _previousBlock; }
    public BlockType getAppendedBlockType() { return _appendedBlockType; }
}
