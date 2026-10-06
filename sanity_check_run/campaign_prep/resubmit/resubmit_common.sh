#!/bin/bash
# Shared logic for run_resubmit_<strategy>.sh. SOURCED (not run) from the
# repo root, by the path sanity_check_run/campaign_prep/resubmit/resubmit_common.sh
# (sbatch copies the job script, so a script-relative path would not work).
#
# The caller sets: STRATEGY, JAR, XMX_FLAG (e.g. -Xmx48G), then calls resubmit_main.
#
# Workspace: resolved ONLY here, from `ws_find atosim_results` or the literal
# path below. WORKSPACE_PATH, WS and any other inherited variable are never
# read. RESUBMIT_WS_DIR is assigned unconditionally before use, so an inherited
# value of the same name could not leak in either.

RESUBMIT_WS_NAME="atosim_results"
RESUBMIT_WS_FALLBACK="/pfs/work9/workspace/scratch/ul_kfo10-atosim_results"
RESUBMIT_DIR="sanity_check_run/campaign_prep/resubmit"
CONFIG_JSON="sanity_check_run/campaign_prep/configuration_hmax600.json"

resubmit_fail() { echo "ERROR: $*" >&2; exit 1; }

resubmit_require_mem() {
    # selfish / lead_stubborn only: MEM_GB and XMX_GB come from the environment.
    if [ -z "${MEM_GB:-}" ] || [ -z "${XMX_GB:-}" ]; then
        resubmit_fail "MEM_GB and XMX_GB must both be set, e.g. 'export MEM_GB=160 XMX_GB=140' before sbatch"
    fi
    case "${MEM_GB}${XMX_GB}" in *[!0-9]*) resubmit_fail "MEM_GB='${MEM_GB}' and XMX_GB='${XMX_GB}' must be whole numbers" ;; esac
    [ "${XMX_GB}" -lt "${MEM_GB}" ] || resubmit_fail "XMX_GB (${XMX_GB}) must be smaller than MEM_GB (${MEM_GB})"
    if [ -n "${SLURM_MEM_PER_NODE:-}" ]; then
        [ "${SLURM_MEM_PER_NODE}" -eq $(( MEM_GB * 1024 )) ] \
            || resubmit_fail "SLURM granted ${SLURM_MEM_PER_NODE} MB but MEM_GB=${MEM_GB} (= $(( MEM_GB * 1024 )) MB); submit with --mem=${MEM_GB}G"
    else
        echo "WARNING: SLURM_MEM_PER_NODE is unset; could not confirm the job was submitted with --mem=${MEM_GB}G" >&2
    fi
    XMX_FLAG="-Xmx${XMX_GB}G"
}

resubmit_resolve_workspace() {
    RESUBMIT_WS_DIR=""
    local found=""
    if command -v ws_find >/dev/null 2>&1; then
        found="$(ws_find "${RESUBMIT_WS_NAME}" 2>/dev/null | head -n 1)"
        found="${found//[$'\r\n']/}"
    fi
    if [ -n "${found}" ]; then
        RESUBMIT_WS_DIR="${found}"; RESUBMIT_WS_VIA="ws_find ${RESUBMIT_WS_NAME}"
    else
        RESUBMIT_WS_DIR="${RESUBMIT_WS_FALLBACK}"; RESUBMIT_WS_VIA="literal fallback (ws_find unavailable or returned nothing)"
    fi
    echo "[WORKSPACE] resolved=${RESUBMIT_WS_DIR} via=${RESUBMIT_WS_VIA}"
    [ -e "${RESUBMIT_WS_DIR}" ] || resubmit_fail "resolved workspace '${RESUBMIT_WS_DIR}' does not exist"
    [ -d "${RESUBMIT_WS_DIR}" ] || resubmit_fail "resolved workspace '${RESUBMIT_WS_DIR}' is not a directory"
    case "$(basename "${RESUBMIT_WS_DIR}")" in
        *"${RESUBMIT_WS_NAME}") ;;
        *) resubmit_fail "basename of '${RESUBMIT_WS_DIR}' does not end with '${RESUBMIT_WS_NAME}'" ;;
    esac
}

