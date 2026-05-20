package org.palladiosimulator.blockchainsystems.threesim.monitoring

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockType
import org.palladiosimulator.blockchainsystems.core.blockchain.BlockAppendedTraceEvent
import org.palladiosimulator.blockchainsystems.core.blockchain.BlockTypeChangedTraceEvent
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogOrigin
import org.palladiosimulator.blockchainsystems.core.geography.GeographicalRegions
import org.palladiosimulator.blockchainsystems.core.mining.BlockMinedTraceEvent
import org.palladiosimulator.blockchainsystems.core.monitoring.abstractions.SimulationMonitor
import org.palladiosimulator.blockchainsystems.core.simulation.termination.LongestChainExceededMaxLengthCondition
import org.palladiosimulator.blockchainsystems.core.system.BlockchainSystem
import org.palladiosimulator.blockchainsystems.core.system.BlockchainSystemNode
import org.palladiosimulator.blockchainsystems.core.transaction.TransactionSubmittedTraceEvent
import org.palladiosimulator.blockchainsystems.core.utils.CounterMap
import org.palladiosimulator.blockchainsystems.threesim.behavior.BlockUtils
import org.palladiosimulator.blockchainsystems.threesim.metrics.calculators.TransactionThroughputCalculator
import org.palladiosimulator.blockchainsystems.threesim.simulation.AttackType
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters
import org.palladiosimulator.blockchainsystems.threesim.utils.BlockchainSystemFailureLog
import org.palladiosimulator.blockchainsystems.threesim.utils.BlocksMap

/**
 * Monitor for the 3SIM simulation.
 *
 * @property maxBlockchainLengthCondition Condition to check if the maximum blockchain length has been exceeded.
 * @property failureThroughputThreshold Throughput threshold below which a failure is considered to have occurred, in transactions per minute.
 *
 * @author Davis Riedel
 */
