#!/bin/bash
#SBATCH --job-name=atosim-stubborn-lead-hmax600
#SBATCH --output=logs/atosim-stubborn-lead-hmax600-%A_%a.out
#SBATCH --error=logs/atosim-stubborn-lead-hmax600-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-499
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# UNIFIED-CAMPAIGN COPY of run_stubborn_lead.sh -- the original is untouched.
# See run_selfish_hmax600.sh for the full rationale (identical for all three
# strategies): applies H_max=600 (maxAllowedBlockchainLength) to the FULL,
# unfiltered 250,000-row sampling/run_configurations_lead_stubborn.csv,
# replacing the safe/risky split (run_stubborn_lead_safe.sh +
# run_stubborn_lead_risky.sh) entirely.
#
# Config: sanity_check_run/campaign_prep/configuration_hmax600.json (a
# byte-identical copy of configuration_risky_hmax600.json, renamed now that
# it is the universal config for the whole campaign -- content unchanged;
# the original configuration_risky_hmax600.json is left in place untouched,
# since run_stubborn_lead_risky.sh still references it).
#
# ROWS_PER_TASK=500, --array=0-499: unchanged from the original safe-batch
# convention.
#
# Output goes to the SAME canonical destination as the original
# (${WORKSPACE_PATH}/lead_stubborn).
#
# Submit via:
#   ./submit_chunked_array.sh run_stubborn_lead_hmax600.sh 250000 [chunk_size] [sleep_between] [start_offset] [rows_per_task]
# or directly:
#   sbatch run_stubborn_lead_hmax600.sh
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

TMP_OUT="${TMPDIR}/lead_stubborn_hmax600_${SLURM_JOB_ID}_${SLURM_ARRAY_TASK_ID}"
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
         sanity_check_run/campaign_prep/configuration_hmax600.json \
         --row-index ${ROW_INDEX} \
         --output-dir "${TMP_OUT}"
done

cp "${TMP_OUT}"/result_run_*.json "${RESULTS_DIR}/" 2>/dev/null || true
