package org.palladiosimulator.blockchainsystems.core.blockchain;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.Blockchain;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainFactory;

public class BlockchainFactoryImpl implements BlockchainFactory {

    private final int _numberOfRequiredSecurityConfirmations;

    public BlockchainFactoryImpl(int numberOfRequiredSecurityConfirmations) {
        _numberOfRequiredSecurityConfirmations = numberOfRequiredSecurityConfirmations;
    }

    @Override
    public Blockchain createBlockchain(Block genesisBlock, String nodeId) {
        BlockchainElement genesisElement = new BlockchainElement(
                genesisBlock, null, BlockchainElementType.Included, 1);
        return new BlockchainImpl(genesisElement, _numberOfRequiredSecurityConfirmations);
    }
}
