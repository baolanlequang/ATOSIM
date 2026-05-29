package org.palladiosimulator.blockchainsystems.threesim.monitoring;

import java.util.Collection;
import java.util.List;

public class ThreesimSimulationMonitorState {

    private final int numberOfNodes;
    private final Collection<Double> hashPowerPerNode;
    private final Collection<Integer> blocksProposedPerNode;
    private final int numberOfGeographicalRegions;
    private final Collection<Integer> numberOfNodesPerRegion;
    private final int numberOfSubmittedTransactions;
    private final int numberOfConfirmedTransactions;
    private final List<Double> tokensHeldPerNode;
    private final Collection<long[]> blockProposalTimeAndConfirmationTimePerConfirmedBlock;
    private final double meanTimeBetweenFailures;
    private final double meanTimeToRepair;
    private final int numberOfStaleBlocks;
    private final int numberOfConfirmedBlocks;
    private final double averageThroughputDuringFailure;
    private final double averageThroughputDuringNormalOperation;
    private final double averageConfirmationLatencyDuringFailure;
    private final double averageConfirmationLatencyDuringNormalOperation;

    public ThreesimSimulationMonitorState(
            int numberOfNodes,
            Collection<Double> hashPowerPerNode,
            Collection<Integer> blocksProposedPerNode,
            int numberOfGeographicalRegions,
            Collection<Integer> numberOfNodesPerRegion,
            int numberOfSubmittedTransactions,
            int numberOfConfirmedTransactions,
            List<Double> tokensHeldPerNode,
            Collection<long[]> blockProposalTimeAndConfirmationTimePerConfirmedBlock,
            double meanTimeBetweenFailures,
            double meanTimeToRepair,
            int numberOfStaleBlocks,
            int numberOfConfirmedBlocks,
            double averageThroughputDuringFailure,
            double averageThroughputDuringNormalOperation,
            double averageConfirmationLatencyDuringFailure,
            double averageConfirmationLatencyDuringNormalOperation
    ) {
        this.numberOfNodes = numberOfNodes;
        this.hashPowerPerNode = hashPowerPerNode;
        this.blocksProposedPerNode = blocksProposedPerNode;
        this.numberOfGeographicalRegions = numberOfGeographicalRegions;
        this.numberOfNodesPerRegion = numberOfNodesPerRegion;
        this.numberOfSubmittedTransactions = numberOfSubmittedTransactions;
        this.numberOfConfirmedTransactions = numberOfConfirmedTransactions;
        this.tokensHeldPerNode = tokensHeldPerNode;
        this.blockProposalTimeAndConfirmationTimePerConfirmedBlock = blockProposalTimeAndConfirmationTimePerConfirmedBlock;
        this.meanTimeBetweenFailures = meanTimeBetweenFailures;
        this.meanTimeToRepair = meanTimeToRepair;
        this.numberOfStaleBlocks = numberOfStaleBlocks;
        this.numberOfConfirmedBlocks = numberOfConfirmedBlocks;
        this.averageThroughputDuringFailure = averageThroughputDuringFailure;
        this.averageThroughputDuringNormalOperation = averageThroughputDuringNormalOperation;
        this.averageConfirmationLatencyDuringFailure = averageConfirmationLatencyDuringFailure;
        this.averageConfirmationLatencyDuringNormalOperation = averageConfirmationLatencyDuringNormalOperation;
    }

    public int getNumberOfNodes() { return numberOfNodes; }
    public Collection<Double> getHashPowerPerNode() { return hashPowerPerNode; }
    public Collection<Integer> getBlocksProposedPerNode() { return blocksProposedPerNode; }
    public int getNumberOfGeographicalRegions() { return numberOfGeographicalRegions; }
    public Collection<Integer> getNumberOfNodesPerRegion() { return numberOfNodesPerRegion; }
    public int getNumberOfSubmittedTransactions() { return numberOfSubmittedTransactions; }
    public int getNumberOfConfirmedTransactions() { return numberOfConfirmedTransactions; }
    public List<Double> getTokensHeldPerNode() { return tokensHeldPerNode; }
    public Collection<long[]> getBlockProposalTimeAndConfirmationTimePerConfirmedBlock() { return blockProposalTimeAndConfirmationTimePerConfirmedBlock; }
    public double getMeanTimeBetweenFailures() { return meanTimeBetweenFailures; }
    public double getMeanTimeToRepair() { return meanTimeToRepair; }
    public int getNumberOfStaleBlocks() { return numberOfStaleBlocks; }
    public int getNumberOfConfirmedBlocks() { return numberOfConfirmedBlocks; }
    public double getAverageThroughputDuringFailure() { return averageThroughputDuringFailure; }
    public double getAverageThroughputDuringNormalOperation() { return averageThroughputDuringNormalOperation; }
    public double getAverageConfirmationLatencyDuringFailure() { return averageConfirmationLatencyDuringFailure; }
    public double getAverageConfirmationLatencyDuringNormalOperation() { return averageConfirmationLatencyDuringNormalOperation; }
}
