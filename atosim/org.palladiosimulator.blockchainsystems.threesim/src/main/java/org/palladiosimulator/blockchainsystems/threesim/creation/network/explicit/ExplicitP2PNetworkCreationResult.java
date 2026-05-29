package org.palladiosimulator.blockchainsystems.threesim.creation.network.explicit;

import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetwork;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkCreationResult;

public class ExplicitP2PNetworkCreationResult implements P2PNetworkCreationResult {

    private final P2PNetwork _createdNetwork;

    public ExplicitP2PNetworkCreationResult(P2PNetwork createdNetwork) {
        _createdNetwork = createdNetwork;
    }

    @Override
    public P2PNetwork getCreatedNetwork() { return _createdNetwork; }
}
