package org.palladiosimulator.blockchainsystems.threesim.metrics;
import org.palladiosimulator.blockchainsystems.threesim.metrics.abstractions.OutputMetric;

/**
 * Count, for this round, of Lead-stubborn's defining PRIVATE_LEAD -&gt; TIED_CONTEST phase
 * transitions (see LeadStubbornMiningNodeBehavior's class doc: "for ANY hidden lead &gt;= 1,
 * reveal exactly one block ... and keep the tied contest alive" -- entering TIED_CONTEST from
 * PRIVATE_LEAD is exactly that reveal). Always 0 for every attack type other than
 * LEAD_STUBBORN_MINING (see ThreesimSimulationMonitor.onTraceEventOccurred's filter).
 *
 * Granularity caveat: AttackPhaseTransitionTraceEvent only fires on a phase CHANGE (see
 * AttackForkState.logPhaseTransitionIfChanged), so this counts distinct PRIVATE_LEAD-to-
 * TIED_CONTEST re-tie EPISODES, not every individual publishOneHiddenBlock() call -- if the
 * public chain catches up more than once while the attacker is already in an ongoing tied
 * contest, each further one-block reveal keeps the phase at TIED_CONTEST (no transition, so no
 * additional count) rather than incrementing again. Extending to per-call granularity would
 * require new event-raising logic in AttackForkState/LeadStubbornMiningNodeBehavior, which is
 * out of scope here (this task only consumes the already-emitted phase-transition event).
 */
public class LeadStubbornLostLeadTransitionCount implements OutputMetric<Integer> {
    public static final String NAME = "Lead-Stubborn Lost-Lead Transitions";
    public static final String UNIT = "transitions";
    private final int _value;
    public LeadStubbornLostLeadTransitionCount(int value) { _value = value; }
    @Override public Integer getValue() { return _value; }
    @Override public String getName() { return NAME; }
    @Override public String getUnit() { return UNIT; }
}
