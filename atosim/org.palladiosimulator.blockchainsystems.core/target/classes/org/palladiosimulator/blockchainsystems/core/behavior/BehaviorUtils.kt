package org.palladiosimulator.blockchainsystems.core.behavior

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockAppendingResultType
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockType
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeContext

enum class AppendOutcome {
  INCLUDED,
  FORKING,
  STALE,
  ORPHAN,
  ALREADY_APPENDED,
  NOT_APPENDED
}

object BehaviorUtils {
  fun appendBlockToBlockchain(block: Block, context: BlockchainSystemNodeContext): Boolean {
    return when (appendBlockToBlockchainDetailed(block, context)) {
      AppendOutcome.INCLUDED,
      AppendOutcome.FORKING -> true
      else -> false
    }
  }

  fun appendBlockToBlockchainDetailed(
    block: Block,
    context: BlockchainSystemNodeContext
  ): AppendOutcome {
    return appendBlockToBlockchainInternal(block, context, mutableSetOf())
  }

  private fun appendBlockToBlockchainInternal(
    block: Block,
    context: BlockchainSystemNodeContext,
    visited: MutableSet<String>
  ): AppendOutcome {
    if (block.hash in visited) return AppendOutcome.ALREADY_APPENDED
    visited.add(block.hash)

    val blockAppendingResult = context.blockchain.appendBlock(block)

    return when (blockAppendingResult.type) {
      BlockAppendingResultType.Appended -> {
        val orphanBlocks = context.orphanBlockPool.takeBlocksByPreviousBlockHash(block.hash)
        orphanBlocks.forEach { orphanBlock ->
          when (appendBlockToBlockchainInternal(orphanBlock, context, visited)) {
            AppendOutcome.INCLUDED,
            AppendOutcome.FORKING -> {
              context.blockPropagationStrategy.distribute(orphanBlock)
            }

            AppendOutcome.STALE,
            AppendOutcome.ORPHAN,
            AppendOutcome.ALREADY_APPENDED,
            AppendOutcome.NOT_APPENDED -> {
            }
          }
        }

        when (blockAppendingResult.blockType) {
          BlockType.IncludedBlock -> AppendOutcome.INCLUDED
          BlockType.ForkingBlock -> AppendOutcome.FORKING
          BlockType.StaleBlock -> AppendOutcome.STALE
          else -> AppendOutcome.NOT_APPENDED
        }
      }

      BlockAppendingResultType.NotAppendedBecauseOrphanBlock -> {
        context.orphanBlockPool.storeBlock(block)
        AppendOutcome.ORPHAN
      }

      BlockAppendingResultType.AlreadyAppended -> AppendOutcome.ALREADY_APPENDED
      else -> AppendOutcome.NOT_APPENDED
    }
  }


}