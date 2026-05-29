package org.palladiosimulator.blockchainsystems.core.transaction;

import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

public class TransactionImpl implements Transaction {

    private final String _txId;
    private final int _size;
    private final long _creationTime;
    private final String _senderId;
    private final String _originNodeId;
    private final String _recipientId;
    private final double _amount;
    private final double _fee;

    public TransactionImpl(
            String txId, int size, long creationTime,
            String senderId, String originNodeId, String recipientId,
            double amount, double fee
    ) {
        _txId = txId;
        _size = size;
        _creationTime = creationTime;
        _senderId = senderId;
        _originNodeId = originNodeId;
        _recipientId = recipientId;
        _amount = amount;
        _fee = fee;
    }

    @Override public String getTxId() { return _txId; }
    @Override public int getSize() { return _size; }
    @Override public long getCreationTime() { return _creationTime; }
    @Override public String getSenderId() { return _senderId; }
    @Override public String getOriginNodeId() { return _originNodeId; }
    @Override public String getRecipientId() { return _recipientId; }
    @Override public double getAmount() { return _amount; }
    @Override public double getFee() { return _fee; }
}
