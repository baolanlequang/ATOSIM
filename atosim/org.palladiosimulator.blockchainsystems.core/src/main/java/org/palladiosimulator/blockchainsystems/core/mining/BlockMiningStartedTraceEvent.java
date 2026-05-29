package org.palladiosimulator.blockchainsystems.core.mining;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;

public class BlockMiningStartedTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "BlockMiningStartedTraceEvent";

    private final long _occurrenceTime;

    public BlockMiningStartedTraceEvent(long occurrenceTime) {
        _occurrenceTime = occurrenceTime;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
}
