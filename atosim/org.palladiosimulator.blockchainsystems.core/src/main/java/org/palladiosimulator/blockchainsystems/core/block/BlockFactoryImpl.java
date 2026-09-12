package org.palladiosimulator.blockchainsystems.core.block;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockFactory;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

import java.util.HashSet;
import java.util.Set;

public class BlockFactoryImpl implements BlockFactory {

    @Override
    public Block createGenesisBlock(String hash) {
        return new BlockImpl(
                hash,
                null, null,
                0, 0,
                new HashSet<>(),
                new HashSet<>()
        );
    }

    @Override
    public Block createBlock(
            String hash, String previousHash, String originId,
            long blockMinedTimestamp, int blockSize, Set<Transaction> transactions
    ) {
        return new BlockImpl(hash, previousHash, originId, blockMinedTimestamp, blockSize, transactions, new HashSet<>());
    }

    @Override
    public Block createBlock(
            String hash, String previousHash, String originId,
            long blockMinedTimestamp, int blockSize, Set<Transaction> transactions, Set<String> tags
    ) {
        return new BlockImpl(hash, previousHash, originId, blockMinedTimestamp, blockSize, transactions, tags);
    }
}
