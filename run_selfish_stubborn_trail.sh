#!/bin/bash
#SBATCH --job-name=atosim-selfish-stubborn-trail
#SBATCH --output=logs/atosim-selfish-stubborn-trail-%A_%a.out
#SBATCH --error=logs/atosim-selfish-stubborn-trail-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-499
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=12:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# One config per array task instead of all 500 sequentially in one JVM:
# monteCarloParallelism is 8 in the base config, so a single config never
# uses more than 8 cores anyway. --row-index selects the SLURM_ARRAY_TASK_ID'th
# row (0-indexed) of the CSV (500 rows -> --array=0-499); each task gets its
# own JVM/heap, so the prior 350gb/310G request (needed only because memory
# wasn't released across 500 sequential runs in one process) no longer applies.
# cpu partition rejects jobs requesting too little memory for highmem, hence
# --partition=cpu here (70 nodes, 380GB/node cap, well above our 64gb ask).
# Per-user fair-share cap is ~1920 cores (20 nodes) -> up to 240 of these
# 8-core tasks can run concurrently; the rest queue.
# Re-tune --time after seeing actual single-config wall times from this batch.

java -Xmx48G \
     -XX:+UseG1GC \
     -XX:ParallelGCThreads=8 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=heapdump_${SLURM_JOB_ID}.hprof \
     -jar atosim-selfish-stubborn-trail.jar \
     sampling/configurations_selfish_stubborn_trail.csv \
     sampling/generated_models \
     simulator/org.palladiosimulator.blockchainsystems.atosim/testmodels/configuration_selfish_stubborn_trail.json \
     --attack-type COMBINED_SELFISH_TRAIL_STUBBORN \
     --row-index ${SLURM_ARRAY_TASK_ID} \
     --output-dir results_new/selfish_trail_stubborn
