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
        for (Transaction tx : sorted) {
            int newSize = currentBlockSize + tx.getSize();
            if (newSize > _maxBlockSize) break;
            currentBlockSize = newSize;
            selected.add(tx);
        }
        return new TransactionSelectionResult(selected, currentBlockSize);
    }

    @Override
    public void dispatchEvent(Event event) {
    }
}
