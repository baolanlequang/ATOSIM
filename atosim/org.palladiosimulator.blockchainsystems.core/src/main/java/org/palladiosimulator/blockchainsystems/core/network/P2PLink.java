package org.palladiosimulator.blockchainsystems.core.network;

import org.palladiosimulator.blockchainsystems.core.common.P2PNetworkObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationLifecycleAwareValueProvider;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.Message;

public class P2PLink extends P2PNetworkObject {

    private final SimulationLifecycleAwareValueProvider<Long> _latencyValueProvider;
    private final SimulationLifecycleAwareValueProvider<Long> _throughputValueProvider;
    private final P2PNode _fromNode;
    private final P2PNode _toNode;

    public P2PLink(
            SimulationLifecycleAwareValueProvider<Long> latencyValueProvider,
            SimulationLifecycleAwareValueProvider<Long> throughputValueProvider,
            P2PNode fromNode,
            P2PNode toNode
    ) {
        _latencyValueProvider = latencyValueProvider;
        _throughputValueProvider = throughputValueProvider;
        _fromNode = fromNode;
        _toNode = toNode;
    }

    public P2PNode getFromNode() { return _fromNode; }
    public P2PNode getToNode() { return _toNode; }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        _latencyValueProvider.initialize(getSimulationContext());
        _throughputValueProvider.initialize(getSimulationContext());
    }

    @Override
    protected void onCleanup() {
        super.onCleanup();
        _latencyValueProvider.cleanup();
        _throughputValueProvider.cleanup();
    }

    public void send(Message messageContent) {
        MessageSentEvent msEvent = new MessageSentEvent(
                getSimulationContext().getSystemClock().getCurrentTime(),
                new P2PLinkMessageFrame(messageContent, getSimulationContext().getSystemClock().getCurrentTime()),
                this,
                _toNode,
                _fromNode
        );
        getSimulationContext().getEventCoordinator().raiseEvent(msEvent);
    }

    @Override
    public void dispatchEvent(Event event) {
        String type = event.getEventType();
        if (MessageDroppedEvent.EVENT_TYPE.equals(type)) {
            handleMessageDroppedEvent((MessageDroppedEvent) event);
        } else if (MessageReceivedEvent.EVENT_TYPE.equals(type)) {
            handleMessageReceivedEvent((MessageReceivedEvent) event);
        } else if (MessageSentEvent.EVENT_TYPE.equals(type)) {
            handleMessageSentEvent((MessageSentEvent) event);
        }
    }

    private void handleMessageReceivedEvent(MessageReceivedEvent event) {
        event.getRecipientNode().onReceive(event.getMessage().getContent(), event.getSenderNode());
    }

    private void handleMessageDroppedEvent(MessageDroppedEvent event) {
        event.getSenderNode().onMessageDropped(event.getMessage().getContent(), event.getRecipientNode());
    }

    private void handleMessageSentEvent(MessageSentEvent event) {
        long bps = _throughputValueProvider.getValue();

        Event nextEvent;
        if (bps <= 0) {
            nextEvent = new MessageDroppedEvent(
                    event.getMessage(),
                    getSimulationContext().getSystemClock().getCurrentTime(),
                    this,
                    event.getRecipientNode(),
                    event.getSenderNode()
            );
        } else {
            long latency = _latencyValueProvider.getValue();
            long messageSize = event.getMessage().getContent().getSize();
            double bypms = (double) bps / 8000.0;
            long transmissionDuration = Math.round(latency + messageSize / bypms);

            nextEvent = new MessageReceivedEvent(
                    event.getMessage(),
                    getSimulationContext().getSystemClock().getCurrentTime() + transmissionDuration,
                    this,
                    event.getRecipientNode(),
                    event.getSenderNode()
            );
        }
        getSimulationContext().getEventCoordinator().raiseEvent(nextEvent);
    }
}
