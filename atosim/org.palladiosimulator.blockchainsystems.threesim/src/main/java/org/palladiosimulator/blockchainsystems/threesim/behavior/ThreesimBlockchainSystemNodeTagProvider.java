package org.palladiosimulator.blockchainsystems.threesim.behavior;

import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeTagProvider;

import java.util.Collections;
import java.util.Set;

public class ThreesimBlockchainSystemNodeTagProvider implements BlockchainSystemNodeTagProvider {

    @Override
    public Set<String> getTags(String nodeId) {
        return Collections.emptySet();
    }
}
