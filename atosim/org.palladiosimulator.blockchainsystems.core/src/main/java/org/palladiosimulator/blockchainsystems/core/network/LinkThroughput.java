package org.palladiosimulator.blockchainsystems.core.network;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TemporalValue;

public class LinkThroughput implements TemporalValue<Long> {

    private final long _value;
    private final long _duration;

    public LinkThroughput(long value, long duration) {
        _value = value;
        _duration = duration;
    }

    @Override public Long getValue() { return _value; }
    @Override public long getDuration() { return _duration; }
}
