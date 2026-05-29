package org.palladiosimulator.blockchainsystems.core.tracing;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogOrigin;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogger;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.BiConsumer;

public class TraceEventLoggerImpl implements TraceEventLogger {

    private final TraceEventLogOrigin _logOrigin;
    private final TraceEventConfiguration _traceEventConfiguration;
    private final TreeSet<TraceEvent> _events;
    private BiConsumer<TraceEvent, TraceEventLogOrigin> _traceEventCallback;

    public TraceEventLoggerImpl(TraceEventLogOrigin logOrigin, TraceEventConfiguration traceEventConfiguration) {
        _logOrigin = logOrigin;
        _traceEventConfiguration = traceEventConfiguration;
        _events = new TreeSet<>(Comparator.comparingLong(TraceEvent::getOccurrenceTime));
    }

    @Override
    public TraceEventLogOrigin getLogOrigin() {
        return _logOrigin;
    }

    @Override
    public void logEvent(TraceEvent traceEvent) {
        _events.add(traceEvent);
        notifyTraceEventOccurred(traceEvent);
    }

    private void notifyTraceEventOccurred(TraceEvent traceEvent) {
        if (_traceEventCallback != null) {
            _traceEventCallback.accept(traceEvent, _logOrigin);
        }
    }

    public void setTraceEventCallback(BiConsumer<TraceEvent, TraceEventLogOrigin> traceEventCallback) {
        _traceEventCallback = traceEventCallback;
    }

    @Override
    public boolean isEventTypeEnabled(String eventType) {
        return _traceEventConfiguration.isEventTypeEnabled(eventType);
    }
}
