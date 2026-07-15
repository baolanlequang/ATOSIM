#!/bin/bash
# Submits a SLURM array job in chunks, since clusters commonly cap a single
# job's array size (MaxArraySize) well below the number of array tasks needed
# to cover the 250,000 rows in sampling/run_configurations_*.csv. Run
# `scontrol show config | grep MaxArraySize` to find your cluster's real
# limit and pass it as CHUNK_SIZE if it differs from the default used here.
#
# Each array task processes ROWS_PER_TASK consecutive CSV rows in a loop
# (see run_selfish.sh etc.) rather than a single row, so that Slurm tasks run
# for a reasonable wall time instead of finishing in well under a minute -
# bwUniCluster 3.0 admins flagged the previous one-row-per-task design as
# highly inefficient scheduler usage. One "chunk" here is a single sbatch
# call covering up to CHUNK_SIZE array tasks, i.e. up to
# CHUNK_SIZE * ROWS_PER_TASK CSV rows.
#
# MaxArraySize caps the array TASK ID VALUE, not the number of tasks in a
# request - e.g. --array=1000-1999 is rejected under a 1000 cap even though
# it's the same size as --array=0-999, because task id 1999 exceeds the cap.
# So every chunk is submitted with task IDs reset to 0-<chunk_size-1>, and
# the real CSV row offset is passed separately via --export=ROW_OFFSET=<n>.
# The target job script (run_selfish.sh etc.) computes
# ROW_START=$(( ${ROW_OFFSET:-0} + SLURM_ARRAY_TASK_ID * ROWS_PER_TASK )) and
# loops --row-index over ROW_START..ROW_START+ROWS_PER_TASK-1, so it still
# lands on the right CSV rows per chunk.
#
# Firing many sbatch calls back-to-back can hit transient controller load
# ("sbatch: error: Slurm temporarily unable to accept job, sleeping and
# retrying" / "Resource temporarily unavailable"). We've confirmed via
# sacctmgr/squeue this isn't a hard per-user submission cap, just transient
# congestion - so each chunk retries indefinitely with backoff (capped at
# 60s) until it succeeds, rather than giving up. SLEEP_BETWEEN paces
# submissions between chunks to reduce how often this happens at all.
#
# Usage:
#   ./submit_chunked_array.sh <job_script> <total_rows> [chunk_size] [sleep_between] [start_offset] [rows_per_task]
#
# Examples:
#   ./submit_chunked_array.sh run_selfish.sh 250000
#   ./submit_chunked_array.sh run_stubborn_lead.sh 250000 100 2
#   # resume from offset 12000 onward, 250 rows/task:
#   ./submit_chunked_array.sh run_selfish.sh 250000 100 8 12000 250
#
# Ctrl-C (or `kill` if backgrounded) stops it between attempts/chunks - it
# will not stop itself, since every chunk is expected to eventually succeed.

set -uo pipefail

if [ "$#" -lt 2 ]; then
    echo "Usage: $0 <job_script> <total_rows> [chunk_size] [sleep_between] [start_offset] [rows_per_task]" >&2
    exit 1
fi

JOB_SCRIPT="$1"
TOTAL_ROWS="$2"
CHUNK_SIZE="${3:-200}"
SLEEP_BETWEEN="${4:-2}"
START_OFFSET="${5:-0}"
ROWS_PER_TASK="${6:-500}"
MAX_BACKOFF=60

if [ ! -f "$JOB_SCRIPT" ]; then
    echo "Job script not found: $JOB_SCRIPT" >&2
    exit 1
fi

start=$START_OFFSET
while [ "$start" -lt "$TOTAL_ROWS" ]; do
    remaining=$(( TOTAL_ROWS - start ))
    remaining_tasks=$(( (remaining + ROWS_PER_TASK - 1) / ROWS_PER_TASK ))
    tasks=$(( remaining_tasks < CHUNK_SIZE ? remaining_tasks : CHUNK_SIZE ))
    end=$(( tasks - 1 ))
    rows_this_chunk=$(( tasks * ROWS_PER_TASK ))

    attempt=1
    while true; do
        echo "Submitting --array=0-${end} (ROW_OFFSET=${start}, ROWS_PER_TASK=${ROWS_PER_TASK}, ~${rows_this_chunk} rows) for ${JOB_SCRIPT} [attempt ${attempt}]"
        if sbatch --array="0-${end}" --export="ALL,ROW_OFFSET=${start},ROWS_PER_TASK=${ROWS_PER_TASK}" "${JOB_SCRIPT}"; then
            break
        fi
        backoff=$(( attempt * 10 ))
        if (( backoff > MAX_BACKOFF )); then
            backoff=$MAX_BACKOFF
        fi
        echo "  submission failed, retrying in ${backoff}s..." >&2
        sleep "$backoff"
        attempt=$(( attempt + 1 ))
    done

    start=$(( start + rows_this_chunk ))
    sleep "$SLEEP_BETWEEN"
done

echo "All chunks submitted."
