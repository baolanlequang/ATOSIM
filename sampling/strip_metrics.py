#!/usr/bin/env python3
"""
Utilities for ATOSIM result_run_*.json files. Two independent subcommands:

  strip    Remove entries named in REMOVE_NAMES from
           simulationResult.simulationRoundResults[*] and
           simulationResult.averageSimulationRoundResult, and rewrite the JSON.
  extract  Parse raw JSON files AS-IS (no stripping applied) and cache one
           summary row per run (input params + success_rate + attacker block
           rewards) to a Parquet file per strategy. Same schema/logic as
           fanova_two_stage.py --extract.

`strip` and `extract` never affect each other: extract always reads the
original metric set regardless of whether files have been stripped, and
stripping never touches the fields extract depends on (inputParameters,
"Selfish Mining Attack Success", "Attacker Block Rewards").

Usage:
    # strip: by default a dry run reporting projected savings only.
    python3 strip_metrics.py strip --root /path/to/trail_stubborn
    python3 strip_metrics.py strip --root /path/to/trail_stubborn --write --in-place
    python3 strip_metrics.py strip --root /path/to/trail_stubborn --write --out-dir /path/to/trail_stubborn_stripped

    # extract: --root is either one strategy folder (with --strategy set) or a
    # parent directory containing one subfolder per strategy.
    python3 strip_metrics.py extract --root /path/to/results_new --cache ./cache
    python3 strip_metrics.py extract --root /path/to/trail_stubborn --cache ./cache --strategy trail_stubborn
"""

import argparse
import glob
import json
import os
from concurrent.futures import ProcessPoolExecutor, as_completed

REMOVE_NAMES = {
    "AvailabilityScalability",
    "AvailabilitySecurity",
    "Consistency",
    "FaultTolerance",
    "Finney Attack Success",
    "GiniCoefficient",
    "HerfindahlHirschmanIndex",
    "Reliability",
}


# ---------------------------------------------------------------------------
# strip
# ---------------------------------------------------------------------------

def strip(obj):
    sr = obj.get("simulationResult", {})

    rounds = sr.get("simulationRoundResults")
    if rounds:
        sr["simulationRoundResults"] = [
            [m for m in rnd if not (isinstance(m, dict) and m.get("name") in REMOVE_NAMES)]
            for rnd in rounds
        ]

    avg = sr.get("averageSimulationRoundResult")
    if avg:
        sr["averageSimulationRoundResult"] = [
            m for m in avg if not (isinstance(m, dict) and m.get("name") in REMOVE_NAMES)
        ]

    return obj


def _process_file_for_strip(path, out_path, write, compact):
    before = os.path.getsize(path)

    with open(path) as f:
        data = json.load(f)

    stripped = strip(data)
    text = json.dumps(stripped, separators=(",", ":")) if compact else json.dumps(stripped, indent=2)
    after = len(text.encode("utf-8"))

    if write:
        os.makedirs(os.path.dirname(out_path), exist_ok=True)
        tmp_path = out_path + ".tmp"
        with open(tmp_path, "w") as f:
            f.write(text)
        os.replace(tmp_path, out_path)

    return before, after


def run_strip(args):
    if args.write and not args.in_place and not args.out_dir:
        raise SystemExit("strip --write requires either --in-place or --out-dir")

    files = sorted(glob.glob(os.path.join(args.root, "**", args.pattern), recursive=True))
    if not files:
        print(f"No files matching {args.pattern} under {args.root}")
        return

    total_before = total_after = 0
    n = 0

    with ProcessPoolExecutor(max_workers=args.workers) as ex:
        futures = {}
        for path in files:
            if args.in_place:
                out_path = path
            elif args.out_dir:
                rel = os.path.relpath(path, args.root)
                out_path = os.path.join(args.out_dir, rel)
            else:
                out_path = None
            futures[ex.submit(_process_file_for_strip, path, out_path, args.write, args.compact)] = path

        for fut in as_completed(futures):
            before, after = fut.result()
            total_before += before
            total_after += after
            n += 1
            if n % 2000 == 0:
                print(f"  processed {n}/{len(files)} files...")

    pct = 100 * (1 - total_after / total_before) if total_before else 0
    mode = "WROTE" if args.write else "DRY RUN (no files written)"
    print(f"\n{mode}: {n} files")
    print(f"  before: {total_before/1e6:.1f} MB")
    print(f"  after:  {total_after/1e6:.1f} MB")
    print(f"  saved:  {pct:.1f}%")


# ---------------------------------------------------------------------------
# extract  (mirrors fanova_two_stage.py --extract; reads raw JSON, no stripping)
# ---------------------------------------------------------------------------

PARAMS = [
    ("Number of nodes",        "validator_count"),
    ("Node degree",            "node_degree"),
    ("Propagation delay",      "propagation_delay"),
    ("Block interval",         "block_creation_interval"),
    ("Max block size",         "max_block_size"),
    ("Attacker hash power",    "attacker_hash_power"),
    ("Tie-breaking parameter", "tie_breaking_parameter"),
]
PARAM_KEYS = [p[1] for p in PARAMS]

SUCCESS_METRIC      = "Selfish Mining Attack Success"
BLOCK_REWARD_METRIC = "Attacker Block Rewards"

CACHE_COLUMNS = (
    ["system_config_id", "attacker_config_id", "attack_strategy"]
    + PARAM_KEYS
    + ["success_rate", "attacker_blocks_all", "attacker_blocks_success"]
)


