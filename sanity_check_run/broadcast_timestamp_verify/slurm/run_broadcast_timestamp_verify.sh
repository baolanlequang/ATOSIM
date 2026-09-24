#!/bin/bash
#SBATCH --job-name=atosim-broadcast-timestamp-verify
#SBATCH --output=logs/atosim-broadcast-timestamp-verify-%A_%a.out
#SBATCH --error=logs/atosim-broadcast-timestamp-verify-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-2
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# ---------------------------------------------------------------------------
# Purpose: verify the new BlockBroadcastTraceEvent instrumentation (raised at
# the four attack behavior classes' publishOneHiddenBlock() -- Selfish,
# Lead-stubborn, Trail-stubborn, EqualFork-stubborn) against a real jar, via
# this project's established v1/v2/v3 protocol (see item 1a's verification
# history): v1 and v2 are two SEPARATE invocations of the SAME row (config_id
# 1 and 2 in run_configurations.csv -- identical parameters: fast/selfish,
# validator_count=20, node_degree=8, bandwidth=125.0), v3 is a different
# config (config_id 3: fast/lead_stubborn, same sys-fast topology, different
# attack strategy -- picked because it's an already-validated row with
# already-generated models under sanity_check_run/custom_fast_slow/
# generated_models/, so nothing new needs generating).
#
# This is the FIRST verify-instrumentation-style job in this project's
# history to actually enable the diagnostic dump
# (-Dthreesim.diagnosticPropagationDump=true) -- run_verify_instrumentation.sh
# never did. That matters for one thing this script handles that its
# template didn't need to: DiagnosticPropagationDumpWriter's output path
# (diagnostic_propagation_dump.csv) is hardcoded RELATIVE, not configurable,
# and is process-wide static state -- with 3 array tasks potentially running
# concurrently from the same submission directory, all three JVMs would
# otherwise race to append to the SAME file. Fixed below by giving each task
# its own working directory (cd into its own TMP_OUT, unique per
# SLURM_ARRAY_TASK_ID) before invoking java, and resolving every other path
# to absolute first (so they still work after the cd) -- see REPO_ROOT below.
# Not a Java source change, a job-script-level workaround for concurrent
# array tasks sharing one hardcoded relative output path.
#
# What to check once results land:
#   1. topologyDeterminismInfo: v1 (array task 0) vs v2 (array task 1) should
#      match exactly -- this instrumentation is purely additive/observational
#      (see ThreesimSimulationMonitor.onTraceEventOccurred's early-return
#      guard for the diagnostic branch, and the always-on
#      BlockBroadcastTraceEvent branch, which only reads state, never
#      perturbs event ordering/RNG draws) so it must not touch the
#      already-established topology-level determinism guarantee from item 1a.
#      v1/v2 should also ideally show the same QUALITATIVE pattern for
#      withholdingTime/propagation-time distributions (not necessarily
#      identical round-by-round, given the already-documented general
#      nondeterminism gap -- see the atosim-simulator-fixes skill -- but the
#      same overall shape).
#   2. v3 (array task 2, different config/strategy) should differ from v1/v2
#      on topologyDeterminismInfo and round outcomes -- confirms nothing
#      about this instrumentation accidentally collapsed distinct configs
#      to the same result.
#   3. In each task's diagnostic_propagation_dump.csv (see per-task output
#      path below): every row's withholdingTime column, when non-blank,
#      must be >= 0 -- 0 for a block whose broadcastTimestamp happens to
#      equal its minedTimestamp (immediately revealed, e.g. mined while
#      already in a tie/contest -- see SelfishMiningNodeBehavior.onBlockMined),
#      strictly > 0 for a block that was genuinely held privately before
#      being revealed later. A negative value would mean broadcastTimestamp
#      predates minedTimestamp -- structurally impossible per
#      BlockBroadcastTraceEvent's own doc (broadcast can only happen at or
#      after a block's own creation), so it would indicate a real bug if
#      ever observed. Blank withholdingTime is expected and NOT a bug for
#      any block that never went through publishOneHiddenBlock (see
#      DiagnosticPropagationDumpWriter's own doc on this fallback).
#   4. In each task's result_run_<config_id>.json: "Propagation Time (Mean)",
#      "Propagation Time (Median)", "Propagation Time (P95)", and the three
#      "...Coverage" variants should now sit at normal network-transit scale
#      (tens to low hundreds of ms, matching this Fast config's own already-
#      measured single/second-hop delays of 16-38ms) rather than the old
#      multi-second/multi-minute outliers previously traced to selfish-mining
#      block-withholding contaminating the metric's mined-timestamp base.
#      Task 2 (fast_lead_stubborn) is the more informative row for this
#      check specifically, since Lead-stubborn also withholds blocks.
# ---------------------------------------------------------------------------

