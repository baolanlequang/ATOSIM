#!/bin/bash
#SBATCH --job-name=atosim-stubborn-trail-risky
#SBATCH --output=logs/atosim-stubborn-trail-risky-%A_%a.out
#SBATCH --error=logs/atosim-stubborn-trail-risky-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-19
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# RISKY-BATCH COPY of run_stubborn_trail.sh -- the original is untouched.
# See run_selfish_risky.sh's header for the full rationale -- identical
# here, just this strategy's CSV/jar/output.
#
# Submit directly (no chunking helper needed at only 20 tasks):
#   sbatch run_stubborn_trail_risky.sh
ROWS_PER_TASK=${ROWS_PER_TASK:-10}
ROW_START=$(( ${ROW_OFFSET:-0} + SLURM_ARRAY_TASK_ID * ROWS_PER_TASK ))
ROW_END=$(( ROW_START + ROWS_PER_TASK - 1 ))

RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi
RESULTS_DIR="${WORKSPACE_PATH}/trail_stubborn_risky"
mkdir -p "${RESULTS_DIR}" logs

TMP_OUT="${TMPDIR}/trail_stubborn_risky_${SLURM_JOB_ID}_${SLURM_ARRAY_TASK_ID}"
mkdir -p "${TMP_OUT}"

for (( ROW_INDEX=ROW_START; ROW_INDEX<=ROW_END; ROW_INDEX++ )); do
    java -Xmx48G \
         -XX:+UseG1GC \
         -XX:ParallelGCThreads=8 \
         -XX:+HeapDumpOnOutOfMemoryError \
         -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_INDEX}.hprof" \
         -jar atosim-stubborn-trail.jar \
         sanity_check_run/campaign_prep/run_configurations_trail_stubborn_risky.csv \
         sampling/generated_models \
         sampling/configuration.json \
         --row-index ${ROW_INDEX} \
         --output-dir "${TMP_OUT}"
done

cp "${TMP_OUT}"/result_run_*.json "${RESULTS_DIR}/" 2>/dev/null || true
