package org.palladiosimulator.blockchainsystems.core.tracing;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogOrigin;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogger;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLoggerContainer;

import java.util.HashMap;
import java.util.HashSet;

public class TraceEventLoggerContainerImpl implements TraceEventLoggerContainer {

    private final TraceEventConfiguration _traceEventConfiguration;
    private final HashMap<String, TraceEventLogger> _loggers = new HashMap<>();
    private final HashSet<TraceEventSubscriber> _eventSubscribers = new HashSet<>();

    public TraceEventLoggerContainerImpl() {
        _traceEventConfiguration = new AllTraceEventsEnabledTraceEventConfiguration();
    }

    public TraceEventLoggerContainerImpl(TraceEventConfiguration traceEventConfiguration) {
        _traceEventConfiguration = traceEventConfiguration;
    }

    public void addSubscriber(TraceEventSubscriber subscriber) {
        _eventSubscribers.add(subscriber);
    }

    public void removeSubscriber(TraceEventSubscriber subscriber) {
        _eventSubscribers.remove(subscriber);
    }

    private void notifySubscribers(TraceEvent traceEvent, TraceEventLogOrigin logOrigin) {
        for (TraceEventSubscriber subscriber : _eventSubscribers) {
            subscriber.onTraceEventOccurred(traceEvent, logOrigin);
        }
    }

    @Override
    public void createTraceEventLogger(TraceEventLogOrigin logOrigin) {
        if (_loggers.containsKey(logOrigin.getId())) return;

        TraceEventLoggerImpl loggerImpl = new TraceEventLoggerImpl(logOrigin, _traceEventConfiguration);
        loggerImpl.setTraceEventCallback((traceEvent, origin) -> notifySubscribers(traceEvent, origin));
        _loggers.put(logOrigin.getId(), loggerImpl);
    }

    @Override
    public TraceEventLogger getLogger(TraceEventLogOrigin logOrigin) {
        return _loggers.get(logOrigin.getId());
    }
}