REPO_ROOT="$(pwd)"
ROW_CONFIG_CSV="${REPO_ROOT}/sanity_check_run/broadcast_timestamp_verify/run_configurations.csv"
# Reuses the EXISTING custom_fast_slow models (sys-fast + atk-fast for both
# selfish and lead_stubborn) -- already validated, nothing new generated.
MODELS_DIR="${REPO_ROOT}/sanity_check_run/custom_fast_slow/generated_models"
BASE_CONFIG="${REPO_ROOT}/sampling/configuration.json"

# array task id -> (row-index, output subfolder). Rows 0 and 1 are the
# identical fast/selfish config (config_id 1, 2) run as two separate
# invocations (v1, v2); row 2 is the different fast/lead_stubborn config
# (config_id 3, v3).
case "${SLURM_ARRAY_TASK_ID}" in
    0) ROW_INDEX=0; OUT_SUBDIR="v1" ;;
    1) ROW_INDEX=1; OUT_SUBDIR="v2" ;;
    2) ROW_INDEX=2; OUT_SUBDIR="v3" ;;
    *)
        echo "ERROR: unexpected SLURM_ARRAY_TASK_ID=${SLURM_ARRAY_TASK_ID} (expected 0-2)" >&2
        exit 1
        ;;
esac

# NEW, DISTINCT output subpath -- broadcast_timestamp_verify/, a sibling of
# every other verification output (custom_fast_slow_verify/,
# custom_fast_slow_verify_propthresh/, hmax_calibration/, etc.), so this
# run's results never get mixed up with any earlier one.
RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi
RESULTS_DIR="${WORKSPACE_PATH}/broadcast_timestamp_verify/${OUT_SUBDIR}"
mkdir -p "${RESULTS_DIR}" "${REPO_ROOT}/logs"

# Write to local SSD during the job, then bulk-copy to the workspace at the
# end -- same pattern as run_verify_instrumentation.sh. Unique per array
# task via SLURM_JOB_ID (a distinct per-task id, not the shared
# SLURM_ARRAY_JOB_ID -- same convention already relied on elsewhere in this
# project, e.g. run_convergence_test_chunked.sh).
TMP_OUT="${TMPDIR}/broadcast_timestamp_verify_${OUT_SUBDIR}_${SLURM_JOB_ID}"
mkdir -p "${TMP_OUT}"

# cd into this task's own TMP_OUT before running java: DiagnosticPropagationDumpWriter
# writes diagnostic_propagation_dump.csv to a hardcoded RELATIVE path (see
# top-of-file comment) -- this keeps each of the 3 concurrent array tasks'
# diagnostic output in its own directory instead of racing to append to one
# shared file. Every other path above was already resolved to absolute
# (via REPO_ROOT) specifically so this cd doesn't break them.
cd "${TMP_OUT}"

java -Dthreesim.diagnosticPropagationDump=true \
     -Xmx48G \
     -XX:+UseG1GC \
     -XX:ParallelGCThreads=8 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_INDEX}.hprof" \
     -jar "${REPO_ROOT}/atosim.jar" \
     "${ROW_CONFIG_CSV}" \
     "${MODELS_DIR}" \
     "${BASE_CONFIG}" \
     --row-index "${ROW_INDEX}" \
     --output-dir "${TMP_OUT}"

cp "${TMP_OUT}"/result_run_*.json "${RESULTS_DIR}/" 2>/dev/null || true
cp "${TMP_OUT}/diagnostic_propagation_dump.csv" "${RESULTS_DIR}/" 2>/dev/null || true
