package org.palladiosimulator.blockchainsystems.core.system;

import org.palladiosimulator.blockchainsystems.core.common.BlockchainSimulationObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.geography.GeographicalRegions;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetwork;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.TransactionSubmissionProcess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BlockchainSystem extends BlockchainSimulationObject {

    private final P2PNetwork _network;
    private final GeographicalRegions _geographicalRegions;
    private final HashSet<BlockchainSystemNode> _nodes;
    private final TransactionSubmissionProcess _transactionSubmissionProcess;
    private final double _blockReward;

    public BlockchainSystem(
            String id,
            String name,
            P2PNetwork network,
            GeographicalRegions geographicalRegions,
            HashSet<BlockchainSystemNode> nodes,
            TransactionSubmissionProcess transactionSubmissionProcess,
            double blockReward
    ) {
        super(id, name);
        _network = network;
        _geographicalRegions = geographicalRegions;
        _nodes = nodes;
        _transactionSubmissionProcess = transactionSubmissionProcess;
        _blockReward = blockReward;
    }

    @Override
    public void onInitialize() {
        _network.initialize(getSimulationContext());

        for (BlockchainSystemNode node : _nodes) {
            node.initialize(getSimulationContext());
            _transactionSubmissionProcess.addOnTransactionSubmittedCallbackSubscriber(node);
        }

        _transactionSubmissionProcess.setOnSelectRecipientNodeIdCallback(this::selectRecipientNodeId);
        _transactionSubmissionProcess.initialize(getSimulationContext());
        _transactionSubmissionProcess.startTransactionSubmissionProcess();
    }

    @Override
    public void onCleanup() {
        _network.cleanup();
        for (BlockchainSystemNode node : _nodes) node.cleanup();
        _transactionSubmissionProcess.cleanup();
    }

    @Override
    public void dispatchEvent(Event event) {
    }

    private String selectRecipientNodeId() {
        List<BlockchainSystemNode> nodeList = new ArrayList<>(_nodes);
        return nodeList.get((int) (Math.random() * nodeList.size())).getId();
    }

    public P2PNetwork getNetwork() { return _network; }
    public GeographicalRegions getGeographicalRegions() { return _geographicalRegions; }
    public HashSet<BlockchainSystemNode> getNodes() { return _nodes; }
    public TransactionSubmissionProcess getTransactionSubmissionProcess() { return _transactionSubmissionProcess; }
    public double getBlockReward() { return _blockReward; }
}
