package org.palladiosimulator.blockchainsystems.threesim.behavior;

import org.palladiosimulator.blockchainsystems.core.common.BlockchainSimulationObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.ValueProvider;
import org.palladiosimulator.blockchainsystems.core.stochastics.PoissonProcess;
import org.palladiosimulator.blockchainsystems.core.transaction.TransactionFactoryImpl;
import org.palladiosimulator.blockchainsystems.core.transaction.TransactionProperties;
import org.palladiosimulator.blockchainsystems.core.transaction.TransactionSubmittedEvent;
import org.palladiosimulator.blockchainsystems.core.transaction.TransactionSubmittedTraceEvent;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TransactionSubmissionProcess;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TransactionSubmittedCallbackSubscriber;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

public class ThreesimTransactionSubmissionProcess extends BlockchainSimulationObject
        implements TransactionSubmissionProcess {

    private final PoissonProcess _poissonProcess;
    private final ValueProvider<TransactionProperties> _transactionPropertiesProvider;

    private Supplier<String> _onSelectRecipientNodeIdCallback;
    private final Set<TransactionSubmittedCallbackSubscriber> _subscribers = new HashSet<>();
    private boolean _isSubmitting = false;

    public ThreesimTransactionSubmissionProcess(
            String id,
            String name,
            double meanTransactionCreationTime,
            ValueProvider<TransactionProperties> transactionPropertiesProvider
    ) {
        super(id, name);
        _poissonProcess = new PoissonProcess(1.0 / meanTransactionCreationTime, RandomGenerator.of("Random"));
        _transactionPropertiesProvider = transactionPropertiesProvider;
    }

    @Override
    public void addOnTransactionSubmittedCallbackSubscriber(TransactionSubmittedCallbackSubscriber subscriber) {
        _subscribers.add(subscriber);
    }

    @Override
    public void setOnSelectRecipientNodeIdCallback(Supplier<String> callback) {
        _onSelectRecipientNodeIdCallback = callback;
    }

    @Override
    public void startTransactionSubmissionProcess() {
        if (_isSubmitting) return;
        _isSubmitting = true;
        scheduleNextEvent();
    }

    @Override
    public void stopTransactionSubmissionProcess() {
        _isSubmitting = false;
    }

    @Override
    public void dispatchEvent(Event event) {
        if (!TransactionSubmittedEvent.EVENT_TYPE.equals(event.getEventType())) return;
        if (!_isSubmitting) return;
        if (_onSelectRecipientNodeIdCallback == null) return;

        String recipientId = _onSelectRecipientNodeIdCallback.get();
        Transaction trx = createTransaction(event.getOccurrenceTime(), recipientId);
        logTrxSubmitted(trx);
        notifySubscribers(trx);
        scheduleNextEvent();
    }

    private Transaction createTransaction(long creationTime, String recipientId) {
        TransactionProperties props = _transactionPropertiesProvider.getValue();
        String senderId = UUID.randomUUID().toString();
        return new TransactionFactoryImpl().createTransaction(
                UUID.randomUUID().toString(),
                props.getSize(),
                creationTime,
                senderId,
                this.getId(),
                recipientId,
                props.getAmount(),
                props.getFee()
        );
    }

    private void logTrxSubmitted(Transaction trx) {
        getTraceEventLogger().logEvent(new TransactionSubmittedTraceEvent(
                getSimulationContext().getSystemClock().getCurrentTime(), trx));
    }

    private void notifySubscribers(Transaction trx) {
        for (TransactionSubmittedCallbackSubscriber s : _subscribers) s.onTransactionSubmitted(trx);
    }

    private void scheduleNextEvent() {
        long nextTime = getSimulationContext().getSystemClock().getCurrentTime() + _poissonProcess.nextPointDistance();
        getSimulationContext().getEventCoordinator().raiseEvent(new TransactionSubmittedEvent(nextTime, this));
    }
}
