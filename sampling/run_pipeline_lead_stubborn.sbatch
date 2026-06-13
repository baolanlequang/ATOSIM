#!/bin/bash
#SBATCH --job-name=lead_stubborn_pipeline
#SBATCH --partition=cpu
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=48
#SBATCH --mem=32G
#SBATCH --time=03:00:00
#SBATCH --output=logs/slurm_pipeline_%j.out
#SBATCH --error=logs/slurm_pipeline_%j.err
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

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
# --cpus-per-task workers in parallel. --time=03:00:00 leaves generous
# margin for real cluster I/O latency on the parallel filesystem.
#
# Adjust --partition, --cpus-per-task, --mem to match actual BwUniCluster
# 3.0 node specs, and edit the module load / venv activation lines below.
# ---------------------------------------------------------------------------

set -euo pipefail

# --- environment setup (edit to match the cluster's actual module names) ---
# module load devel/python/3.9
# source ~/venvs/atosim/bin/activate

# --- pipeline paths (EDIT these to the real paths before submitting) ---
export JSON_DIR=../results_new/lead_stubborn
export CONFIG_CSV=./run_configurations_lead_stubborn.csv
export OUTPUT_DIR=../results_new/output/lead_stubborn

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
