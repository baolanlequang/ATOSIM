#!/bin/bash
# Prints (does NOT run) the sbatch commands to rerun rows 198000-198099 (system 1981, attackers 1-100)
# for selfish and lead_stubborn in chunks of 10 rows, plus one probe command for lead_stubborn row 198051.
# Run from the repo root:   bash sanity_check_run/campaign_prep/resubmit/submit_system1981_commands.sh
#
# Each command exports MEM_GB, XMX_GB, HEAPDUMP, OOM_EXIT and ROW_LIST in the shell and submits with
# --array=0-0 (ROW_LIST replaces the task list; the array id is ignored). No --export flag is used.
# Finished rows are skipped, so reruns are safe. Results go to the production strategy directories in
# atosim_results; per-row status goes to resubmit_logs/<strategy>/row_timing_list_<jobid>_<rows>.csv.
# NOTE: ROW_LIST stays exported in your shell; the final 'unset' lines matter, otherwise a later normal
# run_resubmit_*.sh submission from the same shell would use ROW_LIST instead of the task list.

DIR="sanity_check_run/campaign_prep/resubmit"
FIRST_ROW=198000
LAST_ROW=198099
CHUNK=10
MEM=365
XMX=300

print_env() {
    echo "export MEM_GB=${MEM} XMX_GB=${XMX} HEAPDUMP=off OOM_EXIT=1"
}

echo "mkdir -p logs   # the #SBATCH --output directory must exist at submission time"
echo
for strat in selfish lead_stubborn; do
    echo "# --- ${strat}: rows ${FIRST_ROW}-${LAST_ROW} in chunks of ${CHUNK} rows ---"
    print_env
    for (( start=FIRST_ROW; start<=LAST_ROW; start+=CHUNK )); do
        end=$(( start + CHUNK - 1 )); [ "${end}" -le "${LAST_ROW}" ] || end=${LAST_ROW}
        list="$(seq -s, "${start}" "${end}" | sed "s/,$//")"
        echo "export ROW_LIST=${list}"
        echo "sbatch --array=0-0 --mem=\${MEM_GB}G ${DIR}/run_resubmit_${strat}.sh"
    done
    echo
done

echo "# --- probe: lead_stubborn row 198051 (config_id 198052) alone ---"
print_env
echo "export ROW_LIST=198051"
echo "sbatch --array=0-0 --mem=\${MEM_GB}G ${DIR}/run_resubmit_lead_stubborn.sh"
echo
echo "# --- reset the shell afterwards ---"
echo "unset ROW_LIST HEAPDUMP OOM_EXIT"
