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
    private final long _validationSequence;
    private final HashSet<BlockchainElement> _nextBlockchainElements = new HashSet<>();

    public BlockchainElement(
            Block block,
            BlockchainElement previousBlockchainElement,
            BlockchainElementType type,
            long position,
            long validationSequence
    ) {
        _block = block;
        _previousBlockchainElement = previousBlockchainElement;
        _type = type;
        _position = position;
        _validationSequence = validationSequence;
        if (previousBlockchainElement != null) {
            previousBlockchainElement._nextBlockchainElements.add(this);
        }
    }

    public Block getBlock() { return _block; }
    public BlockchainElement getPreviousBlockchainElement() { return _previousBlockchainElement; }
    public BlockchainElementType getType() { return _type; }
    public void setType(BlockchainElementType type) { _type = type; }
    public long getPosition() { return _position; }

    /** Monotonically increasing per-node counter assigned when this element's block completed
     * validation and was appended to this node's local blockchain view -- the arrival order used
     * to break equal-work ties between candidate tips deterministically (see
     * {@link org.palladiosimulator.blockchainsystems.core.system.abstractions.ReadonlyBlockchain#getPreferredTipOfLongestChains()}),
     * instead of unordered {@code HashSet} iteration. */
    public long getValidationSequence() { return _validationSequence; }

    public Set<BlockchainElement> getNextBlockchainElements() {
        return Collections.unmodifiableSet(_nextBlockchainElements);
    }
}
