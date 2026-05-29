package org.palladiosimulator.blockchainsystems.core.transaction.abstractions;

import org.palladiosimulator.blockchainsystems.core.propagation.Propagatable;

public interface Transaction extends Propagatable {
    String getTxId();
    int getSize();
    long getCreationTime();
    String getSenderId();
    String getOriginNodeId();
    String getRecipientId();
    double getAmount();
    double getFee();
}
