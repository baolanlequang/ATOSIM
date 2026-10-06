#!/bin/bash
# Identify configs that failed / produced no output in the three full-campaign
# (hmax600) jobs, using only sacct + the filesystem. No JSON is read or
# downloaded. Run by hand on the login node, from the repo root:
#
#   bash sanity_check_run/campaign_prep/find_missing_configs.sh
#   bash sanity_check_run/campaign_prep/find_missing_configs.sh JOB_SELFISH JOB_LEAD JOB_TRAIL
#   bash sanity_check_run/campaign_prep/find_missing_configs.sh -S 2026-09-01 -w /path/to/ws
#
# Positional args (names OR numeric job IDs; give all three or none):
#   1: selfish job   2: lead_stubborn job   3: trail_stubborn job
#   defaults: atosim-selfish-hmax600 atosim-stubborn-lead-hmax600 atosim-stubborn-trail-hmax600
#
# Options:
#   -S <date>   sacct start date (default: 60 days ago; sacct only looks at
#               "today" without -S)
#   -w <path>   results workspace (default: $WORKSPACE_PATH, else `ws_find atosim_results`)
#   -c <dir>    dir holding run_configurations_<strategy>.csv (default: sampling)
#   -o <dir>    output dir (default: sanity_check_run/campaign_prep)
#   -r <n>      ROWS_PER_TASK (default: 500)
#   -O <n>      START_OFFSET passed to submit_chunked_array.sh (default: 0)
#
# Task -> row mapping. submit_chunked_array.sh submits several array jobs per
# strategy (task ids reset to 0..chunk-1, real offset via ROW_OFFSET env var,
# which sacct cannot see). The offset is therefore reconstructed: array jobs
# sharing the job name are ordered by ascending JobID and their task counts
# accumulated, i.e. block = (tasks in earlier array jobs) + task_id, and the
# row range is block*ROWS_PER_TASK .. +ROWS_PER_TASK-1 (0-indexed data rows,
# the same indexing as --row-index). This assumes chunks were submitted in
# order and no same-named resubmissions exist; if not, the filesystem check
# below is still ground truth and the sacct-vs-files mismatch flag will fire.
#
# Output per strategy: missing_configs_<strategy>.csv with every config that
# is in a non-COMPLETED task OR has no non-empty result_run_<config_id>.json:
#   config_id,node_degree,bandwidth,max_block_size,validator_count,sacct_state,has_output
#
# Depends only on: bash, sacct, awk, sed, find, sort, date.

set -u

SINCE="$(date -d '60 days ago' +%Y-%m-%d 2>/dev/null || echo 2026-01-01)"
WS="${WORKSPACE_PATH:-}"
CSV_DIR="sampling"
OUT_DIR="sanity_check_run/campaign_prep"
ROWS=500
START_OFFSET=0

while getopts "S:w:c:o:r:O:h" opt; do
    case "$opt" in
        S) SINCE="$OPTARG" ;;
        w) WS="$OPTARG" ;;
        c) CSV_DIR="$OPTARG" ;;
        o) OUT_DIR="$OPTARG" ;;
        r) ROWS="$OPTARG" ;;
        O) START_OFFSET="$OPTARG" ;;
        h|*) sed -n '2,36p' "$0"; exit 1 ;;
    esac
done
shift $((OPTIND - 1))

JOB_SELFISH="${1:-atosim-selfish-hmax600}"
JOB_LEAD="${2:-atosim-stubborn-lead-hmax600}"
JOB_TRAIL="${3:-atosim-stubborn-trail-hmax600}"

if [ -z "$WS" ]; then
    WS="$(ws_find atosim_results 2>/dev/null)"
fi
if [ -z "$WS" ] || [ ! -d "$WS" ]; then
    echo "ERROR: results workspace not found (use -w <path> or set WORKSPACE_PATH)" >&2
    exit 1
fi
mkdir -p "$OUT_DIR"

TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT

STRATEGIES=(selfish lead_stubborn trail_stubborn)
JOBS=("$JOB_SELFISH" "$JOB_LEAD" "$JOB_TRAIL")

# Select sacct by job name, or by job id when the argument is numeric.
sacct_for() {
    local job="$1"
    local sel="--name=$job"
    case "$job" in ''|*[!0-9]*) ;; *) sel="--jobs=$job" ;; esac
    sacct -n -X -P -S "$SINCE" $sel --format=JobID,JobName,State,ExitCode,Elapsed,MaxRSS
}

