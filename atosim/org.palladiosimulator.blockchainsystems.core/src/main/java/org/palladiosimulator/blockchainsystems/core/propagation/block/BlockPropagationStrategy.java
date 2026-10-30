package org.palladiosimulator.blockchainsystems.core.propagation.block;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.network.MessageDroppedTraceEvent;
import org.palladiosimulator.blockchainsystems.core.propagation.GossipPropagationStrategy;
import org.palladiosimulator.blockchainsystems.core.propagation.MessageImpl;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.Message;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkEndpoint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockPropagationStrategy extends GossipPropagationStrategy<Block> {

    private static final String INV_MESSAGE_KEY = "BLOCK_INV";
    private static final String GET_DATA_MESSAGE_KEY = "BLOCK_GET_DATA";
    private static final String ELEMENT_MESSAGE_KEY = "BLOCK_MSG";

    private static final int MESSAGE_HEADER_BYTE_SIZE = 24;
    private static final int INV_MESSAGE_BYTE_SIZE = 20;
    private static final int GET_DATA_MESSAGE_BYTE_SIZE = 20;

    private final Set<String> _requestedBlockIds = new HashSet<>();

    // Known-block set (AC_fix_plan item 1.2/1.3). A hash enters on first receipt of its body
    // (handleElementMessageReceived) and on local mining (markKnown, called from
    // BlockchainSystemNode's mining callback) -- never at request time, since a GETDATA that is
    // never answered would then block the block for good. Every later announcement or body for a
    // known hash is dropped here and never reaches the behaviour, the validator or forwarding --
    // in particular the repeated copies of an honest block an attacker received but deliberately
    // did not store while holding a hidden lead. The value is the body itself, kept for
    // BehaviorUtils' ancestor fill (getKnownBlock). Membership-only use, never iterated, so a
    // HashMap does not introduce iteration-order nondeterminism.
    private final Map<String, Block> _knownBlocks = new HashMap<>();

    // Duplicate counters (Q4: kept separate). "ChainOrPool" = caught by the pre-existing
    // chain/orphan-pool checks; "KnownSetOnly" = caught only by the new known-block set.
    private long _duplicateBodiesDroppedChainOrPool = 0;
    private long _duplicateBodiesDroppedKnownSetOnly = 0;
    private long _duplicateInvsIgnoredChainOrPool = 0;
    private long _duplicateInvsIgnoredKnownSetOnly = 0;

    @Override
    public void markKnown(Block block) {
        _knownBlocks.putIfAbsent(block.getHash(), block);
    }

    /** The body of a block this node has received or mined before, or null. */
    public Block getKnownBlock(String hash) {
        return hash != null ? _knownBlocks.get(hash) : null;
    }

    public long getDuplicateBodiesDroppedChainOrPool() { return _duplicateBodiesDroppedChainOrPool; }
    public long getDuplicateBodiesDroppedKnownSetOnly() { return _duplicateBodiesDroppedKnownSetOnly; }
    public long getDuplicateInvsIgnoredChainOrPool() { return _duplicateInvsIgnoredChainOrPool; }
    public long getDuplicateInvsIgnoredKnownSetOnly() { return _duplicateInvsIgnoredKnownSetOnly; }

    @Override protected String getInvMessageKey() { return INV_MESSAGE_KEY; }
    @Override protected String getGetDataMessageKey() { return GET_DATA_MESSAGE_KEY; }
    @Override protected String getElementMessageKey() { return ELEMENT_MESSAGE_KEY; }

    @Override
    protected Message createInvMessage(Block element) {
        return new MessageImpl(element.getHash(), INV_MESSAGE_KEY, MESSAGE_HEADER_BYTE_SIZE + INV_MESSAGE_BYTE_SIZE);
    }

    @Override
    protected Message createGetDataMessage(String elementId) {
        return new MessageImpl(elementId, GET_DATA_MESSAGE_KEY, MESSAGE_HEADER_BYTE_SIZE + GET_DATA_MESSAGE_BYTE_SIZE);
    }

    @Override
    protected Message createElementMessage(Block element) {
        return new MessageImpl(element, ELEMENT_MESSAGE_KEY, MESSAGE_HEADER_BYTE_SIZE + element.getSize());
    }

    @Override
    protected void handleInvMessageReceived(Message message, P2PNetworkEndpoint senderNetworkEndpoint) {
        String hash = (String) message.getContent();
        if (_context == null) return;

        if (_context.getBlockchain().hasBlockWithHash(hash) ||
                _context.getOrphanBlockPool().hasBlockWithHash(hash)) {
            _requestedBlockIds.remove(hash);
            _duplicateInvsIgnoredChainOrPool++;
            return;
        }

        if (_knownBlocks.containsKey(hash)) {
            _duplicateInvsIgnoredKnownSetOnly++;
            return;
        }

        if (!_requestedBlockIds.add(hash)) return;

        if (_networkInterface != null) {
            _networkInterface.send(createGetDataMessage(hash), senderNetworkEndpoint);
        }
    }

    @Override
    protected void handleGetDataMessageReceived(Message message, P2PNetworkEndpoint senderNetworkEndpoint) {
        String hash = (String) message.getContent();
        if (_context == null) return;
        Block block = _context.getBlockchain().getBlock(hash);
        if (block != null && _networkInterface != null) {
            _networkInterface.send(createElementMessage(block), senderNetworkEndpoint);
            logBlockSent(block, senderNetworkEndpoint);
        }
    }

    @Override
    protected void handleElementMessageReceived(Message message, P2PNetworkEndpoint senderNetworkEndpoint) {
        Block block = (Block) message.getContent();
        if (_context == null) return;

        _requestedBlockIds.remove(block.getHash());

        if (_context.getBlockchain().hasBlockWithHash(block.getHash()) ||
                _context.getOrphanBlockPool().hasBlockWithHash(block.getHash())) {
            _duplicateBodiesDroppedChainOrPool++;
            return;
        }

        if (_knownBlocks.containsKey(block.getHash())) {
            _duplicateBodiesDroppedKnownSetOnly++;
            return;
        }
        _knownBlocks.put(block.getHash(), block);

        // A parent this node already knows (received or mined) is not fetched again: the
        // re-delivered body would reach the behaviour a second time. If the behaviour later
        // appends this block, BehaviorUtils' ancestor fill supplies the parent from _knownBlocks.
        String previousHash = block.getPreviousHash();
        if (previousHash != null &&
                !_context.getBlockchain().hasBlockWithHash(previousHash) &&
                !_context.getOrphanBlockPool().hasBlockWithHash(previousHash) &&
                !_knownBlocks.containsKey(previousHash) &&
                _requestedBlockIds.add(previousHash)) {
            if (_networkInterface != null) {
                _networkInterface.send(createGetDataMessage(previousHash), senderNetworkEndpoint);
            }
        }

        logBlockReceived(block, senderNetworkEndpoint);
        notifyBlockReceived(block);
    }

    @Override
    protected void handleMessageDropped(Message message, P2PNetworkEndpoint recipientNetworkEndpoint) {
        if (_networkInterface == null) {
            throw new IllegalStateException("Network interface is not set for BlockPropagationStrategy.");
        }
        getTraceEventLogger().logEvent(new MessageDroppedTraceEvent(
                message,
                getSimulationContext().getSystemClock().getCurrentTime(),
                recipientNetworkEndpoint,
                _networkInterface
        ));
    }

    private void notifyBlockReceived(Block block) {
        if (_onReceivedCallback != null) _onReceivedCallback.accept(block);
    }

    private void logBlockSent(Block block, P2PNetworkEndpoint receiverNetworkEndpoint) {
        getTraceEventLogger().logEvent(new BlockSentTraceEvent(
                getSimulationContext().getSystemClock().getCurrentTime(),
                block, receiverNetworkEndpoint));
    }

    private void logBlockReceived(Block block, P2PNetworkEndpoint senderNetworkEndpoint) {
        getTraceEventLogger().logEvent(new BlockReceivedTraceEvent(
                getSimulationContext().getSystemClock().getCurrentTime(),
                block, senderNetworkEndpoint));
    }
}
