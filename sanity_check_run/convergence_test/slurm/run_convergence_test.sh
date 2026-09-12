#!/bin/bash
#SBATCH --job-name=atosim-convergence-test
#SBATCH --output=logs/atosim-convergence-test-%A_%a.out
#SBATCH --error=logs/atosim-convergence-test-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-104
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# ---------------------------------------------------------------------------
# Purpose (colleague request): determine whether conditional_reorg_depth's
# poor fANOVA fit (CV R^2 near/below 0 in every prior run, at the
# production default of 200 Monte Carlo replications/config) is because 200
# reps is too few -- i.e. the per-config D-bar+ estimate is dominated by
# Monte Carlo noise, which defeats the RF surrogate regardless of any real
# parameter sensitivity -- or because the outcome genuinely has little
# systematic variation no matter how many reps are used. This job re-runs
# the SAME 35 selfish configs (sanity_check_run/convergence_test/
# selected_configs.csv, selected to span node_degree/bandwidth/
# block_creation_interval/max_block_size while excluding the known-expensive
# Slow-like corner and capping validator_count at a moderate level -- see
# that file and the task's Phase 1 reasoning) at three additional
# replication counts: 500, 1000, 5000. The existing 200-replication data for
# these same 35 configs is already in
# sanity_check_run/03_pipeline_check/parquet_10k/analysis_settings_selfish_10k.parquet
# and is reused as-is for the N=200 data point -- NOT re-run here.
#
# What to check once results land (separate follow-up task, not done here):
#   1. Per-config D-bar+ (conditional_reorg_depth) estimate stability across
#      N=200/500/1000/5000 -- does the estimate visibly stabilize (variance
#      across replication counts shrinking, consistent with a real signal
#      obscured by MC noise at N=200), or does it stay just as noisy/
#      unstable even at N=5000 (consistent with a genuinely weak/absent
#      systematic effect)?
#   2. fANOVA CV R^2 for conditional_reorg_depth, refit separately at each
#      replication level on these same 35 configs -- does CV R^2 improve
#      monotonically with N (supports "too few reps"), stay flat/near-zero
#      throughout (supports "genuinely weak signal"), or something in
#      between?
#
# Array structure: one task per (config x replication tier), 35 configs x 3
# tiers (500/1000/5000) = 105 tasks, --array=0-104. Task ID decomposes as:
#   CONFIG_IDX = SLURM_ARRAY_TASK_ID / 3   (0-34, indexes into selected_configs.csv)
#   TIER_IDX   = SLURM_ARRAY_TASK_ID % 3   (0=500, 1=1000, 2=5000)
# Reading the config row from the CSV by line number (not a 105-entry case
# statement) follows the same "read an explicit row list from a CSV" pattern
# already established in sanity_check_run/slurm/run_selfish_sanity_10k.sh,
# just keyed by index arithmetic instead of a strategy column filter, since
# every row here is already selfish-only.
#
# --time=72:00:00, uniform across all 105 tasks (not tiered per-array-index
# -- SLURM applies one --time to the whole array): sized for the WORST case
# (5000-rep tier), not the typical case, per this task's explicit "err
# toward generous rather than tight" instruction. Reasoning: this project's
# own established per-row timing baseline at 200 reps, drawn from the live
# 10k sample (see run_selfish.sh's own comments) is median ~16s / mean ~19s
# / p99 ~97s / max ~204s. Cost scales roughly linearly with replication
# count (each MC round is close to independent unit work -- confirmed by
# reading MonteCarloSimulation.run(): a plain per-round task loop over
# numberOfRounds, no round-count-dependent structural cost beyond that). At
# 25x reps (5000 vs 200), naive linear scaling of that historical max
# (204s x 25 = 5100s, ~85 min) would already fit inside a few hours -- but
# this subset was filtered to exclude the low-degree+low-bandwidth+
# large-block corner and cap validator_count, NOT to exclude large
# max_block_size in isolation (several selected rows still have
# max_block_size up to ~7.98M bytes, comparable to the pathological
# Slow config's 8M). The already-confirmed Slow investigation attributed
# that config's runaway cost (>8h, still not finished, at only 200 reps) to
# a per-transaction trace-event notification cost that compounds with
# max_block_size (deterministicFullBlock mode fills every block to
# capacity) x validator_count (one mempool per node) x round count -- and
# this subset's worst rows share the max_block_size factor (though at less
# than half Slow's validator_count, and with non-minimal node_degree/
# bandwidth in most rows). Given that residual uncertainty and 25x more
# rounds than the already-measured baseline, 72:00:00 (this project's own
# established convention for "genuinely uncertain, could be expensive"
# runs -- see run_custom_fast_slow.sh) is used directly rather than a
# tighter linear-scaling estimate, erring toward generous. The 500/1000-rep
# tiers will in practice finish in a small fraction of this window (SLURM
# jobs that finish early simply exit early -- there is no cost to the
# uniform, worst-case-sized budget being generous for the cheaper tiers).
#
# --cpus-per-task/--mem/--partition unchanged from every other script in
# this project's history: a single row's simulation still uses at most
# monteCarloParallelism=8 cores regardless of replication count.
# ---------------------------------------------------------------------------

