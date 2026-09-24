package org.palladiosimulator.blockchainsystems.core.block;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;

// Diagnostic-only: this node's reception timestamp for a block (the moment validateBlock()
// is invoked, before any queueing/processing delay -- see BlockValidatorImpl). Only raised
// when the -Dthreesim.diagnosticPropagationDump=true diagnostic dump is enabled; never part
// of the production trace-event stream otherwise.
public class BlockValidationStartedTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "BlockValidationStartedTraceEvent";

    private final long _occurrenceTime;
    private final Block _block;

    public BlockValidationStartedTraceEvent(long occurrenceTime, Block block) {
        _occurrenceTime = occurrenceTime;
        _block = block;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public Block getBlock() { return _block; }
}
