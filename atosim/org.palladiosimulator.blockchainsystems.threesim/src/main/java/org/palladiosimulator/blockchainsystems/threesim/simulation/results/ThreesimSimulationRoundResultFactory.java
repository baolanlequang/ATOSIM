package org.palladiosimulator.blockchainsystems.threesim.simulation.results;

import org.palladiosimulator.blockchainsystems.threesim.metrics.FinneyAttackSuccess;
import org.palladiosimulator.blockchainsystems.threesim.metrics.RaceAttackSuccess;
import org.palladiosimulator.blockchainsystems.threesim.metrics.calculators.*;
import org.palladiosimulator.blockchainsystems.threesim.metrics.utils.OutputMetricsSet;
import org.palladiosimulator.blockchainsystems.threesim.monitoring.ThreesimSimulationMonitor;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;

public class ThreesimSimulationRoundResultFactory {

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
        double lambdaH = (1.0 - _parameters.getAttackerHashPower()) / _parameters.getBlockInterval();

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

                new FinneyAttackSuccess(_monitor.hasFinneyAttackSucceeded()),
                new RaceAttackSuccess(_monitor.hasRaceAttackSucceeded()),

                new DoubleSpendSuccessProbabilityCalculator(
                        _parameters.getAttackerHashPower(),
                        _parameters.getConfirmationDepth()).calculate(),

                new AttackSuccessTimeCalculator(_monitor.getAttackSuccessTime()).calculate(),

                new ForkProbabilityCalculator(lambdaH, _parameters.getPropagationDelay()).calculate()
        ));
    }
}
