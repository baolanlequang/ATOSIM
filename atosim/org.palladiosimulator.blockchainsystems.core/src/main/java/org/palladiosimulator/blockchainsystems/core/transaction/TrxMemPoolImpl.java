package org.palladiosimulator.blockchainsystems.core.transaction;

import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TrxMemPool;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class TrxMemPoolImpl extends BlockchainNodeObject implements TrxMemPool {

    private final String _nodeId;
    private final TreeSet<Transaction> _mempool;

    public TrxMemPoolImpl(String nodeId) {
        _nodeId = nodeId;
        _mempool = new TreeSet<>(Comparator
                .<Transaction>comparingDouble(t -> -(t.getFee() / t.getSize()))
                .thenComparing(Transaction::getTxId));
    }

    @Override
    public void dispatchEvent(Event event) {
    }

    @Override
    public void storeTransaction(Transaction transaction) {
        _mempool.add(transaction);
        logTransactionStoredEvent(transaction);
    }

    @Override
    public void storeTransactions(Collection<Transaction> transactions) {
        for (Transaction t : transactions) storeTransaction(t);
    }

    @Override
    public void removeTransaction(Transaction transaction) {
        _mempool.remove(transaction);
        logTransactionRemovedEvent(transaction);
    }

    @Override
    public void removeTransactions(Collection<Transaction> transactions) {
        for (Transaction t : transactions) removeTransaction(t);
    }

    @Override
    public Transaction getTransactionById(String txId) {
        return _mempool.stream().filter(t -> t.getTxId().equals(txId)).findFirst().orElse(null);
    }

    @Override
    public List<Transaction> getTransactionsSortedByFeeRate() {
        return List.copyOf(_mempool);
    }

    private void logTransactionStoredEvent(Transaction transaction) {
        TransactionStoredInMemPoolTraceEvent event = new TransactionStoredInMemPoolTraceEvent(
                getSimulationContext().getSystemClock().getCurrentTime(), transaction);
        getTraceEventLogger().logEvent(event);
    }

    private void logTransactionRemovedEvent(Transaction transaction) {
        TransactionRemovedFromMemPoolTraceEvent event = new TransactionRemovedFromMemPoolTraceEvent(
                getSimulationContext().getSystemClock().getCurrentTime(), transaction);
        getTraceEventLogger().logEvent(event);
    }
}