for i in 0 1 2; do
    strat="${STRATEGIES[$i]}"
    job="${JOBS[$i]}"
    csv="$CSV_DIR/run_configurations_${strat}.csv"
    out="$OUT_DIR/missing_configs_${strat}.csv"

    echo "=== $strat  (job: $job) ==="
    if [ ! -f "$csv" ]; then
        echo "  ERROR: $csv not found (run from the repo root or pass -c)" >&2
        continue
    fi

    # 1. sacct -> "<block>,<state>" for every array task (all states).
    sacct_for "$job" > "$TMP/sacct.raw" 2>"$TMP/sacct.err"
    if [ ! -s "$TMP/sacct.raw" ]; then
        echo "  WARNING: sacct returned no rows for '$job' since $SINCE" \
             "($(head -c 200 "$TMP/sacct.err"))" >&2
    fi

    awk -F'|' -v rows="$ROWS" -v start_off="$START_OFFSET" '
        function expand(spec, base, state,    n, parts, k, r, lo, hi, t) {
            gsub(/[\[\]]/, "", spec); sub(/%.*/, "", spec)
            n = split(spec, parts, ",")
            for (k = 1; k <= n; k++) {
                if (split(parts[k], r, "-") == 2) { lo = r[1] + 0; hi = r[2] + 0 }
                else { lo = parts[k] + 0; hi = lo }
                for (t = lo; t <= hi; t++) {
                    cnt[base]++
                    if (t + 1 > maxt[base]) maxt[base] = t + 1
                    rec[++nrec] = base SUBSEP t SUBSEP state
                }
            }
        }
        {
            if (index($1, "_") == 0) next
            split($1, a, "_")
            if (!(a[1] in seen)) { seen[a[1]] = 1; bases[++nb] = a[1] + 0 }
            st = $3; sub(/ .*/, "", st)          # "CANCELLED by 123" -> CANCELLED
            expand(a[2], a[1] + 0, st)
        }
        END {
            for (x = 2; x <= nb; x++) {          # numeric insertion sort
                v = bases[x]; y = x - 1
                while (y >= 1 && bases[y] > v) { bases[y+1] = bases[y]; y-- }
                bases[y+1] = v
            }
            acc = start_off / rows
            for (x = 1; x <= nb; x++) {
                off[bases[x]] = acc
                if (x < nb && maxt[bases[x]] != cnt[bases[x]])
                    printf "  WARNING: array job %d has %d tasks but max task id+1=%d (gaps; offsets after it may be off)\n", \
                        bases[x], cnt[bases[x]], maxt[bases[x]] > "/dev/stderr"
                acc += cnt[bases[x]]
            }
            for (k = 1; k <= nrec; k++) {
                split(rec[k], f, SUBSEP)
                print off[f[1] + 0] + f[2] "," f[3]
            }
        }' "$TMP/sacct.raw" | sort -t, -k1,1n > "$TMP/tasks.csv"

    total_tasks=$(wc -l < "$TMP/tasks.csv")
    grep -v ',COMPLETED$' "$TMP/tasks.csv" > "$TMP/failed_blocks.csv"
    failed_tasks=$(wc -l < "$TMP/failed_blocks.csv")

    echo "  sacct: $total_tasks array tasks, $failed_tasks not COMPLETED"
    if [ "$failed_tasks" -gt 0 ]; then
        echo "  state breakdown (non-COMPLETED):"
        cut -d, -f2 "$TMP/failed_blocks.csv" | sort | uniq -c | sed 's/^/    /'
        echo "  failed row ranges (block: first-last):"
        awk -F, -v rows="$ROWS" '{ printf "    task-block %d: rows %d-%d  [%s]\n", $1, $1*rows, $1*rows+rows-1, $2 }' \
            "$TMP/failed_blocks.csv" | head -50
        [ "$failed_tasks" -gt 50 ] && echo "    ... ($((failed_tasks - 50)) more)"
    fi

    # 2. Filesystem: config_ids that have a non-empty result file.
    find "$WS/$strat" -maxdepth 1 -name 'result_run_*.json' -size +0 2>/dev/null \
        | sed -e 's#.*/result_run_##' -e 's#\.json$##' | sort -u > "$TMP/found_ids.txt"
    found=$(wc -l < "$TMP/found_ids.txt")
    echo "  files: $found non-empty result_run_*.json in $WS/$strat"

    # 3. Join CSV rows against failed blocks and found ids.
    awk -F, -v rows="$ROWS" -v out="$out" -v sumf="$TMP/summary.txt" '
        { sub(/\r$/, "") }
        FILENAME == ARGV[1] { found[$1] = 1; next }
        FILENAME == ARGV[2] { gsub(/ /, "_", $2); fb[$1 + 0] = $2; next }
        FNR == 1 {
            for (c = 1; c <= NF; c++) col[$c] = c
            print "config_id,node_degree,bandwidth,max_block_size,validator_count,sacct_state,has_output" > out
            next
        }
        {
            cid = $col["config_id"]
            blk = int((FNR - 2) / rows)
            st  = (blk in fb) ? fb[blk] : "COMPLETED_OR_UNKNOWN"
            sf  = (blk in fb)
            has = (cid in found)
            n++
            if (sf) nsf++
            if (!has) nmiss++
            if (sf && has) sf_but_out++
            if (!sf && !has) ok_but_nofile++
            if (sf || !has) {
                print cid, $col["node_degree"], $col["bandwidth"], $col["max_block_size"], \
                      $col["validator_count"], st, (has ? 1 : 0) > out
            }
        }
        END {
            printf "%d %d %d %d %d\n", n, nsf + 0, nmiss + 0, sf_but_out + 0, ok_but_nofile + 0 > sumf
        }' "$TMP/found_ids.txt" "$TMP/failed_blocks.csv" "$csv"

    read -r n_rows n_sacct_cfg n_nofile sf_but_out ok_but_nofile < "$TMP/summary.txt"

    echo "  CSV rows: $n_rows"
    echo "  configs in non-COMPLETED tasks (sacct):        $n_sacct_cfg"
    echo "  configs with no non-empty result file (files): $n_nofile"
    if [ "$n_sacct_cfg" -eq "$n_nofile" ] && [ "$sf_but_out" -eq 0 ] && [ "$ok_but_nofile" -eq 0 ]; then
        echo "  MATCH: sacct failures and missing outputs agree."
    else
        echo "  MISMATCH:"
        echo "    $sf_but_out configs in a failed task but HAVE output (task failed after writing, or offset mapping is off)"
        echo "    $ok_but_nofile configs in a COMPLETED/unknown task but have NO output (silent loss, purged job, or offset mapping is off)"
    fi
    echo "  -> $out"
    echo
done
