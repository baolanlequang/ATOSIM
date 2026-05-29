package org.palladiosimulator.blockchainsystems.core.system.abstractions;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface ReadonlyBlockchain {
    Set<Block> getBlocks();
    Set<Block> getLastBlocksOfLongestChains();
    Set<Block> getBlocksAtPosition(long position);
    long getPositionOfBlock(Block block);
    long getLength();
    boolean hasBlockWithHash(String hash);
    Block getBlock(String hash);
    Set<Block> getImmediateSuccessorBlocks(String hash);
    Set<Block> getSuccessorBlocks(String hash);
    long getLongestSuccessorChainLength(String hash);
    List<ArrayList<Block>> getLongestChains();
}
