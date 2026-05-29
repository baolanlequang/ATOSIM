package org.palladiosimulator.blockchainsystems.core.transaction;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

public class TransactionStoredInMemPoolTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "TransactionStoredInMemPoolTraceEvent";

    private final long _occurrenceTime;
    private final Transaction _storedTransaction;

    public TransactionStoredInMemPoolTraceEvent(long occurrenceTime, Transaction storedTransaction) {
        _occurrenceTime = occurrenceTime;
        _storedTransaction = storedTransaction;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public Transaction getStoredTransaction() { return _storedTransaction; }
}
