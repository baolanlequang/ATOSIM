#!/bin/bash
#SBATCH --job-name=atosim-selfish-hmax600
#SBATCH --output=logs/atosim-selfish-hmax600-%A_%a.out
#SBATCH --error=logs/atosim-selfish-hmax600-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-499
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# UNIFIED-CAMPAIGN COPY of run_selfish.sh -- the original is untouched. This
# replaces the safe/risky split (run_selfish_safe.sh + run_selfish_risky.sh)
# entirely: applies H_max=600 (maxAllowedBlockchainLength) to the FULL,
# unfiltered 250,000-row sampling/run_configurations_selfish.csv, instead of
# only the 200-row risky batch getting H_max=600 while the remaining 249,800
# "safe" rows ran at the default H_max=30.
#
# Why: the safe/risky split existed to sidestep an unresolved question --
# exactly how much of the parameter space falls in the confirmed "Slow
# pathology" corner (node_degree==2, bandwidth<=7.1, max_block_size>=6e6,
# validator_count>=750, all four jointly). Calibration
# (sanity_check_run/convergence_test/hmax_calibration/, 6 representative rows
# from that corner spanning both risky (system_config_id, bandwidth) cells --
# 831/5.0 and 832/7.1 -- and low/mid/high attacker_hash_power) showed
# resolution reaching 100% at H_max=600, with cost not scaling linearly (most
# episodes still resolve well before even the old H_max=30 cap). That
# calibration was run on 6 representative SLOW configs only, not on
# fast/typical ones -- but raising a cap that a fast-resolving config rarely
# even approaches should not add cost for it, since quiescence still fires
# early regardless of how high the cap is set. Applying H_max=600 uniformly
# removes the need to precisely bound the Slow-pathology region at all.
#
# Config: sanity_check_run/campaign_prep/configuration_hmax600.json (a
# byte-identical copy of configuration_risky_hmax600.json, renamed now that
# it is the universal config for the whole campaign, not just the risky
# batch -- content unchanged; the original configuration_risky_hmax600.json
# is left in place untouched, since run_selfish_risky.sh still references it).
#
# ROWS_PER_TASK=500, --array=0-499: unchanged from the original safe-batch
# convention (matches run_selfish.sh/run_selfish_safe.sh) -- 250,000/500=500
# tasks, ~2.6h/task on average at the original per-row cost; H_max=600
# should not change this for the vast majority of (non-pathological) rows,
# per the calibration finding above.
#
# Output goes to the SAME canonical destination as the original
# (${WORKSPACE_PATH}/selfish) -- this single unified run now covers the
# entire dataset that the safe+risky split would have covered jointly.
#
# Submit via (same pattern as the original):
#   ./submit_chunked_array.sh run_selfish_hmax600.sh 250000 [chunk_size] [sleep_between] [start_offset] [rows_per_task]
# or directly:
#   sbatch run_selfish_hmax600.sh
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

TMP_OUT="${TMPDIR}/selfish_hmax600_${SLURM_JOB_ID}_${SLURM_ARRAY_TASK_ID}"
mkdir -p "${TMP_OUT}"

for (( ROW_INDEX=ROW_START; ROW_INDEX<=ROW_END; ROW_INDEX++ )); do
    java -Xmx48G \
         -XX:+UseG1GC \
         -XX:ParallelGCThreads=8 \
         -XX:+HeapDumpOnOutOfMemoryError \
         -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_INDEX}.hprof" \
         -jar atosim.jar \
         sampling/run_configurations_selfish.csv \
         sampling/generated_models \
         sanity_check_run/campaign_prep/configuration_hmax600.json \
         --row-index ${ROW_INDEX} \
         --output-dir "${TMP_OUT}"
done

cp "${TMP_OUT}"/result_run_*.json "${RESULTS_DIR}/" 2>/dev/null || true
