package org.palladiosimulator.blockchainsystems.threesim.simulation;

import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationRound;
import org.palladiosimulator.blockchainsystems.core.simulation.termination.LongestChainExceededMaxLengthCondition;
import org.palladiosimulator.blockchainsystems.core.tracing.TraceEventLogOutput;
import org.palladiosimulator.blockchainsystems.threesim.creation.BlockchainSystemWithParameters;
import org.palladiosimulator.blockchainsystems.threesim.creation.ThreesimBlockchainSystemFactory;
import org.palladiosimulator.blockchainsystems.threesim.creation.TopologyDeterminismInfo;
import org.palladiosimulator.blockchainsystems.threesim.monitoring.ThreesimSimulationMonitor;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimSimulationRoundResult;
import org.palladiosimulator.blockchainsystems.threesim.simulation.results.ThreesimSimulationRoundResultFactory;

import java.util.Set;

public class ThreesimSimulationRound
        extends SimulationRound<ThreesimSimulationMonitor, ThreesimSimulationRoundResult> {

    private final ThreesimSimulationParameters _effectiveParameters;
    private final TopologyDeterminismInfo _topologyDeterminismInfo;

    public ThreesimSimulationRound(
            ThreesimBlockchainSystemFactory blockchainSystemFactory,
            Set<TraceEventLogOutput> logOutputs,
            long maxAllowedBlockchainLength,
            ThreesimSimulationParameters originalParameters,
            int replicationId) {

        this(blockchainSystemFactory.createBlockchainSystem(originalParameters, replicationId),
                logOutputs, maxAllowedBlockchainLength, originalParameters);
    }

    private ThreesimSimulationRound(
            BlockchainSystemWithParameters result,
            Set<TraceEventLogOutput> logOutputs,
            long maxAllowedBlockchainLength,
            ThreesimSimulationParameters originalParameters) {

        super(result.getSystem(), logOutputs,
                new ThreesimSimulationMonitor(
                        new LongestChainExceededMaxLengthCondition(maxAllowedBlockchainLength),
                        originalParameters.getFailureThroughputThreshold(),
                        result.getEffectiveParameters()));
        _effectiveParameters = result.getEffectiveParameters();
        _topologyDeterminismInfo = result.getTopologyDeterminismInfo();
    }

    @Override
    public ThreesimSimulationRoundResult createSimulationRoundResult(long finalSystemTime) {
        return new ThreesimSimulationRoundResultFactory(_effectiveParameters, monitor, finalSystemTime, _topologyDeterminismInfo)
                .createSimulationRoundResult();
    }
}
