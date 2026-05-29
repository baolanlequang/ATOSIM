package org.palladiosimulator.blockchainsystems.threesim.simulation;

import java.util.Collections;
import java.util.Set;

public class ThreesimSimulationParameters {

    private final double failureThroughputThreshold;
    private final double shannonEntropyK;
    private final double nakamotoCoefficientThreshold;
    private final double reliabilityObservationTimespan;
    private final AttackType attackType;
    private final boolean combinedAttackEnabled;
    private final AttackType secondaryAttackType;
    private final Set<String> attackerNodeIds;
    private final double attackerHashPower;
    private final double gamma;
    private final long deltaA;
    private final long deltaB;
    private final int confirmationDepth;
    private final double blockInterval;
    private final double propagationDelay;
    private final int nodeDegree;
    private final int maxBlockSize;
    private final double networkBandwidth;

    public ThreesimSimulationParameters(
            double failureThroughputThreshold,
            double shannonEntropyK,
            double nakamotoCoefficientThreshold,
            double reliabilityObservationTimespan,
            AttackType attackType,
            boolean combinedAttackEnabled,
            AttackType secondaryAttackType,
            Set<String> attackerNodeIds,
            double attackerHashPower,
            double gamma,
            long deltaA,
            long deltaB,
            int confirmationDepth,
            double blockInterval,
            double propagationDelay,
            int nodeDegree,
            int maxBlockSize,
            double networkBandwidth
    ) {
        this.failureThroughputThreshold = failureThroughputThreshold;
        this.shannonEntropyK = shannonEntropyK;
        this.nakamotoCoefficientThreshold = nakamotoCoefficientThreshold;
        this.reliabilityObservationTimespan = reliabilityObservationTimespan;
        this.attackType = attackType != null ? attackType : AttackType.NONE;
        this.combinedAttackEnabled = combinedAttackEnabled;
        this.secondaryAttackType = secondaryAttackType != null ? secondaryAttackType : AttackType.NONE;
        this.attackerNodeIds = attackerNodeIds != null ? attackerNodeIds : Collections.emptySet();
        this.attackerHashPower = attackerHashPower;
        this.gamma = gamma;
        this.deltaA = deltaA;
        this.deltaB = deltaB;
        this.confirmationDepth = confirmationDepth;
        this.blockInterval = blockInterval;
        this.propagationDelay = propagationDelay;
        this.nodeDegree = nodeDegree;
        this.maxBlockSize = maxBlockSize;
        this.networkBandwidth = networkBandwidth;
    }

    public double getFailureThroughputThreshold() { return failureThroughputThreshold; }
    public double getShannonEntropyK() { return shannonEntropyK; }
    public double getNakamotoCoefficientThreshold() { return nakamotoCoefficientThreshold; }
    public double getReliabilityObservationTimespan() { return reliabilityObservationTimespan; }
    public AttackType getAttackType() { return attackType; }
    public boolean isCombinedAttackEnabled() { return combinedAttackEnabled; }
    public AttackType getSecondaryAttackType() { return secondaryAttackType; }
    public Set<String> getAttackerNodeIds() { return attackerNodeIds; }
    public double getAttackerHashPower() { return attackerHashPower; }
    public double getGamma() { return gamma; }
    public long getDeltaA() { return deltaA; }
    public long getDeltaB() { return deltaB; }
    public int getConfirmationDepth() { return confirmationDepth; }
    public double getBlockInterval() { return blockInterval; }
    public double getPropagationDelay() { return propagationDelay; }
    public int getNodeDegree() { return nodeDegree; }
    public int getMaxBlockSize() { return maxBlockSize; }
    public double getNetworkBandwidth() { return networkBandwidth; }
}
