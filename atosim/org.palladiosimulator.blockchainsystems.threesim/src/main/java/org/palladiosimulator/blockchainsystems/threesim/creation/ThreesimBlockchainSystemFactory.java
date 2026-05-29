package org.palladiosimulator.blockchainsystems.threesim.creation;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainSystem;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.NetworkTopology;
import org.palladiosimulator.blockchainsystems.core.block.BlockFactoryImpl;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockFactory;
import org.palladiosimulator.blockchainsystems.core.blockchain.BlockchainFactoryImpl;
import org.palladiosimulator.blockchainsystems.core.geography.GeographicalRegionsResolver;
import org.palladiosimulator.blockchainsystems.core.orphanblockpool.OrphanBlockPoolFactoryImpl;
import org.palladiosimulator.blockchainsystems.core.propagation.block.BlockPropagationStrategyFactoryImpl;
import org.palladiosimulator.blockchainsystems.core.propagation.transaction.RaceAwareTransactionPropagationStrategy;
import org.palladiosimulator.blockchainsystems.core.propagation.transaction.TransactionPropagationStrategy;
import org.palladiosimulator.blockchainsystems.core.propagation.transaction.TransactionPropagationStrategyFactoryImpl;
import org.palladiosimulator.blockchainsystems.core.system.BlockchainSystemNodeFactory;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.NodeP2PNetworkInterface;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetwork;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkCreationResult;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkFactory;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.ResourcePowerCalculator;
import org.palladiosimulator.blockchainsystems.core.transaction.TrxMemPoolFactoryImpl;
import org.palladiosimulator.blockchainsystems.threesim.behavior.ThreesimBlockchainSystemNodeBehaviorFactory;
import org.palladiosimulator.blockchainsystems.threesim.behavior.ThreesimBlockchainSystemNodeTagProvider;
import org.palladiosimulator.blockchainsystems.threesim.behavior.ThreesimTransactionSelectionProcessFactory;
import org.palladiosimulator.blockchainsystems.threesim.creation.abstractions.NodeAllocationResolver;
import org.palladiosimulator.blockchainsystems.threesim.creation.geography.ThreesimGeographicalRegionsResolver;
import org.palladiosimulator.blockchainsystems.threesim.simulation.AttackType;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class ThreesimBlockchainSystemFactory {

    protected final BlockchainSystem designBlockchainSystem;
    protected final NetworkTopology networkTopology;
    protected ThreesimSimulationParameters simulationParameters;

    protected ThreesimBlockchainSystemFactory(BlockchainSystem designBlockchainSystem,
            NetworkTopology networkTopology) {
        this.designBlockchainSystem = designBlockchainSystem;
        this.networkTopology = networkTopology;
    }

    protected abstract P2PNetworkFactory createP2PNetworkFactory();
    protected abstract NodeAllocationResolver getNodeAllocationResolver(P2PNetworkCreationResult networkCreationResult);
    protected abstract ResourcePowerCalculator getResourcePowerCalculator(P2PNetworkCreationResult networkCreationResult);

    public BlockchainSystemWithParameters createBlockchainSystem(ThreesimSimulationParameters params) {
        this.simulationParameters = params;

        P2PNetworkFactory networkFactory = createP2PNetworkFactory();
        P2PNetworkCreationResult networkCreationResult = networkFactory.createP2PNetwork();

        NodeAllocationResolver nodeAllocationResolver = getNodeAllocationResolver(networkCreationResult);

        Set<String> runtimeAttackerNodeIds = networkCreationResult.getCreatedNetwork().getNodes().stream()
                .map(NodeP2PNetworkInterface::getEndpointId)
                .filter(runtimeNodeId -> {
                    var alloc = nodeAllocationResolver.getNodeAllocation(runtimeNodeId);
                    var nodeSystem = alloc != null ? alloc.getNodeSystem() : null;
                    String nodeSystemId = nodeSystem != null ? EcoreUtil.getID(nodeSystem) : null;
                    return nodeSystemId != null && params.getAttackerNodeIds().contains(nodeSystemId);
                })
                .collect(Collectors.toSet());

        ThreesimSimulationParameters effectiveParameters = new ThreesimSimulationParameters(
                params.getFailureThroughputThreshold(),
                params.getShannonEntropyK(),
                params.getNakamotoCoefficientThreshold(),
                params.getReliabilityObservationTimespan(),
                params.getAttackType(),
                params.isCombinedAttackEnabled(),
                params.getSecondaryAttackType(),
                runtimeAttackerNodeIds,
                params.getAttackerHashPower(),
                params.getGamma(),
                params.getDeltaA(),
                params.getDeltaB(),
                params.getConfirmationDepth(),
                params.getBlockInterval(),
                params.getPropagationDelay(),
                params.getNodeDegree(),
                params.getMaxBlockSize(),
                params.getNetworkBandwidth()
        );

        ResourcePowerCalculator baseResourcePowerCalculator = getResourcePowerCalculator(networkCreationResult);
        ResourcePowerCalculator resourcePowerCalculator =
                (!effectiveParameters.getAttackerNodeIds().isEmpty() && effectiveParameters.getAttackerHashPower() > 0.0)
                        ? new AttackAwareResourcePowerCalculator(baseResourcePowerCalculator,
                                effectiveParameters.getAttackerNodeIds(), effectiveParameters.getAttackerHashPower())
                        : baseResourcePowerCalculator;

        ThreesimGeographicalRegionsResolver geographicalRegionsResolver =
                new ThreesimGeographicalRegionsResolver(
                        designBlockchainSystem.getGeographicalRegionsSpecification(), nodeAllocationResolver);

        BlockFactory blockFactory = new BlockFactoryImpl();

        BlockchainSystemNodeFactory nodeFactory = createBlockchainSystemNodeFactory(
                nodeAllocationResolver, resourcePowerCalculator, blockFactory,
                geographicalRegionsResolver, effectiveParameters);

        org.palladiosimulator.blockchainsystems.core.system.BlockchainSystem system =
                createBlockchainSystemInstance(networkCreationResult.getCreatedNetwork(), blockFactory,
                        nodeFactory, geographicalRegionsResolver,
                        designBlockchainSystem.getSpecification().getBlockReward());

        return new BlockchainSystemWithParameters(system, effectiveParameters);
    }

    private org.palladiosimulator.blockchainsystems.core.system.BlockchainSystem createBlockchainSystemInstance(
            P2PNetwork network, BlockFactory blockFactory, BlockchainSystemNodeFactory nodeFactory,
            GeographicalRegionsResolver geographicalRegionsResolver, double blockReward) {

        String id = UUID.randomUUID().toString();
        String name = "BlockchainSystem_" + id.substring(0, 8);

        var genesisBlock = blockFactory.createGenesisBlock();
        var nodes = network.getNodes().stream()
                .map(ni -> nodeFactory.createBlockchainSystemNode(ni, genesisBlock))
                .collect(Collectors.toCollection(HashSet::new));

        var trxPropSpec = designBlockchainSystem.getTransactionsSpecification().getTransactionPropertiesSpecification();
        double meanTrxInterval = designBlockchainSystem.getTransactionsSpecification().getMeanTransactionCreationInterval();

        var txSubmissionProcess = new org.palladiosimulator.blockchainsystems.threesim.behavior.ThreesimTransactionSubmissionProcess(
                id, name, meanTrxInterval,
                TransactionPropertiesValueProviderAdapter.create(trxPropSpec, java.util.random.RandomGenerator.of("Random")));

        var geoRegions = geographicalRegionsResolver.resolveGeographicalRegions();

        return new org.palladiosimulator.blockchainsystems.core.system.BlockchainSystem(
                id, name, network, geoRegions, nodes, txSubmissionProcess, blockReward);
    }

    private BlockchainSystemNodeFactory createBlockchainSystemNodeFactory(
            NodeAllocationResolver nodeAllocationResolver,
            ResourcePowerCalculator resourcePowerCalculator,
            BlockFactory blockFactory,
            ThreesimGeographicalRegionsResolver geographicalRegionsResolver,
            ThreesimSimulationParameters effectiveParameters) {

        var blockchainFactory = new BlockchainFactoryImpl(
                designBlockchainSystem.getSpecification().getNumOfRequiredSecurityConfirmations());

        var txPropStrategyFactory = new TransactionPropagationStrategyFactoryImpl(
                effectiveParameters.getAttackType() == AttackType.RACE
                        ? () -> new RaceAwareTransactionPropagationStrategy(
                                effectiveParameters.getAttackerNodeIds(),
                                effectiveParameters.getDeltaA(),
                                effectiveParameters.getDeltaB())
                        : TransactionPropagationStrategy::new);

        return new BlockchainSystemNodeFactory(
                blockFactory,
                blockchainFactory,
                new ThreesimMiningProcessFactory(effectiveParameters.getBlockInterval(), resourcePowerCalculator),
                new ThreesimTransactionSelectionProcessFactory(effectiveParameters.getMaxBlockSize()),
                new ThreesimBlockValidatorFactory(nodeAllocationResolver),
                new BlockPropagationStrategyFactoryImpl(),
                txPropStrategyFactory,
                new TrxMemPoolFactoryImpl(),
                new OrphanBlockPoolFactoryImpl(),
                new ThreesimBlockchainSystemNodeBehaviorFactory(effectiveParameters),
                geographicalRegionsResolver,
                resourcePowerCalculator,
                new ThreesimBlockchainSystemNodeTagProvider()
        );
    }
}
