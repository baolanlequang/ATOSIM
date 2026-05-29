package org.palladiosimulator.blockchainsystems.core.mining;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;

public class BlockMiningRestartedTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "BlockMiningRestartedTraceEvent";

    private final long _occurrenceTime;
    private final String _previousBlockHash;

    public BlockMiningRestartedTraceEvent(long occurrenceTime, String previousBlockHash) {
        _occurrenceTime = occurrenceTime;
        _previousBlockHash = previousBlockHash;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public String getPreviousBlockHash() { return _previousBlockHash; }
}
