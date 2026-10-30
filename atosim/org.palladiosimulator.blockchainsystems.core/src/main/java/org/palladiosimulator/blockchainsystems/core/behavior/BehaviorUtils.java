package org.palladiosimulator.blockchainsystems.core.behavior;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockAppendingResult;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockAppendingResultType;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockType;
import org.palladiosimulator.blockchainsystems.core.propagation.block.BlockPropagationStrategy;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeContext;

import java.util.HashSet;
import java.util.Set;

public class BehaviorUtils {

    public static final BehaviorUtils INSTANCE = new BehaviorUtils();

    public boolean appendBlockToBlockchain(Block block, BlockchainSystemNodeContext context) {
        AppendOutcome outcome = appendBlockToBlockchainDetailed(block, context);
        return outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING;
    }

    public AppendOutcome appendBlockToBlockchainDetailed(Block block, BlockchainSystemNodeContext context) {
        return appendBlockToBlockchainInternal(block, context, new HashSet<>());
    }

    private AppendOutcome appendBlockToBlockchainInternal(
            Block block,
            BlockchainSystemNodeContext context,
            Set<String> visited
    ) {
        if (visited.contains(block.getHash())) return AppendOutcome.ALREADY_APPENDED;
        visited.add(block.getHash());

        BlockAppendingResult blockAppendingResult = context.getBlockchain().appendBlock(block);

        if (blockAppendingResult.getType() == BlockAppendingResultType.Appended) {
            Set<Block> orphanBlocks = context.getOrphanBlockPool().takeBlocksByPreviousBlockHash(block.getHash());
            for (Block orphanBlock : orphanBlocks) {
                AppendOutcome orphanOutcome = appendBlockToBlockchainInternal(orphanBlock, context, visited);
                if (orphanOutcome == AppendOutcome.INCLUDED || orphanOutcome == AppendOutcome.FORKING) {
                    context.getBlockPropagationStrategy().distribute(orphanBlock);
                }
            }

            BlockType blockType = blockAppendingResult.getBlockType();
            if (blockType == BlockType.IncludedBlock) return AppendOutcome.INCLUDED;
            if (blockType == BlockType.ForkingBlock) return AppendOutcome.FORKING;
            if (blockType == BlockType.StaleBlock) return AppendOutcome.STALE;
            return AppendOutcome.NOT_APPENDED;

        } else if (blockAppendingResult.getType() == BlockAppendingResultType.NotAppendedBecauseOrphanBlock) {
            // Ancestor fill (AC_fix_plan item 1.4): the gossip layer no longer re-delivers a block
            // this node has already received (BlockPropagationStrategy's known-block set), so a
            // parent the behaviour received but deliberately did not store (an attacker holding a
            // hidden lead) would otherwise never arrive and this block would stay an orphan for
            // good. Take the parent's body from the node's own store and append it first, without
            // calling the behaviour -- no strategy decision is re-triggered. Only the behaviour's
            // own decision to append a descendant reaches this point.
            Block parent = knownParentFromNetwork(block, context);
            if (parent != null && !visited.contains(parent.getHash())) {
                AppendOutcome parentOutcome = appendBlockToBlockchainInternal(parent, context, visited);
                if (parentOutcome == AppendOutcome.INCLUDED || parentOutcome == AppendOutcome.FORKING) {
                    context.getBlockPropagationStrategy().distribute(parent);
                }
                if (context.getBlockchain().hasBlockWithHash(parent.getHash())) {
                    visited.remove(block.getHash());
                    return appendBlockToBlockchainInternal(block, context, visited);
                }
            }
            context.getOrphanBlockPool().storeBlock(block);
            return AppendOutcome.ORPHAN;

        } else if (blockAppendingResult.getType() == BlockAppendingResultType.AlreadyAppended) {
            return AppendOutcome.ALREADY_APPENDED;

        } else {
            return AppendOutcome.NOT_APPENDED;
        }
    }

    // The parent's body, if this node received it over the network before and has not stored it
    // in its chain or orphan pool. A block this node mined itself is never filled in: an attacker's
    // own hidden block must only become public through the behaviour's own publication logic.
    private Block knownParentFromNetwork(Block block, BlockchainSystemNodeContext context) {
        String previousHash = block.getPreviousHash();
        if (previousHash == null) return null;
        if (!(context.getBlockPropagationStrategy() instanceof BlockPropagationStrategy blockPropagation)) return null;
        Block parent = blockPropagation.getKnownBlock(previousHash);
        if (parent == null) return null;
        if (context.getBlockchain().hasBlockWithHash(previousHash)
                || context.getOrphanBlockPool().hasBlockWithHash(previousHash)) return null;
        if (parent.getOriginId() != null && parent.getOriginId().equals(context.getId())) return null;
        return parent;
    }
}
