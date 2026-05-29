package org.palladiosimulator.blockchainsystems.core.transaction;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

public class TransactionRemovedFromMemPoolTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "TransactionRemovedFromMemPoolTraceEvent";

    private final long _occurrenceTime;
    private final Transaction _removedTransaction;

    public TransactionRemovedFromMemPoolTraceEvent(long occurrenceTime, Transaction removedTransaction) {
        _occurrenceTime = occurrenceTime;
        _removedTransaction = removedTransaction;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public Transaction getRemovedTransaction() { return _removedTransaction; }
}
