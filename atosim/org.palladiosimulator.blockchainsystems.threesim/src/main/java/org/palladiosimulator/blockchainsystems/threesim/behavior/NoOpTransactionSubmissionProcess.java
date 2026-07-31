package org.palladiosimulator.blockchainsystems.threesim.behavior;

import org.palladiosimulator.blockchainsystems.core.common.BlockchainSimulationObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TransactionSubmissionProcess;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TransactionSubmittedCallbackSubscriber;

import java.util.function.Supplier;

/**
 * Stand-in for BlockchainSystem's required transaction-submission lifecycle when transaction
 * generation mode is {@code DETERMINISTIC_FULL_BLOCK}: no transaction ever arrives, since block
 * content is manufactured directly at block-creation time instead.
 */
public class NoOpTransactionSubmissionProcess extends BlockchainSimulationObject
        implements TransactionSubmissionProcess {

    public NoOpTransactionSubmissionProcess(String id, String name) {
        super(id, name);
    }

    @Override
    public void addOnTransactionSubmittedCallbackSubscriber(TransactionSubmittedCallbackSubscriber subscriber) {
    }

    @Override
    public void setOnSelectRecipientNodeIdCallback(Supplier<String> onSelectRecipientNodeIdCallback) {
    }

    @Override
    public void startTransactionSubmissionProcess() {
    }

    @Override
    public void stopTransactionSubmissionProcess() {
    }

    @Override
    public void dispatchEvent(Event event) {
    }
}
