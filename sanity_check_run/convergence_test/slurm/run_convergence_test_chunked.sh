#!/bin/bash
#SBATCH --job-name=atosim-convergence-test
#SBATCH --output=logs/atosim-convergence-test-%A_%a.out
#SBATCH --error=logs/atosim-convergence-test-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-104
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# ---------------------------------------------------------------------------
# CHUNK-COMPATIBLE COPY of run_convergence_test.sh, for use with
# submit_chunked_array.sh -- the original is untouched; see this project's
# established pattern (run_selfish.sh etc.) for why a copy, not an in-place
# edit, is used here.
#
# The only change from run_convergence_test.sh: the (config, tier) index is
# now derived from GLOBAL_TASK_ID = ${ROW_OFFSET:-0} + SLURM_ARRAY_TASK_ID
# instead of from SLURM_ARRAY_TASK_ID directly. submit_chunked_array.sh
# always resets each chunk's array task IDs back to 0-<chunk_size-1> (SLURM
# caps the array task-id VALUE, not just the count) and passes the real
# offset separately via the ROW_OFFSET environment variable it exports before
# each `sbatch --array=...` call -- see that script's own header comment.
# Its model is built around ROWS_PER_TASK *consecutive* CSV rows per array
# task (matching run_selfish.sh's 250k-row sweep design); this job has no
# such concept -- each array task is exactly ONE (config, tier) combination,
# not a range -- so it is invoked with ROWS_PER_TASK=1, and "one row" in the
# helper's vocabulary maps onto "one (config, tier) task" here. With
# ROWS_PER_TASK=1, GLOBAL_TASK_ID = ROW_OFFSET + SLURM_ARRAY_TASK_ID * 1 is
# exactly the helper's own ROW_START formula, just not computed by the
# helper itself (the target script always owns that arithmetic, per its
# documented convention).
#
# The #SBATCH --array=0-104 directive above is a harmless default/fallback,
# identical in spirit to run_selfish.sh's own baked-in --array=0-499: SLURM
# gives a CLI `sbatch --array=...` argument precedence over an embedded
# `#SBATCH --array` directive, and submit_chunked_array.sh always supplies
# --array on the command line, so this line is never actually what governs
# submission through the helper -- kept only so the script is still directly
# submittable stand-alone (e.g. for testing a single chunk's worth manually)
# without needing ROW_OFFSET set.
#
# See run_convergence_test.sh for the full purpose/rationale (colleague's
# conditional_reorg_depth fANOVA-fit convergence test) and --time reasoning
# -- both unchanged here, not repeated.
# ---------------------------------------------------------------------------

SELECTED_CONFIGS_CSV="sanity_check_run/convergence_test/selected_configs.csv"

# GLOBAL_TASK_ID: the true (config, tier) index across the WHOLE 105-task
# job, reconstructed from this chunk's local SLURM_ARRAY_TASK_ID (always
# 0-based per chunk) plus the real offset submit_chunked_array.sh exports.
# Standalone (no chunking helper): ROW_OFFSET is unset, defaults to 0, and
# GLOBAL_TASK_ID == SLURM_ARRAY_TASK_ID exactly as in the unchunked script.
GLOBAL_TASK_ID=$(( ${ROW_OFFSET:-0} + SLURM_ARRAY_TASK_ID ))

CONFIG_IDX=$(( GLOBAL_TASK_ID / 3 ))
TIER_IDX=$(( GLOBAL_TASK_ID % 3 ))

case "${TIER_IDX}" in
    0) N_REPS=500;  TIER_CONFIG="sanity_check_run/convergence_test/configuration_mc500.json" ;;
    1) N_REPS=1000; TIER_CONFIG="sanity_check_run/convergence_test/configuration_mc1000.json" ;;
    2) N_REPS=5000; TIER_CONFIG="sanity_check_run/convergence_test/configuration_mc5000.json" ;;
esac

# +2: skip the header line (1) and convert 0-indexed CONFIG_IDX to a 1-indexed
# data-row line number (2 = first data row).
CSV_LINE=$(( CONFIG_IDX + 2 ))
ROW=$(sed -n "${CSV_LINE}p" "${SELECTED_CONFIGS_CSV}")
if [ -z "${ROW}" ]; then
    echo "ERROR: no row at line ${CSV_LINE} of ${SELECTED_CONFIGS_CSV} (GLOBAL_TASK_ID=${GLOBAL_TASK_ID}, CONFIG_IDX=${CONFIG_IDX}, ROW_OFFSET=${ROW_OFFSET:-0}, SLURM_ARRAY_TASK_ID=${SLURM_ARRAY_TASK_ID})" >&2
    exit 1
fi
ROW_INDEX=$(echo "${ROW}" | awk -F',' '{print $2}')
CONFIG_ID=$(echo "${ROW}" | awk -F',' '{print $3}')

if [ -z "${ROW_INDEX}" ] || [ -z "${CONFIG_ID}" ]; then
    echo "ERROR: failed to parse row_index/config_id from row: ${ROW}" >&2
    exit 1
fi

ROW_CONFIG_CSV="sampling/run_configurations_selfish.csv"
MODELS_DIR="sampling/generated_models"

RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi

RESULTS_DIR="${WORKSPACE_PATH}/convergence_test/mc${N_REPS}/${CONFIG_ID}"
mkdir -p "${RESULTS_DIR}" logs

TMP_OUT="${TMPDIR}/convergence_test_mc${N_REPS}_${CONFIG_ID}_${SLURM_JOB_ID}"
mkdir -p "${TMP_OUT}"

java -Xmx48G \
     -XX:+UseG1GC \
     -XX:ParallelGCThreads=8 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_INDEX}.hprof" \
     -jar atosim.jar \
     "${ROW_CONFIG_CSV}" \
     "${MODELS_DIR}" \
     "${TIER_CONFIG}" \
     --row-index "${ROW_INDEX}" \
     --output-dir "${TMP_OUT}"

cp "${TMP_OUT}"/result_run_*.json "${RESULTS_DIR}/" 2>/dev/null || true
