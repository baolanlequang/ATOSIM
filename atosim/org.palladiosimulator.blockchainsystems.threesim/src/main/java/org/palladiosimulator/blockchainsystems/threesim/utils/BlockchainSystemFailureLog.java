package org.palladiosimulator.blockchainsystems.threesim.utils;

import java.util.ArrayList;
import java.util.List;

public class BlockchainSystemFailureLog {

    private static class Entry {
        final long occurrenceTime;
        Long duration;

        Entry(long occurrenceTime) {
            this.occurrenceTime = occurrenceTime;
        }
    }

    private final List<Entry> log = new ArrayList<>();

    public void failureStarted(long occurrenceTime) {
        if (isFailureOngoing()) return;
        log.add(new Entry(occurrenceTime));
    }

    public void failureEnded(long occurrenceTime) {
        if (!isFailureOngoing()) return;
        Entry last = log.get(log.size() - 1);
        last.duration = occurrenceTime - last.occurrenceTime;
    }

    public boolean isFailureOngoing() {
        return !log.isEmpty() && log.get(log.size() - 1).duration == null;
    }

    public double calculateMeanFailureDuration() {
        List<Long> durations = new ArrayList<>();
        for (Entry e : log) {
            if (e.duration != null) durations.add(e.duration);
        }
        if (durations.isEmpty()) return -1.0;
        return durations.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }

    public int getNumberOfFailures() {
        return log.size();
    }
}
