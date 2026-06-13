#!/bin/bash
#SBATCH --job-name=prcc-fanova-revenue
#SBATCH --output=../logs/prcc-fanova-revenue-%j.out
#SBATCH --error=../logs/prcc-fanova-revenue-%j.err
#SBATCH --partition=highmem
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=96
#SBATCH --exclusive
#SBATCH --time=04:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# Submit from the repo root: sbatch sampling/run_prcc_fanova_revenue.sh
# Pass-through flags to the python script, e.g.:
#   sbatch sampling/run_prcc_fanova_revenue.sh --revenue --latex --plot
# Defaults to --revenue --latex --plot if nothing is given.

set -euo pipefail

ROOT_DIR="results_new"
OUT_DIR="analysis/prcc_fanova"
mkdir -p logs "${OUT_DIR}"

# The script's parallelism comes entirely from sklearn's RandomForestRegressor
# (n_jobs=-1, via joblib/loky) -- not from extra worker processes here. Pin
# BLAS thread pools to 1 so they don't oversubscribe on top of joblib's
# process-level parallelism, and cap loky at exactly the cpus Slurm gave us.
export OMP_NUM_THREADS=1
export MKL_NUM_THREADS=1
export OPENBLAS_NUM_THREADS=1
export NUMEXPR_NUM_THREADS=1
export LOKY_MAX_CPU_COUNT="${SLURM_CPUS_PER_TASK}"

ARGS=("$@")
if [ ${#ARGS[@]} -eq 0 ]; then
    ARGS=(--revenue --latex --plot)
fi

python3 sampling/prcc_fanova_revenue.py \
    --root "${ROOT_DIR}" \
    --outdir "${OUT_DIR}" \
    "${ARGS[@]}"
