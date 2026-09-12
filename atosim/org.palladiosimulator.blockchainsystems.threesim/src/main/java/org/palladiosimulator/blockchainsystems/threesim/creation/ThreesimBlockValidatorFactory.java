package org.palladiosimulator.blockchainsystems.threesim.creation;

import org.palladiosimulator.blockchainsystems.bscm.blockchainsystemComponentRepository.BlockValiationDurationSpecification;
import org.palladiosimulator.blockchainsystems.bscm.blockchainsystemComponentRepository.BlockValidatorComponent;
import org.palladiosimulator.blockchainsystems.bscm.nodeallocation.NodeAllocationContext;
import org.palladiosimulator.blockchainsystems.core.block.BlockValidatorImpl;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockValidator;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockValidatorFactory;
import org.palladiosimulator.blockchainsystems.threesim.creation.abstractions.NodeAllocationResolver;
import org.palladiosimulator.blockchainsystems.threesim.simulation.DeterministicSeeds;

public class ThreesimBlockValidatorFactory implements BlockValidatorFactory {

    private final NodeAllocationResolver _nodeAllocationResolver;
    private final boolean _staticValidationDelayEnabled;
    private final long _rootSeed;

    public ThreesimBlockValidatorFactory(NodeAllocationResolver nodeAllocationResolver, boolean staticValidationDelayEnabled, long rootSeed) {
        _nodeAllocationResolver = nodeAllocationResolver;
        _staticValidationDelayEnabled = staticValidationDelayEnabled;
        _rootSeed = rootSeed;
    }

    @Override
    public BlockValidator createBlockValidator(String nodeId) {
        var allocation = _nodeAllocationResolver.getNodeAllocation(nodeId);
        if (allocation == null) throw new IllegalArgumentException(
                "No BlockValidatorComponent found for node with ID: " + nodeId);

        BlockValidatorComponent component = null;
        for (NodeAllocationContext ctx : allocation.getAllocationContexts()) {
            if (ctx.getAssemblyContext().getEncapsulatedComponent() instanceof BlockValidatorComponent c) {
                component = c;
                break;
            }
        }
        if (component == null) throw new IllegalArgumentException(
                "No BlockValidatorComponent found for node with ID: " + nodeId);

        BlockValidationDurationProviderAdapter adapter = BlockValidationDurationProviderAdapter.create(
                component.getValidationDuration(), DeterministicSeeds.seededGenerator(_rootSeed, "validation:" + nodeId));
        return new BlockValidatorImpl(adapter, _staticValidationDelayEnabled);
    }
}
