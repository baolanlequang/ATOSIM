package org.palladiosimulator.blockchainsystems.core.network;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.EventDispatchable;

public class MessageDroppedEvent implements Event {

    public static final String EVENT_TYPE = "MessageDroppedEvent";

    private final P2PLinkMessageFrame _message;
    private final long _occurrenceTime;
    private final EventDispatchable _target;
    private final P2PNode _recipientNode;
    private final P2PNode _senderNode;

    public MessageDroppedEvent(
            P2PLinkMessageFrame message,
            long occurrenceTime,
            EventDispatchable target,
            P2PNode recipientNode,
            P2PNode senderNode
    ) {
        _message = message;
        _occurrenceTime = occurrenceTime;
        _target = target;
        _recipientNode = recipientNode;
        _senderNode = senderNode;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    @Override public EventDispatchable getOrigin() { return _target; }
    @Override public String getValueFormatted() { return toString(); }

    public P2PLinkMessageFrame getMessage() { return _message; }
    public P2PNode getRecipientNode() { return _recipientNode; }
    public P2PNode getSenderNode() { return _senderNode; }
}
