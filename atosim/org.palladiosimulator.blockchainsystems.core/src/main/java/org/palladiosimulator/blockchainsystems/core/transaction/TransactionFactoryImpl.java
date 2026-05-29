package org.palladiosimulator.blockchainsystems.core.transaction;

import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TransactionFactory;

public class TransactionFactoryImpl implements TransactionFactory {

    @Override
    public Transaction createTransaction(
            String txId, int size, long creationTime,
            String senderId, String originNodeId, String recipientId,
            double amount, double fee
    ) {
        return new TransactionImpl(txId, size, creationTime, senderId, originNodeId, recipientId, amount, fee);
    }
}
