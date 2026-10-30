package org.palladiosimulator.blockchainsystems.threesim.simulation.results;

/**
 * Per-replication diagnostics (AC_fix_plan item 4), built once by ThreesimSimulationMonitor when
 * the round terminates and carried on ThreesimSimulationRoundResult. Serialized as one object per
 * round in the top-level "replicationDiagnostics" array; deliberately not an OutputMetric, so it is
 * never averaged into averageSimulationRoundResult (the depth fields are null where undefined).
 *
 * Duplicate counters (decision Q4): "ChainOrPool" = dropped by the pre-existing chain/orphan-pool
 * checks of BlockPropagationStrategy; "KnownSetOnly" = dropped only by the new known-block set.
 * "Network" sums over all nodes, "Attacker" over attacker nodes only.
 *
 * @param attackerBlocksMined            blocks mined by attacker nodes (published or not, incl. abandoned)
 * @param totalBlocksMined               blocks mined by all nodes, incl. stale ones
 * @param unpublishedAttackerBlocksAtEnd unpublished attacker blocks at termination, any branch
 * @param attackerHoldsRoundOpenAtEnd    the Q2/Q3 end-rule test at termination (only true for an unresolved round)
 * @param honestAdoptionDepthMax         max over honest nodes of the depth of the earliest reorg that put S on
 *                                       their chain; null unless status is success or if no such node
 * @param honestAdoptionDepthMedian      median of the same (mean of the two middle values for an even count)
 * @param honestNodesWithAdoptionReorg   number of honest nodes with such a reorg; null unless success
 * @param honestNodeCount                number of honest nodes
 * @param honestOutcomeDisagreementAtEnd honest nodes whose outcome (canonical chain contains a block of X, the
 *                                       attacker blocks that appeared in any recorded replacing chain) differs
 *                                       from the status witness's outcome at termination; 0 in every resolved round
 * @param droppedUnpublishableAttackerBlocks hidden attacker blocks dropped because they could no longer be
 *                                       published (trail-stubborn defensive drop, AD 5.5); non-zero is a finding
 * @param simulatedDurationMs            simulated time from the start of the round (time 0) to the moment its end
 *                                       was decided: the occurrence time of the last processed event. Blocks are
 *                                       counted up to that same moment, so blocks / duration is a rate over one
 *                                       window (the clock's value at loop exit is the next, unprocessed slice)
 * @param miningSchedulesWhilePendingAttacker  attacker nodes: mining events scheduled while one was still pending
 *                                       and not cancelled by the caller first (MiningProcessImpl). Since the
 *                                       AH fix each is a harmless resample; before it, each left a second
 *                                       pending event (the old double-scheduling bug)
 * @param miningSchedulesWhilePendingNetwork   the same, summed over all nodes
 */
public record ReplicationDiagnostics(
        long attackerBlocksMined,
        long totalBlocksMined,
        int unpublishedAttackerBlocksAtEnd,
        boolean attackerHoldsRoundOpenAtEnd,
        long duplicateBodiesDroppedChainOrPoolNetwork,
        long duplicateBodiesDroppedKnownSetOnlyNetwork,
        long duplicateInvsIgnoredChainOrPoolNetwork,
        long duplicateInvsIgnoredKnownSetOnlyNetwork,
        long duplicateBodiesDroppedChainOrPoolAttacker,
        long duplicateBodiesDroppedKnownSetOnlyAttacker,
        long duplicateInvsIgnoredChainOrPoolAttacker,
        long duplicateInvsIgnoredKnownSetOnlyAttacker,
        Long honestAdoptionDepthMax,
        Double honestAdoptionDepthMedian,
        Integer honestNodesWithAdoptionReorg,
        int honestNodeCount,
        int honestOutcomeDisagreementAtEnd,
        long droppedUnpublishableAttackerBlocks,
        long simulatedDurationMs,
        long miningSchedulesWhilePendingAttacker,
        long miningSchedulesWhilePendingNetwork) {
}
