package org.palladiosimulator.blockchainsystems.core.behavior;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;

/**
 * Item 9: emitted whenever an attack behavior's computed {@link AttackPhase} changes (see
 * AttackForkState.logPhaseTransitionIfChanged). Diagnostic only -- records what happened, for
 * small-run inspection; nothing in the simulation reads this event back.
 */
public class AttackPhaseTransitionTraceEvent implements TraceEvent {

    public static final String EVENT_TYPE = "AttackPhaseTransitionTraceEvent";

    private final long _occurrenceTime;
    private final AttackPhase _oldPhase;
    private final AttackPhase _newPhase;
    private final int _hiddenLead;
    private final int _deficit;
    private final String _forkPointHash;

    public AttackPhaseTransitionTraceEvent(
            long occurrenceTime, AttackPhase oldPhase, AttackPhase newPhase,
            int hiddenLead, int deficit, String forkPointHash) {
        _occurrenceTime = occurrenceTime;
        _oldPhase = oldPhase;
        _newPhase = newPhase;
        _hiddenLead = hiddenLead;
        _deficit = deficit;
        _forkPointHash = forkPointHash;
    }

    @Override public long getOccurrenceTime() { return _occurrenceTime; }
    @Override public String getEventType() { return EVENT_TYPE; }
    public AttackPhase getOldPhase() { return _oldPhase; }
    public AttackPhase getNewPhase() { return _newPhase; }
    public int getHiddenLead() { return _hiddenLead; }
    public int getDeficit() { return _deficit; }
    public String getForkPointHash() { return _forkPointHash; }
}
