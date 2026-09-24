#!/bin/bash
#SBATCH --job-name=atosim-widevar-v2-n5000
#SBATCH --output=logs/atosim-widevar-v2-n5000-%A_%a.out
#SBATCH --error=logs/atosim-widevar-v2-n5000-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-377
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# ---------------------------------------------------------------------------
# Purpose: N=5000 tier of the wide-variance v2 convergence test. See
# run_widevar_v2_n500_chunked.sh for the full purpose/rationale (colleague's
# conditional_reorg_depth fANOVA-fit convergence curve for
# wide_variance_subset_v2.csv, 378 rows / 126 groups, N=200 anchor already
# on disk, not re-run here) -- not repeated here. N=500 and N=1000 are
# separate sibling scripts.
#
# Chunk-compatible for submit_chunked_array.sh: GLOBAL_TASK_ID =
# ${ROW_OFFSET:-0} + SLURM_ARRAY_TASK_ID, ROWS_PER_TASK=1 (one array task
# per CSV row). --array=0-377 above is the stand-alone-submission fallback.
#
# --time=72:00:00 -- NOT a further linear step from the N=500/N=1000
# tiers' generous-but-bounded budgets, deliberately. Two independent,
# already-recorded reasons this tier is qualitatively riskier, not just
# quantitatively 5x/10x bigger:
#   1. The earlier 35-config convergence-test job (run_convergence_test.sh,
#      which explicitly EXCLUDED the low-degree/low-bandwidth/large-block
#      "Slow pathology" corner) only completed 29/35 rows at N=5000 in the
#      time available, even with that exclusion and the same 72:00:00
#      budget.
#   2. Unlike that 35-config set, wide_variance_subset_v2.csv was NOT
#      filtered to exclude this corner: 6 of its 378 rows fall squarely
#      inside it (node_degree<=2, bandwidth<=7.1, max_block_size>=6M,
#      validator_count>=750), the same corner that produced a row still
#      unfinished after >8h at only N=200 (run_selfish_risky.sh's own
#      documented precedent). Naive linear scaling of the general-
#      population baseline (max ~204s at N=200 -> ~5100s/~85min at N=5000,
#      the same figure run_convergence_test.sh used) does not apply to
#      these 6 rows at all -- that baseline's max/p99 were drawn from a
#      population that itself excludes genuine Slow-pathology cases (they
#      were curated out into the separate risky-batch CSV), so it is not a
#      valid extrapolation base for these specific 6 rows. Their real cost
#      at N=5000 is unknown and could plausibly exceed even 72h for one or
#      more of them.
# 72:00:00 (this project's own established convention for "genuinely
# uncertain, could be expensive" runs) is used as the least-bad available
# budget given that uncertainty, matching run_convergence_test.sh's own
# N=5000 choice -- but this job is expected, going in, to plausibly finish
# fewer than 378/378 rows within it, for reasons stronger than that
# precedent already had. See this task's Phase 2 report for the explicit
# recommendation to Lân on whether to submit this tier now or stage it
# separately from N=500/N=1000.
# ---------------------------------------------------------------------------

WIDEVAR_CSV="sanity_check_run/convergence_test/wide_variance_subset_v2.csv"
TIER_CONFIG="sanity_check_run/convergence_test/configuration_mc5000.json"
N_REPS=5000

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
