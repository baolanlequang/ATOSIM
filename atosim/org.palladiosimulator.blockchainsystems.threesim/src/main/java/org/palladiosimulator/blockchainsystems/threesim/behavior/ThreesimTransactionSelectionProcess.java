package org.palladiosimulator.blockchainsystems.threesim.behavior;

import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeContext;
import org.palladiosimulator.blockchainsystems.core.transaction.TransactionSelectionResult;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TransactionSelectionProcess;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThreesimTransactionSelectionProcess extends BlockchainNodeObject implements TransactionSelectionProcess {

    private final int _maxBlockSize;

    public ThreesimTransactionSelectionProcess(int maxBlockSize) {
        _maxBlockSize = maxBlockSize;
    }

    @Override
    public TransactionSelectionResult selectTransactionsForBlock(BlockchainSystemNodeContext context) {
        int currentBlockSize = 0;
        Set<Transaction> selected = new HashSet<>();
        List<Transaction> sorted = context.getTrxMemPool().getTransactionsSortedByFeeRate();
        // Item 7: a transaction that doesn't fit is skipped, not treated as the end of packing --
        // sorted is by fee rate, not by size, so a later (lower-fee) transaction can still be
        // smaller and fit even after an earlier, higher-fee one didn't. The loop naturally stops
        // once every remaining transaction has been tried, which is exactly "stop only once
        // nothing that could still fit remains" for this selector (a fixed, finite, already-known
        // candidate list, unlike the deterministic-full-block selector's regenerable distribution
        // -- no separate capacity-vs-floor precondition is needed here to guarantee termination).
        for (Transaction tx : sorted) {
            int newSize = currentBlockSize + tx.getSize();
            if (newSize > _maxBlockSize) continue;
            currentBlockSize = newSize;
            selected.add(tx);
        }
        return new TransactionSelectionResult(selected, currentBlockSize);
    }

    @Override
    public void dispatchEvent(Event event) {
    }
}
