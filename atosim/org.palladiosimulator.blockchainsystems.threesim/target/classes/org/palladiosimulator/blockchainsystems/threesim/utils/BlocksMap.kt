package org.palladiosimulator.blockchainsystems.threesim.utils

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block

/**
 * @property threshold The minimum number of nodes that must agree on a block for it to be considered valid.
 *
 * A block becomes "valid" once it has been observed by at least [threshold] distinct nodes.
 * The timestamp stored for a valid block is the first timestamp at which it crossed that threshold.
 */
class BlocksMap(
  private val threshold: Int
) {
  private data class Entry(
    val block: Block,
    val nodeIds: MutableSet<String> = mutableSetOf()
  )

  private val blocks: MutableMap<String, Entry> = mutableMapOf()
  private val timestamps: MutableMap<String, Long> = mutableMapOf()

  fun addNodeToBlock(block: Block, nodeId: String, timestamp: Long) {
    val entry = blocks.getOrPut(block.hash) { Entry(block) }

    val wasValidBefore = entry.nodeIds.size >= threshold
    val added = entry.nodeIds.add(nodeId)
    val isValidNow = entry.nodeIds.size >= threshold

    if (added && !wasValidBefore && isValidNow) {
      timestamps.putIfAbsent(block.hash, timestamp)
    }
  }

  fun removeNodeFromBlock(blockHash: String, nodeId: String) {
    val entry = blocks[blockHash] ?: return

    entry.nodeIds.remove(nodeId)

    if (entry.nodeIds.isEmpty()) {
      blocks.remove(blockHash)
      timestamps.remove(blockHash)
      return
    }

    if (entry.nodeIds.size < threshold) {
      timestamps.remove(blockHash)
    }
  }

  fun isBlockValid(blockHash: String): Boolean {
    return (blocks[blockHash]?.nodeIds?.size ?: 0) >= threshold
  }

  fun getNumberOfBlocks(): Int {
    return blocks.size
  }

  fun getNumberOfValidBlocks(): Int {
    return timestamps.size
  }

  fun getBlocks(): List<Pair<Block, Long>> {
    return blocks.mapNotNull { (hash, entry) ->
      timestamps[hash]?.let { Pair(entry.block, it) }
    }
  }

  fun getValidBlocks(): List<Pair<Block, Long>> {
    return blocks.mapNotNull { (hash, entry) ->
      timestamps[hash]?.let { Pair(entry.block, it) }
    }
  }

  fun clear() {
    blocks.clear()
    timestamps.clear()
  }
}