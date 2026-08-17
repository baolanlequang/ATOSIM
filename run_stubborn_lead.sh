#!/bin/bash
#SBATCH --job-name=atosim-stubborn-lead
#SBATCH --output=logs/atosim-stubborn-lead-%A_%a.out
#SBATCH --error=logs/atosim-stubborn-lead-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-499
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# Each array task now runs ROWS_PER_TASK consecutive CSV rows sequentially
# (one `java -jar` call per row, in a loop), instead of one row per task.
# The cluster admins flagged the previous one-row-per-task design: measured
# from completed runs (results_new/trail_stubborn, n=10000 sample),
# per-row simulationTime is median ~16s / mean ~19s / p99 ~97s / max ~204s,
# so single-row array tasks were finishing in well under a minute - a highly
# inefficient number of tiny scheduled units on bwUniCluster 3.0.
#
# Batching ROWS_PER_TASK=500 rows/task -> 250000/500=500 array tasks total
# for the full CSV, each running ~2.6h on average (500 * ~19s), comfortably
# inside --time=12:00:00 with ample headroom for slower rows.
#
# monteCarloParallelism is 8 in the base config, so a single row's simulation
# never uses more than 8 cores anyway; batching rows sequentially within one
# task does not change that, it just amortizes Slurm scheduling overhead
# (queueing/accounting/node allocation) across many rows instead of paying it
# once per row.
ROWS_PER_TASK=${ROWS_PER_TASK:-500}
ROW_START=$(( ${ROW_OFFSET:-0} + SLURM_ARRAY_TASK_ID * ROWS_PER_TASK ))
ROW_END=$(( ROW_START + ROWS_PER_TASK - 1 ))

# The #SBATCH --array=0-499 above is the default for direct `sbatch
# run_stubborn_lead.sh` runs, covering the full 250,000-row CSV at
# ROWS_PER_TASK=500. To use a different ROWS_PER_TASK, or to stay under a
# cluster MaxArraySize below 500, submit via:
#   ./submit_chunked_array.sh run_stubborn_lead.sh 250000 [chunk_size] [sleep_between] [start_offset] [rows_per_task]
# Rows past the end of the CSV are skipped by ATOSIMSimulator itself (it logs
# an out-of-bounds error for that --row-index and moves on), so a
# ROWS_PER_TASK that doesn't evenly divide the remaining rows in the last
# chunk is safe, just slightly wasteful for the handful of tail iterations.
#
# cpu partition rejects jobs requesting too little memory for highmem, hence
# --partition=cpu here (70 nodes, 380GB/node cap, well above our 64gb ask).
# Per-user fair-share cap is ~1920 cores (20 nodes) -> up to 240 of these
# 8-core tasks can run concurrently; the rest queue.

# Final output (result_run_*.json across up to 250,000 rows) must NOT land
# under $HOME - it's on Lustre with a small quota, backed up to tape, and
# meant for source/config files, not bulk data (this previously hit "Disk
# quota exceeded" writing results_new/lead_stubborn there). bwUniCluster 3.0
# workspaces give 40 TiB / 20M inodes per user instead - allocate one before
# submitting this job:
#   ws_allocate atosim_results 60
RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi
RESULTS_DIR="${WORKSPACE_PATH}/lead_stubborn"
mkdir -p "${RESULTS_DIR}" logs

# Write to local SSD during job to avoid hammering the workspace filesystem
# with small I/O ops, then bulk-copy to the workspace once at the end of the task.
TMP_OUT="${TMPDIR}/lead_stubborn_${SLURM_JOB_ID}_${SLURM_ARRAY_TASK_ID}"
mkdir -p "${TMP_OUT}"

for (( ROW_INDEX=ROW_START; ROW_INDEX<=ROW_END; ROW_INDEX++ )); do
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
