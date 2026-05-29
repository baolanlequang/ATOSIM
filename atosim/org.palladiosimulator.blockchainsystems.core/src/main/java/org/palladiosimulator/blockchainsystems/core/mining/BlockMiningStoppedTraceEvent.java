package org.palladiosimulator.blockchainsystems.core.mining;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;

public class BlockMiningStoppedTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "BlockMiningStoppedTraceEvent";

    private final long _occurrenceTime;

    public BlockMiningStoppedTraceEvent(long occurrenceTime) {
        _occurrenceTime = occurrenceTime;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
}
