package org.palladiosimulator.blockchainsystems.core.simulation.termination;

public class LongestChainExceededMaxLengthCondition {

    private final long _maxAllowedLength;
    private long _currentLength = 0;

    public LongestChainExceededMaxLengthCondition(long maxAllowedLength) {
        _maxAllowedLength = maxAllowedLength;
    }

    public long getCurrentLength() { return _currentLength; }

    public boolean hasLengthExceeded() {
        return _currentLength >= _maxAllowedLength;
    }

    public void onBlockAppended(long blockPosition) {
        _currentLength = Math.max(blockPosition, _currentLength);
    }
}
