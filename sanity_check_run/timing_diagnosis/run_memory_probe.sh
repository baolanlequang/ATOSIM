#!/bin/bash
#SBATCH --job-name=atosim-memory-probe
#SBATCH --output=logs/atosim-memory-probe-%j.out
#SBATCH --error=logs/atosim-memory-probe-%j.err
#SBATCH --partition=cpu
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# Memory probe for selfish row 83000 (config_id=83001, sys-831): was stuck in GC
# thrash for 35 h at -Xmx48G / --mem=64G / monteCarloParallelism=8. Records a GC
# log and a periodic live-object class histogram so the heap growth can be
# attributed (rounds accumulating results vs. 8 concurrent rounds).
#
# Memory and heap come from the environment variables MEM_GB and XMX_GB.
# #SBATCH directives are not expanded by the shell, so there is deliberately NO
# --mem line in this header: pass it on the sbatch command line (not --export):
#
#   export MEM_GB=64 XMX_GB=48
#   sbatch --mem=${MEM_GB}G sanity_check_run/timing_diagnosis/run_memory_probe.sh
#
# The job checks that SLURM granted exactly MEM_GB and that XMX_GB < MEM_GB, so a
# forgotten --mem fails loudly instead of silently running with the default.
#
# Everything else matches run_timing_diagnosis.sh / run_selfish_hmax600.sh:
# atosim.jar, sanity_check_run/campaign_prep/configuration_hmax600.json (so
# monteCarloParallelism and every simulation input are unchanged), the same CSV
# and --row-index handling, same JVM flags except -Xmx, results moved after the row.
#
# Output (all under one directory per submission):
#   ${WORKSPACE_PATH}/sanity_check_run/timing_diagnosis/memory_probe_selfish_83000_mem<MEM>_xmx<XMX>_<jobid>/
#     gc.log (+ rotated gc.log.0..), histo.log, row_timing.csv, result_run_83001.json

STRATEGY=selfish
JAR="atosim.jar"
CONFIG_JSON="sanity_check_run/campaign_prep/configuration_hmax600.json"
ROW_INDEX=83000
HISTO_INTERVAL_S=1200

if [ -z "${MEM_GB:-}" ] || [ -z "${XMX_GB:-}" ]; then
    echo "ERROR: MEM_GB and XMX_GB must both be set, e.g. 'export MEM_GB=64 XMX_GB=48' before sbatch" >&2
    exit 1
fi
case "${MEM_GB}${XMX_GB}" in *[!0-9]*) echo "ERROR: MEM_GB='${MEM_GB}' and XMX_GB='${XMX_GB}' must be whole numbers" >&2; exit 1 ;; esac
if [ "${XMX_GB}" -ge "${MEM_GB}" ]; then
    echo "ERROR: XMX_GB (${XMX_GB}) must be smaller than MEM_GB (${MEM_GB})" >&2
    exit 1
fi
if [ -n "${SLURM_MEM_PER_NODE:-}" ]; then
    if [ "${SLURM_MEM_PER_NODE}" -ne $(( MEM_GB * 1024 )) ]; then
        echo "ERROR: SLURM granted ${SLURM_MEM_PER_NODE} MB but MEM_GB=${MEM_GB} (= $(( MEM_GB * 1024 )) MB); submit with --mem=${MEM_GB}G" >&2
        exit 1
    fi
else
    echo "WARNING: SLURM_MEM_PER_NODE is unset; could not confirm the job was submitted with --mem=${MEM_GB}G" >&2
fi

RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
if [ -z "${WORKSPACE_PATH}" ]; then
    echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found. Allocate it first: ws_allocate ${RESULTS_WORKSPACE} <days>" >&2
    exit 1
fi
[ -f "${JAR}" ] || { echo "ERROR: ${JAR} missing (run from the repo root)" >&2; exit 1; }
[ -f "${CONFIG_JSON}" ] || { echo "ERROR: ${CONFIG_JSON} missing (run from the repo root)" >&2; exit 1; }

