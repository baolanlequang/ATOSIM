package org.palladiosimulator.blockchainsystems.core.behavior;

/**
 * Read-only view of an attack behaviour's private-branch progress, used by the simulation
 * monitor's end rule (AC_fix_plan item 2, decisions Q2/Q3) and by the per-replication
 * diagnostics. Implementations only expose state they already keep; nothing here influences a
 * strategy decision. Honest behaviours do not implement it and count as "nothing held".
 */
public interface UnpublishedBlockCounter {

    /** Sentinel for {@link #getBranchStartTime()} when no private branch has been started. */
    long NO_BRANCH = Long.MIN_VALUE;

    /** Number of mined attacker blocks that have not been published yet. */
    int getUnpublishedBlockCount();

    /**
     * Mined timestamp of the first block of the current private branch (the block that set the
     * branch's fork point), or {@link #NO_BRANCH}. An open contest and a trailing deficit belong
     * to this same branch.
     */
    long getBranchStartTime();

    /** True while a publicly visible contest (tie) created by the attacker is still open. */
    boolean hasOpenContest();

    /** Blocks the attacker trails the public chain by, as the behaviour tracks it (0 if not trailing). */
    int getTrailingDeficit();

    /**
     * Hidden blocks dropped because their publication appended as STALE or ALREADY_APPENDED, i.e.
     * they could no longer become public (AD_trail_check 5.5). Only the trail-stubborn behaviour
     * drops blocks; a non-zero count is a finding, not normal behaviour.
     */
    default long getDroppedUnpublishableBlockCount() {
        return 0;
    }

    /**
     * Q2/Q3: the attacker holds the round open iff its current private branch started at or
     * before the last recorded reorganization and still has unpublished blocks, an open contest
     * or a trailing deficit. A branch started after the last reorganization is a new attempt and
     * does not hold the round open.
     */
    default boolean holdsRoundOpen(long lastReorgTimestamp) {
        long start = getBranchStartTime();
        if (start == NO_BRANCH || start > lastReorgTimestamp) return false;
        return getUnpublishedBlockCount() > 0 || hasOpenContest() || getTrailingDeficit() > 0;
    }
}
