package org.palladiosimulator.blockchainsystems.core.behavior;

/**
 * Item 9: the phases the four stubborn-mining-family attack behaviors (Selfish, Lead-stubborn,
 * Trail-stubborn, Equal-fork-stubborn) move through, as derived from their existing state
 * (AttackForkState) purely for diagnostic tracing -- this enum is a read model computed FROM the
 * existing boolean/counter state (see AttackForkState.computePhase), never a source of truth
 * used to gate any decision. Every strategy's own branching logic keeps using its own literal
 * field checks (inTieState, deficit > 0, hiddenLead() comparisons, ...) exactly as before; this
 * enum only names, for logging, what those checks already jointly imply.
 *
 * TRAILING is reachable only by Trail-stubborn (Tj-stubborn); the other three never set
 * deficit above 0, so computePhase never returns TRAILING for them.
 */
public enum AttackPhase {
    /** No active private branch: privateChain is empty and the attacker is not trailing. */
    SYNCED,
    /** Hidden material accumulating, nothing revealed, not currently trailing. */
    PRIVATE_LEAD,
    /** Part of the private branch has been revealed; an active public tie/race is underway. */
    TIED_CONTEST,
    /** Trail-stubborn only: behind the public chain, still trying to catch back up. */
    TRAILING
}
