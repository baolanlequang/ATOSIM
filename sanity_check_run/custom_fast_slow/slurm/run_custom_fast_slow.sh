#!/bin/bash
#SBATCH --job-name=atosim-custom-fast-slow
#SBATCH --output=logs/atosim-custom-fast-slow-%A_%a.out
#SBATCH --error=logs/atosim-custom-fast-slow-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-3
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# One SLURM array task per row (4 total: Fast x {selfish, lead_stubborn},
# Slow x {selfish, lead_stubborn}) -- NOT batched like run_selfish.sh's
# ROWS_PER_TASK convention, because these 4 rows have wildly different
# costs, not the ~16-97s/row seen across the live sampled design. A local
# run of row-index 2 (Slow/selfish) was killed after running >8h with no
# sign of finishing -- confirmed NOT hung (jstack showed real, changing
# progress through legitimate code paths: TrxMemPoolImpl.removeTransactions
# firing a full trace-event notification per removed transaction, and
# Slow's max_block_size=8MB under deterministicFullBlock mode fills every
# block to capacity across validator_count=1000 separate node-local
# mempools -- likely a very large, if not literally unbounded for this
# task's purposes, transaction-count-driven cost, not an infinite loop).
# One array task per row means each row gets its own node and its own
# --time budget, and a slow row can't block the others from finishing --
# this IS the "speed boost" SLURM provides here: wall-clock parallelism
# across the 4 rows, not a faster per-row computation (the underlying
# per-transaction trace-event cost is CPU-bound single-node work,
# unaffected by which machine it runs on beyond raw per-core speed).
#
# --time=72:00:00 matches this project's own established safety-margin
# convention (run_selfish.sh etc.) -- deliberately generous since Slow's
# actual upper bound is unknown (>8h and climbing when killed locally).
# If a task hits this wall, SLURM kills it and no result JSON is written
# for that row; increase --time and resubmit just that array index
# (--array=2) if that happens.
#
# --cpus-per-task=8 matches monteCarloParallelism=8 in sampling/configuration.json
# -- a single row's simulation never uses more than 8 cores regardless of
# topology size, so this is unchanged from the production sanity scripts.
#
# Jar choice: atosim.jar for ALL FOUR rows (not atosim-stubborn-lead.jar for
# the lead_stubborn rows) -- confirmed in this project's own history that
# atosim.jar dispatches by the CSV's attack_strategy column, not by jar
# filename (verified directly: row-index 0/Fast-selfish and row-index
# 1/Fast-lead_stubborn both produced inputParameters.attack_strategy
# matching their CSV row when run through atosim.jar alone). Only atosim.jar
# was authorized for the local runs that established this; if that
# authorization has lapsed by the time this is submitted, re-confirm before
# running on the cluster.

ROW_CONFIG_CSV="sanity_check_run/custom_fast_slow/run_configurations.csv"
MODELS_DIR="sanity_check_run/custom_fast_slow/generated_models"
BASE_CONFIG="sampling/configuration.json"

# array task id -> (row-index, output subfolder), matching the 4 CSV rows
# written by the generation task (config_id 1-4, 0-indexed row 0-3).
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

# Same workspace convention as sanity_check_run/slurm/run_selfish_sanity.sh
# etc.: a sanity_check_run/ subpath INSIDE the existing atosim_results
# workspace, not the live per-strategy dirs the real 250k run writes to, and
# not requiring a second ws_allocate.
RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi
RESULTS_DIR="${WORKSPACE_PATH}/sanity_check_run/custom_fast_slow/${OUT_SUBDIR}"
mkdir -p "${RESULTS_DIR}" logs

# Write to local SSD during the job, then bulk-copy to the workspace at the end.
TMP_OUT="${TMPDIR}/custom_fast_slow_${OUT_SUBDIR}_${SLURM_JOB_ID}"
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
