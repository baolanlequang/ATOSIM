package org.palladiosimulator.blockchainsystems.core.simulation;

import org.palladiosimulator.blockchainsystems.core.propagation.block.BlockReceivedTraceEvent;
import org.palladiosimulator.blockchainsystems.core.propagation.block.BlockSentTraceEvent;
import org.palladiosimulator.blockchainsystems.core.tracing.TraceEventConfiguration;

import java.util.HashSet;
import java.util.Set;

public class DisableNetworkEventsTraceEventsConfiguration implements TraceEventConfiguration {

    private final Set<String> _networkEventTypes = new HashSet<>();

    public DisableNetworkEventsTraceEventsConfiguration() {
        _networkEventTypes.add(BlockReceivedTraceEvent.EVENT_TYPE);
        _networkEventTypes.add(BlockSentTraceEvent.EVENT_TYPE);
    }

    @Override
    public boolean isEventTypeEnabled(String eventType) {
        return !_networkEventTypes.contains(eventType);
    }
}