class ThreesimSimulationMonitor(
  private val maxBlockchainLengthCondition: LongestChainExceededMaxLengthCondition,
  private val failureThroughputThreshold: Double,
  private val simulationParameters: ThreesimSimulationParameters
) : SimulationMonitor {

  private lateinit var nodes: MutableSet<BlockchainSystemNode>

  private lateinit var geographicalRegions: GeographicalRegions

  private var numberOfSubmittedTransactions: Int = 0

  private var blockReward: Double? = null

  private var finneyAttackSucceeded: Boolean = false
  private var finneyCandidateMinedTime: Long? = null
  private var finneyCandidateBlockHash: String? = null

  private var raceAttackSucceeded: Boolean = false

  // fork tracking by parent hash (previousHash)
  private val confirmedByPrevHash: MutableMap<String, MutableSet<String>> = mutableMapOf()
  private val staleByPrevHash: MutableMap<String, MutableSet<String>> = mutableMapOf()

  // confirmed child index for descendant-depth tracking on the attacker's winning race branch
  private val confirmedChildrenByParentHash: MutableMap<String, MutableSet<String>> = mutableMapOf()

  // two-stage race outcome tracking
  private var raceWinningPrevHash: String? = null
  private var raceWinningAttackerBlockHash: String? = null
  private var raceProvisionalSuccessTime: Long? = null

  private lateinit var blocksProposedPerNode: CounterMap<String>

  private lateinit var includedBlocks: BlocksMap
  private lateinit var confirmedBlocks: BlocksMap
  private lateinit var staleBlocks: BlocksMap
  private lateinit var forkedBlocks: BlocksMap

  private val failureLog = BlockchainSystemFailureLog()
  private val blockRewardMonitor = BlockRewardMonitor()

  private val throughputsDuringFailure: MutableList<Double> = mutableListOf()
  private val confirmationLatenciesDuringFailure: MutableList<Long> = mutableListOf()

  private val throughputsWithoutFailure: MutableList<Double> = mutableListOf()
  private val confirmationLatenciesWithoutFailure: MutableList<Long> = mutableListOf()

  private val blockPropagationDelays = mutableListOf<Long>()

  private var lastThroughputCheckTimestamp: Long = 0
  private var attackSuccessTime: Long? = null

  // for debug
  private var lastTerminationDebugConfirmedBlocks: Int? = null
  private var lastTerminationDebugRaceAttackSucceeded: Boolean? = null
  private var lastTerminationDebugReachedDepth: Boolean? = null

  // Deduplicate monitor-side accounting so the same confirmed block hash does not
  // contribute throughput / reward multiple times across multiple node-local events.
  private val throughputAccountedConfirmedBlockHashes: MutableSet<String> = mutableSetOf()
  private val rewardAccountedConfirmedBlockHashes: MutableSet<String> = mutableSetOf()

  override fun initialize(blockchainSystem: BlockchainSystem) {
    nodes = blockchainSystem.nodes
    geographicalRegions = blockchainSystem.geographicalRegions
    blockReward = blockchainSystem.blockReward

    blocksProposedPerNode = CounterMap.create(nodes.map { it.id })

    val majorityThreshold = calculateMajorityThreshold()
    includedBlocks = BlocksMap(majorityThreshold)
    confirmedBlocks = BlocksMap(majorityThreshold)
    staleBlocks = BlocksMap(majorityThreshold)
    forkedBlocks = BlocksMap(majorityThreshold)
  }

  fun getFinalState(
    finalSystemTime: Long,
  ): ThreesimSimulationMonitorState {
    return ThreesimSimulationMonitorState(
      numberOfNodes = nodes.size,
      hashPowerPerNode = calculateHashPowerPerNode(),
      blocksProposedPerNode = calculateBlocksProposedPerNode(),
      numberOfGeographicalRegions = calculateNumberOfGeographicalRegions(),
      numberOfNodesPerRegion = calculateNumberOfNodesPerRegion(),
      numberOfSubmittedTransactions = numberOfSubmittedTransactions,
      numberOfConfirmedTransactions = calculateNumberOfConfirmedTransactions(),
      blockProposalTimeAndConfirmationTimePerConfirmedBlock = calculateBlockProposalTimeAndConfirmationTimePerConfirmedBlock(),
      meanTimeBetweenFailures = calculateMeanTimeBetweenFailures(finalSystemTime),
      meanTimeToRepair = calculateMeanTimeToRepair(),
      numberOfStaleBlocks = calculateNumberOfStaleBlocks(),
      numberOfConfirmedBlocks = calculateNumberOfConfirmedBlocks(),
      tokensHeldPerNode = calculateTokensHeldPerNode(),
      averageThroughputDuringFailure = calculateAverageThroughputDuringFailure(),
      averageThroughputDuringNormalOperation = calculateAverageThroughputWithoutFailure(),
      averageConfirmationLatencyDuringFailure = calculateAverageConfirmationLatencyDuringFailure(),
      averageConfirmationLatencyDuringNormalOperation = calculateAverageConfirmationLatencyWithoutFailure()
    )
  }

  private fun addBlock(blockType: BlockType, block: Block, nodeId: String, occurrenceTime: Long) {
    when (blockType) {
      BlockType.IncludedBlock -> includedBlocks
      BlockType.ConfirmedBlock -> confirmedBlocks
      BlockType.StaleBlock -> staleBlocks
      BlockType.ForkingBlock -> forkedBlocks
    }.addNodeToBlock(block, nodeId, occurrenceTime)
  }

  private fun removeBlock(blockType: BlockType, blockHash: String, nodeId: String) {
    when (blockType) {
      BlockType.IncludedBlock -> includedBlocks
      BlockType.ConfirmedBlock -> confirmedBlocks
      BlockType.StaleBlock -> staleBlocks
      BlockType.ForkingBlock -> forkedBlocks
    }.removeNodeFromBlock(blockHash, nodeId)
  }

  override fun onTraceEventOccurred(
    event: TraceEvent,
    logOrigin: TraceEventLogOrigin
  ) {
    when (event.eventType) {
      BlockMinedTraceEvent.EVENT_TYPE -> {
        val e = event as BlockMinedTraceEvent
        val block = e.block

        // Finney implementation: record privately mined block
        // finney premined exactly one block
        // block is mined before release, honest mining does not withhold blocks
        // --> first attacker-mined block is the Finney block
        if (finneyCandidateBlockHash == null && isAttacker(block.originId)) {
          finneyCandidateBlockHash = block.hash
          finneyCandidateMinedTime = e.occurrenceTime
        }

        if (BlockUtils.isBlockForked(block)) {
          forkedBlocks.addNodeToBlock(block, logOrigin.id, e.occurrenceTime)
        }

        blocksProposedPerNode.increment(logOrigin.id)
      }

      BlockAppendedTraceEvent.EVENT_TYPE -> {
        val e = event as BlockAppendedTraceEvent
        val nodeId = logOrigin.id

        addBlock(e.appendedBlockType, e.appendedBlock, nodeId, e.occurrenceTime)

        if (e.appendedBlockType == BlockType.ConfirmedBlock || e.appendedBlockType == BlockType.StaleBlock) {
          updateRaceOutcomeIfRelevant(e.appendedBlock, e.appendedBlockType, e.occurrenceTime)
        }

        maxBlockchainLengthCondition.onBlockAppended(e.blockPosition)

        if (
          e.appendedBlockType == BlockType.ConfirmedBlock &&
          throughputAccountedConfirmedBlockHashes.add(e.appendedBlock.hash)
        ) {
          monitorThroughputForNewlyConfirmedBlock(e.appendedBlock, e.occurrenceTime)
        }

        if (
          e.appendedBlockType == BlockType.ConfirmedBlock &&
          rewardAccountedConfirmedBlockHashes.add(e.appendedBlock.hash)
        ) {
          recordBlockReward(e.appendedBlock)
        }

        if (
          e.appendedBlockType == BlockType.ConfirmedBlock &&
          simulationParameters.attackType == AttackType.FINNEY &&
          e.appendedBlock.hash == finneyCandidateBlockHash &&
          finneyCandidateMinedTime != null &&
          e.occurrenceTime > finneyCandidateMinedTime!!
        ) {
          finneyAttackSucceeded = true
        }
      }

      BlockTypeChangedTraceEvent.EVENT_TYPE -> {
        val e = event as BlockTypeChangedTraceEvent
        val nodeId = logOrigin.id

        removeBlock(e.oldBlockType, e.block.hash, nodeId)
        addBlock(e.newBlockType, e.block, nodeId, e.occurrenceTime)

        if (e.newBlockType == BlockType.ConfirmedBlock || e.newBlockType == BlockType.StaleBlock) {
          updateRaceOutcomeIfRelevant(e.block, e.newBlockType, e.occurrenceTime)
        }

        if (
          e.newBlockType == BlockType.ConfirmedBlock &&
          throughputAccountedConfirmedBlockHashes.add(e.block.hash)
        ) {
          monitorThroughputForNewlyConfirmedBlock(e.block, e.occurrenceTime)
        }

        if (
          e.newBlockType == BlockType.ConfirmedBlock &&
          rewardAccountedConfirmedBlockHashes.add(e.block.hash)
        ) {
          recordBlockReward(e.block)
        }

        if (
          e.newBlockType == BlockType.ConfirmedBlock &&
          simulationParameters.attackType == AttackType.FINNEY &&
          e.block.hash == finneyCandidateBlockHash &&
          finneyCandidateMinedTime != null &&
          e.occurrenceTime > finneyCandidateMinedTime!!
        ) {
          finneyAttackSucceeded = true
        }
      }

      TransactionSubmittedTraceEvent.EVENT_TYPE -> {
        numberOfSubmittedTransactions++
      }
    }
  }

  override fun shouldTerminate(): Boolean {
    val maxExceeded = maxBlockchainLengthCondition.hasLengthExceeded()

    if (simulationParameters.attackType == AttackType.RACE) {
      val confirmedBlocks = calculateNumberOfConfirmedBlocks()
      val reachedDepth = confirmedBlocks >= simulationParameters.confirmationDepth

      if (
        lastTerminationDebugConfirmedBlocks != confirmedBlocks ||
        lastTerminationDebugRaceAttackSucceeded != raceAttackSucceeded ||
        lastTerminationDebugReachedDepth != reachedDepth
      ) {

        lastTerminationDebugConfirmedBlocks = confirmedBlocks
        lastTerminationDebugRaceAttackSucceeded = raceAttackSucceeded
        lastTerminationDebugReachedDepth = reachedDepth
      }

      if (reachedDepth) {
        return true
      }
    }

    return false
  }

  private fun monitorThroughputForNewlyConfirmedBlock(confirmedBlock: Block, occurrenceTime: Long) {
    if (lastThroughputCheckTimestamp == 0L) {
      lastThroughputCheckTimestamp = occurrenceTime
      return
    }

    val observationTime = occurrenceTime - lastThroughputCheckTimestamp

    val throughput = TransactionThroughputCalculator(
      numberOfConfirmedTransactions = confirmedBlock.transactions.size,
      observationTime = observationTime
    ).calculate().value

    val confirmationLatency = occurrenceTime - confirmedBlock.blockMinedTimestamp

    if (failureLog.isFailureOngoing()) {
      if (throughput > failureThroughputThreshold) {
        failureLog.failureEnded(occurrenceTime)
      }
    } else {
      if (throughput <= failureThroughputThreshold) {
        failureLog.failureStarted(occurrenceTime)
      }
    }

    if (failureLog.isFailureOngoing()) {
      throughputsDuringFailure.add(throughput)
      confirmationLatenciesDuringFailure.add(confirmationLatency)
    } else {
      throughputsWithoutFailure.add(throughput)
      confirmationLatenciesWithoutFailure.add(confirmationLatency)
    }

    lastThroughputCheckTimestamp = occurrenceTime
  }

  private fun calculateMajorityThreshold(): Int {
    return (nodes.size / 2) + 1
  }

  private fun calculateNumberOfConfirmedBlocks(): Int {
    return confirmedBlocks.getNumberOfValidBlocks()
  }

  private fun calculateNumberOfConfirmedTransactions(): Int {
    return confirmedBlocks.getValidBlocks()
      .sumOf { it.first.transactions.size }
  }

  private fun calculateNumberOfStaleBlocks(): Int {
    return staleBlocks.getNumberOfValidBlocks()
  }

  private fun calculateBlocksProposedPerNode(): Collection<Int> {
    return blocksProposedPerNode.values
  }

  private fun calculateHashPowerPerNode(): Collection<Double> {
    return nodes.map { it.resourcePower }
  }

  private fun calculateNumberOfNodesPerRegion(): Collection<Int> {
    return nodes
      .groupingBy { it.geographicalRegion.region }
      .eachCount().values
  }

  private fun calculateBlockProposalTimeAndConfirmationTimePerConfirmedBlock(): Collection<Pair<Long, Long>> {
    return confirmedBlocks.getValidBlocks()
      .map { Pair(it.first.blockMinedTimestamp, it.second) }
  }

  private fun calculateTokensHeldPerNode(): List<Double> {
    val blockReward = this.blockReward ?: throw IllegalStateException("Block reward is not set")

    val blocks = confirmedBlocks.getValidBlocks()
      .filter { it.first.originId != null }
      .groupBy { it.first.originId }

    return nodes.map { node ->
      blocks[node.id]?.fold(0.0) { acc, block ->
        acc + blockReward + block.first.transactions.sumOf { it.fee }
      } ?: 0.0
    }
  }

  private fun calculateMeanTimeBetweenFailures(observationTime: Long): Double {
    val numFailures = failureLog.getNumberOfFailures()
    if (numFailures <= 0) return Double.POSITIVE_INFINITY
    return observationTime.toDouble() / numFailures
  }

  private fun calculateMeanTimeToRepair(): Double {
    return failureLog.calculateMeanFailureDuration()
  }

  private fun calculateNumberOfGeographicalRegions(): Int {
    return geographicalRegions.getNumberOfRegions()
  }

  private fun calculateAverageThroughputDuringFailure(): Double {
    return if (throughputsDuringFailure.isEmpty()) {
      -1.0
    } else {
      throughputsDuringFailure.average()
    }
  }

  private fun calculateAverageConfirmationLatencyDuringFailure(): Double {
    return if (confirmationLatenciesDuringFailure.isEmpty()) {
      -1.0
    } else {
      confirmationLatenciesDuringFailure.average()
    }
  }

  private fun calculateAverageThroughputWithoutFailure(): Double {
    return if (throughputsWithoutFailure.isEmpty()) {
      -1.0
    } else {
      throughputsWithoutFailure.average()
    }
  }

  private fun calculateAverageConfirmationLatencyWithoutFailure(): Double {
    return if (confirmationLatenciesWithoutFailure.isEmpty()) {
      -1.0
    } else {
      confirmationLatenciesWithoutFailure.average()
    }
  }

  private fun updateRaceOutcomeIfRelevant(block: Block, newType: BlockType, occurrenceTime: Long) {
    if (raceAttackSucceeded) return
    if (simulationParameters.attackType != AttackType.RACE) return

    val blockHash = block.hash
    val prevHash = block.previousHash
    val attacker = isAttacker(block.originId)

    when (newType) {
      BlockType.ConfirmedBlock -> {
        if (prevHash != null) {
          addTo(confirmedChildrenByParentHash, prevHash, blockHash)
        }

        if (attacker && prevHash != null) {
          addTo(confirmedByPrevHash, prevHash, blockHash)
        }
      }

      BlockType.StaleBlock -> {
        if (!attacker && prevHash != null) {
          addTo(staleByPrevHash, prevHash, blockHash)
        }
      }

      else -> return
    }

    // Stage 1: detect provisional race win
    if (raceWinningAttackerBlockHash == null && prevHash != null) {
      val attackerConfirmed = confirmedByPrevHash[prevHash].orEmpty()
      val honestStale = staleByPrevHash[prevHash].orEmpty()

      val winningAttackerHash =
        attackerConfirmed.firstOrNull { attackerHash ->
          honestStale.any { honestHash -> honestHash != attackerHash }
        }

      if (winningAttackerHash != null) {
        raceWinningPrevHash = prevHash
        raceWinningAttackerBlockHash = winningAttackerHash
        raceProvisionalSuccessTime = occurrenceTime
      }
    }

    // Stage 2: finalize only after enough confirmed descendants on that attacker branch
    val winningHash = raceWinningAttackerBlockHash ?: return
    val confirmationsOnWinningBranch = countConfirmedDepthFrom(winningHash)

    if (confirmationsOnWinningBranch >= simulationParameters.confirmationDepth) {
      raceAttackSucceeded = true

      if (attackSuccessTime == null) {
        attackSuccessTime = raceProvisionalSuccessTime ?: occurrenceTime
      }
    }
  }

  private fun countConfirmedDepthFrom(rootHash: String): Int {
    var depth = 1
    var current = rootHash

    while (true) {
      val children = confirmedChildrenByParentHash[current].orEmpty()
      val next = children.firstOrNull() ?: break
      depth++
      current = next
    }

    return depth
  }

  private fun isAttacker(originId: String?): Boolean {
    return originId != null && simulationParameters.attackerNodeIds.contains(originId)
  }

  private fun addTo(map: MutableMap<String, MutableSet<String>>, prevHash: String, blockHash: String) {
    map.getOrPut(prevHash) { mutableSetOf() }.add(blockHash)
  }

  fun recordBlockReward(block: Block) {
    blockRewardMonitor.recordBlockReward(block)
  }

  fun getTotalBlockRewards(): Int =
    blockRewardMonitor.totalRewards

  fun getBlockRewardsForNode(nodeId: String): Int =
    blockRewardMonitor.getRewardsForNode(nodeId)

  fun hasFinneyAttackSucceeded(): Boolean = finneyAttackSucceeded

  fun hasRaceAttackSucceeded(): Boolean = raceAttackSucceeded

  fun getAttackSuccessTime(): Long? = attackSuccessTime
}