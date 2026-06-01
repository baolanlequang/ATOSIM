package org.palladiosimulator.blockchainsystems.threesim.behavior;

import org.palladiosimulator.blockchainsystems.core.behavior.CombinedSelfishFinneyNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.CombinedSelfishLeadStubbornNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.CombinedSelfishRaceNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.CombinedSelfishTrailStubbornNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.EqualForkStubbornMiningNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.FinneyMiningNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.GammaAwareHonestBlockchainSystemNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.HonestBlockchainSystemNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.LeadStubbornMiningNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.RaceMiningNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.SelfishMiningNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.behavior.TrailStubbornMiningNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeBehaviorFactory;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;

public class ThreesimBlockchainSystemNodeBehaviorFactory implements BlockchainSystemNodeBehaviorFactory {

    private final ThreesimSimulationParameters _simulationParameters;

    public ThreesimBlockchainSystemNodeBehaviorFactory(ThreesimSimulationParameters simulationParameters) {
        _simulationParameters = simulationParameters;
    }

    @Override
    public BlockchainSystemNodeBehavior create(String nodeId) {
        boolean isAttacker = _simulationParameters.getAttackerNodeIds().contains(nodeId);

        if (isAttacker) {
            return switch (_simulationParameters.getAttackType()) {
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
        }

        return switch (_simulationParameters.getAttackType()) {
            case SELFISH_MINING, LEAD_STUBBORN_MINING, EQUAL_FORK_STUBBORN_MINING,
                 TRAIL_STUBBORN_MINING, RACE, COMBINED_SELFISH_RACE, COMBINED_SELFISH_FINNEY,
                 COMBINED_SELFISH_LEAD_STUBBORN, COMBINED_SELFISH_TRAIL_STUBBORN, MAJORITY ->
                    new GammaAwareHonestBlockchainSystemNodeBehavior(
                            _simulationParameters.getAttackerNodeIds(),
                            _simulationParameters.getGamma()
                    );
            default -> new HonestBlockchainSystemNodeBehavior();
        };
    }
}
