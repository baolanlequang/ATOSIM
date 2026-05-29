package org.palladiosimulator.blockchainsystems.core.system.abstractions;

public interface BlockchainSystemNodeBehaviorFactory {
    BlockchainSystemNodeBehavior create(String nodeId);
}
