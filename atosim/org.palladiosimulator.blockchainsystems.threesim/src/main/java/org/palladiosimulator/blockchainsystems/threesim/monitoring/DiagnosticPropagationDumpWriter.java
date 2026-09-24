package org.palladiosimulator.blockchainsystems.threesim.monitoring;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

// One-off diagnostic dump: per-node publish/reception/validation timestamps for every block
// mined in the first MAX_ROUNDS_TO_DUMP Monte Carlo rounds of the run (rounds 0 through
// MAX_ROUNDS_TO_DUMP - 1, by replicationId, not completion order), answering the colleague's
// request for a raw propagation-time example (confirming propagation time starts at
// block-publish, not at the end of mining). Gated off by default via the
// -Dthreesim.diagnosticPropagationDump=true system property (ENABLED, read once at class-init
// time -- same property BlockValidatorImpl checks before ever constructing the two new
// diagnostic-only trace events this class consumes). Not a production output artifact: writes
// a separate, fixed-path CSV (diagnostic_propagation_dump.csv in the working directory),
// entirely outside ThreesimJsonSerializer's schema and the normal --output-dir.
//
// Round-based sampling (not a fixed total block count): a prior fixed-N-blocks-total design
// (MAX_BLOCKS_TO_DUMP=1, first distinct block hash seen anywhere) never actually captured a
// withheld-then-revealed block across three real verification runs (v1/v2/v3), since which
// block happens to be "first seen" process-wide is down to chance and most blocks in any
// network are honest-mined/immediately-broadcast. Capturing EVERY block across a handful of
// early rounds instead guarantees at least one attacker-mined block is seen whenever the
// attacker mines at all within that window (a near-certainty within 10 rounds at any
// reasonable attacker hash power), whether withheld or immediately revealed -- both outcomes
// are useful to see, not just the withheld case.
//
// Process-wide (static) rather than per-round/per-monitor, deliberately: a fresh
// ThreesimSimulationMonitor is constructed per round, so per-instance state can't tell "has
// MAX_ROUNDS_TO_DUMP rounds' worth already been written by OTHER monitor instances" -- the
// shared static state is what lets every round's monitor agree on the same window and append
// to the same file. monteCarloParallelism runs rounds on parallel worker threads, so all
// access here is synchronized; this is diagnostic-only, never on the production hot path
// (ENABLED is false for every currently-running/scheduled batch), so the synchronization cost
// is irrelevant.
final class DiagnosticPropagationDumpWriter {

    static final boolean ENABLED = Boolean.getBoolean("threesim.diagnosticPropagationDump");

    // All blocks in replicationId 0..MAX_ROUNDS_TO_DUMP-1, no per-round or per-block cap within
    // that window -- see the class doc above for why a round-based window replaced the earlier
    // fixed-total-block-count design.
    private static final int MAX_ROUNDS_TO_DUMP = 10;
    private static final String OUTPUT_PATH = "diagnostic_propagation_dump.csv";
    private static final String HEADER =
            "blockHash,minedTimestamp,broadcastTimestamp,withholdingTime,nodeId,receptionTimestamp,"
            + "validationCompleteTimestamp,propagationDelay";

    private static final Object LOCK = new Object();
    // (blockHash + "\0" + nodeId) -> receptionTimestamp, held from BlockValidationStartedTraceEvent
    // until the matching BlockValidationFinishedTraceEvent arrives for the same (block, node) and
    // completes the row. Events for one node are strictly in reception-then-completion order
    // (BlockValidatorImpl raises BlockValidationStartedEvent before ever scheduling the
    // corresponding BlockValidationFinishedEvent -- see that class), so "started always precedes
    // finished for the same (block, node)" is guaranteed, not assumed.
    private static final Map<String, Long> PENDING_RECEPTION = new LinkedHashMap<>();
    // blockHash -> true network-broadcast timestamp (BlockBroadcastTraceEvent), recorded once,
    // globally, per tracked block -- NOT per node (unlike receptionTimestamp/
    // validationCompleteTimestamp, which are per-node). Populated by recordBroadcast(), called
    // from ThreesimSimulationMonitor's (non-diagnostic-only) BlockBroadcastTraceEvent handler.
    // Only ever populated for a block that went through one of the four attack behavior classes'
    // publishOneHiddenBlock() -- absent (and the two derived columns left blank) for every other
    // block, exactly as ThreesimSimulationMonitor.recordBlockPropagation's fallback treats it.
    // Always arrives before the first recordValidationComplete() call for the same block, by the
    // same causal-ordering argument as PENDING_RECEPTION above: broadcast necessarily precedes
    // any node's reception of that same block.
    private static final Map<String, Long> BROADCAST_TIMESTAMPS = new LinkedHashMap<>();
    private static boolean headerWritten = false;

