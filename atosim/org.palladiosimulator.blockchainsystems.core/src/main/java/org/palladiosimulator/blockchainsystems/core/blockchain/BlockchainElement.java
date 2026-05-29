package org.palladiosimulator.blockchainsystems.core.blockchain;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BlockchainElement {

    private final Block _block;
    private final BlockchainElement _previousBlockchainElement;
    private BlockchainElementType _type;
    private final long _position;
    private final HashSet<BlockchainElement> _nextBlockchainElements = new HashSet<>();

    public BlockchainElement(
            Block block,
            BlockchainElement previousBlockchainElement,
            BlockchainElementType type,
            long position
    ) {
        _block = block;
        _previousBlockchainElement = previousBlockchainElement;
        _type = type;
        _position = position;
        if (previousBlockchainElement != null) {
            previousBlockchainElement._nextBlockchainElements.add(this);
        }
    }

    public Block getBlock() { return _block; }
    public BlockchainElement getPreviousBlockchainElement() { return _previousBlockchainElement; }
    public BlockchainElementType getType() { return _type; }
    public void setType(BlockchainElementType type) { _type = type; }
    public long getPosition() { return _position; }

    public Set<BlockchainElement> getNextBlockchainElements() {
        return Collections.unmodifiableSet(_nextBlockchainElements);
    }
}
