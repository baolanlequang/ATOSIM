package org.palladiosimulator.blockchainsystems.threesim.creation;

import org.palladiosimulator.blockchainsystems.core.mining.MiningProcessImpl;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.MiningProcess;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.MiningProcessFactory;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.ResourcePowerCalculator;

import java.util.random.RandomGenerator;

public class ThreesimMiningProcessFactory implements MiningProcessFactory {

    private final double _meanBlockTime;
    private final ResourcePowerCalculator _resourcePowerCalculator;

    public ThreesimMiningProcessFactory(double meanBlockTime, ResourcePowerCalculator resourcePowerCalculator) {
        if (meanBlockTime <= 0.0) throw new IllegalArgumentException("meanBlockTime must be > 0, but was " + meanBlockTime);
        _meanBlockTime = meanBlockTime;
        _resourcePowerCalculator = resourcePowerCalculator;
    }

    @Override
    public MiningProcess createMiningProcess(String nodeId) {
        Double nodeResourcePower = _resourcePowerCalculator.getResourcePowerOfNode(nodeId);
        if (nodeResourcePower == null) throw new IllegalArgumentException(
                "Node with ID " + nodeId + " does not have a defined resource power.");
        if (nodeResourcePower <= 0.0) throw new IllegalArgumentException(
                "Node with ID " + nodeId + " has non-positive resource power: " + nodeResourcePower);

        double globalResourcePower = _resourcePowerCalculator.calculateGlobalResourcePower();
        if (globalResourcePower <= 0.0) throw new IllegalArgumentException(
                "Global resource power must be > 0, but was " + globalResourcePower);

        double share = nodeResourcePower / globalResourcePower;
        if (share <= 0.0) throw new IllegalArgumentException(
                "Node with ID " + nodeId + " has non-positive resource power share: " + share);

        double nodeMeanBlockTime = _meanBlockTime / share;
        return new MiningProcessImpl(nodeMeanBlockTime, RandomGenerator.of("Random"));
    }
}
