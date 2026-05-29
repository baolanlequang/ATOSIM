package org.palladiosimulator.blockchainsystems.core.block.abstractions;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.Traceable;

import java.util.function.BiConsumer;

public interface BlockValidator extends Traceable {
    void validateBlock(Block block);
    void setOnBlockValidatedCallback(BiConsumer<Block, Boolean> onBlockValidatedCallback);
}
