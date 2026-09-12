package org.palladiosimulator.blockchainsystems.core.behavior;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationContext;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogger;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Item 9: shared private-branch/fork state for the four stubborn-mining-family attack behaviors
 * (Selfish, Lead-stubborn, Trail-stubborn, Equal-fork-stubborn). Each behavior class owns one
 * instance and reads/writes it directly in place of what were previously local fields -- this is
 * a pure state container, not a policy. Every strategy keeps its own exact onBlockValidated/
 * onBlockMined branching logic (which fields to check, in which order, with which thresholds)
 * completely unchanged; only the storage location of those fields moved.
 *
 * Covers item 9's seven checklist concepts:
 * - attacker branch: {@link #getPrivateChain()} (List&lt;Block&gt;, unchanged from before)
 * - unpublished suffix: the same list -- in this codebase's model these are the same thing by
 *   construction (a block in privateChain is, by definition, not yet published/distributed); see
 *   {@link #getUnpublishedSuffix()}, a same-list alias kept only for checklist-terminology
 *   traceability, not a second copy of the data.
 * - published prefix: {@link #getPublishedInCurrentContest()}, kept as the pre-existing counter
 *   (not upgraded to actual block references) -- safest option, matches current semantics
 *   exactly, and every current strategy already treats it as write-only bookkeeping (set once or
 *   incremented, never read back in a decision -- true in all four classes before this refactor,
 *   preserved as-is here, not something this task adjudicates or fixes).
 * - fork point: {@link #getForkPointHash()}, new -- the hash of the block the current private
 *   branch diverged from. Set exactly once per fresh branch, the moment {@link #recordMinedBlock}
 *   is called while the private chain was empty (i.e. the first block of a new branch); this is a
 *   pure observation derived from data already at hand (the new block's own previousHash) and
 *   does not influence any existing decision anywhere.
 * - public competitor branch: deliberately NOT stored (see {@link #getPublicCompetitorTip}) --
 *   a stored reference would go stale the moment another node mines, since the public chain
 *   keeps moving independently of this attacker's state. Exposed as a live per-call query
 *   against the node's own Blockchain instead, which already only reflects appended/public
 *   blocks (an attacker's own unpublished privateChain blocks are never appended until
 *   published), so it already *is* "the public view" from this node's perspective at any instant.
 * - phase: {@link AttackPhase} + {@link #computePhase}, a read model computed from the fields
 *   below for logging only, never a decision input.
 * - lead/deficit: {@link #hiddenLead()} stays a recomputed method (privateChain.size()), matching
 *   every class's existing semantics; {@link #getDeficit()}/{@link #setDeficit} is an explicit
 *   field, as it already was in Trail-stubborn (the only class that ever writes to it).
 */
public class AttackForkState {

    private final List<Block> privateChain = new ArrayList<>();
    private String ownTipHash = null;
    private String forkPointHash = null;
    private boolean inContest = false;
    private int publishedInCurrentContest = 0;
    private int deficit = 0;

    public List<Block> getPrivateChain() { return privateChain; }

    /** Same list as {@link #getPrivateChain()} -- see the class doc's "unpublished suffix" note. */
    public List<Block> getUnpublishedSuffix() { return privateChain; }

    public String getOwnTipHash() { return ownTipHash; }
    public void setOwnTipHash(String ownTipHash) { this.ownTipHash = ownTipHash; }

    public String getForkPointHash() { return forkPointHash; }

    public boolean isInContest() { return inContest; }
    public void setInContest(boolean inContest) { this.inContest = inContest; }

    public int getPublishedInCurrentContest() { return publishedInCurrentContest; }
    public void setPublishedInCurrentContest(int value) { this.publishedInCurrentContest = value; }
    public void incrementPublishedInCurrentContest() { this.publishedInCurrentContest++; }

    public int getDeficit() { return deficit; }
    public void setDeficit(int deficit) { this.deficit = deficit; }
    public void incrementDeficit() { this.deficit++; }
    public void decrementDeficit() { this.deficit--; }

    public int hiddenLead() { return privateChain.size(); }

    /**
     * Records a newly mined attacker block: appends it to the private chain and updates the own
     * tip, exactly like the pre-refactor "privateChain.add(block); ownTipHash = block.getHash();"
     * pair that appeared verbatim in every class's onBlockMined. Additionally records the fork
     * point the first time this is called for a fresh branch (privateChain empty beforehand) --
     * purely observational, see the class doc.
     */
    public void recordMinedBlock(Block block) {
        if (privateChain.isEmpty()) {
            forkPointHash = block.getPreviousHash();
        }
        privateChain.add(block);
        ownTipHash = block.getHash();
    }

    /** Returns the oldest hidden block (the next to publish), or null if none. Matches the
     * pre-refactor "privateChain.get(0)" exactly -- does not remove it. */
    public Block peekNextToPublish() {
        return privateChain.isEmpty() ? null : privateChain.get(0);
    }

    /** Removes the oldest hidden block by index, matching the pre-refactor
     * "privateChain.remove(0)" exactly (index-based, not object-identity-based). */
    public void removeFirstPending() {
        privateChain.remove(0);
    }

    /**
     * Live view of the public competitor branch: this node's own Blockchain, which only ever
     * reflects appended (public or already-published) blocks -- see the class doc's "public
     * competitor branch" note. Not stored; queried fresh on every call.
     */
    public Block getPublicCompetitorTip(BlockchainSystemNodeContext context) {
        return context.getBlockchain().getPreferredTipOfLongestChains();
    }

    /** Clears contest bookkeeping only, matching every class's existing clearXxxStateOnly(). */
    public void clearContestOnly() {
        inContest = false;
        publishedInCurrentContest = 0;
    }

    /** Full reset, matching every class's existing resetPrivateState(). */
    public void reset() {
        privateChain.clear();
        ownTipHash = null;
        forkPointHash = null;
        deficit = 0;
        clearContestOnly();
    }

    /**
     * Derives the current {@link AttackPhase} from this state, for diagnostic logging only.
     * Mirrors the precedence already implicit in every class's own onBlockValidated branch order
     * (deficit check first where it exists, then contest state, then hidden lead) -- this is a
     * read model consistent with the real code's own priority, not an independent judgment.
     */
    public static AttackPhase computePhase(AttackForkState state) {
        if (state.deficit > 0) return AttackPhase.TRAILING;
        if (state.inContest) return AttackPhase.TIED_CONTEST;
        if (state.hiddenLead() > 0) return AttackPhase.PRIVATE_LEAD;
        return AttackPhase.SYNCED;
    }

    /**
     * Logs an {@link AttackPhaseTransitionTraceEvent} iff the phase actually changed between
     * phaseBefore and this state's current (post-call) phase. Null-safe on both simulationContext
     * and logger: SelfishMiningNodeBehavior/LeadStubbornMiningNodeBehavior/etc. are also used as
     * internal sub-behaviors inside the CombinedSelfish*NodeBehavior wrappers (see e.g.
     * CombinedSelfishLeadStubbornNodeBehavior), which never call initialize()/initializeLogger()
     * on those sub-instances -- only on the outer wrapper. In that case getSimulationContext()/
     * getTraceEventLogger() are null on the sub-instance, and this silently no-ops rather than
     * throwing, exactly as the pre-refactor code did by simply never calling either method at
     * all. Diagnostic tracing is therefore only available when a behavior is used directly
     * (SELFISH_MINING, LEAD_STUBBORN_MINING, TRAIL_STUBBORN_MINING, EQUAL_FORK_STUBBORN_MINING),
     * not through a COMBINED_* wrapper -- acceptable for a "small diagnostic run" capability.
     */
    public static void logPhaseTransitionIfChanged(
            SimulationContext simulationContext, TraceEventLogger logger,
            AttackForkState state, AttackPhase phaseBefore) {
        if (simulationContext == null || logger == null
                || !logger.isEventTypeEnabled(AttackPhaseTransitionTraceEvent.EVENT_TYPE)) {
            return;
        }
        AttackPhase phaseAfter = computePhase(state);
        if (phaseAfter == phaseBefore) {
            return;
        }
        logger.logEvent(new AttackPhaseTransitionTraceEvent(
                simulationContext.getSystemClock().getCurrentTime(),
                phaseBefore, phaseAfter,
                state.hiddenLead(), state.getDeficit(), state.getForkPointHash()));
    }
}