def _success_rate(j):
    vals = []
    for rnd in j["simulationResult"].get("simulationRoundResults", []):
        for m in rnd:
            if isinstance(m, dict) and m.get("name") == SUCCESS_METRIC:
                vals.append(float(m["value"]))
                break
    return float("nan") if not vals else sum(vals) / len(vals)


def _block_rewards(j):
    """Mean Attacker Block Rewards over all rounds, and over successful-attack rounds only."""
    all_blocks, success_blocks = [], []
    for rnd in j["simulationResult"].get("simulationRoundResults", []):
        metrics = {m["name"]: m["value"] for m in rnd if isinstance(m, dict)}
        ab = metrics.get(BLOCK_REWARD_METRIC)
        s = metrics.get(SUCCESS_METRIC)
        if ab is None:
            continue
        all_blocks.append(float(ab))
        if s is not None and float(s) == 1.0:
            success_blocks.append(float(ab))
    blocks_all = sum(all_blocks) / len(all_blocks) if all_blocks else float("nan")
    blocks_success = sum(success_blocks) / len(success_blocks) if success_blocks else float("nan")
    return blocks_all, blocks_success


def _parse_file_for_extract(fp):
    """Parse one raw result JSON as-is (no stripping applied). Pickle-safe for ProcessPoolExecutor."""
    try:
        with open(fp) as f:
            j = json.load(f)
        ip = j["inputParameters"]
        row = {key: float(ip[key]) for key in PARAM_KEYS}
        row["system_config_id"]   = str(ip.get("system_config_id", ""))
        row["attacker_config_id"] = str(ip.get("attacker_config_id", ""))
        row["attack_strategy"]    = str(ip.get("attack_strategy", ""))
        row["success_rate"] = _success_rate(j)
        blocks_all, blocks_success = _block_rewards(j)
        row["attacker_blocks_all"]     = blocks_all
        row["attacker_blocks_success"] = blocks_success
        return row
    except Exception:
        return None


def run_extract(args):
    try:
        import pandas as pd
    except ImportError:
        raise SystemExit("extract requires pandas + pyarrow: pip install pandas pyarrow")

    os.makedirs(args.cache, exist_ok=True)

    if args.strategy:
        folders = [(args.strategy, args.root)]
    else:
        folders = sorted(
            (name, os.path.join(args.root, name))
            for name in os.listdir(args.root)
            if os.path.isdir(os.path.join(args.root, name))
            and glob.glob(os.path.join(args.root, name, args.pattern))
        )

    if not folders:
        print(f"No strategy folders with {args.pattern} found under {args.root}")
        return

    for strategy, folder in folders:
        files = sorted(
            glob.glob(os.path.join(folder, args.pattern)),
            key=lambda p: int("".join(c for c in os.path.basename(p) if c.isdigit()) or "0"),
        )
        print(f"\n[{strategy}] Extracting {len(files):,} files ...")

        rows = [None] * len(files)
        with ProcessPoolExecutor(max_workers=args.workers) as ex:
            futures = {ex.submit(_parse_file_for_extract, fp): i for i, fp in enumerate(files)}
            for fut in as_completed(futures):
                rows[futures[fut]] = fut.result()

        rows = [r for r in rows if r is not None]
        df = pd.DataFrame(rows, columns=CACHE_COLUMNS)
        df = df.dropna(subset=["success_rate", "attacker_blocks_all"])

        out_path = os.path.join(args.cache, f"{strategy}.parquet")
        df.to_parquet(out_path, index=False)
        print(f"  -> {len(df):,} valid rows | {out_path} ({os.path.getsize(out_path)/1e6:.1f} MB)")


# ---------------------------------------------------------------------------
# CLI
# ---------------------------------------------------------------------------

def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = ap.add_subparsers(dest="command", required=True)

    sp = sub.add_parser("strip", help="Remove selected metrics and rewrite JSON files")
    sp.add_argument("--root", required=True, help="Directory containing result_run_*.json files (searched recursively)")
    sp.add_argument("--pattern", default="result_run_*.json")
    sp.add_argument("--out-dir", help="Output directory (mirrors --root layout). Required unless --in-place.")
    sp.add_argument("--in-place", action="store_true", help="Overwrite files in --root instead of writing to --out-dir")
    sp.add_argument("--write", action="store_true", help="Actually write output. Without this flag, only reports projected savings.")
    sp.add_argument("--compact", action="store_true", help="Write minified JSON (no whitespace) instead of indent=2")
    sp.add_argument("--workers", type=int, default=os.cpu_count() or 4)

    ep = sub.add_parser("extract", help="Parse raw JSON (no stripping) and cache summary stats to Parquet")
    ep.add_argument("--root", required=True, help="Parent dir with one subfolder per strategy, or a single strategy folder if --strategy is set")
    ep.add_argument("--cache", required=True, help="Output directory for <strategy>.parquet files")
    ep.add_argument("--strategy", help="Treat --root itself as this single strategy's folder")
    ep.add_argument("--pattern", default="result_run_*.json")
    ep.add_argument("--workers", type=int, default=os.cpu_count() or 4)

    args = ap.parse_args()

    if args.command == "strip":
        run_strip(args)
    else:
        run_extract(args)


if __name__ == "__main__":
    main()
