package org.palladiosimulator.blockchainsystems.core.network;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.EventCoordinator;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.EventDispatchable;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationContext;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationLifecycleAwareValueProvider;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.SystemClock;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLoggerContainer;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Item 8: standalone check for the message-transmission-delay formula
 * (tau_tx = 8 * size_bytes / bandwidth_bits_per_second, converted to milliseconds) across
 * representative (blockSize, bandwidth) pairs drawn from the real sampling ranges
 * (sampling/lhs_generate_sample_two_stage.py): max_block_size in [250_000, 8_000_000] bytes
 * (uniform), bandwidth from the fixed 10-value set {5.0, 7.1, 10.2, 14.6, 20.9, 29.9, 42.7, 61.0,
 * 87.4, 125.0} Mbit/s (not continuously sampled -- these are the only bandwidth values the
 * pipeline ever produces).
 *
 * Exercises the real, unmodified P2PLink.send() -> dispatchEvent() -> (private)
 * handleMessageSentEvent() code path via a minimal local EventCoordinator/SimulationContext test
 * double -- it does not reimplement or bypass the formula under test, and it does not modify
 * P2PLink or AbstractThreesimP2PNetworkFactory (both confirmed already correct; this only adds
 * the missing verification artifact for item 8's checklist wording). No JUnit/Mockito, per the
 * standing project decision: plain assertions via IllegalStateException, same pattern as
 * AttackAwareResourcePowerCalculator.
 *
 * Standalone runnable, no jar/Eclipse build required: only depends on this module's own classes
 * (plus JDK), so `javac`+`java` against org.palladiosimulator.blockchainsystems.core's compiled
 * output is sufficient.
 */
public class TransmissionDelayFormulaCheck {

    // Mbit/s -> bit/s, matching AbstractThreesimP2PNetworkFactory.java:41's conversion exactly
    // (bps = networkBandwidth * 1_000_000.0) -- not re-derived from that class (which requires an
    // EMF LinkThroughputSpecification and isn't cleanly callable standalone), just the same
    // dimensionless constant, since that conversion is a single multiplication with nothing else
    // to isolate.
    private static final double MBPS_TO_BPS = 1_000_000.0;

    public static void main(String[] args) {
        int[] blockSizesBytes = {250_000, 4_000_000, 8_000_000};
        double[] bandwidthsMbps = {5.0, 29.9, 125.0};

        int checked = 0;
        for (int blockSize : blockSizesBytes) {
            for (double bandwidthMbps : bandwidthsMbps) {
                check(blockSize, bandwidthMbps);
                checked++;
            }
        }
        System.out.println("TransmissionDelayFormulaCheck: all " + checked
                + " representative (blockSize, bandwidth) pairs passed.");
    }

    private static void check(int blockSizeBytes, double bandwidthMbps) {
        long bandwidthBps = (long) (bandwidthMbps * MBPS_TO_BPS);
        long latencyMs = 0L;
        long startTime = 1_000_000L; // arbitrary non-zero start, so a pass also confirms the delay is relative

        CapturingEventCoordinator coordinator = new CapturingEventCoordinator();
        SimulationContext context = new FixedSimulationContext(startTime, coordinator);

        P2PLink link = new P2PLink(
                new ConstantLongProvider(latencyMs),
                new ConstantLongProvider(bandwidthBps),
                new P2PNode("A"),
                new P2PNode("B"));
        link.initialize(context);

        link.send(new FixedSizeMessage(blockSizeBytes));
        if (coordinator.raised.size() != 1) {
            throw new IllegalStateException(
                    "Expected exactly 1 event raised by send(), got " + coordinator.raised.size());
        }
        link.dispatchEvent(coordinator.raised.get(0));
        if (coordinator.raised.size() != 2) {
            throw new IllegalStateException(
                    "Expected exactly 2 events raised after dispatching the sent event, got " + coordinator.raised.size());
        }

        long actualDelayMs = coordinator.raised.get(1).getOccurrenceTime() - startTime;

        // tau_tx = 8 * size_bytes / bandwidth_bits_per_second, in seconds; * 1000 for ms.
        double expectedTransmissionMs = 8.0 * blockSizeBytes / bandwidthBps * 1000.0;
        long expectedDelayMs = Math.round(latencyMs + expectedTransmissionMs);

        if (actualDelayMs != expectedDelayMs) {
            throw new IllegalStateException(String.format(Locale.US,
                    "Transmission delay mismatch for blockSize=%d bytes, bandwidth=%.1f Mbit/s: expected %d ms, got %d ms",
                    blockSizeBytes, bandwidthMbps, expectedDelayMs, actualDelayMs));
        }

        System.out.printf(Locale.US, "  OK: blockSize=%,d bytes, bandwidth=%.1f Mbit/s -> delay=%d ms (expected %d ms)%n",
                blockSizeBytes, bandwidthMbps, actualDelayMs, expectedDelayMs);
    }

    // --- minimal test doubles implementing real production interfaces; no production class touched ---

    private static class CapturingEventCoordinator implements EventCoordinator {
        final List<Event> raised = new ArrayList<>();
        @Override public void raiseEvent(Event event) { raised.add(event); }
        @Override public void cancelEventsFor(EventDispatchable eventOrigin) { }
    }

    private static class FixedSimulationContext implements SimulationContext {
        private final SystemClock clock;
        private final EventCoordinator coordinator;
        FixedSimulationContext(long currentTime, EventCoordinator coordinator) {
            this.clock = () -> currentTime;
            this.coordinator = coordinator;
        }
        @Override public EventCoordinator getEventCoordinator() { return coordinator; }
        @Override public SystemClock getSystemClock() { return clock; }
        @Override public TraceEventLoggerContainer getTraceEventLoggerContainer() {
            throw new UnsupportedOperationException("Not needed by the send()/dispatchEvent() path under test.");
        }
    }

    private static class ConstantLongProvider implements SimulationLifecycleAwareValueProvider<Long> {
        private final long value;
        ConstantLongProvider(long value) { this.value = value; }
        @Override public Long getValue() { return value; }
        @Override public void initialize(SimulationContext simulationContext) { }
        @Override public void cleanup() { }
    }

    private static class FixedSizeMessage implements Message {
        private final int size;
        FixedSizeMessage(int size) { this.size = size; }
        @Override public Object getContent() { return this; }
        @Override public String getContentType() { return "test-message"; }
        @Override public int getSize() { return size; }
    }
}
