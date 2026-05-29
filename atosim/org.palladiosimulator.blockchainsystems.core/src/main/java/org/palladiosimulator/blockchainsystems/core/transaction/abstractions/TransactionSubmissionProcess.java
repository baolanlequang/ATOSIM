package org.palladiosimulator.blockchainsystems.core.transaction.abstractions;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.Traceable;

import java.util.function.Supplier;

public interface TransactionSubmissionProcess extends Traceable {
    void addOnTransactionSubmittedCallbackSubscriber(TransactionSubmittedCallbackSubscriber subscriber);
    void setOnSelectRecipientNodeIdCallback(Supplier<String> onSelectRecipientNodeIdCallback);
    void startTransactionSubmissionProcess();
    void stopTransactionSubmissionProcess();
}
