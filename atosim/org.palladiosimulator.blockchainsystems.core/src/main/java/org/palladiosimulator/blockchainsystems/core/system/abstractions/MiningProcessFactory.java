package org.palladiosimulator.blockchainsystems.core.system.abstractions;

public interface MiningProcessFactory {
    MiningProcess createMiningProcess(String nodeId);
}
