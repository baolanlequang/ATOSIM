package org.palladiosimulator.blockchainsystems.core.system.abstractions;

public interface ResourcePowerCalculator {
    double calculateGlobalResourcePower();
    Double getResourcePowerOfNode(String nodeId);
}
