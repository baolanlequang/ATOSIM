package org.palladiosimulator.blockchainsystems.core.propagation.block;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.propagation.PropagationStrategy;
import org.palladiosimulator.blockchainsystems.core.propagation.PropagationStrategyFactory;

public class BlockPropagationStrategyFactoryImpl implements PropagationStrategyFactory<Block> {

    @Override
    public PropagationStrategy<Block> createPropagationStrategy() {
        return new BlockPropagationStrategy();
    }
}
