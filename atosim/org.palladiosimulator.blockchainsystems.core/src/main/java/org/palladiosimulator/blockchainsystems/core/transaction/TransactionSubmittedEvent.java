package org.palladiosimulator.blockchainsystems.core.transaction;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.EventDispatchable;

public record TransactionSubmittedEvent(
        long occurrenceTime,
        EventDispatchable target
) implements Event {

    public static final String EVENT_TYPE = "TransactionSubmittedEvent";

    @Override
    public long getOccurrenceTime() { return this.occurrenceTime(); }

    @Override
    public String getEventType() { return EVENT_TYPE; }

    @Override
    public EventDispatchable getOrigin() { return this.target(); }

    @Override
    public String getValueFormatted() { return this.toString(); }
}
