package org.palladiosimulator.blockchainsystems.core.transaction;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

public class TransactionSubmittedTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "TransactionSubmittedTraceEvent";

    private final long _occurrenceTime;
    private final Transaction _transaction;

    public TransactionSubmittedTraceEvent(long occurrenceTime, Transaction transaction) {
        _occurrenceTime = occurrenceTime;
        _transaction = transaction;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public Transaction getTransaction() { return _transaction; }
}
