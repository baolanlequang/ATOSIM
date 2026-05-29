package org.palladiosimulator.blockchainsystems.threesim.creation.network.connectedsubgraphs;

import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainSystem;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.ConnectedSubgraphsNetworkTopology;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkCreationResult;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkFactory;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.ResourcePowerCalculator;
import org.palladiosimulator.blockchainsystems.threesim.creation.ThreesimBlockchainSystemFactory;
import org.palladiosimulator.blockchainsystems.threesim.creation.abstractions.NodeAllocationResolver;

public class ConnectedSubgraphNetworkBlockchainSystemFactory extends ThreesimBlockchainSystemFactory {

    public ConnectedSubgraphNetworkBlockchainSystemFactory(BlockchainSystem designBlockchainSystem,
            ConnectedSubgraphsNetworkTopology connectedSubgraphsTopology) {
        super(designBlockchainSystem, connectedSubgraphsTopology);
    }

    @Override
    protected P2PNetworkFactory createP2PNetworkFactory() {
        return new ConnectedSubgraphP2PNetworkFactory(
                (ConnectedSubgraphsNetworkTopology) networkTopology, simulationParameters);
    }

    @Override
    protected NodeAllocationResolver getNodeAllocationResolver(P2PNetworkCreationResult networkCreationResult) {
        return new ConnectedSubgraphNetworkNodeAllocationResolver(
                (ConnectedSubgraphsNetworkTopology) networkTopology,
                ((ConnectedSubgraphNetworkCreationResult) networkCreationResult).getNodeIdToNodeTemplateIdMapping());
    }

    @Override
    protected ResourcePowerCalculator getResourcePowerCalculator(P2PNetworkCreationResult networkCreationResult) {
        return new ConnectedSubgraphNetworkResourcePowerCalculator(
                (ConnectedSubgraphsNetworkTopology) networkTopology,
                ((ConnectedSubgraphNetworkCreationResult) networkCreationResult).getNodeIdToNodeTemplateIdMapping());
    }
}