SELECTED_CONFIGS_CSV="sanity_check_run/convergence_test/selected_configs.csv"

CONFIG_IDX=$(( SLURM_ARRAY_TASK_ID / 3 ))
TIER_IDX=$(( SLURM_ARRAY_TASK_ID % 3 ))

case "${TIER_IDX}" in
    0) N_REPS=500;  TIER_CONFIG="sanity_check_run/convergence_test/configuration_mc500.json" ;;
    1) N_REPS=1000; TIER_CONFIG="sanity_check_run/convergence_test/configuration_mc1000.json" ;;
    2) N_REPS=5000; TIER_CONFIG="sanity_check_run/convergence_test/configuration_mc5000.json" ;;
esac

# +2: skip the header line (1) and convert 0-indexed CONFIG_IDX to a 1-indexed
# data-row line number (2 = first data row).
CSV_LINE=$(( CONFIG_IDX + 2 ))
ROW=$(sed -n "${CSV_LINE}p" "${SELECTED_CONFIGS_CSV}")
if [ -z "${ROW}" ]; then
    echo "ERROR: no row at line ${CSV_LINE} of ${SELECTED_CONFIGS_CSV} (CONFIG_IDX=${CONFIG_IDX}, SLURM_ARRAY_TASK_ID=${SLURM_ARRAY_TASK_ID})" >&2
    exit 1
fi
ROW_INDEX=$(echo "${ROW}" | awk -F',' '{print $2}')
CONFIG_ID=$(echo "${ROW}" | awk -F',' '{print $3}')

if [ -z "${ROW_INDEX}" ] || [ -z "${CONFIG_ID}" ]; then
    echo "ERROR: failed to parse row_index/config_id from row: ${ROW}" >&2
    exit 1
fi

# Same production CSV/models the rest of the project's selfish runs use --
# this task only swaps the base config (replication count), not the row
# source or generated models, per the hard constraint against touching
# sampling/generated_models/ or live CSVs.
ROW_CONFIG_CSV="sampling/run_configurations_selfish.csv"
MODELS_DIR="sampling/generated_models"

RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi

# Organized by replication level first, then config_id, so results can be
# grouped by N straightforwardly when comparing D-bar+ stability/fANOVA
# CV R^2 across replication counts.
RESULTS_DIR="${WORKSPACE_PATH}/convergence_test/mc${N_REPS}/${CONFIG_ID}"
mkdir -p "${RESULTS_DIR}" logs

TMP_OUT="${TMPDIR}/convergence_test_mc${N_REPS}_${CONFIG_ID}_${SLURM_JOB_ID}"
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
