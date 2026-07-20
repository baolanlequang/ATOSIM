#!/bin/bash
#SBATCH --job-name=atosim-selfish-missing
#SBATCH --output=logs/atosim-selfish-missing-%A_%a.out
#SBATCH --error=logs/atosim-selfish-missing-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-151
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=12:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# Resume run for run_selfish.sh: identical to it except the input CSV is
# sampling/missing_rows_selfish.csv (produced by check_missing_results.py
# --write-missing-csv) instead of the full run_configurations_selfish.csv.
#
# This works unmodified with submit_chunked_array.sh and ATOSIMSimulator
# because --row-index selects a row *position* in whichever CSV is passed,
# while the output filename (result_run_<runId>.json) is always the row's
# config_id column value, not its position (see ATOSIMSimulator.toRunId).
# So row 0 of missing_rows_selfish.csv still produces the correctly-numbered
# result_run_<that row's original config_id>.json - no collision with, and
# no need to touch, the config_ids that already completed successfully.
#
# Default --array=0-151 above covers all rows in missing_rows_selfish.csv at
# ROWS_PER_TASK=500 (ceil(75523/500)=152 tasks) for a direct `sbatch` run.
# Recompute this (or just use submit_chunked_array.sh below) if the missing
# count differs when you actually run check_missing_results.py.
#
# Preferred submission (handles MaxArraySize + transient sbatch retries):
#   ./submit_chunked_array.sh run_selfish_missing.sh <rows in missing_rows_selfish.csv minus header>

mkdir -p results_new/selfish logs

ROWS_PER_TASK=${ROWS_PER_TASK:-500}
ROW_START=$(( ${ROW_OFFSET:-0} + SLURM_ARRAY_TASK_ID * ROWS_PER_TASK ))
ROW_END=$(( ROW_START + ROWS_PER_TASK - 1 ))

TMP_OUT="${TMPDIR}/selfish_missing_${SLURM_JOB_ID}_${SLURM_ARRAY_TASK_ID}"
mkdir -p "${TMP_OUT}"

for (( ROW_INDEX=ROW_START; ROW_INDEX<=ROW_END; ROW_INDEX++ )); do
    java -Xmx48G \
         -XX:+UseG1GC \
         -XX:ParallelGCThreads=8 \
         -XX:+HeapDumpOnOutOfMemoryError \
         -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_INDEX}.hprof" \
         -jar atosim.jar \
         sampling/missing_rows_selfish.csv \
         sampling/generated_models \
         sampling/configuration.json \
         --row-index ${ROW_INDEX} \
         --output-dir "${TMP_OUT}"
done

cp "${TMP_OUT}"/result_run_*.json results_new/selfish/ 2>/dev/null || true
