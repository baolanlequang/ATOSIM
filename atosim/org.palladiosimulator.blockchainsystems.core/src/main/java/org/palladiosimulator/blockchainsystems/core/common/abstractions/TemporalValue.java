package org.palladiosimulator.blockchainsystems.core.common.abstractions;

public interface TemporalValue<T> {
    T getValue();
    long getDuration();
}
