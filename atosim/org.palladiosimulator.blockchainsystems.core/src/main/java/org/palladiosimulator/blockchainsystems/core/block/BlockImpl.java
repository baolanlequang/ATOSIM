package org.palladiosimulator.blockchainsystems.core.block;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

import java.util.Set;

public class BlockImpl implements Block {

    private final String _hash;
    private final String _previousHash;
    private final String _originId;
    private final long _blockMinedTimestamp;
    private final int _size;
    private final Set<Transaction> _transactions;
    private final Set<String> _tags;

    public BlockImpl(
            String hash,
            String previousHash,
            String originId,
            long blockMinedTimestamp,
            int size,
            Set<Transaction> transactions,
            Set<String> tags
    ) {
        _hash = hash;
        _previousHash = previousHash;
        _originId = originId;
        _blockMinedTimestamp = blockMinedTimestamp;
        _size = size;
        _transactions = transactions;
        _tags = tags;
    }

    @Override
    public String getHash() { return _hash; }

    @Override
    public String getPreviousHash() { return _previousHash; }

    @Override
    public String getOriginId() { return _originId; }

    @Override
    public int getSize() { return _size; }

    @Override
    public long getBlockMinedTimestamp() { return _blockMinedTimestamp; }

    @Override
    public Set<Transaction> getTransactions() { return _transactions; }

    @Override
    public boolean hasTag(String tag) { return _tags.contains(tag); }

    public Set<String> getTags() { return _tags; }
}
