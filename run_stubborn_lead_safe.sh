#!/bin/bash
#SBATCH --job-name=atosim-stubborn-lead-safe
#SBATCH --output=logs/atosim-stubborn-lead-safe-%A_%a.out
#SBATCH --error=logs/atosim-stubborn-lead-safe-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-499
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# SAFE-BATCH COPY of run_stubborn_lead.sh -- the original is untouched. See
# run_selfish_safe.sh's header for the full rationale (risk-region
# definition, why 500 tasks are still needed, why output stays at the
# canonical path) -- identical here, just this strategy's CSV/jar/output.
#
# Submit via:
#   ./submit_chunked_array.sh run_stubborn_lead_safe.sh 249800 20 5 0 500
ROWS_PER_TASK=${ROWS_PER_TASK:-500}
ROW_START=$(( ${ROW_OFFSET:-0} + SLURM_ARRAY_TASK_ID * ROWS_PER_TASK ))
ROW_END=$(( ROW_START + ROWS_PER_TASK - 1 ))

RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi
RESULTS_DIR="${WORKSPACE_PATH}/lead_stubborn"
mkdir -p "${RESULTS_DIR}" logs

TMP_OUT="${TMPDIR}/lead_stubborn_safe_${SLURM_JOB_ID}_${SLURM_ARRAY_TASK_ID}"
mkdir -p "${TMP_OUT}"

for (( ROW_INDEX=ROW_START; ROW_INDEX<=ROW_END; ROW_INDEX++ )); do
    java -Xmx48G \
         -XX:+UseG1GC \
         -XX:ParallelGCThreads=8 \
         -XX:+HeapDumpOnOutOfMemoryError \
         -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_INDEX}.hprof" \
         -jar atosim-stubborn-lead.jar \
         sanity_check_run/campaign_prep/run_configurations_lead_stubborn_safe.csv \
         sampling/generated_models \
         sampling/configuration.json \
         --row-index ${ROW_INDEX} \
         --output-dir "${TMP_OUT}"
done

cp "${TMP_OUT}"/result_run_*.json "${RESULTS_DIR}/" 2>/dev/null || true
