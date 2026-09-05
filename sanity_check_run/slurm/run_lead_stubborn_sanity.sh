#!/bin/bash
#SBATCH --job-name=atosim-stubborn-lead-sanity
#SBATCH --output=logs/atosim-stubborn-lead-sanity-%A_%a.out
#SBATCH --error=logs/atosim-stubborn-lead-sanity-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-1
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=12:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# Adapted from ../../run_stubborn_lead.sh for the 3000-row (1000/strategy)
# sanity-check sample against the regenerated node_degree in [2,8] design,
# before committing to the full 250k-row production run. Row selection:
# sanity_check_run/00_selected_rows/selected_rows_1000.csv.
#
# --- Adaptation vs run_stubborn_lead.sh ------------------------------------
# The original script indexes a CONTIGUOUS row range
# (ROW_START..ROW_START+ROWS_PER_TASK-1) computed from
# SLURM_ARRAY_TASK_ID * ROWS_PER_TASK, since it sweeps the full ordered CSV.
# Our 1000 selected rows are a stratified subset scattered across the full
# 250,000-row CSV (not contiguous), so that arithmetic doesn't apply here.
# Instead, this script reads the explicit list of row_index values for this
# strategy out of SELECTED_ROWS_CSV, sorts it, and slices that FIXED LIST by
# SLURM_ARRAY_TASK_ID -- same "array task -> chunk of ROWS_PER_TASK
# row-index values -> inner loop of java invocations" structure as the
# original, just indexing into an explicit list instead of a numeric range.
ROWS_PER_TASK=${ROWS_PER_TASK:-500}
SELECTED_ROWS_CSV="${SELECTED_ROWS_CSV:-sanity_check_run/00_selected_rows/selected_rows_1000.csv}"

mapfile -t ROW_INDICES < <(
    awk -F',' -v strat="lead_stubborn" '
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

# --cpus-per-task/--mem/--partition unchanged from run_stubborn_lead.sh: a
# single row's simulation still uses up to monteCarloParallelism=8 cores
# regardless of how many total rows are being swept, so per-task resource
# needs don't scale down with the smaller sanity sample -- only wall time
# and array size do (see below).
#
# --time reduced 72:00:00 -> 12:00:00: run_stubborn_lead.sh's own comment
# already established ROWS_PER_TASK=500 is "comfortably inside
# --time=12:00:00" per task (500 rows * mean ~19s/row =~2.6h; the 72h figure
# there is a large safety margin sized for the full 250k-row campaign's
# worst-case tail across hundreds of concurrent tasks). Reusing the same
# ROWS_PER_TASK means reusing that same validated per-task time budget
# directly, just with far fewer tasks.
#
# --array reduced 0-499 -> 0-1: 1000 selected rows / ROWS_PER_TASK=500 = 2
# tasks exactly (no remainder), instead of the 500 tasks needed to cover the
# full 250,000-row CSV.

# Separate results location: a sanity_check_run/ subpath INSIDE the same
# atosim_results workspace, not the live per-strategy dirs
# (${WORKSPACE_PATH}/lead_stubborn etc.) that the real 250k run writes to.
# Chosen over a distinctly-named scratch workspace to avoid requiring a
# second `ws_allocate` call for a 3000-row test -- the existing
# 40TiB/20M-inode workspace has ample room, and this subpath keeps sanity
# output fully out of the real per-strategy directories the analysis
# pipeline reads from.
RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi
RESULTS_DIR="${WORKSPACE_PATH}/sanity_check_run/lead_stubborn"
mkdir -p "${RESULTS_DIR}" logs

# Write to local SSD during job to avoid hammering the workspace filesystem
# with small I/O ops, then bulk-copy to the workspace once at the end of the task.
TMP_OUT="${TMPDIR}/lead_stubborn_sanity_${SLURM_JOB_ID}_${SLURM_ARRAY_TASK_ID}"
mkdir -p "${TMP_OUT}"

for (( i=SLICE_START; i<=SLICE_END; i++ )); do
    ROW_INDEX=${ROW_INDICES[i]}
    java -Xmx48G \
         -XX:+UseG1GC \
         -XX:ParallelGCThreads=8 \
         -XX:+HeapDumpOnOutOfMemoryError \
         -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_INDEX}.hprof" \
         -jar atosim-stubborn-lead.jar \
         sampling/run_configurations_lead_stubborn.csv \
         sampling/generated_models \
         sampling/configuration.json \
         --row-index ${ROW_INDEX} \
         --output-dir "${TMP_OUT}"
done

cp "${TMP_OUT}"/result_run_*.json "${RESULTS_DIR}/" 2>/dev/null || true
