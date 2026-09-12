#!/bin/bash
#SBATCH --job-name=atosim-selfish-safe
#SBATCH --output=logs/atosim-selfish-safe-%A_%a.out
#SBATCH --error=logs/atosim-selfish-safe-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-499
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# SAFE-BATCH COPY of run_selfish.sh -- the original is untouched. The ONLY
# change from the original: reads sanity_check_run/campaign_prep/
# run_configurations_selfish_safe.csv (249,800 rows -- the live 250,000-row
# design with 200 rows filtered OUT: the 2 (system_config_id, bandwidth)
# cells matching the confirmed "Slow pathology" risk region -- node_degree==2,
# bandwidth<=7.1, max_block_size>=6,000,000, validator_count>=750, all four
# jointly -- x 100 attacker configs each) instead of the full, unfiltered
# sampling/run_configurations_selfish.csv. Those 200 risky rows are run
# separately by run_selfish_risky.sh, at a much smaller ROWS_PER_TASK to
# contain blast radius -- see that script's own header for why.
#
# ROWS_PER_TASK=500, --array=0-499 unchanged from the original: 249,800
# rows / 500 = 499.6, so 500 tasks are still needed (ceil), with the last
# task only covering 300 rows (249500-249799) -- safe, per the original
# script's own established convention: ATOSIMSimulator skips out-of-range
# --row-index values itself, so an uneven last chunk is only slightly
# wasteful, not a correctness issue.
#
# Output goes to the SAME canonical destination as the original
# (${WORKSPACE_PATH}/selfish) -- this IS the bulk (99.92%) of the real
# campaign's data, not a separate "safe-only" bucket; only the risky
# batch (run_selfish_risky.sh) uses an isolated output subpath, to avoid
# any chance of a still-in-flight risky task colliding with or corrupting
# this batch's already-complete output. Merging the two batches' output
# for analysis is a deliberate, separate task once both have completed --
# not addressed here.
#
# Submit via (same pattern as the original, just pointed at this script and
# the smaller safe-batch row count):
#   ./submit_chunked_array.sh run_selfish_safe.sh 249800 20 5 0 500
ROWS_PER_TASK=${ROWS_PER_TASK:-500}
ROW_START=$(( ${ROW_OFFSET:-0} + SLURM_ARRAY_TASK_ID * ROWS_PER_TASK ))
ROW_END=$(( ROW_START + ROWS_PER_TASK - 1 ))

RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi
RESULTS_DIR="${WORKSPACE_PATH}/selfish"
mkdir -p "${RESULTS_DIR}" logs

TMP_OUT="${TMPDIR}/selfish_safe_${SLURM_JOB_ID}_${SLURM_ARRAY_TASK_ID}"
mkdir -p "${TMP_OUT}"

for (( ROW_INDEX=ROW_START; ROW_INDEX<=ROW_END; ROW_INDEX++ )); do
    java -Xmx48G \
         -XX:+UseG1GC \
         -XX:ParallelGCThreads=8 \
         -XX:+HeapDumpOnOutOfMemoryError \
         -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_INDEX}.hprof" \
         -jar atosim.jar \
         sanity_check_run/campaign_prep/run_configurations_selfish_safe.csv \
         sampling/generated_models \
         sampling/configuration.json \
         --row-index ${ROW_INDEX} \
         --output-dir "${TMP_OUT}"
done

cp "${TMP_OUT}"/result_run_*.json "${RESULTS_DIR}/" 2>/dev/null || true
