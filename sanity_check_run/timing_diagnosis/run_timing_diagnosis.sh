#!/bin/bash
#SBATCH --job-name=atosim-timing-diag
#SBATCH --output=logs/atosim-timing-diag-%A_%a.out
#SBATCH --error=logs/atosim-timing-diag-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-5
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=64gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# Per-row timing diagnosis of the TIMEOUT task-blocks (scenario A: a few
# pathological rows vs scenario B: uniformly slow block).
#
# Submit from the repo root (no build step; runs the unmodified production jars):
#   sbatch --export=ALL,TRAIL_START_ROWS=<r1>:<r2>:<r3>:<r4> \
#          sanity_check_run/timing_diagnosis/run_timing_diagnosis.sh
# TRAIL_START_ROWS = four 0-indexed first-row values (colon-separated) of
# 500-row trail_stubborn blocks, i.e. row_start = task_block * 500
# (config_ids row_start+1 .. row_start+500).
#
# Array tasks (one 500-row block each, ROWS_PER_TASK unchanged from production):
#   0  selfish        rows 83000-83499    (config_id 83001-83500)
#   1  lead_stubborn  rows 198000-198499  (config_id 198001-198500)
#   2-5 trail_stubborn  the four TRAIL_START_ROWS blocks, in the order given
#
# --time=72:00:00, NOT 48: the production hmax600 scripts already ran with
# 72:00:00 and these blocks still hit TIMEOUT, so anything shorter could
# time out again before the picture is complete. Override with
# `sbatch --time=...` if the partition allows less/more.
#
# Differences vs run_*_hmax600.sh (none touch simulation inputs or logic):
#   * per-row timing comes from the shell wrapper only (the production jars
#     are used unmodified, so there are no Java-side [ROW-TIMING] lines)
#   * results go to ${WORKSPACE_PATH}/sanity_check_run/timing_diagnosis/<strategy>_<start>/,
#     never to the production per-strategy directories
#   * results are moved to the workspace after EVERY row (production copies
#     only after the whole task, so a TIMEOUT loses everything), so partial
#     progress survives
#   * a shell-level row_timing.csv is appended per row (start/end epoch,
#     elapsed, exit code), independent of the Java [ROW-TIMING] lines
#   * mid-row-kill marker: a "START" line is written BEFORE each java call, so
#     if the task is killed, the last START without a matching END is the row
#     that was running at the time.

ROWS_PER_TASK=${ROWS_PER_TASK:-500}
CONFIG_JSON="sanity_check_run/campaign_prep/configuration_hmax600.json"

case "${SLURM_ARRAY_TASK_ID}" in
    0) STRATEGY=selfish;        JAR_BASE=atosim;                ROW_START=83000  ;;
    1) STRATEGY=lead_stubborn;  JAR_BASE=atosim-stubborn-lead;  ROW_START=198000 ;;
    2|3|4|5)
        STRATEGY=trail_stubborn; JAR_BASE=atosim-stubborn-trail
        if [ -z "${TRAIL_START_ROWS:-}" ]; then
            echo "ERROR: TRAIL_START_ROWS not set (e.g. --export=ALL,TRAIL_START_ROWS=1000:2000:3000:4000)" >&2
            exit 1
        fi
        IFS=':' read -r -a TRAIL <<< "${TRAIL_START_ROWS}"
        if [ "${#TRAIL[@]}" -ne 4 ]; then
            echo "ERROR: TRAIL_START_ROWS must have exactly 4 colon-separated values, got '${TRAIL_START_ROWS}'" >&2
            exit 1
        fi
        ROW_START="${TRAIL[$(( SLURM_ARRAY_TASK_ID - 2 ))]}"
        ;;
    *) echo "ERROR: unexpected array task id ${SLURM_ARRAY_TASK_ID}" >&2; exit 1 ;;
esac
case "${ROW_START}" in ''|*[!0-9]*) echo "ERROR: bad row start '${ROW_START}'" >&2; exit 1 ;; esac
ROW_END=$(( ROW_START + ROWS_PER_TASK - 1 ))

RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi
JAR="${JAR_BASE}.jar"
[ -f "${JAR}" ] || { echo "ERROR: ${JAR} missing (run from the repo root)" >&2; exit 1; }
[ -f "${CONFIG_JSON}" ] || { echo "ERROR: ${CONFIG_JSON} missing (run from repo root)" >&2; exit 1; }

RESULTS_DIR="${WORKSPACE_PATH}/sanity_check_run/timing_diagnosis/${STRATEGY}_${ROW_START}"
mkdir -p "${RESULTS_DIR}" logs
TIMING_CSV="${RESULTS_DIR}/row_timing.csv"
[ -f "${TIMING_CSV}" ] || echo "row_index,start_epoch,end_epoch,elapsed_s,exit_code" > "${TIMING_CSV}"

TMP_OUT="${TMPDIR}/timing_diag_${SLURM_JOB_ID}_${SLURM_ARRAY_TASK_ID}"
mkdir -p "${TMP_OUT}"

echo "[TASK] strategy=${STRATEGY} rows=${ROW_START}-${ROW_END} jar=${JAR} job=${SLURM_JOB_ID}_${SLURM_ARRAY_TASK_ID} node=$(hostname) at=$(date -Is)"

for (( ROW_INDEX=ROW_START; ROW_INDEX<=ROW_END; ROW_INDEX++ )); do
    T0=$(date +%s)
    echo "[ROW START] row_index=${ROW_INDEX} epoch=${T0}"
    java -Xmx48G \
         -XX:+UseG1GC \
         -XX:ParallelGCThreads=8 \
         -XX:+HeapDumpOnOutOfMemoryError \
         -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_INDEX}.hprof" \
         -jar "${JAR}" \
         "sampling/run_configurations_${STRATEGY}.csv" \
         sampling/generated_models \
         "${CONFIG_JSON}" \
         --row-index ${ROW_INDEX} \
         --output-dir "${TMP_OUT}"
    RC=$?
    T1=$(date +%s)
    echo "[ROW END] row_index=${ROW_INDEX} elapsed_s=$(( T1 - T0 )) exit=${RC}"
    echo "${ROW_INDEX},${T0},${T1},$(( T1 - T0 )),${RC}" >> "${TIMING_CSV}"
    # Persist this row's output now so a later TIMEOUT cannot lose it.
    mv "${TMP_OUT}"/result_run_* "${RESULTS_DIR}/" 2>/dev/null || true
done

echo "[TASK DONE] strategy=${STRATEGY} rows=${ROW_START}-${ROW_END} at=$(date -Is)"