    private DiagnosticPropagationDumpWriter() {
    }

    // Call before recording anything for a block. Returns true iff this round's replicationId
    // falls in the first MAX_ROUNDS_TO_DUMP rounds (0..MAX_ROUNDS_TO_DUMP-1) -- the caller
    // should skip recording entirely when false, so nothing beyond the intended small window
    // ever gets written. No per-block/per-round cap within that window: every block in an
    // in-window round is recorded, deliberately (see class doc above for why a fixed total
    // block count across the whole run was replaced with this).
    static boolean shouldTrack(int replicationId) {
        return ENABLED && replicationId < MAX_ROUNDS_TO_DUMP;
    }

    // Call from the BlockValidationStartedTraceEvent handler (caller must already have checked
    // shouldTrack(replicationId)). Buffers the reception timestamp until recordValidationComplete
    // is called for the same (block, node).
    static void recordReception(String blockHash, String nodeId, long receptionTimestamp) {
        synchronized (LOCK) {
            PENDING_RECEPTION.put(key(blockHash, nodeId), receptionTimestamp);
        }
    }

    // Call from ThreesimSimulationMonitor's BlockBroadcastTraceEvent handler (caller must
    // already have checked shouldTrack(replicationId)). Once per tracked block, not per node.
    static void recordBroadcast(String blockHash, long broadcastTimestamp) {
        synchronized (LOCK) {
            BROADCAST_TIMESTAMPS.putIfAbsent(blockHash, broadcastTimestamp);
        }
    }

    // Call from the BlockValidationFinishedTraceEvent handler (caller must already have checked
    // shouldTrack(replicationId)). Writes one complete row combining the buffered reception
    // timestamp with this validation-complete timestamp and minedTimestamp. If no matching
    // reception was buffered (should not happen given the ordering guarantee documented on
    // PENDING_RECEPTION above), the row is skipped rather than written with a fabricated value.
    static void recordValidationComplete(String blockHash, long minedTimestamp, String nodeId,
            long validationCompleteTimestamp) {
        synchronized (LOCK) {
            Long receptionTimestamp = PENDING_RECEPTION.remove(key(blockHash, nodeId));
            if (receptionTimestamp == null) {
                return;
            }
            Long broadcastTimestamp = BROADCAST_TIMESTAMPS.get(blockHash);
            writeRow(blockHash, minedTimestamp, broadcastTimestamp, nodeId, receptionTimestamp,
                    validationCompleteTimestamp);
        }
    }

    private static String key(String blockHash, String nodeId) {
        return blockHash + "\0" + nodeId;
    }

    // propagationDelay stays receptionTimestamp - minedTimestamp, unchanged from before this
    // task -- this diagnostic dump's own already-verified definition, not touched here.
    // broadcastTimestamp/withholdingTime are new, separate columns (blank when this block never
    // went through publishOneHiddenBlock -- see BROADCAST_TIMESTAMPS's doc above), letting the
    // reader see the mined-to-broadcast (withholding) and reception-to-mined (this column's
    // existing propagationDelay) components side by side rather than only their sum.
    private static void writeRow(String blockHash, long minedTimestamp, Long broadcastTimestamp, String nodeId,
            long receptionTimestamp, long validationCompleteTimestamp) {
        try (FileWriter fw = new FileWriter(OUTPUT_PATH, true); PrintWriter pw = new PrintWriter(fw)) {
            if (!headerWritten) {
                pw.println(HEADER);
                headerWritten = true;
            }
            long propagationDelay = receptionTimestamp - minedTimestamp;
            String broadcastCol = broadcastTimestamp != null ? String.valueOf(broadcastTimestamp) : "";
            String withholdingCol = broadcastTimestamp != null ? String.valueOf(broadcastTimestamp - minedTimestamp) : "";
            pw.println(blockHash + "," + minedTimestamp + "," + broadcastCol + "," + withholdingCol + ","
                    + nodeId + "," + receptionTimestamp + "," + validationCompleteTimestamp + "," + propagationDelay);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write diagnostic propagation dump to " + OUTPUT_PATH, e);
        }
    }
}