resubmit_main() {
    local dry="${DRY_RUN:-0}"
    local task_offset="${TASK_OFFSET:-0}"
    local list_file="${RESUBMIT_DIR}/resubmit_${STRATEGY}.txt"
    local csv="sampling/run_configurations_${STRATEGY}.csv"

    case "${task_offset}" in ''|*[!0-9]*) resubmit_fail "TASK_OFFSET='${task_offset}' is not a whole number" ;; esac

    resubmit_resolve_workspace
    # Single source of truth for every destination below.
    local dest_dir="${RESUBMIT_WS_DIR}/${STRATEGY}"
    local log_dir="${RESUBMIT_WS_DIR}/resubmit_logs/${STRATEGY}"

    [ -f "${JAR}" ] || resubmit_fail "${JAR} missing (run from the repo root)"
    [ -f "${CONFIG_JSON}" ] || resubmit_fail "${CONFIG_JSON} missing (run from the repo root)"
    [ -f "${csv}" ] || resubmit_fail "${csv} missing"
    [ -d sampling/generated_models ] || resubmit_fail "sampling/generated_models missing"
    [ -f "${list_file}" ] || resubmit_fail "${list_file} missing (run make_resubmit_lists.py)"
    [ -n "${TMPDIR:-}" ] && [ -d "${TMPDIR}" ] || resubmit_fail "TMPDIR is unset or not a directory"

    # Strategy destination: the production scripts do `mkdir -p "${RESULTS_DIR}"`
    # on the same <workspace>/<strategy> path, so creating it is consistent.
    if [ -d "${dest_dir}" ]; then
        echo "[DEST] ${dest_dir} exists"
    elif [ "${dry}" = "1" ]; then
        echo "[DEST] ${dest_dir} does not exist (would be created, as the production scripts do with mkdir -p)"
    else
        mkdir -p "${dest_dir}" || resubmit_fail "cannot create ${dest_dir}"
        echo "[DEST] ${dest_dir} created"
    fi

    local array_id="${SLURM_ARRAY_TASK_ID:-0}"
    local task_id=$(( array_id + task_offset ))
    local line first_row count
    line="$(sed -n "$(( task_id + 1 ))p" "${list_file}")"
    [ -n "${line}" ] || resubmit_fail "task ${task_id} (array id ${array_id} + TASK_OFFSET ${task_offset}) is beyond the end of ${list_file} ($(wc -l < "${list_file}") tasks)"
    read -r first_row count <<< "${line}"
    case "${first_row}${count}" in ''|*[!0-9]*) resubmit_fail "bad line '${line}' in ${list_file}" ;; esac
    local last_row=$(( first_row + count - 1 ))

    local row_timing="${log_dir}/row_timing_task${task_id}.csv"
    local tmp_out="${TMPDIR}/resubmit_${STRATEGY}_${SLURM_JOB_ID:-dry}_${array_id}"

    echo "[TASK] strategy=${STRATEGY} task_id=${task_id} (array=${array_id} offset=${task_offset}) rows=${first_row}-${last_row} config_ids=$(( first_row + 1 ))-$(( last_row + 1 )) jar=${JAR} xmx=${XMX_FLAG} job=${SLURM_JOB_ID:-none} node=$(hostname) at=$(date -Is)"
    echo "[DEST] results=${dest_dir} row_timing=${row_timing}"

    java_cmd() {
        JAVA_CMD=(java "${XMX_FLAG}"
            -XX:+UseG1GC
            -XX:ParallelGCThreads=8
            -XX:+HeapDumpOnOutOfMemoryError
            "-XX:HeapDumpPath=${tmp_out}/heapdump_row$1.hprof"
            -jar "${JAR}"
            "${csv}"
            sampling/generated_models
            "${CONFIG_JSON}"
            --row-index "$1"
            --output-dir "${tmp_out}")
    }

    if [ "${dry}" = "1" ]; then
        local present=0 r
        for (( r=first_row; r<=last_row; r++ )); do
            [ -s "${dest_dir}/result_run_$(( r + 1 )).json" ] && present=$(( present + 1 ))
        done
        echo "[DRY_RUN] ${present}/${count} rows of this task already have a non-empty result in ${dest_dir} (would be skipped)"
        java_cmd "${first_row}"
        echo "[DRY_RUN] java command for the first row:"
        printf '  %q' "${JAVA_CMD[@]}"; echo
        echo "[DRY_RUN] exiting without starting java"
        exit 0
    fi

    mkdir -p "${log_dir}" "${tmp_out}" logs || resubmit_fail "cannot create ${log_dir} / ${tmp_out}"
    [ -f "${row_timing}" ] || echo "job_id,task_id,row_index,config_id,start_epoch,end_epoch,elapsed_s,exit_code,status" > "${row_timing}"

    local i row config_id dest_file t0 t1 rc status f base
    for (( i=0; i<count; i++ )); do
        row=$(( first_row + i )); config_id=$(( row + 1 ))
        dest_file="${dest_dir}/result_run_${config_id}.json"
        t0=$(date +%s)
        if [ -s "${dest_file}" ]; then
            echo "[ROW SKIP] row_index=${row} config_id=${config_id}: non-empty result already in ${dest_dir}"
            echo "${SLURM_JOB_ID:-},${task_id},${row},${config_id},${t0},${t0},0,0,skipped_exists" >> "${row_timing}"
            continue
        fi
        rm -f "${tmp_out}"/result_run_*
        echo "[ROW START] row_index=${row} config_id=${config_id} epoch=${t0}"
        java_cmd "${row}"
        "${JAVA_CMD[@]}"
        rc=$?
        t1=$(date +%s)
        status=no_output
        if [ -s "${tmp_out}/result_run_${config_id}.json" ]; then
            status=ok
            # Siblings first, the .json last, so the .json's presence means the row is complete.
            # Copy to a hidden temp name in the destination, then rename (atomic on one filesystem).
            for f in "${tmp_out}"/result_run_${config_id}.*; do
                [ "$(basename "${f}")" = "result_run_${config_id}.json" ] && continue
                base="$(basename "${f}")"
                cp "${f}" "${dest_dir}/.part_${base}" && mv -f "${dest_dir}/.part_${base}" "${dest_dir}/${base}" || status=copy_failed
            done
            cp "${tmp_out}/result_run_${config_id}.json" "${dest_dir}/.part_result_run_${config_id}.json" \
                && mv -f "${dest_dir}/.part_result_run_${config_id}.json" "${dest_file}" || status=copy_failed
        fi
        echo "[ROW END] row_index=${row} config_id=${config_id} elapsed_s=$(( t1 - t0 )) exit=${rc} status=${status}"
        echo "${SLURM_JOB_ID:-},${task_id},${row},${config_id},${t0},${t1},$(( t1 - t0 )),${rc},${status}" >> "${row_timing}"
        [ "${status}" = "ok" ] && rm -f "${tmp_out}"/result_run_*
    done

    echo "[TASK DONE] strategy=${STRATEGY} task_id=${task_id} rows=${first_row}-${last_row} at=$(date -Is)"
}
