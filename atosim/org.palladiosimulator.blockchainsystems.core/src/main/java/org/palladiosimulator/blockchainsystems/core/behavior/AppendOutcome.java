package org.palladiosimulator.blockchainsystems.core.behavior;

public enum AppendOutcome {
    INCLUDED,
    FORKING,
    STALE,
    ORPHAN,
    ALREADY_APPENDED,
    NOT_APPENDED
}
