package org.palladiosimulator.blockchainsystems.core.block.abstractions;

public interface BlockValidatorFactory {
    BlockValidator createBlockValidator(String nodeId);
}
