package org.palladiosimulator.blockchainsystems.core.behavior;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockAppendingResult;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockAppendingResultType;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockType;
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
            context.getOrphanBlockPool().storeBlock(block);
            return AppendOutcome.ORPHAN;

        } else if (blockAppendingResult.getType() == BlockAppendingResultType.AlreadyAppended) {
            return AppendOutcome.ALREADY_APPENDED;

        } else {
            return AppendOutcome.NOT_APPENDED;
        }
    }
}
