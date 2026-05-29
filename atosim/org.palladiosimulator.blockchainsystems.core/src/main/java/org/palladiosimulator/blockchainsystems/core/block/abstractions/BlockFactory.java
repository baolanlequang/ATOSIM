package org.palladiosimulator.blockchainsystems.core.block.abstractions;

import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

import java.util.Set;

public interface BlockFactory {
    Block createGenesisBlock();

    Block createBlock(
            String hash,
            String previousHash,
            String originId,
            long blockMinedTimestamp,
            int blockSize,
            Set<Transaction> transactions
    );

    Block createBlock(
            String hash,
            String previousHash,
            String originId,
            long blockMinedTimestamp,
            int blockSize,
            Set<Transaction> transactions,
            Set<String> tags
    );
}
