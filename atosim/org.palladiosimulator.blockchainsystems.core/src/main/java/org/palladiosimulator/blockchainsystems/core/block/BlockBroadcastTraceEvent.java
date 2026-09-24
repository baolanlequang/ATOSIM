package org.palladiosimulator.blockchainsystems.core.block;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;

// Marks the true network-broadcast moment of a block: the instant distribute() is called on
// the originating node's BlockPropagationStrategy, as opposed to getBlockMinedTimestamp()
// (the moment of local mining, which can precede broadcast by an arbitrary amount for a
// privately-withheld attacker block -- see the four attack behavior classes'
// publishOneHiddenBlock()). Unlike BlockValidationStartedTraceEvent/
// BlockValidationFinishedTraceEvent, this is NOT diagnostic-only: it is raised unconditionally
// (see the four attack behavior classes) because ThreesimSimulationMonitor's production
// propagation-time metrics (recordBlockPropagation) now use it as the delay's start point,
// falling back to getBlockMinedTimestamp() when no broadcast event was recorded for a given
// block hash (blocks broadcast immediately upon mining, or by a behavior class not raising
// this event, never accumulate a mined-to-broadcast gap in the first place -- see
// ThreesimSimulationMonitor.recordBlockPropagation's fallback).
public class BlockBroadcastTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "BlockBroadcastTraceEvent";

    private final long _occurrenceTime;
    private final Block _block;

    public BlockBroadcastTraceEvent(long occurrenceTime, Block block) {
        _occurrenceTime = occurrenceTime;
        _block = block;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public Block getBlock() { return _block; }
}
