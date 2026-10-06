#!/bin/bash
# Prints (does NOT run) the export and sbatch lines for the resubmission, one block per
# chunk of at most MAX_ARRAY (=1001, the cluster's MaxArraySize) tasks. Run from the repo
# root. Task counts come from resubmit_<strategy>.txt (make_resubmit_lists.py).
# No --export is used anywhere: variables are exported in the shell before sbatch.
#
#   bash sanity_check_run/campaign_prep/resubmit/submit_resubmit_commands.sh
#
# MEM_GB / XMX_GB below are the values PRINTED for selfish and lead_stubborn (the heap
# comes from the environment there); 160 / 140 is a suggestion based on the measured
# working -Xmx140G -- change them as needed. Trail uses a fixed --mem=64gb / -Xmx48G.

MAX_ARRAY=1001
DIR="sanity_check_run/campaign_prep/resubmit"
PRINT_MEM_GB=160
PRINT_XMX_GB=140

print_strategy() {   # strategy needs_env(0|1)
    local strat="$1" needs_env="$2"
    local list="${DIR}/resubmit_${strat}.txt"
    [ -f "${list}" ] || { echo "ERROR: ${list} missing (run make_resubmit_lists.py)" >&2; exit 1; }
    local total offset=0 n
    total=$(wc -l < "${list}" | tr -d ' ')
    echo "# --- ${strat}: ${total} tasks, $(( (total + MAX_ARRAY - 1) / MAX_ARRAY )) submission(s) of <= ${MAX_ARRAY} tasks ---"
    while [ "${offset}" -lt "${total}" ]; do
        n=$(( total - offset )); [ "${n}" -le "${MAX_ARRAY}" ] || n=${MAX_ARRAY}
        if [ "${needs_env}" = "1" ]; then
            echo "export MEM_GB=${PRINT_MEM_GB} XMX_GB=${PRINT_XMX_GB}"
            echo "export TASK_OFFSET=${offset}"
            echo "sbatch --array=0-$(( n - 1 )) --mem=\${MEM_GB}G ${DIR}/run_resubmit_${strat}.sh"
        else
            echo "export TASK_OFFSET=${offset}"
            echo "sbatch --array=0-$(( n - 1 )) ${DIR}/run_resubmit_${strat}.sh"
        fi
        offset=$(( offset + n ))
    done
    echo
}

echo "mkdir -p logs   # the #SBATCH --output directory must exist at submission time"
echo
print_strategy selfish 1
print_strategy lead_stubborn 1
print_strategy trail_stubborn 0
echo "export TASK_OFFSET=0   # reset"
