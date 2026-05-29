package org.palladiosimulator.blockchainsystems.core.network;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.Message;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkEndpoint;

public class MessageDroppedTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "MessageDroppedEvent";

    private final Message _message;
    private final long _occurrenceTime;
    private final P2PNetworkEndpoint _recipientNode;
    private final P2PNetworkEndpoint _senderNode;

    public MessageDroppedTraceEvent(
            Message message,
            long occurrenceTime,
            P2PNetworkEndpoint recipientNode,
            P2PNetworkEndpoint senderNode
    ) {
        _message = message;
        _occurrenceTime = occurrenceTime;
        _recipientNode = recipientNode;
        _senderNode = senderNode;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public Message getMessage() { return _message; }
    public P2PNetworkEndpoint getRecipientNode() { return _recipientNode; }
    public P2PNetworkEndpoint getSenderNode() { return _senderNode; }
}
