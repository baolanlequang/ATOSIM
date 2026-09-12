#!/bin/bash
#SBATCH --job-name=atosim-selfish-risky
#SBATCH --output=logs/atosim-selfish-risky-%A_%a.out
#SBATCH --error=logs/atosim-selfish-risky-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-19
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# RISKY-BATCH COPY of run_selfish.sh -- the original is untouched. Runs
# ONLY the 200 rows filtered into sanity_check_run/campaign_prep/
# run_configurations_selfish_risky.csv (2 (system_config_id, bandwidth)
# cells matching the confirmed "Slow pathology" region: node_degree==2,
# bandwidth<=7.1, max_block_size>=6,000,000, validator_count>=750, all
# four jointly -- x 100 attacker configs each). The remaining 249,800 rows
# are run separately by run_selfish_safe.sh at the original ROWS_PER_TASK.
#
# ROWS_PER_TASK=10 (not the original's 500): a single pathological row
# (the confirmed real case: >8h at 200 rounds, still not finished) blocks
# every other row queued after it in the SAME array task until that task's
# wall-clock kills it. Capping ROWS_PER_TASK at 10 caps the worst-case
# blast radius at 10 rows lost per timeout instead of 500 -- a >>50x
# reduction, for the one batch specifically curated to contain configs
# expected to trigger this. 200 rows / 10 = 20 array tasks total -- small
# enough that NO chunking via submit_chunked_array.sh is needed at all
# (20 is exactly the size already proven to submit successfully in this
# project's own recent testing); submit directly with a single `sbatch`.
#
# --time=72:00:00 kept UNCHANGED from the original, not shortened, even
# though a typical 10-row task should finish in ~3 minutes (10 x ~19s
# mean): this batch exists specifically because SOME of its rows are
# expected to be the pathological, many-hours-long case -- shrinking
# --time here would kill exactly the rows this batch was built to give
# room to, defeating its purpose. The blast-radius mitigation is in
# ROWS_PER_TASK (how much OTHER work one bad row can block), not in a
# shorter wall-clock (which would just make MORE tasks hit the ceiling).
#
# Output goes to an ISOLATED subpath (${WORKSPACE_PATH}/selfish_risky),
# distinct from the safe batch's canonical ${WORKSPACE_PATH}/selfish --
# so a still-in-flight (or eventually-timed-out) risky task can never
# collide with or be mistaken for the safe batch's already-complete
# output. Merging the two batches for analysis is a deliberate, separate
# task once both have completed -- not addressed here.
#
# Submit directly (no chunking helper needed at only 20 tasks):
#   sbatch run_selfish_risky.sh
ROWS_PER_TASK=${ROWS_PER_TASK:-10}
ROW_START=$(( ${ROW_OFFSET:-0} + SLURM_ARRAY_TASK_ID * ROWS_PER_TASK ))
ROW_END=$(( ROW_START + ROWS_PER_TASK - 1 ))

RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi
RESULTS_DIR="${WORKSPACE_PATH}/selfish_risky"
mkdir -p "${RESULTS_DIR}" logs

TMP_OUT="${TMPDIR}/selfish_risky_${SLURM_JOB_ID}_${SLURM_ARRAY_TASK_ID}"
mkdir -p "${TMP_OUT}"

for (( ROW_INDEX=ROW_START; ROW_INDEX<=ROW_END; ROW_INDEX++ )); do
    java -Xmx48G \
         -XX:+UseG1GC \
         -XX:ParallelGCThreads=8 \
         -XX:+HeapDumpOnOutOfMemoryError \
         -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_INDEX}.hprof" \
         -jar atosim.jar \
         sanity_check_run/campaign_prep/run_configurations_selfish_risky.csv \
         sampling/generated_models \
         sampling/configuration.json \
         --row-index ${ROW_INDEX} \
         --output-dir "${TMP_OUT}"
done

cp "${TMP_OUT}"/result_run_*.json "${RESULTS_DIR}/" 2>/dev/null || true
