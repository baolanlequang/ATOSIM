package org.palladiosimulator.blockchainsystems.threesim.simulation.results;

import org.palladiosimulator.blockchainsystems.threesim.metrics.ActualBlockSize;
import org.palladiosimulator.blockchainsystems.threesim.metrics.AttackerBlockRewards;
import org.palladiosimulator.blockchainsystems.threesim.metrics.BlockTransactionCount;
import org.palladiosimulator.blockchainsystems.threesim.metrics.FinneyAttackSuccess;
import org.palladiosimulator.blockchainsystems.threesim.metrics.ForkDurationMean;
import org.palladiosimulator.blockchainsystems.threesim.metrics.ForkDurationMedian;
import org.palladiosimulator.blockchainsystems.threesim.metrics.LeadStubbornLostLeadTransitionCount;
import org.palladiosimulator.blockchainsystems.threesim.metrics.PropagationTimeMean;
import org.palladiosimulator.blockchainsystems.threesim.metrics.PropagationTimeMedian;
import org.palladiosimulator.blockchainsystems.threesim.metrics.PropagationTimeP95;
import org.palladiosimulator.blockchainsystems.threesim.metrics.PropagationTimeToP90Coverage;
import org.palladiosimulator.blockchainsystems.threesim.metrics.PropagationTimeToP95Coverage;
import org.palladiosimulator.blockchainsystems.threesim.metrics.PropagationTimeToP100Coverage;
import org.palladiosimulator.blockchainsystems.threesim.metrics.RaceAttackSuccess;
import org.palladiosimulator.blockchainsystems.threesim.metrics.SelfishMiningAttackSuccess;
import org.palladiosimulator.blockchainsystems.threesim.metrics.TotalBlockRewards;
import org.palladiosimulator.blockchainsystems.threesim.metrics.calculators.*;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.OutputMetricsSet;
import org.palladiosimulator.blockchainsystems.threesim.monitoring.ThreesimSimulationMonitor;
import org.palladiosimulator.blockchainsystems.threesim.creation.TopologyDeterminismInfo;
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
    private final TopologyDeterminismInfo _topologyDeterminismInfo;

    public ThreesimSimulationRoundResultFactory(ThreesimSimulationParameters parameters,
            ThreesimSimulationMonitor monitor, long finalSystemTime) {
        this(parameters, monitor, finalSystemTime, null);
    }

    public ThreesimSimulationRoundResultFactory(ThreesimSimulationParameters parameters,
            ThreesimSimulationMonitor monitor, long finalSystemTime, TopologyDeterminismInfo topologyDeterminismInfo) {
        _parameters = parameters;
        _monitor = monitor;
        _finalSystemTime = finalSystemTime;
        _topologyDeterminismInfo = topologyDeterminismInfo;
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

                // Item 7: mean actual packed size / transaction count across every block mined
                // this round -- distinct from the configured maxBlockSize cap (already output
                // separately in threesimSimulationParameters, unchanged).
                new ActualBlockSize(_monitor.getMeanActualBlockSize()),
                new BlockTransactionCount(_monitor.getMeanBlockTransactionCount()),

                // Part B: count of Lead-stubborn's defining PRIVATE_LEAD -> TIED_CONTEST phase
                // transitions this round (0 for every other attack type) -- see
                // LeadStubbornLostLeadTransitionCount's doc for the granularity caveat.
                new LeadStubbornLostLeadTransitionCount(_monitor.getLeadStubbornLostLeadTransitions()),

                // Colleague review item 6: propagation-time quantiles (mean/median/p95) and
                // fork-duration (mean/median) -- see ThreesimSimulationMonitor's field/method
                // docs for what's measured and why (-1.0 = no data this round for that metric).
                new PropagationTimeMean(_monitor.getMeanPropagationTime()),
                new PropagationTimeMedian(_monitor.getMedianPropagationTime()),
                new PropagationTimeP95(_monitor.getP95PropagationTime()),

                // Topology/forwarding/timing investigation follow-up: percentage-based coverage
                // thresholds (90%/95%/100%, 100% kept alongside unchanged) -- see
                // ThreesimSimulationMonitor.recordBlockPropagation's doc for why 100%-only
                // structurally never completes on high-diameter, low-degree topologies.
                new PropagationTimeToP90Coverage(_monitor.getMeanPropagationTimeP90Coverage()),
                new PropagationTimeToP95Coverage(_monitor.getMeanPropagationTimeP95Coverage()),
                new PropagationTimeToP100Coverage(_monitor.getMeanPropagationTimeP100Coverage()),

                new ForkDurationMean(_monitor.getMeanForkDuration()),
                new ForkDurationMedian(_monitor.getMedianForkDuration()),

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
        ), _monitor.getChainReorganizations(), _topologyDeterminismInfo, _monitor.getEpisodeStatus(),
                _monitor.getDecisiveAttackerReorg());
    }
}
