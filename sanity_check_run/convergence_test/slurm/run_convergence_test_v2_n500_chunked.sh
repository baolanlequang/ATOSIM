#!/bin/bash
#SBATCH --job-name=atosim-convergence-test-v2-n500
#SBATCH --output=logs/atosim-convergence-test-v2-n500-%A_%a.out
#SBATCH --error=logs/atosim-convergence-test-v2-n500-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-43
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# ---------------------------------------------------------------------------
# Redesigned convergence-test config subset (v2): corrects a design flaw in
# the original 35-config subset (selected_configs.csv), whose 35 rows landed
# on 33 nearly-all-distinct system_config_id values (31/33 singleton groups)
# -- GroupKFold on that subset degenerated to near-leave-one-out, which is
# almost certainly why Step 3's fANOVA CV R^2 got WORSE at higher replication
# counts (-0.20 / -0.25 / -1.61 at N=500/1000/5000) even though Step 2 showed
# genuine per-config D-bar+ stabilization -- a small-sample CV artifact, not
# necessarily evidence against the "more reps helps" hypothesis.
#
# selected_configs_v2.csv instead selects 11 DISTINCT (system_config_id,
# bandwidth) cells (spanning node_degree 2-8, bandwidth 5.0-125.0,
# block_creation_interval and max_block_size low/mid/high, validator_count
# capped at the sample median and the known-expensive low-degree +
# low-bandwidth + large-block corner excluded, matching v1's own selection
# discipline) x 4 real attacker_config_id draws per cell = 44 rows, 11
# GroupKFold groups of 4 each -- genuine repeated-measures structure, the
# same shape that worked in both prior successful fANOVA fits (the 1000-row
# and 10k-row samples, which both had real system-config repetition).
#
# Per explicit scope decision: N=500 ONLY. If this shows fANOVA CV R^2
# improving over the existing N=200 baseline with this corrected group
# structure, that is the signal to prepare N=1000/N=5000 for this same v2
# subset as a follow-up; if not, better to find that out before spending
# more compute on higher tiers. configuration_mc1000.json/mc5000.json are
# intentionally not referenced anywhere in this script.
#
# One array task per (config, attacker) row -- 44 rows, --array=0-43. No
# tier multiplexing needed (unlike run_convergence_test_chunked.sh's
# GLOBAL_TASK_ID/3, %3 split across 3 replication levels): every row here is
# N=500, so GLOBAL_TASK_ID indexes selected_configs_v2.csv directly.
#
# Chunking: submit via submit_chunked_array.sh exactly as the original
# convergence-test job was (ROW_OFFSET/ROWS_PER_TASK env vars it exports),
# with ROWS_PER_TASK=1 for the same reason as run_convergence_test_chunked.sh
# -- see that script's own header comment for the full rationale (SLURM caps
# the array task-ID value, not the task count, so each chunk resets to
# 0-<chunk_size-1> and the real offset is threaded through separately).
# GLOBAL_TASK_ID falls back to plain SLURM_ARRAY_TASK_ID (ROW_OFFSET unset,
# defaults to 0) if this script is ever submitted stand-alone instead of
# through the chunking helper.
#
# Existing data note: config_id 178537 (group06) coincides with a row
# already present in the original v1 subset, which already has N=500 data
# collected for it (${WORKSPACE_PATH}/convergence_test/mc500/178537/) -- not
# excluded from this run for simplicity/uniformity, so it will produce a
# second independent N=500 replicate rather than being skipped; a
# negligible, harmless duplication, not a bug.
#
# --time/--cpus-per-task/--mem/--partition: same reasoning/values as
# run_convergence_test_chunked.sh (single N=500 row's cost is far below the
# N=5000 tier that reasoning was sized for -- kept identical rather than
# re-derived, since 72h remains a safe, if generous, upper bound here too).
# ---------------------------------------------------------------------------

SELECTED_CONFIGS_CSV="sanity_check_run/convergence_test/selected_configs_v2.csv"
TIER_CONFIG="sanity_check_run/convergence_test/configuration_mc500.json"
N_REPS=500

GLOBAL_TASK_ID=$(( ${ROW_OFFSET:-0} + SLURM_ARRAY_TASK_ID ))

# +2: skip the header line (1) and convert 0-indexed GLOBAL_TASK_ID to a
# 1-indexed data-row line number (2 = first data row).
CSV_LINE=$(( GLOBAL_TASK_ID + 2 ))
ROW=$(sed -n "${CSV_LINE}p" "${SELECTED_CONFIGS_CSV}")
if [ -z "${ROW}" ]; then
    echo "ERROR: no row at line ${CSV_LINE} of ${SELECTED_CONFIGS_CSV} (GLOBAL_TASK_ID=${GLOBAL_TASK_ID}, ROW_OFFSET=${ROW_OFFSET:-0}, SLURM_ARRAY_TASK_ID=${SLURM_ARRAY_TASK_ID})" >&2
    exit 1
fi
ROW_INDEX=$(echo "${ROW}" | awk -F',' '{print $2}')
CONFIG_ID=$(echo "${ROW}" | awk -F',' '{print $3}')

if [ -z "${ROW_INDEX}" ] || [ -z "${CONFIG_ID}" ]; then
    echo "ERROR: failed to parse row_index/config_id from row: ${ROW}" >&2
    exit 1
fi

ROW_CONFIG_CSV="sampling/run_configurations_selfish.csv"
MODELS_DIR="sampling/generated_models"

RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi

# Separate top-level subpath -- convergence_test_v2/ -- so this run's results
# never get mixed up with the original 35-config subset's
# convergence_test/mc{500,1000,5000}/ output.
RESULTS_DIR="${WORKSPACE_PATH}/convergence_test_v2/mc${N_REPS}/${CONFIG_ID}"
mkdir -p "${RESULTS_DIR}" logs

TMP_OUT="${TMPDIR}/convergence_test_v2_mc${N_REPS}_${CONFIG_ID}_${SLURM_JOB_ID}"
mkdir -p "${TMP_OUT}"

java -Xmx48G \
     -XX:+UseG1GC \
     -XX:ParallelGCThreads=8 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_INDEX}.hprof" \
     -jar atosim.jar \
     "${ROW_CONFIG_CSV}" \
     "${MODELS_DIR}" \
     "${TIER_CONFIG}" \
     --row-index "${ROW_INDEX}" \
     --output-dir "${TMP_OUT}"

cp "${TMP_OUT}"/result_run_*.json "${RESULTS_DIR}/" 2>/dev/null || true
