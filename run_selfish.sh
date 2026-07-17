#!/bin/bash
#SBATCH --job-name=atosim-selfish
#SBATCH --output=logs/atosim-selfish-%A_%a.out
#SBATCH --error=logs/atosim-selfish-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-499
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=12:00:00
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
# run_selfish.sh` runs, covering the full 250,000-row CSV at ROWS_PER_TASK=500.
# To use a different ROWS_PER_TASK, or to stay under a cluster MaxArraySize
# below 500, submit via:
#   ./submit_chunked_array.sh run_selfish.sh 250000 [chunk_size] [sleep_between] [start_offset] [rows_per_task]
# Rows past the end of the CSV are skipped by ATOSIMSimulator itself (it logs
# an out-of-bounds error for that --row-index and moves on), so a
# ROWS_PER_TASK that doesn't evenly divide the remaining rows in the last
# chunk is safe, just slightly wasteful for the handful of tail iterations.
#
# cpu partition rejects jobs requesting too little memory for highmem, hence
# --partition=cpu here (70 nodes, 380GB/node cap, well above our 64gb ask).
# Per-user fair-share cap is ~1920 cores (20 nodes) -> up to 240 of these
# 8-core tasks can run concurrently; the rest queue.

mkdir -p results_new/selfish logs

for (( ROW_INDEX=ROW_START; ROW_INDEX<=ROW_END; ROW_INDEX++ )); do
    java -Xmx48G \
         -XX:+UseG1GC \
         -XX:ParallelGCThreads=8 \
         -XX:+HeapDumpOnOutOfMemoryError \
         -XX:HeapDumpPath=heapdump_${SLURM_JOB_ID}_row${ROW_INDEX}.hprof \
         -jar atosim.jar \
         sampling/run_configurations_selfish.csv \
         sampling/generated_models \
         sampling/configuration.json \
         --row-index ${ROW_INDEX} \
         --output-dir results_new/selfish
done
