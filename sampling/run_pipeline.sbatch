#!/bin/bash
#SBATCH --job-name=selfish_pipeline
#SBATCH --partition=dev_cpu
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=16
#SBATCH --mem=8G
#SBATCH --time=00:30:00
#SBATCH --output=logs/slurm_pipeline_%j.out
#SBATCH --error=logs/slurm_pipeline_%j.err

# ---------------------------------------------------------------------------
# Single, longer-running SLURM job for the full selfish-mining severity
# pipeline (extraction + aggregation + system-level aggregation).
#
# Deliberately NOT a job array: parallelism across the ~100,000 JSON
# result files is handled inside this one job via a Python process pool
# (--cpus-per-task worker processes on one node), so there is exactly
# one SLURM job to schedule instead of many short-lived sub-jobs.
#
# Benchmarked cost: ~0.009s pure parse time per file -> ~15 CPU-minutes
# total for 100,000 files single-threaded, correspondingly faster with
# --cpus-per-task workers in parallel. Resource requests below are kept
# modest (16 cores / 8G / 1h) on purpose: smaller requests are easier
# for the scheduler to backfill, which usually means a much shorter
# queue wait than requesting a full node. Increase --cpus-per-task only
# if the queue wait at the current size is not the bottleneck.
#
# Adjust --partition, --cpus-per-task, --mem to match actual BwUniCluster
# 3.0 node specs, and edit the module load / venv activation lines below.
# Check `sinfo` for a short-job/dev partition with faster turnaround, and
# use `squeue --start -j <jobid>` after submitting to see the estimated
# start time.
#
# Using dev_cpu (short-job/dev partition) for fast queue turnaround.
# Dev partitions typically enforce a low max walltime (often <=30-60 min) -
# verify the actual limit with `sinfo -p dev_cpu -o "%P %l"` and adjust
# --time accordingly; sbatch will reject the job immediately if --time
# exceeds the partition's max.
# ---------------------------------------------------------------------------

set -euo pipefail

# --- environment setup (edit to match the cluster's actual module names) ---
# module load devel/python/3.9
# source ~/venvs/atosim/bin/activate

# --- pipeline paths (EDIT these to the real paths before submitting) ---
export JSON_DIR=../results_new/selfish
export CONFIG_CSV=./run_configurations_selfish.csv
export OUTPUT_DIR=../results_new/output/selfish

N_BOOTSTRAP="${N_BOOTSTRAP:-1000}"
EXPECTED_I="${EXPECTED_I:-500}"
EXPECTED_J="${EXPECTED_J:-200}"
R_ROUNDS="${R_ROUNDS:-500}"

mkdir -p logs "${OUTPUT_DIR}"

echo "JSON_DIR   = ${JSON_DIR}"
echo "CONFIG_CSV = ${CONFIG_CSV}"
echo "OUTPUT_DIR = ${OUTPUT_DIR}"
echo "CPUS       = ${SLURM_CPUS_PER_TASK:-unset}"

python3 run_selfish_pipeline.py \
    --json-dir "${JSON_DIR}" \
    --config-csv "${CONFIG_CSV}" \
    --output-dir "${OUTPUT_DIR}" \
    --num-workers "${SLURM_CPUS_PER_TASK}" \
    --expected-i "${EXPECTED_I}" \
    --expected-j "${EXPECTED_J}" \
    --r-rounds "${R_ROUNDS}" \
    --n-bootstrap "${N_BOOTSTRAP}"
