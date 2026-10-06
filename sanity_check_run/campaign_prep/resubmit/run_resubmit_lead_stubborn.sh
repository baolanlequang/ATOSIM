#!/bin/bash
#SBATCH --job-name=atosim-resubmit-lead_stubborn
#SBATCH --output=logs/atosim-resubmit-lead_stubborn-%A_%a.out
#SBATCH --error=logs/atosim-resubmit-lead_stubborn-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --time=72:00:00

# Resubmission of the TIMEOUT rows of the hmax600 campaign for lead_stubborn
# (task list: resubmit_lead_stubborn.txt, built by make_resubmit_lists.py; <= 50 rows/task).
# Task = SLURM_ARRAY_TASK_ID + TASK_OFFSET (TASK_OFFSET from the environment, default 0).
# Each row's result is moved to <workspace>/lead_stubborn/ immediately after the row, a
# non-empty existing result is skipped (reruns are safe), and a per-task
# row_timing_task<N>.csv goes to <workspace>/resubmit_logs/lead_stubborn/.
# The workspace is resolved inside resubmit_common.sh (ws_find atosim_results, else a
# literal path); no inherited WORKSPACE_PATH/WS is read. DRY_RUN=1 prints the plan and
# exits before java:  MEM_GB=160 XMX_GB=140 DRY_RUN=1 bash sanity_check_run/campaign_prep/resubmit/run_resubmit_lead_stubborn.sh
# No --mem is in this header when the heap comes from the environment; see submit_resubmit_commands.sh.
#
# Differences vs run_stubborn_lead_hmax600.sh: no --array (given on the sbatch command line), no mail
# directives (up to ~2,300 array tasks would send thousands of mails), per-row result move.

STRATEGY=lead_stubborn
JAR=atosim-stubborn-lead.jar
COMMON="sanity_check_run/campaign_prep/resubmit/resubmit_common.sh"
[ -f "${COMMON}" ] || { echo "ERROR: ${COMMON} not found (submit/run from the repo root)" >&2; exit 1; }
source "${COMMON}"

resubmit_require_mem
resubmit_main
