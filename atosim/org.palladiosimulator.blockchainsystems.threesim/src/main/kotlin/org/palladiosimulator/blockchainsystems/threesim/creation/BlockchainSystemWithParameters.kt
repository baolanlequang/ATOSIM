package org.palladiosimulator.blockchainsystems.threesim.creation

import org.palladiosimulator.blockchainsystems.core.system.BlockchainSystem
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters

data class BlockchainSystemWithParameters(
    val system: BlockchainSystem,
    val effectiveParameters: ThreesimSimulationParameters
)