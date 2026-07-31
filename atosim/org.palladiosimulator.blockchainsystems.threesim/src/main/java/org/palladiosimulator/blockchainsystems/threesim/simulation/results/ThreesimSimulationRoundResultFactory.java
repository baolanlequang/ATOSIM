package org.palladiosimulator.blockchainsystems.threesim.simulation.results;

import org.palladiosimulator.blockchainsystems.threesim.metrics.AttackerBlockRewards;
import org.palladiosimulator.blockchainsystems.threesim.metrics.FinneyAttackSuccess;
import org.palladiosimulator.blockchainsystems.threesim.metrics.RaceAttackSuccess;
import org.palladiosimulator.blockchainsystems.threesim.metrics.SelfishMiningAttackSuccess;
import org.palladiosimulator.blockchainsystems.threesim.metrics.TotalBlockRewards;
import org.palladiosimulator.blockchainsystems.threesim.metrics.calculators.*;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.OutputMetricsSet;
import org.palladiosimulator.blockchainsystems.threesim.monitoring.ThreesimSimulationMonitor;
import org.palladiosimulator.blockchainsystems.threesim.simulation.AttackType;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;

import java.util.Set;

public class ThreesimSimulationRoundResultFactory {

    // Attack types whose profitability is defined the same way as selfish mining (Eyal & Sirer 2014;
    // stubborn mining generalizes selfish mining the same way per Nayak et al. 2016): attacker's
    // relative revenue exceeding their fair hash-power share. RACE keeps its own race-to-depth
    // (double-spend) success criterion via RaceAttackSuccess, so it is excluded here.
    private static final Set<AttackType> REVENUE_SHARE_SUCCESS_ATTACK_TYPES = Set.of(
            AttackType.SELFISH_MINING,
            AttackType.LEAD_STUBBORN_MINING,
            AttackType.EQUAL_FORK_STUBBORN_MINING,
            AttackType.TRAIL_STUBBORN_MINING,
            AttackType.COMBINED_SELFISH_RACE,
            AttackType.COMBINED_SELFISH_FINNEY,
            AttackType.COMBINED_SELFISH_LEAD_STUBBORN,
            AttackType.COMBINED_SELFISH_TRAIL_STUBBORN);

    private final ThreesimSimulationParameters _parameters;
    private final ThreesimSimulationMonitor _monitor;
    private final long _finalSystemTime;

    public ThreesimSimulationRoundResultFactory(ThreesimSimulationParameters parameters,
            ThreesimSimulationMonitor monitor, long finalSystemTime) {
        _parameters = parameters;
        _monitor = monitor;
        _finalSystemTime = finalSystemTime;
    }

    public ThreesimSimulationRoundResult createSimulationRoundResult() {
        var state = _monitor.getFinalState(_finalSystemTime);
        double attackerRewards = _parameters.getAttackerNodeIds().stream()
                .mapToInt(_monitor::getBlockRewardsForNode).sum();
        double totalRewards = _monitor.getTotalBlockRewards();

        double attackerRevenueShareFraction = totalRewards == 0.0 ? 0.0 : attackerRewards / totalRewards;
        boolean selfishMiningAttackSucceeded = REVENUE_SHARE_SUCCESS_ATTACK_TYPES.contains(_parameters.getAttackType())
                ? attackerRevenueShareFraction > _parameters.getAttackerHashPower()
                : _monitor.hasSelfishMiningAttackSucceeded();

        return new ThreesimSimulationRoundResult(OutputMetricsSet.from(

                new ShannonEntropyCalculator(
                        _parameters.getShannonEntropyK(),
                        state.getBlocksProposedPerNode()).calculate(),

                new GeographicalDiversityCalculator(
                        state.getNumberOfNodes(),
                        state.getNumberOfGeographicalRegions(),
                        state.getNumberOfNodesPerRegion()).calculate(),

                new NakamotoCoefficientCalculator(
                        state.getHashPowerPerNode(),
                        _parameters.getNakamotoCoefficientThreshold()).calculate(),

                new HerfindahlHirschmanIndexCalculator(
                        state.getTokensHeldPerNode()).calculate(),

                new GiniCoefficientCalculator(
                        state.getTokensHeldPerNode()).calculate(),

                new AvailabilityScalabilityCalculator(
                        state.getNumberOfConfirmedTransactions(),
                        state.getNumberOfSubmittedTransactions()).calculate(),

                new TransactionThroughputCalculator(
                        state.getNumberOfConfirmedTransactions(),
                        _finalSystemTime).calculate(),

                new AvailabilitySecurityCalculator(
                        state.getMeanTimeBetweenFailures(),
                        state.getMeanTimeToRepair()).calculate(),

                new ConsistencyCalculator(
                        state.getBlockProposalTimeAndConfirmationTimePerConfirmedBlock()).calculate(),

                new FaultToleranceCalculator(
                        state.getAverageThroughputDuringNormalOperation(),
                        state.getAverageThroughputDuringFailure(),
                        state.getAverageConfirmationLatencyDuringNormalOperation(),
                        state.getAverageConfirmationLatencyDuringFailure()).calculate(),

                new ReliabilityCalculator(
                        _parameters.getReliabilityObservationTimespan(),
                        state.getMeanTimeBetweenFailures()).calculate(),

                new StaleBlockRateCalculator(
                        state.getNumberOfStaleBlocks(),
                        state.getNumberOfConfirmedBlocks()).calculate(),

                new AttackerRevenueShareCalculator(attackerRewards, totalRewards).calculate(),
                new AttackerBlockRewards((int) attackerRewards),
                new TotalBlockRewards((int) totalRewards),

                new FinneyAttackSuccess(_monitor.hasFinneyAttackSucceeded()),
                new RaceAttackSuccess(_monitor.hasRaceAttackSucceeded()),
                new SelfishMiningAttackSuccess(selfishMiningAttackSucceeded),

                new DoubleSpendSuccessProbabilityCalculator(
                        _parameters.getAttackerHashPower(),
                        _parameters.getConfirmationDepth()).calculate(),

                new AttackSuccessTimeCalculator(_monitor.getAttackSuccessTime()).calculate()
        ), _monitor.getChainReorganizations());
    }
}
