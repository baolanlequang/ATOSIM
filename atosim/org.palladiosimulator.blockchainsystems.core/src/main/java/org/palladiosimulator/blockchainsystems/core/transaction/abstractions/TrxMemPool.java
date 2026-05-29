package org.palladiosimulator.blockchainsystems.core.transaction.abstractions;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.Traceable;

import java.util.Collection;
import java.util.List;

public interface TrxMemPool extends Traceable {
    void storeTransaction(Transaction transaction);
    void storeTransactions(Collection<Transaction> transactions);
    void removeTransaction(Transaction transaction);
    void removeTransactions(Collection<Transaction> transactions);
    Transaction getTransactionById(String txId);
    List<Transaction> getTransactionsSortedByFeeRate();
}
