package org.palladiosimulator.blockchainsystems.core.mining;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;

public class BlockMinedTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "BlockMinedTraceEvent";

    private final long _occurrenceTime;
    private final Block _block;

    public BlockMinedTraceEvent(long occurrenceTime, Block block) {
        _occurrenceTime = occurrenceTime;
        _block = block;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public Block getBlock() { return _block; }
}
