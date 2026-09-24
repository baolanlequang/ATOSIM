package org.palladiosimulator.blockchainsystems.core.block;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;

// Diagnostic-only: this node's validation-completion timestamp for a block (finishTime --
// see BlockValidatorImpl). Only raised when the -Dthreesim.diagnosticPropagationDump=true
// diagnostic dump is enabled; never part of the production trace-event stream otherwise.
public class BlockValidationFinishedTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "BlockValidationFinishedTraceEvent";

    private final long _occurrenceTime;
    private final Block _block;

    public BlockValidationFinishedTraceEvent(long occurrenceTime, Block block) {
        _occurrenceTime = occurrenceTime;
        _block = block;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public Block getBlock() { return _block; }
}
