package org.palladiosimulator.blockchainsystems.core.transaction.abstractions;

public interface TransactionFactory {
    Transaction createTransaction(
            String txId,
            int size,
            long creationTime,
            String senderId,
            String originNodeId,
            String recipientId,
            double amount,
            double fee
    );
}
