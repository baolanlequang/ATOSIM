#!/bin/bash
#SBATCH --job-name=atosim-custom-fast-slow-verify-propthresh
#SBATCH --output=logs/atosim-custom-fast-slow-verify-propthresh-%A_%a.out
#SBATCH --error=logs/atosim-custom-fast-slow-verify-propthresh-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-3
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# Re-run of the same 4 custom Fast/Slow rows as run_custom_fast_slow.sh /
# run_verify_instrumentation.sh (Fast x {selfish, lead_stubborn},
# Slow x {selfish, lead_stubborn}) -- NOT a new config, NOT a fix to
# anything in either of those scripts. This exists solely to verify,
# against a real jar run, the percentage-based propagation-time coverage
# thresholds added since run_verify_instrumentation.sh's run (90%/95%/100%
# node coverage, replacing "100%-only" as the sole propagation-time
# completion criterion -- see ThreesimSimulationMonitor.recordBlockPropagation
# and the topology/forwarding/timing investigation this follows up on).
#
# Fast already showed real (non-sentinel) propagation-time values before
# this fix and doesn't strictly need re-running for this specific purpose,
# but is included anyway for a clean four-row comparison, reusing the same
# array structure as the prior verification job rather than special-casing
# it down to 2 rows.
#
# Once results land, check each result_run_<N>.json's per-round output
# metrics array for three NEW entries (in addition to the pre-existing
# "Propagation Time (Mean)"/"(Median)"/"(P95)", which are unchanged and NOT
# what this run is testing):
#   - "Propagation Time (Mean, to 90% Coverage)"
#   - "Propagation Time (Mean, to 95% Coverage)"
#   - "Propagation Time (Mean, to 100% Coverage)"
# Expected result:
#   - Fast (array tasks 0, 1): all three should show real values close to
#     each other and close to the pre-existing "Propagation Time (Mean)"
#     (Fast already reached 100% coverage routinely, so 90%/95%/100% should
#     mostly coincide or be very close).
#   - Slow (array tasks 2, 3): "to 90% Coverage" and "to 95% Coverage"
#     should show REAL (non -1.0) values for at least SOME of the 200
#     rounds per strategy -- this is the actual fix being verified. "to
#     100% Coverage" should continue showing -1.0 throughout (0/200 valid
#     rounds), matching the already-confirmed structural finding that full
#     100%-of-1000-nodes coverage is unreachable within Slow's round time
#     budget on its degree-2 ring topology (diameter 500 hops), independent
#     of this fix -- that is expected and correct, not a regression.
#
# Everything else below (SLURM directives, jar choice, per-row cost
# rationale, TMPDIR staging) is copied verbatim from
# run_verify_instrumentation.sh / run_custom_fast_slow.sh -- see those
# scripts' own comments and the sibling README.md for the full "why 1
# array task per row / why 72h / why atosim.jar for all four rows"
# reasoning; not repeated here.

ROW_CONFIG_CSV="sanity_check_run/custom_fast_slow/run_configurations.csv"
MODELS_DIR="sanity_check_run/custom_fast_slow/generated_models"
BASE_CONFIG="sampling/configuration.json"

# array task id -> (row-index, output subfolder), matching the 4 CSV rows
# (config_id 1-4, 0-indexed row 0-3) -- identical mapping to
# run_custom_fast_slow.sh / run_verify_instrumentation.sh.
case "${SLURM_ARRAY_TASK_ID}" in
    0) ROW_INDEX=0; OUT_SUBDIR="fast_selfish" ;;
    1) ROW_INDEX=1; OUT_SUBDIR="fast_lead_stubborn" ;;
    2) ROW_INDEX=2; OUT_SUBDIR="slow_selfish" ;;
    3) ROW_INDEX=3; OUT_SUBDIR="slow_lead_stubborn" ;;
    *)
        echo "ERROR: unexpected SLURM_ARRAY_TASK_ID=${SLURM_ARRAY_TASK_ID} (expected 0-3)" >&2
        exit 1
        ;;
esac

# NEW, DISTINCT output subpath -- custom_fast_slow_verify_propthresh/, a
# sibling of (not nested inside) sanity_check_run/custom_fast_slow/,
# custom_fast_slow_verify/ (the prior instrumentation-verification run),
# and sanity_check_run_10k/, so this run's results never get mixed up
# with or overwrite any of those when reading results later.
RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi
RESULTS_DIR="${WORKSPACE_PATH}/custom_fast_slow_verify_propthresh/${OUT_SUBDIR}"
mkdir -p "${RESULTS_DIR}" logs

# Write to local SSD during the job, then bulk-copy to the workspace at the end.
TMP_OUT="${TMPDIR}/custom_fast_slow_verify_propthresh_${OUT_SUBDIR}_${SLURM_JOB_ID}"
mkdir -p "${TMP_OUT}"

java -Xmx48G \
     -XX:+UseG1GC \
     -XX:ParallelGCThreads=8 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_INDEX}.hprof" \
     -jar atosim.jar \
     "${ROW_CONFIG_CSV}" \
     "${MODELS_DIR}" \
     "${BASE_CONFIG}" \
     --row-index "${ROW_INDEX}" \
     --output-dir "${TMP_OUT}"

cp "${TMP_OUT}"/result_run_*.json "${RESULTS_DIR}/" 2>/dev/null || true
