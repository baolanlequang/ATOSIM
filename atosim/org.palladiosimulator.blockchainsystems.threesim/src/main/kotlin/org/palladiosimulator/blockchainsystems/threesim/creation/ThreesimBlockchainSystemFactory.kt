package org.palladiosimulator.blockchainsystems.threesim.creation

import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainSystem as DesignBlockchainSystem
import org.palladiosimulator.blockchainsystems.core.blockchain.BlockchainFactoryImpl
import org.palladiosimulator.blockchainsystems.core.propagation.block.BlockPropagationStrategyFactoryImpl
import org.palladiosimulator.blockchainsystems.core.propagation.transaction.TransactionPropagationStrategyFactoryImpl
import org.palladiosimulator.blockchainsystems.core.block.BlockFactoryImpl
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockFactory
import org.palladiosimulator.blockchainsystems.core.geography.GeographicalRegionsResolver
import org.palladiosimulator.blockchainsystems.core.orphanblockpool.OrphanBlockPoolFactoryImpl
import org.palladiosimulator.blockchainsystems.core.system.BlockchainSystem
import org.palladiosimulator.blockchainsystems.core.system.BlockchainSystemNodeFactory
import org.palladiosimulator.blockchainsystems.core.system.abstractions.*
import org.palladiosimulator.blockchainsystems.core.transaction.TrxMemPoolFactoryImpl
import org.palladiosimulator.blockchainsystems.threesim.behavior.*
import org.palladiosimulator.blockchainsystems.threesim.creation.geography.ThreesimGeographicalRegionsResolver
import org.palladiosimulator.blockchainsystems.threesim.creation.abstractions.NodeAllocationResolver
import org.palladiosimulator.blockchainsystems.threesim.simulation.*
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.NetworkTopology
import org.palladiosimulator.blockchainsystems.core.propagation.transaction.*
import java.util.UUID
import java.util.random.RandomGenerator
import org.eclipse.emf.ecore.util.EcoreUtil

