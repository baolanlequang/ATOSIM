package org.palladiosimulator.blockchainsystems.core.block.abstractions;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.Taggable;
import org.palladiosimulator.blockchainsystems.core.propagation.Propagatable;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

import java.util.Set;

public interface Block extends Taggable, Propagatable {
    String getHash();
    String getPreviousHash();
    String getOriginId();
    int getSize();
    long getBlockMinedTimestamp();
    Set<Transaction> getTransactions();
}
