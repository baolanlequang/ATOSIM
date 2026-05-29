package org.palladiosimulator.blockchainsystems.core.propagation.transaction;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkEndpoint;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

public class TransactionSentTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "TransactionSentTraceEvent";

    private final long _occurrenceTime;
    private final Transaction _sentTransaction;
    private final P2PNetworkEndpoint _receivingNetworkEndpoint;

    public TransactionSentTraceEvent(long occurrenceTime, Transaction sentTransaction, P2PNetworkEndpoint receivingNetworkEndpoint) {
        _occurrenceTime = occurrenceTime;
        _sentTransaction = sentTransaction;
        _receivingNetworkEndpoint = receivingNetworkEndpoint;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public Transaction getSentTransaction() { return _sentTransaction; }
    public P2PNetworkEndpoint getReceivingNetworkEndpoint() { return _receivingNetworkEndpoint; }
}
