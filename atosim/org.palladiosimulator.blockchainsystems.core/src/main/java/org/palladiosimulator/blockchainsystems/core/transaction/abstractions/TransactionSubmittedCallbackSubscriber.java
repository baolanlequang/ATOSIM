package org.palladiosimulator.blockchainsystems.core.transaction.abstractions;

public interface TransactionSubmittedCallbackSubscriber {
    void onTransactionSubmitted(Transaction transaction);
}
