package org.palladiosimulator.blockchainsystems.core.blockchain;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockAppendingResult;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockType;
import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.Blockchain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockchainImpl extends BlockchainNodeObject implements Blockchain {

    private static final long INITIAL_BLOCKCHAIN_LENGTH = 1L;
    private static final long UNKNOWN_BLOCK_POSITION_RESULT = -1L;

    private final BlockchainElement _genesisBlock;
    private final int _numberOfRequiredSecurityConfirmations;
    private final HashSet<BlockchainElement> _longestChainsLastBlocks;
    private final HashMap<String, BlockchainElement> _blockchainElementsMap;
    private long _length;

    public BlockchainImpl(BlockchainElement genesisBlock, int numberOfRequiredSecurityConfirmations) {
        _genesisBlock = genesisBlock;
        _numberOfRequiredSecurityConfirmations = numberOfRequiredSecurityConfirmations;
        _longestChainsLastBlocks = new HashSet<>();
        _longestChainsLastBlocks.add(genesisBlock);
        _blockchainElementsMap = new HashMap<>();
        _blockchainElementsMap.put(genesisBlock.getBlock().getHash(), genesisBlock);
        _length = INITIAL_BLOCKCHAIN_LENGTH;
    }

    @Override
    public void dispatchEvent(Event event) {
    }

    @Override
    public Set<Block> getLastBlocksOfLongestChains() {
        return _longestChainsLastBlocks.stream().map(BlockchainElement::getBlock).collect(Collectors.toSet());
    }

    @Override
    public boolean hasBlockWithHash(String hash) {
        return _blockchainElementsMap.containsKey(hash);
    }

    @Override
    public Block getBlock(String hash) {
        BlockchainElement el = _blockchainElementsMap.get(hash);
        return el != null ? el.getBlock() : null;
    }

    @Override
    public BlockAppendingResult appendBlock(Block block) {
        if (hasBlockWithHash(block.getHash())) {
            return BlockAppendingResult.createBlockAlreadyAppendedResult();
        }

        BlockchainElement prev = _blockchainElementsMap.get(block.getPreviousHash());
        if (prev == null) {
            return BlockAppendingResult.createBlockNoAppendedBecauseOrphanBlockResult();
        }

        long newPos = prev.getPosition() + 1;
        if (_length < newPos) {
            appendIncludedBlock(block, prev, newPos);
            return BlockAppendingResult.createBlockAppendedResult(BlockType.IncludedBlock);
        } else if (_length == newPos) {
            appendForkingBlock(block, prev, newPos);
            return BlockAppendingResult.createBlockAppendedResult(BlockType.ForkingBlock);
        } else {
            appendStaleBlock(block, prev, newPos);
            return BlockAppendingResult.createBlockAppendedResult(BlockType.StaleBlock);
        }
    }

    private void appendIncludedBlock(Block block, BlockchainElement prev, long pos) {
        BlockchainElement el = new BlockchainElement(block, prev, BlockchainElementType.Included, pos);
        _blockchainElementsMap.put(block.getHash(), el);
        _length = pos;

        _longestChainsLastBlocks.remove(prev);
        HashSet<BlockchainElement> stale = new HashSet<>(_longestChainsLastBlocks);
        _longestChainsLastBlocks.clear();
        _longestChainsLastBlocks.add(el);

        logBlockAppended(block, pos, prev.getBlock(), BlockType.IncludedBlock);

        for (BlockchainElement be : stale) {
            traverseAndChangeTypes(be, BlockchainElementType.Forking, BlockchainElementType.Stale);
        }
        traverseAndChangeTypes(prev, BlockchainElementType.Forking, BlockchainElementType.Included);
        markConfirmedBlocks(el);
    }

    private void appendForkingBlock(Block block, BlockchainElement prev, long pos) {
        BlockchainElement el = new BlockchainElement(block, prev, BlockchainElementType.Forking, pos);
        _blockchainElementsMap.put(block.getHash(), el);
        _longestChainsLastBlocks.add(el);

        logBlockAppended(block, pos, prev.getBlock(), BlockType.ForkingBlock);

        BlockchainElement forkOrigin = getForkOrigin();
        for (BlockchainElement be : _longestChainsLastBlocks) {
            traverseUntilAndChangeToForking(be, forkOrigin);
        }
    }

    private void appendStaleBlock(Block block, BlockchainElement prev, long pos) {
        BlockchainElement el = new BlockchainElement(block, prev, BlockchainElementType.Stale, pos);
        _blockchainElementsMap.put(block.getHash(), el);
        logBlockAppended(block, pos, prev.getBlock(), BlockType.StaleBlock);
    }

    private void traverseUntilAndChangeToForking(BlockchainElement start, BlockchainElement until) {
        BlockchainElement cur = start;
        while (cur != until) {
            changeBlockType(cur, BlockchainElementType.Forking);
            cur = cur.getPreviousBlockchainElement();
        }
    }

    private void markConfirmedBlocks(BlockchainElement start) {
        BlockchainElement cur = start;
        for (int i = 0; i < _numberOfRequiredSecurityConfirmations; i++) {
            if (cur == null) return;
            cur = cur.getPreviousBlockchainElement();
        }
        if (cur == null) return;
        traverseAndChangeTypes(cur, BlockchainElementType.Included, BlockchainElementType.Confirmed);
    }

    private BlockchainElement getForkOrigin() {
        HashSet<BlockchainElement> elements = new HashSet<>();
        long min = _longestChainsLastBlocks.stream().mapToLong(BlockchainElement::getPosition).min().orElse(0);

        for (BlockchainElement be : _longestChainsLastBlocks) {
            BlockchainElement cur = be;
            while (cur != null && cur.getPosition() > min) cur = cur.getPreviousBlockchainElement();
            if (cur != null) elements.add(cur);
        }

        while (elements.size() > 1) {
            HashSet<BlockchainElement> prev = new HashSet<>();
            for (BlockchainElement be : elements) {
                if (be.getPreviousBlockchainElement() != null) prev.add(be.getPreviousBlockchainElement());
            }
            elements = prev;
        }
        return elements.iterator().next();
    }

    private void traverseAndChangeTypes(BlockchainElement start, BlockchainElementType whileType, BlockchainElementType newType) {
        BlockchainElement cur = start;
        while (cur != null && cur.getType() == whileType) {
            changeBlockType(cur, newType);
            cur = cur.getPreviousBlockchainElement();
        }
    }

    private void changeBlockType(BlockchainElement el, BlockchainElementType newType) {
        if (el.getType() == newType) return;
        BlockchainElementType old = el.getType();
        el.setType(newType);
        logBlockTypeChanged(el, old, newType);
    }

    private void logBlockTypeChanged(BlockchainElement el, BlockchainElementType oldType, BlockchainElementType newType) {
        if (!getTraceEventLogger().isEventTypeEnabled(BlockTypeChangedTraceEvent.EVENT_TYPE)) return;
        getTraceEventLogger().logEvent(new BlockTypeChangedTraceEvent(
                getSimulationContext().getSystemClock().getCurrentTime(),
                el.getBlock(), toBlockType(oldType), toBlockType(newType)));
    }

    private void logBlockAppended(Block block, long pos, Block prev, BlockType type) {
        if (!getTraceEventLogger().isEventTypeEnabled(BlockAppendedTraceEvent.EVENT_TYPE)) return;
        getTraceEventLogger().logEvent(new BlockAppendedTraceEvent(
                getSimulationContext().getSystemClock().getCurrentTime(), block, pos, prev, type));
    }

    @Override
    public Set<Block> getBlocksAtPosition(long position) {
        if (position < INITIAL_BLOCKCHAIN_LENGTH) return Collections.emptySet();
        return _blockchainElementsMap.values().stream()
                .filter(e -> e.getPosition() == position).map(BlockchainElement::getBlock).collect(Collectors.toSet());
    }

    @Override
    public long getLength() { return _length; }

    @Override
    public Set<Block> getBlocks() {
        return _blockchainElementsMap.values().stream().map(BlockchainElement::getBlock).collect(Collectors.toSet());
    }

    @Override
    public long getPositionOfBlock(Block block) {
        BlockchainElement el = _blockchainElementsMap.get(block.getHash());
        return el != null ? el.getPosition() : UNKNOWN_BLOCK_POSITION_RESULT;
    }

    @Override
    public Set<Block> getSuccessorBlocks(String hash) {
        if (!hasBlockWithHash(hash)) return null;
        BlockchainElement start = _blockchainElementsMap.get(hash);
        HashSet<BlockchainElement> successors = new HashSet<>();
        Set<BlockchainElement> immediate = start.getNextBlockchainElements();
        while (!immediate.isEmpty()) {
            HashSet<BlockchainElement> next = new HashSet<>();
            for (BlockchainElement be : immediate) next.addAll(be.getNextBlockchainElements());
            successors.addAll(immediate);
            immediate = next;
        }
        return successors.stream().map(BlockchainElement::getBlock).collect(Collectors.toSet());
    }

    @Override
    public Set<Block> getImmediateSuccessorBlocks(String hash) {
        if (!hasBlockWithHash(hash)) return null;
        return _blockchainElementsMap.get(hash).getNextBlockchainElements()
                .stream().map(BlockchainElement::getBlock).collect(Collectors.toSet());
    }

    @Override
    public long getLongestSuccessorChainLength(String hash) {
        if (!hasBlockWithHash(hash)) return 0;
        Set<BlockchainElement> successors = _blockchainElementsMap.get(hash).getNextBlockchainElements();
        long length = 0;
        while (!successors.isEmpty()) {
            HashSet<BlockchainElement> next = new HashSet<>();
            for (BlockchainElement be : successors) next.addAll(be.getNextBlockchainElements());
            successors = next;
            length++;
        }
        return length;
    }

    @Override
    public List<ArrayList<Block>> getLongestChains() {
        List<ArrayList<Block>> result = new ArrayList<>();
        for (BlockchainElement tip : _longestChainsLastBlocks) {
            ArrayList<Block> chain = new ArrayList<>();
            BlockchainElement cur = tip;
            while (cur != null) {
                chain.add(cur.getBlock());
                cur = cur.getPreviousBlockchainElement();
            }
            Collections.reverse(chain);
            result.add(chain);
        }
        return result;
    }

    private static BlockType toBlockType(BlockchainElementType type) {
        return switch (type) {
            case Forking -> BlockType.ForkingBlock;
            case Included -> BlockType.IncludedBlock;
            case Stale -> BlockType.StaleBlock;
            case Confirmed -> BlockType.ConfirmedBlock;
        };
    }
}
