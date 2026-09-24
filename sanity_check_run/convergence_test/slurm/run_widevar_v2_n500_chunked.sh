#!/bin/bash
#SBATCH --job-name=atosim-widevar-v2-n500
#SBATCH --output=logs/atosim-widevar-v2-n500-%A_%a.out
#SBATCH --error=logs/atosim-widevar-v2-n500-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-377
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=6:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# ---------------------------------------------------------------------------
# Purpose: N=500 tier of the wide-variance v2 convergence test. Colleague
# wants to see how conditional_reorg_depth's fANOVA CV R^2 and main
# importance estimates (validator_count, node_degree) evolve with
# replication count for sanity_check_run/convergence_test/
# wide_variance_subset_v2.csv -- the 378-row (126 groups x 3) subset that
# finally produced an interpretable fit (CV R^2=+0.507) at the existing
# N=200 anchor, already on disk and NOT re-run here. This tier adds N=500
# only; N=1000 and N=5000 are separate sibling scripts
# (run_widevar_v2_n1000_chunked.sh, run_widevar_v2_n5000_chunked.sh).
#
# Chunk-compatible for submit_chunked_array.sh, following
# run_convergence_test_chunked.sh's established pattern: GLOBAL_TASK_ID =
# ${ROW_OFFSET:-0} + SLURM_ARRAY_TASK_ID, ROWS_PER_TASK=1 (one array task
# per CSV row, not a range -- there are no "tiers" folded into this array,
# unlike that script's config x tier interleaving, since this job is
# already single-tier). --array=0-377 above is the harmless
# stand-alone-submission fallback (SLURM prefers a CLI --array over the
# embedded directive, and submit_chunked_array.sh always supplies one).
#
# --time=6:00:00: linear-scaling this project's own already-recorded
# general-population per-row baseline at N=200 (median ~16s / mean ~19s /
# p99 ~97s / max ~204s, from run_selfish_risky.sh's own established
# figures) by 2.5x (500/200) gives a max of ~510s (~8.5 min) -- 6:00:00 is
# >40x that, deliberately generous because this subset is NOT
# representative of the general population that baseline was drawn from:
# it is 52.4% node_degree=2 rows (198/378) and includes 6 rows that fall
# squarely in the confirmed Slow-pathology corner (node_degree<=2,
# bandwidth<=7.1, max_block_size>=6M, validator_count>=750) that the
# earlier 35-config convergence-test set deliberately excluded. N=500 is
# still close enough to the well-characterized N=200 regime that this
# margin is expected to comfortably cover even those 6 rows; the much
# larger risk sits in the N=5000 tier -- see that script's own --time
# reasoning and this job's Phase 2 cost-estimate report.
# ---------------------------------------------------------------------------

WIDEVAR_CSV="sanity_check_run/convergence_test/wide_variance_subset_v2.csv"
TIER_CONFIG="sanity_check_run/convergence_test/configuration_mc500.json"
N_REPS=500

GLOBAL_TASK_ID=$(( ${ROW_OFFSET:-0} + SLURM_ARRAY_TASK_ID ))

# +2: skip the header line (1) and convert 0-indexed GLOBAL_TASK_ID to a
# 1-indexed data-row line number (2 = first data row).
CSV_LINE=$(( GLOBAL_TASK_ID + 2 ))
ROW=$(sed -n "${CSV_LINE}p" "${WIDEVAR_CSV}")
if [ -z "${ROW}" ]; then
    echo "ERROR: no row at line ${CSV_LINE} of ${WIDEVAR_CSV} (GLOBAL_TASK_ID=${GLOBAL_TASK_ID}, ROW_OFFSET=${ROW_OFFSET:-0}, SLURM_ARRAY_TASK_ID=${SLURM_ARRAY_TASK_ID})" >&2
    exit 1
fi
# wide_variance_subset_v2.csv columns: strategy,row_index,config_id,... --
# row_index is column 2, config_id is column 3, same position
# run_convergence_test_chunked.sh already relies on for its own CSV.
ROW_INDEX=$(echo "${ROW}" | awk -F',' '{print $2}')
CONFIG_ID=$(echo "${ROW}" | awk -F',' '{print $3}')

if [ -z "${ROW_INDEX}" ] || [ -z "${CONFIG_ID}" ]; then
    echo "ERROR: failed to parse row_index/config_id from row: ${ROW}" >&2
    exit 1
fi

# All 378 rows are strategy=selfish (confirmed against the live file) --
# no per-row strategy branching needed.
ROW_CONFIG_CSV="sampling/run_configurations_selfish.csv"
MODELS_DIR="sampling/generated_models"

RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi

# convergence_test_v2/, distinct from the earlier 35-config run's
# convergence_test/ output path, so the two datasets can never collide.
RESULTS_DIR="${WORKSPACE_PATH}/convergence_test_v2/mc${N_REPS}/${CONFIG_ID}"
mkdir -p "${RESULTS_DIR}" logs

TMP_OUT="${TMPDIR}/widevar_v2_mc${N_REPS}_${CONFIG_ID}_${SLURM_JOB_ID}"
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
