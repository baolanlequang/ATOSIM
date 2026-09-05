#!/bin/bash
#SBATCH --job-name=atosim-stubborn-trail-sanity-10k
#SBATCH --output=logs/atosim-stubborn-trail-sanity-10k-%A_%a.out
#SBATCH --error=logs/atosim-stubborn-trail-sanity-10k-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-19
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=12:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# Adapted from ../../run_stubborn_trail.sh (via the 1000-row
# run_trail_stubborn_sanity.sh) for the 30,000-row (10,000/strategy)
# sanity-check sample against the regenerated node_degree in [2,8] design --
# one scale step up from the 3,000-row (1,000/strategy) sanity check, before
# committing to the full 250k-row production run. Row selection:
# sanity_check_run/00_selected_rows/selected_rows_10000.csv.
#
# --- Adaptation vs run_stubborn_trail.sh -----------------------------------
# The original script indexes a CONTIGUOUS row range
# (ROW_START..ROW_START+ROWS_PER_TASK-1) computed from
# SLURM_ARRAY_TASK_ID * ROWS_PER_TASK, since it sweeps the full ordered CSV.
# Our 10,000 selected rows are a stratified subset scattered across the full
# 250,000-row CSV (not contiguous), so that arithmetic doesn't apply here.
# Instead, this script reads the explicit list of row_index values for this
# strategy out of SELECTED_ROWS_CSV, sorts it, and slices that FIXED LIST by
# SLURM_ARRAY_TASK_ID -- same "array task -> chunk of ROWS_PER_TASK
# row-index values -> inner loop of java invocations" structure as the
# original, just indexing into an explicit list instead of a numeric range.
# Identical mechanism to the 1000-row sanity script; only the input CSV,
# --array range, and output subpath differ (see below).
ROWS_PER_TASK=${ROWS_PER_TASK:-500}
SELECTED_ROWS_CSV="${SELECTED_ROWS_CSV:-sanity_check_run/00_selected_rows/selected_rows_10000.csv}"

mapfile -t ROW_INDICES < <(
    awk -F',' -v strat="trail_stubborn" '
        NR==1 { for (i=1;i<=NF;i++) { if ($i=="strategy") sc=i; if ($i=="row_index") ric=i }; next }
        $sc==strat { print $ric }
    ' "${SELECTED_ROWS_CSV}" | sort -n
)
TOTAL_SELECTED=${#ROW_INDICES[@]}

SLICE_START=$(( SLURM_ARRAY_TASK_ID * ROWS_PER_TASK ))
if (( SLICE_START >= TOTAL_SELECTED )); then
    echo "Array task ${SLURM_ARRAY_TASK_ID}: no selected rows at offset ${SLICE_START} (only ${TOTAL_SELECTED} selected)."
    exit 0
fi
SLICE_END=$(( SLICE_START + ROWS_PER_TASK - 1 ))
if (( SLICE_END >= TOTAL_SELECTED )); then
    SLICE_END=$(( TOTAL_SELECTED - 1 ))
fi

# --cpus-per-task/--mem/--partition unchanged from run_stubborn_trail.sh and
# the 1000-row sanity script: a single row's simulation still uses up to
# monteCarloParallelism=8 cores regardless of how many total rows are being
# swept or how many array tasks there are -- per-task resource need is a
# function of ROWS_PER_TASK only, which hasn't changed, so this needs no
# re-evaluation at 10x the row count.
#
# --time unchanged at 12:00:00 (NOT re-derived, confirmed still valid):
# ROWS_PER_TASK is still 500 -- identical per-task workload to the 1000-row
# sanity script (500 rows * mean ~19s/row =~2.6h per task, comfortably
# inside 12h) -- only the NUMBER of tasks changed (2 -> 20), not the size of
# each one, so the existing time budget reasoning carries over unchanged.
#
# --array 0-1 -> 0-19: 10,000 selected rows / ROWS_PER_TASK=500 = 20 tasks
# exactly (no remainder). 20 is far below even this project's own
# conservative documented MaxArraySize assumption (submit_chunked_array.sh's
# CHUNK_SIZE default of 200), so no chunked submission is needed here --
# confirm against the cluster's actual `scontrol show config | grep
# MaxArraySize` before submitting if in doubt, but 20 tasks should be safe
# under essentially any real cluster limit.

# Separate results location: sanity_check_run_10k/ -- a NEW, DISTINCT
# top-level subpath inside the same atosim_results workspace, sibling to
# (not nested inside) the 1000-row sanity_check_run/ subpath, and separate
# from the live per-strategy dirs (${WORKSPACE_PATH}/trail_stubborn etc.)
# that the real 250k run writes to. Kept distinct from the 1000-row sanity
# output so the two sanity scales don't get mixed when analyzing results
# later.
RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi
RESULTS_DIR="${WORKSPACE_PATH}/sanity_check_run_10k/trail_stubborn"
mkdir -p "${RESULTS_DIR}" logs

# Write to local SSD during job to avoid hammering the workspace filesystem
# with small I/O ops, then bulk-copy to the workspace once at the end of the task.
TMP_OUT="${TMPDIR}/trail_stubborn_sanity_10k_${SLURM_JOB_ID}_${SLURM_ARRAY_TASK_ID}"
mkdir -p "${TMP_OUT}"

for (( i=SLICE_START; i<=SLICE_END; i++ )); do
    ROW_INDEX=${ROW_INDICES[i]}
    java -Xmx48G \
         -XX:+UseG1GC \
         -XX:ParallelGCThreads=8 \
         -XX:+HeapDumpOnOutOfMemoryError \
         -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_INDEX}.hprof" \
         -jar atosim-stubborn-trail.jar \
         sampling/run_configurations_trail_stubborn.csv \
         sampling/generated_models \
         sampling/configuration.json \
         --row-index ${ROW_INDEX} \
         --output-dir "${TMP_OUT}"
done

cp "${TMP_OUT}"/result_run_*.json "${RESULTS_DIR}/" 2>/dev/null || true