OUT_DIR="${WORKSPACE_PATH}/sanity_check_run/timing_diagnosis/memory_probe_${STRATEGY}_${ROW_INDEX}_mem${MEM_GB}_xmx${XMX_GB}_${SLURM_JOB_ID}"
mkdir -p "${OUT_DIR}" logs
TIMING_CSV="${OUT_DIR}/row_timing.csv"
HISTO_LOG="${OUT_DIR}/histo.log"
JOB_STDOUT="logs/atosim-memory-probe-${SLURM_JOB_ID}.out"
echo "row_index,start_epoch,end_epoch,elapsed_s,exit_code" > "${TIMING_CSV}"

TMP_OUT="${TMPDIR}/memory_probe_${SLURM_JOB_ID}"
mkdir -p "${TMP_OUT}"

echo "[TASK] strategy=${STRATEGY} row_index=${ROW_INDEX} jar=${JAR} mem_gb=${MEM_GB} xmx_gb=${XMX_GB} job=${SLURM_JOB_ID} node=$(hostname) out=${OUT_DIR} at=$(date -Is)"

# Histogram sampler. Runs sequentially in ONE background loop, so a new histogram
# can never start while the previous one is still running. The sleep is chopped
# into 10 s steps so the loop exits promptly once the JVM is gone.
histo_loop() {
    local pid="$1" waited=0 tool=""
    if command -v jmap >/dev/null 2>&1; then
        tool="jmap"
    elif command -v jcmd >/dev/null 2>&1; then
        tool="jcmd"
    else
        echo "$(date -Is) neither jmap nor jcmd on PATH; no histograms will be taken" >> "${HISTO_LOG}"
        return
    fi
    while kill -0 "${pid}" 2>/dev/null; do
        sleep 10
        waited=$(( waited + 10 ))
        [ "${waited}" -ge "${HISTO_INTERVAL_S}" ] || continue
        waited=0
        kill -0 "${pid}" 2>/dev/null || break
        {
            echo "===== $(date -Is) pid=${pid} tool=${tool} rss_kb=$(ps -o rss= -p "${pid}" 2>/dev/null | tr -d ' ') ====="
            echo "latest round line: $(grep 'Monte Carlo round' "${JOB_STDOUT}" 2>/dev/null | tail -n 1)"
            if [ "${tool}" = "jmap" ]; then
                timeout 3600 jmap -histo:live "${pid}" 2>&1 | head -30
            else
                timeout 3600 jcmd "${pid}" GC.class_histogram 2>&1 | head -30
            fi
            echo
        } >> "${HISTO_LOG}"
        # the histogram duration counts towards the next interval only after it
        # finishes, so samples are always >= HISTO_INTERVAL_S apart
    done
}

T0=$(date +%s)
echo "[ROW START] row_index=${ROW_INDEX} epoch=${T0}"
java -Xmx${XMX_GB}G \
     -XX:+UseG1GC \
     -XX:ParallelGCThreads=8 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath="${TMP_OUT}/heapdump_row${ROW_INDEX}.hprof" \
     "-Xlog:gc*:file=${OUT_DIR}/gc.log:time,uptime:filecount=5,filesize=50m" \
     -jar "${JAR}" \
     "sampling/run_configurations_${STRATEGY}.csv" \
     sampling/generated_models \
     "${CONFIG_JSON}" \
     --row-index ${ROW_INDEX} \
     --output-dir "${TMP_OUT}" &
JAVA_PID=$!

histo_loop "${JAVA_PID}" &
MONITOR_PID=$!
trap 'kill "${MONITOR_PID}" 2>/dev/null' EXIT

wait "${JAVA_PID}"
RC=$?
kill "${MONITOR_PID}" 2>/dev/null
wait "${MONITOR_PID}" 2>/dev/null

T1=$(date +%s)
echo "[ROW END] row_index=${ROW_INDEX} elapsed_s=$(( T1 - T0 )) exit=${RC}"
echo "${ROW_INDEX},${T0},${T1},$(( T1 - T0 )),${RC}" >> "${TIMING_CSV}"
# Persist this row's output now (same as run_timing_diagnosis.sh).
mv "${TMP_OUT}"/result_run_* "${OUT_DIR}/" 2>/dev/null || true

echo "[TASK DONE] strategy=${STRATEGY} row_index=${ROW_INDEX} at=$(date -Is)"
