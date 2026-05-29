package org.palladiosimulator.blockchainsystems.core.transaction;

import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

import java.util.Set;

public class TransactionSelectionResult {

    private final Set<Transaction> _transactions;
    private final int _totalSize;

    public TransactionSelectionResult(Set<Transaction> transactions, int totalSize) {
        _transactions = transactions;
        _totalSize = totalSize;
    }

    public Set<Transaction> getTransactions() {
        return _transactions;
    }

    public int getTotalSize() {
        return _totalSize;
    }
}
