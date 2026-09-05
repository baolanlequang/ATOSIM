#!/bin/bash
#SBATCH --job-name=atosim-custom-fast-slow-verify
#SBATCH --output=logs/atosim-custom-fast-slow-verify-%A_%a.out
#SBATCH --error=logs/atosim-custom-fast-slow-verify-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-3
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# Re-run of the same 4 custom Fast/Slow rows as
# run_custom_fast_slow.sh (Fast x {selfish, lead_stubborn},
# Slow x {selfish, lead_stubborn}) -- NOT a new config, NOT a fix to
# anything in that script. This exists solely to verify, against a real
# jar run, the two instrumentation additions made since that prior run:
#
#   1. Lead-stubborn lost-lead transition counter (ThreesimSimulationMonitor
#      consuming AttackPhaseTransitionTraceEvent) -- once results land,
#      check each result_run_<N>.json's output metrics array for an entry
#      named "Lead-Stubborn Lost-Lead Transitions": should be present with
#      value 0 for the two selfish rows (array task 0 = fast_selfish,
#      2 = slow_selfish -- this metric is hard-gated to attackType ==
#      LEAD_STUBBORN_MINING, see ThreesimSimulationMonitor), and present
#      with a plausible NON-ZERO value for the two lead_stubborn rows
#      (array task 1 = fast_lead_stubborn, 3 = slow_lead_stubborn) --
#      a lead-stubborn attack that never once lost its lead across an
#      entire run would be implausible and worth investigating if seen.
#   2. Propagation-time / fork-duration metrics -- check for
#      "Propagation Time (Mean)", "Propagation Time (Median)",
#      "Propagation Time (P95)", "Fork Duration (Mean)", and
#      "Fork Duration (Median)" in ALL FOUR rows' output metrics (these are
#      not strategy-gated). Values are in ms; -1.0 means no block reached
#      full network coverage / no reorg resolved that round respectively
#      (see PropagationTimeMean/ForkDurationMean's Javadoc) -- not
#      necessarily a bug on its own, but worth noticing if EVERY round of
#      a row shows -1.0, since that would mean the metric never had data
#      to report for that entire configuration.
#
# Everything else below (SLURM directives, jar choice, per-row cost
# rationale, TMPDIR staging) is copied verbatim from
# run_custom_fast_slow.sh -- see that script's own comments and its
# sibling README.md for the full "why 1 array task per row / why 72h /
# why atosim.jar for all four rows" reasoning; not repeated here.

ROW_CONFIG_CSV="sanity_check_run/custom_fast_slow/run_configurations.csv"
MODELS_DIR="sanity_check_run/custom_fast_slow/generated_models"
BASE_CONFIG="sampling/configuration.json"

# array task id -> (row-index, output subfolder), matching the 4 CSV rows
# (config_id 1-4, 0-indexed row 0-3) -- identical mapping to
# run_custom_fast_slow.sh.
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

# NEW, DISTINCT output subpath -- custom_fast_slow_verify/, a sibling of
# (not nested inside) both the earlier sanity_check_run/custom_fast_slow/
# output and sanity_check_run_10k/, so this verification run's results
# never get mixed up with either when reading results later.
RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi
RESULTS_DIR="${WORKSPACE_PATH}/custom_fast_slow_verify/${OUT_SUBDIR}"
mkdir -p "${RESULTS_DIR}" logs

# Write to local SSD during the job, then bulk-copy to the workspace at the end.
TMP_OUT="${TMPDIR}/custom_fast_slow_verify_${OUT_SUBDIR}_${SLURM_JOB_ID}"
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
