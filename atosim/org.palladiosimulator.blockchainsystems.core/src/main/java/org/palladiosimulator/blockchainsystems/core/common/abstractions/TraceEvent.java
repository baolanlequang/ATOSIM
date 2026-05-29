package org.palladiosimulator.blockchainsystems.core.common.abstractions;

public interface TraceEvent {
    long getOccurrenceTime();
    String getEventType();
}