abstract class ThreesimBlockchainSystemFactory(
  protected val designBlockchainSystem: DesignBlockchainSystem,
  protected val networkTopology: NetworkTopology,
) {
  protected lateinit var simulationParameters: ThreesimSimulationParameters
  protected abstract fun createP2PNetworkFactory(): P2PNetworkFactory
  protected abstract fun getNodeAllocationResolver(networkCreationResult: P2PNetworkCreationResult): NodeAllocationResolver
  protected abstract fun getResourcePowerCalculator(networkCreationResult: P2PNetworkCreationResult): ResourcePowerCalculator

  fun createBlockchainSystem(
    simulationParameters: ThreesimSimulationParameters
  ): BlockchainSystemWithParameters {
    this.simulationParameters = simulationParameters

    val networkFactory = createP2PNetworkFactory()
    val networkCreationResult = networkFactory.createP2PNetwork()

    val nodeAllocationResolver = getNodeAllocationResolver(networkCreationResult)

    // 🔥 Translate attacker IDs ONCE here
    val runtimeAttackerNodeIds: Set<String> =
      networkCreationResult.createdNetwork.nodes
        .map { it.endpointId }
        .filter { runtimeNodeId ->
          val alloc = nodeAllocationResolver.getNodeAllocation(runtimeNodeId)
          val nodeSystemId = alloc?.nodeSystem?.let { EcoreUtil.getID(it) }
          nodeSystemId != null &&
                  simulationParameters.attackerNodeIds.contains(nodeSystemId)
        }
        .toSet()

    val effectiveParameters = ThreesimSimulationParameters(
      failureThroughputThreshold = simulationParameters.failureThroughputThreshold,
      shannonEntropyK = simulationParameters.shannonEntropyK,
      nakamotoCoefficientThreshold = simulationParameters.nakamotoCoefficientThreshold,
      reliabilityObservationTimespan = simulationParameters.reliabilityObservationTimespan,
      attackType = simulationParameters.attackType,
      attackerNodeIds = runtimeAttackerNodeIds,
      attackerHashPower = simulationParameters.attackerHashPower,
      gamma = simulationParameters.gamma,
      deltaA = simulationParameters.deltaA,
      deltaB = simulationParameters.deltaB,
      confirmationDepth = simulationParameters.confirmationDepth,
      blockInterval = simulationParameters.blockInterval,
      propagationDelay = simulationParameters.propagationDelay,
      nodeDegree = simulationParameters.nodeDegree,
      maxBlockSize = simulationParameters.maxBlockSize,
      networkBandwidth = simulationParameters.networkBandwidth
    )

    val baseResourcePowerCalculator =
      getResourcePowerCalculator(networkCreationResult)

    val resourcePowerCalculator =
      if (effectiveParameters.attackerNodeIds.isNotEmpty() &&
        effectiveParameters.attackerHashPower > 0.0) {
        AttackAwareResourcePowerCalculator(
          delegate = baseResourcePowerCalculator,
          attackerNodeIds = effectiveParameters.attackerNodeIds,
          attackerHashPower = effectiveParameters.attackerHashPower
        )
      } else baseResourcePowerCalculator

    val geographicalRegionsResolver =
      ThreesimGeographicalRegionsResolver(
        designBlockchainSystem.geographicalRegionsSpecification,
        nodeAllocationResolver
      )

    val blockFactory: BlockFactory = BlockFactoryImpl()

    val nodeFactory = createBlockchainSystemNodeFactory(
      nodeAllocationResolver,
      resourcePowerCalculator,
      blockFactory,
      geographicalRegionsResolver,
      effectiveParameters
    )

    val system = createBlockchainSystemInstance(
      networkCreationResult.createdNetwork,
      blockFactory,
      nodeFactory,
      geographicalRegionsResolver,
      designBlockchainSystem.specification.blockReward
    )

    return BlockchainSystemWithParameters(system, effectiveParameters)
  }

  private fun createBlockchainSystemInstance(
    network: P2PNetwork,
    blockFactory: BlockFactory,
    nodeFactory: BlockchainSystemNodeFactory,
    geographicalRegionsResolver: GeographicalRegionsResolver,
    blockReward: Double
  ): BlockchainSystem {

    val blockchainSystemId = UUID.randomUUID().toString()
    val blockchainSystemName = "BlockchainSystem_" + blockchainSystemId.substring(0, 8)

    val genesisBlock = blockFactory.createGenesisBlock()

    val blockchainSystemNodes = network.nodes
      .map { nodeFactory.createBlockchainSystemNode(it, genesisBlock) }
      .toHashSet()

    val trxPropSpec = designBlockchainSystem.transactionsSpecification.transactionPropertiesSpecification
    val meanTrxCreationInterval = designBlockchainSystem.transactionsSpecification.meanTransactionCreationInterval

    val transactionSubmissionProcess =
      ThreesimTransactionSubmissionProcess(
        blockchainSystemId,
        blockchainSystemName,
        meanTrxCreationInterval,
        TransactionPropertiesValueProviderAdapter.create(
          trxPropSpec,
          RandomGenerator.of("Random")
        )
      )

    val geographicalRegions = geographicalRegionsResolver.resolveGeographicalRegions()

    return BlockchainSystem(
      blockchainSystemId,
      blockchainSystemName,
      network,
      geographicalRegions,
      blockchainSystemNodes,
      transactionSubmissionProcess,
      blockReward
    )
  }

  private fun createBlockchainSystemNodeFactory(
    nodeAllocationResolver: NodeAllocationResolver,
    resourcePowerCalculator: ResourcePowerCalculator,
    blockFactory: BlockFactory,
    geographicalRegionsResolver: ThreesimGeographicalRegionsResolver,
    effectiveParameters: ThreesimSimulationParameters
  ): BlockchainSystemNodeFactory {

    val blockchainFactory =
      BlockchainFactoryImpl(
        designBlockchainSystem.specification.numOfRequiredSecurityConfirmations
      )

    val transactionPropagationStrategyFactory =
      TransactionPropagationStrategyFactoryImpl {
        if (effectiveParameters.attackType == AttackType.RACE) {
          RaceAwareTransactionPropagationStrategy(
            effectiveParameters.attackerNodeIds,
            effectiveParameters.deltaA,
            effectiveParameters.deltaB
          )
        } else TransactionPropagationStrategy()
      }

    return BlockchainSystemNodeFactory(
      blockFactory,
      blockchainFactory,
      ThreesimMiningProcessFactory(
        effectiveParameters.blockInterval,
        resourcePowerCalculator
      ),
      ThreesimTransactionSelectionProcessFactory(
        effectiveParameters.maxBlockSize
      ),
      ThreesimBlockValidatorFactory(nodeAllocationResolver),
      BlockPropagationStrategyFactoryImpl(),
      transactionPropagationStrategyFactory,
      TrxMemPoolFactoryImpl(),
      OrphanBlockPoolFactoryImpl(),
      ThreesimBlockchainSystemNodeBehaviorFactory(effectiveParameters),
      geographicalRegionsResolver,
      resourcePowerCalculator,
      ThreesimBlockchainSystemNodeTagProvider()
    )
  }
}