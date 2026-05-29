package org.palladiosimulator.blockchainsystems.core.orphanblockpool;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;

public class BlockStoredInOrphanPoolTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "BlockStoredInOrphanPoolTraceEvent";

    private final long _occurrenceTime;
    private final Block _storedBlock;

    public BlockStoredInOrphanPoolTraceEvent(long occurrenceTime, Block storedBlock) {
        _occurrenceTime = occurrenceTime;
        _storedBlock = storedBlock;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public Block getStoredBlock() { return _storedBlock; }
}
