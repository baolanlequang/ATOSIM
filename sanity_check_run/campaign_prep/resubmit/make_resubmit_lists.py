#!/usr/bin/env python3
"""Build the resubmission task lists from missing_configs_<strategy>.csv.

Reads sanity_check_run/campaign_prep/missing_configs_<strategy>.csv (header is
comma-separated, data rows are space-separated because of the known OFS bug in
find_missing_configs.sh -- both separators are accepted per line). Row index =
config_id - 1. Missing row indices are sorted and grouped into runs of
consecutive rows, each run split into tasks of at most --rows-per-task rows.

Writes resubmit_<strategy>.txt (one line per task: "<first_row_index> <row_count>")
into --out-dir and prints, per strategy, the task count and the number of sbatch
submissions needed at --max-array tasks each. Standard library only.
"""
import argparse
import math
import os
import re
import sys

STRATEGIES = ["selfish", "lead_stubborn", "trail_stubborn"]


def read_missing_rows(path):
    with open(path) as f:
        header = f.readline()
        if not header.startswith("config_id"):
            sys.exit("ERROR: {} does not start with a 'config_id' header".format(path))
        rows = set()
        for line in f:
            tok = re.split(r"[,\s]+", line.strip())
            if tok and tok[0]:
                rows.add(int(tok[0]) - 1)
    return sorted(rows)


def chunk(rows, rows_per_task):
    tasks = []
    first, count, prev = None, 0, None
    for r in rows:
        if first is not None and r == prev + 1 and count < rows_per_task:
            count += 1
        else:
            if first is not None:
                tasks.append((first, count))
            first, count = r, 1
        prev = r
    if first is not None:
        tasks.append((first, count))
    return tasks


def main():
    here = os.path.dirname(os.path.abspath(__file__))
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--missing-dir", default=os.path.join(here, ".."))
    ap.add_argument("--out-dir", default=here)
    ap.add_argument("--rows-per-task", type=int, default=50)
    ap.add_argument("--max-array", type=int, default=1001)
    args = ap.parse_args()

    for strat in STRATEGIES:
        src = os.path.join(args.missing_dir, "missing_configs_{}.csv".format(strat))
        rows = read_missing_rows(src)
        tasks = chunk(rows, args.rows_per_task)
        assert sum(c for _, c in tasks) == len(rows)
        dst = os.path.join(args.out_dir, "resubmit_{}.txt".format(strat))
        with open(dst, "w") as f:
            for first, count in tasks:
                f.write("{} {}\n".format(first, count))
        print("{:<15s} missing rows={:>7d}  tasks={:>5d}  sbatch submissions (<= {} tasks each)={}  -> {}".format(
            strat, len(rows), len(tasks), args.max_array,
            math.ceil(len(tasks) / args.max_array), dst))


if __name__ == "__main__":
    main()
