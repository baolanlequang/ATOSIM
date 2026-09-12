package org.palladiosimulator.blockchainsystems.threesim.behavior;

import org.palladiosimulator.blockchainsystems.core.behavior.BlockHashSeedable;
import org.palladiosimulator.blockchainsystems.core.behavior.CombinedSelfishFinneyNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.CombinedSelfishLeadStubbornNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.CombinedSelfishRaceNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.CombinedSelfishTrailStubbornNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.EqualForkStubbornMiningNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.FinneyMiningNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.HonestBlockchainSystemNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.LeadStubbornMiningNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.RaceMiningNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.SelfishMiningNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.TrailStubbornMiningNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeBehaviorFactory;
import org.palladiosimulator.blockchainsystems.threesim.simulation.DeterministicSeeds;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;

import java.util.random.RandomGenerator;

public class ThreesimBlockchainSystemNodeBehaviorFactory implements BlockchainSystemNodeBehaviorFactory {

    private final ThreesimSimulationParameters _simulationParameters;
    private final long _rootSeed;

    public ThreesimBlockchainSystemNodeBehaviorFactory(ThreesimSimulationParameters simulationParameters, long rootSeed) {
        _simulationParameters = simulationParameters;
        _rootSeed = rootSeed;
    }

    @Override
    public BlockchainSystemNodeBehavior create(String nodeId) {
        boolean isAttacker = _simulationParameters.getAttackerNodeIds().contains(nodeId);

        BlockchainSystemNodeBehavior behavior;
        if (isAttacker) {
            behavior = switch (_simulationParameters.getAttackType()) {
                case RACE -> new RaceMiningNodeBehavior();
                case MAJORITY -> new HonestBlockchainSystemNodeBehavior();
                case SELFISH_MINING -> new SelfishMiningNodeBehavior();
                case LEAD_STUBBORN_MINING -> new LeadStubbornMiningNodeBehavior();
                case EQUAL_FORK_STUBBORN_MINING -> new EqualForkStubbornMiningNodeBehavior();
                case TRAIL_STUBBORN_MINING -> new TrailStubbornMiningNodeBehavior();
                case FINNEY -> new FinneyMiningNodeBehavior();
                case COMBINED_SELFISH_RACE -> new CombinedSelfishRaceNodeBehavior();
                case COMBINED_SELFISH_FINNEY -> new CombinedSelfishFinneyNodeBehavior();
                case COMBINED_SELFISH_LEAD_STUBBORN -> new CombinedSelfishLeadStubbornNodeBehavior();
                case COMBINED_SELFISH_TRAIL_STUBBORN -> new CombinedSelfishTrailStubbornNodeBehavior();
                default -> new HonestBlockchainSystemNodeBehavior();
            };
        } else {
            behavior = new HonestBlockchainSystemNodeBehavior();
        }

        // Every behavior class that generates a block hash in onCreatingBlock (directly or via
        // an internal sub-behavior) implements BlockHashSeedable; a per-node seeded generator
        // replaces the unseeded UUID.randomUUID() each of them used before.
        if (behavior instanceof BlockHashSeedable seedable) {
            RandomGenerator blockHashGenerator = DeterministicSeeds.seededGenerator(_rootSeed, "blockHash:" + nodeId);
            seedable.setBlockHashGenerator(blockHashGenerator);
        }

        return behavior;
    }
}
