#!/bin/bash
#SBATCH --job-name=atosim-hmax-calibration
#SBATCH --output=logs/atosim-hmax-calibration-%A_%a.out
#SBATCH --error=logs/atosim-hmax-calibration-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-23
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=24:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# ---------------------------------------------------------------------------
# Purpose: calibrate maxAllowedBlockchainLength (H_max) for the risky batch's
# Slow-region configs (sanity_check_run/campaign_prep/
# run_configurations_selfish_risky.csv). Colleague's critique: ASR computed
# from only the resolved subset is likely survivorship-biased, since the
# unresolved rounds (cut off by H_max=30 without reaching quiescence) are
# plausibly the longest, hardest-to-resolve episodes. This job measures, for
# 6 representative rows from the risky batch (spanning both its
# (system_config_id, bandwidth) cells -- 831/5.0 and 832/7.1 -- and low/mid/
# high attacker_hash_power: 0.211/0.349/0.490), how resolution rate and time
# cost change across 4 candidate H_max levels (60/150/300/600 = 2x/5x/10x/
# 20x the current baseline of 30). This does NOT touch the live risky batch
# or its config -- see the scratch config files in this same directory
# (configuration_hmax{60,150,300,600}.json, each also reduced to
# numberOfMonteCarloRounds=25 for affordability; resolution-rate estimates
# at n=25 are for calibration/ranking only, not final data) and the 6-row
# calibration_rows.csv (a copy of 6 rows from the risky CSV, same column
# format, untouched original).
#
# Array structure: one task per (row x H_max level), 6 rows x 4 levels = 24
# tasks, --array=0-23. Task ID decomposes as:
#   ROW_IDX  = SLURM_ARRAY_TASK_ID / 4   (0-5, indexes into calibration_rows.csv)
#   HMAX_IDX = SLURM_ARRAY_TASK_ID % 4   (0=H60, 1=H150, 2=H300, 3=H600)
# 24 tasks is well under the sizes this project has previously submitted
# directly without chunking (the risky batch itself ran 20 tasks directly --
# see run_selfish_risky.sh); submit directly with a single `sbatch`.
#
# --time=24:00:00, uniform across all 24 tasks (SLURM applies one --time to
# the whole array): these 6 rows are drawn from the EXACT (system_config_id,
# bandwidth, node_degree) corner already confirmed pathological by the risky
# batch itself (node_degree=2, bandwidth in {5.0,7.1} -- see run_selfish_
# risky.sh's own comment: one row in this region ran >8h at 200 rounds/
# H_max=30 and still hadn't finished). Here numberOfMonteCarloRounds=25 (13%
# of that 200-round baseline), which should proportionally shrink runtime --
# but H_max is raised up to 20x (600 vs 30), and if quiescence is rarely
# reached in this region (the mechanism this job exists to test), tasks at
# the higher H_max levels could run every round out to the full cap, so
# per-round cost is not guaranteed to shrink in proportion to the round-count
# cut alone. Given that residual uncertainty in a corner already known to
# produce multi-hour, non-terminating rounds, 24:00:00 is chosen as
# conservatively generous without defaulting to this project's full 72h
# "genuinely uncertain" convention (see run_convergence_test.sh) -- if a task
# is still running with no sign of finishing well before this ceiling, that
# is itself calibration-relevant information (Phase 3 of the parent task asks
# to flag exactly this).
#
# --cpus-per-task/--mem/--partition unchanged from every other script in this
# project's history: a single row's simulation still uses at most
# monteCarloParallelism=8 cores regardless of round count or H_max.
#
# Output goes to an ISOLATED subpath (${WORKSPACE_PATH}/hmax_calibration/
# hmax<level>_row<row_idx>), distinct from every other batch's output --
# never touches the safe batch's canonical ${WORKSPACE_PATH}/selfish or the
# risky batch's ${WORKSPACE_PATH}/selfish_risky.
#
# Submit directly (no chunking helper needed at only 24 tasks):
#   sbatch sanity_check_run/convergence_test/hmax_calibration/run_hmax_calibration.sh
# ---------------------------------------------------------------------------

CALIBRATION_ROWS_CSV="sanity_check_run/convergence_test/hmax_calibration/calibration_rows.csv"
MODELS_DIR="sampling/generated_models"

ROW_IDX=$(( SLURM_ARRAY_TASK_ID / 4 ))
HMAX_IDX=$(( SLURM_ARRAY_TASK_ID % 4 ))

case "${HMAX_IDX}" in
    0) HMAX_LEVEL=60;  HMAX_CONFIG="sanity_check_run/convergence_test/hmax_calibration/configuration_hmax60.json" ;;
    1) HMAX_LEVEL=150; HMAX_CONFIG="sanity_check_run/convergence_test/hmax_calibration/configuration_hmax150.json" ;;
    2) HMAX_LEVEL=300; HMAX_CONFIG="sanity_check_run/convergence_test/hmax_calibration/configuration_hmax300.json" ;;
    3) HMAX_LEVEL=600; HMAX_CONFIG="sanity_check_run/convergence_test/hmax_calibration/configuration_hmax600.json" ;;
esac

RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi

RESULTS_DIR="${WORKSPACE_PATH}/hmax_calibration/hmax${HMAX_LEVEL}_row${ROW_IDX}"
mkdir -p "${RESULTS_DIR}" logs

TMP_OUT="${TMPDIR}/hmax_calibration_hmax${HMAX_LEVEL}_row${ROW_IDX}_${SLURM_JOB_ID}"
mkdir -p "${TMP_OUT}"

java -Xmx48G \
     -XX:+UseG1GC \
     -XX:ParallelGCThreads=8 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_IDX}.hprof" \
     -jar atosim.jar \
     "${CALIBRATION_ROWS_CSV}" \
     "${MODELS_DIR}" \
     "${HMAX_CONFIG}" \
     --row-index "${ROW_IDX}" \
     --output-dir "${TMP_OUT}"

cp "${TMP_OUT}"/result_run_*.json "${RESULTS_DIR}/" 2>/dev/null || true
