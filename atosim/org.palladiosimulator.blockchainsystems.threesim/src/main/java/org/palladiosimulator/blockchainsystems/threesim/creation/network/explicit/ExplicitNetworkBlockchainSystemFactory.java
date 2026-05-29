package org.palladiosimulator.blockchainsystems.threesim.creation.network.explicit;

import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.ExplicitNetworkTopology;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkCreationResult;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkFactory;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.ResourcePowerCalculator;
import org.palladiosimulator.blockchainsystems.threesim.creation.ThreesimBlockchainSystemFactory;
import org.palladiosimulator.blockchainsystems.threesim.creation.abstractions.NodeAllocationResolver;

import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainSystem;

public class ExplicitNetworkBlockchainSystemFactory extends ThreesimBlockchainSystemFactory {

    public ExplicitNetworkBlockchainSystemFactory(BlockchainSystem designBlockchainSystem,
            ExplicitNetworkTopology explicitTopology) {
        super(designBlockchainSystem, explicitTopology);
    }

    @Override
    protected P2PNetworkFactory createP2PNetworkFactory() {
        return new ExplicitTopologyP2PNetworkFactory(
                (ExplicitNetworkTopology) networkTopology, simulationParameters);
    }

    @Override
    protected NodeAllocationResolver getNodeAllocationResolver(P2PNetworkCreationResult networkCreationResult) {
        return new ExplicitNetworkNodeAllocationResolver((ExplicitNetworkTopology) networkTopology);
    }

    @Override
    protected ResourcePowerCalculator getResourcePowerCalculator(P2PNetworkCreationResult networkCreationResult) {
        return new ExplicitNetworkResourcePowerCalculator((ExplicitNetworkTopology) networkTopology);
    }
}
