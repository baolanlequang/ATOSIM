#!/usr/bin/env python3
"""
run_selfish_pipeline.py

Combined single-job pipeline for the selfish mining severity analysis.

Runs as ONE SLURM job (no job array). Parallelism across the ~100,000
JSON result files is achieved with a process pool INSIDE this single job
(one job, many worker processes on the same node), instead of splitting
work across many short-lived SLURM array sub-jobs. This avoids the
scheduling overhead of many sub-minute array tasks.

P_ij (attack success rate) uses status == "success" (D5's decisive-reorg
definition, invariant-8 verified): P_ij = mean(1[status_r == "success"]).
The corrected-Eyal-Sirer revenue-share-threshold formula below is no longer
used for P_ij -- confirmed (prior task) numerically identical to the old,
now-superseded JSON "Selfish Mining Attack Success" flag build_analysis_table.py
was fixed to stop using, so it was a redundant re-derivation of the same
stale definition, not an independently meaningful metric. It's kept ONLY
for B_ij/B_bar_i/V_bar_i (the attacker-block-reward-weighted metric,
intentionally kept alongside the D-bar-plus/V_reorg severity metric --
those measure genuinely different things and both stay):

    y_corrected = (revenue_share_i / 100) > attacker_hash_power
    B_ij        = mean(attacker_blocks_i | y_corrected_i) over Monte Carlo rounds

Pipeline stages, all within this one process:

    Stage 1 (parallel, per file) - for every JSON file, extract only the
        needed fields, validate them, compute the corrected Y_ijr and
        the per-(i,j) summary (P_ij, B_ij, n_rounds, n_success). Runs
        across --num-workers worker processes.

    Stage 2 (single-threaded) - merge the ~100,000 per-(i,j) summary
        rows with the system/attack-context parameters from the
        configuration CSV. Writes agg_selfish_full.csv (equivalent to
        the old Step 1 output, already merged).

    Stage 3 (single-threaded) - aggregate over J attacker-context
        configs to system_config_id level: P_bar_i, B_bar_i, V_bar_i,
        with a bootstrap CI for P_bar_i. Writes system_level_selfish.csv
        (equivalent to the old Step 2 output).

Python 3.9
"""

import argparse
import glob
import json
import os
import sys
import time
import traceback
from concurrent.futures import ProcessPoolExecutor, as_completed
from datetime import datetime

# ---------------------------------------------------------------------------
# Dependency check
# ---------------------------------------------------------------------------

REQUIRED_PACKAGES = ["pandas", "numpy"]


def check_dependencies():
    missing = []
    for pkg in REQUIRED_PACKAGES:
        try:
            __import__(pkg)
        except ImportError:
            missing.append(pkg)
    if missing:
        sys.stderr.write(
            "ERROR: missing required Python packages: {}\n".format(", ".join(missing))
        )
        sys.stderr.write("Install with:\n")
        sys.stderr.write("    pip install {}\n".format(" ".join(missing)))
        sys.exit(1)


check_dependencies()

import numpy as np  # noqa: E402
import pandas as pd  # noqa: E402


# ---------------------------------------------------------------------------
# Logging helper
# ---------------------------------------------------------------------------

class TextLogger:
    def __init__(self, log_path):
        self.log_path = log_path
        os.makedirs(os.path.dirname(log_path), exist_ok=True)
        self._fh = open(log_path, "a", encoding="utf-8")

    def write(self, message):
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        line = "[{}] {}".format(timestamp, message)
        print(line)
        self._fh.write(line + "\n")
        self._fh.flush()

    def close(self):
        self._fh.close()


# ---------------------------------------------------------------------------
# Stage 1 worker: process ONE JSON file -> one (i,j) summary row
# Must be a top-level function so it can be pickled for multiprocessing.
# ---------------------------------------------------------------------------

def process_one_file(path):
    """Returns a dict summary for this file, or an error marker."""
    try:
        with open(path, "r", encoding="utf-8") as f:
            data = json.load(f)

        config_id = data.get("config_id")
        input_params = data.get("inputParameters", {})
        attacker_hash_power = float(input_params.get("attacker_hash_power"))

        sim_result = data.get("simulationResult", {})
        round_results = sim_result.get("simulationRoundResults", [])

        # status / attackerCausedChainReorganizationDepths are sibling
        # per-round arrays on simulationResult (not part of the named-metric
        # list above). This is D5's own decisive-reorg SUCCESS definition
        # (attackerCausedChain...[i] > 0 iff status[i] == "success", per
        # invariant 8) -- drives P_ij below and the D-bar-plus severity
        # metric further down. DIFFERENT from the corrected-Eyal-Sirer
        # y_corrected computed below: y_corrected is an economic-
        # profitability threshold (realized revenue share vs. fair
        # hash-power share), while status is a purely mechanical "did the
        # attacker's block win the reorg race" outcome. Empirically these
        # diverge on ~17% of rounds (status==success but y_corrected==0 is
        # common; the reverse is rare) -- y_corrected is kept only for
        # B_ij/B_bar_i/V_bar_i now (see module docstring), not for P_ij.
        status_list = sim_result.get("status", [])
        depth_list = sim_result.get("attackerCausedChainReorganizationDepths", [])

        n_rounds_kept = 0
        n_rounds_dropped = 0
        n_success = 0
        success_block_sum = 0.0

        revenue_share_sum = 0.0
        n_revenue_share_valid = 0

        stale_rate_sum = 0.0
        n_stale_rate_valid = 0

        n_status_success = 0
        n_status_failure = 0
        status_success_depth_sum = 0.0

        for round_index, round_metrics in enumerate(round_results):
            metrics = {m["name"]: m.get("value") for m in round_metrics}

            revenue_share = metrics.get("Attacker Revenue Share")
            attacker_blocks = metrics.get("Attacker Block Rewards")
            total_blocks = metrics.get("Total Block Rewards")
            stale_rate = metrics.get("StaleBlockRate")

            # --- validation (data cleaning) ---
            if revenue_share is None or not (0.0 <= float(revenue_share) <= 100.0):
                n_rounds_dropped += 1
                continue
            if attacker_blocks is None or float(attacker_blocks) < 0:
                n_rounds_dropped += 1
                continue
            if total_blocks is None or float(total_blocks) < 0:
                n_rounds_dropped += 1
                continue

            n_rounds_kept += 1
            revenue_share_sum += float(revenue_share)
            n_revenue_share_valid += 1

            # StaleBlockRate is tracked independently: a round with an
            # invalid/missing stale rate still counts for P_ij/B_ij, it is
            # simply excluded from the stale-rate mean. Stored as a
            # percentage (0-100), converted to a 0-1 fraction below.
            if stale_rate is not None and 0.0 <= float(stale_rate) <= 100.0:
                stale_rate_sum += float(stale_rate) / 100.0
                n_stale_rate_valid += 1

            # --- corrected Eyal-Sirer criterion: used ONLY for B_ij/B_bar_i/
            # V_bar_i (the attacker-block-reward-weighted metric) below, NOT
            # for P_ij anymore -- see module docstring for why.
            y_corrected = 1 if (float(revenue_share) / 100.0) > attacker_hash_power else 0
            if y_corrected == 1:
                n_success += 1
                success_block_sum += float(attacker_blocks)

            # --- status-based success (D5's decisive-reorg definition; see
            # module docstring). Drives P_ij below and Dbar_plus_ij further
            # down. Conditioned on the same n_rounds_kept validity gate as
            # everything else in this loop, so R stays a single agreed-upon
            # per-file round count across every metric this function emits.
            if round_index < len(status_list) and round_index < len(depth_list):
                if status_list[round_index] == "success":
                    n_status_success += 1
                    status_success_depth_sum += float(depth_list[round_index])
                elif status_list[round_index] == "failure":
                    n_status_failure += 1

        # P_ij: attack success rate, status-based (see module docstring).
        # FIX (colleague review, ported from build_analysis_table.py's
        # verified sanity copy: sanity_check_run/03_pipeline_check/
        # pv_fix_sanity/build_analysis_table_sanity.py -- no smoke-test copy
        # of this file exists, so this fix is applied directly here,
        # verified against the same known Fast/Slow expected values used to
        # verify that copy): denominator changed from n_rounds_kept (ALL
        # valid rounds, including unresolved) to (n_status_success +
        # n_status_failure) -- resolved rounds only. Dividing by all rounds
        # implicitly treated every unresolved round as if it were a
        # failure, contradicting D3 ("unresolved episodes must never be
        # counted as failures"). NaN (not 0) when there are zero resolved
        # rounds.
        n_resolved = n_status_success + n_status_failure
        p_ij = (n_status_success / n_resolved) if n_resolved > 0 else np.nan
        # B_ij: still y_corrected-based -- n_success/success_block_sum above
        # are untouched, feeding only B_ij/B_bar_i/V_bar_i now.
        b_ij = (success_block_sum / n_success) if n_success > 0 else np.nan
        revenue_share_mean = (
            (revenue_share_sum / n_revenue_share_valid / 100.0)
            if n_revenue_share_valid > 0
            else np.nan
        )
        stale_block_rate_mean = (
            (stale_rate_sum / n_stale_rate_valid) if n_stale_rate_valid > 0 else np.nan
        )

        # Dbar_plus_ij: mean attacker-caused reorg depth, conditional on
        # status=="success". NaN (not 0) when n_status_success==0 -- same
        # "undefined, not zero" convention as B_ij above, since a pair with
        # no successful rounds has no observed depth to average.
        dbar_plus_ij = (
            (status_success_depth_sum / n_status_success) if n_status_success > 0 else np.nan
        )

        return {
            "ok": True,
            "config_id": config_id,
            "P_ij": p_ij,
            "B_ij": b_ij,
            "Dbar_plus_ij": dbar_plus_ij,
            "revenue_share_mean": revenue_share_mean,
            "stale_block_rate_mean": stale_block_rate_mean,
            "n_rounds": n_rounds_kept,
            "n_rounds_dropped": n_rounds_dropped,
            "n_success": n_success,
            "n_status_success": n_status_success,
            "n_status_failure": n_status_failure,
            "error": None,
            "path": path,
        }

    except Exception as exc:  # noqa: BLE001
        return {
            "ok": False,
            "config_id": None,
            "P_ij": None,
            "B_ij": None,
            "Dbar_plus_ij": None,
            "revenue_share_mean": None,
            "stale_block_rate_mean": None,
            "n_rounds": 0,
            "n_rounds_dropped": 0,
            "n_success": 0,
            "n_status_success": 0,
            "n_status_failure": 0,
            "error": "{}: {}".format(type(exc).__name__, exc),
            "path": path,
        }


# ---------------------------------------------------------------------------
# Stage 1 driver: run process_one_file across all JSON files with a pool
# ---------------------------------------------------------------------------

def run_stage1_parallel(json_files, num_workers, logger, progress_every=10000):
    results = []
    n_done = 0
    n_failed = 0
    t0 = time.time()

    logger.write(
        "Stage 1: processing {} JSON files with {} worker processes".format(
            len(json_files), num_workers
        )
    )

    with ProcessPoolExecutor(max_workers=num_workers) as executor:
        futures = {executor.submit(process_one_file, p): p for p in json_files}
        for future in as_completed(futures):
            res = future.result()
            if not res["ok"]:
                n_failed += 1
                logger.write("WARNING: failed to parse {}: {}".format(res["path"], res["error"]))
            else:
                results.append(res)
            n_done += 1
            if n_done % progress_every == 0:
                elapsed = time.time() - t0
                rate = n_done / elapsed if elapsed > 0 else 0.0
                logger.write(
                    "Stage 1 progress: {}/{} files done ({:.1f} files/s, "
                    "{:.1f} min elapsed)".format(
                        n_done, len(json_files), rate, elapsed / 60.0
                    )
                )

    elapsed = time.time() - t0
    logger.write(
        "Stage 1 done: {} files OK, {} files failed, elapsed {:.1f} min".format(
            len(results), n_failed, elapsed / 60.0
        )
    )
    return results, n_failed


# ---------------------------------------------------------------------------
# Stage 2: merge per-file summaries with configuration parameters
# ---------------------------------------------------------------------------

def build_merged_table(results, config_csv_df, logger):
    rows = [
        {
            "config_id": r["config_id"],
            "P_ij": r["P_ij"],
            "B_ij": r["B_ij"],
            "Dbar_plus_ij": r["Dbar_plus_ij"],
            "revenue_share_mean": r["revenue_share_mean"],
            "stale_block_rate_mean": r["stale_block_rate_mean"],
            "n_rounds": r["n_rounds"],
            "n_success": r["n_success"],
            "n_status_success": r["n_status_success"],
        }
        for r in results
    ]
    summary_df = pd.DataFrame(rows)

    # Normalize config_id dtype on both sides before merging. Without this,
    # a mismatch (e.g. int64 from JSON vs object/string from the CSV, which
    # can happen if the CSV has any blank or non-numeric config_id cells)
    # causes pandas to refuse the merge outright.
    config_csv_df = config_csv_df.copy()
    summary_df["config_id"] = pd.to_numeric(summary_df["config_id"], errors="coerce").astype("Int64")
    config_csv_df["config_id"] = pd.to_numeric(config_csv_df["config_id"], errors="coerce").astype("Int64")

    n_bad_summary = int(summary_df["config_id"].isna().sum())
    n_bad_csv = int(config_csv_df["config_id"].isna().sum())
    if n_bad_summary > 0:
        logger.write(
            "WARNING: {} rows from JSON results had a non-numeric or missing "
            "config_id and will not match any CSV row".format(n_bad_summary)
        )
    if n_bad_csv > 0:
        logger.write(
            "WARNING: {} rows in the configuration CSV had a non-numeric or "
            "missing config_id".format(n_bad_csv)
        )

    merged = summary_df.merge(config_csv_df, on="config_id", how="left")

    n_unmatched = merged["system_config_id"].isna().sum()
    if n_unmatched > 0:
        logger.write(
            "WARNING: {} rows had no matching config_id in the configuration CSV".format(
                n_unmatched
            )
        )

    logger.write("Stage 2 done: merged table has {} rows".format(len(merged)))
    return merged


def check_completeness(merged, expected_i, expected_j, logger):
    n_i = merged["system_config_id"].nunique()
    n_j = merged["attacker_config_id"].nunique()
    n_pairs = len(merged)
    expected_pairs = expected_i * expected_j
    logger.write(
        "Completeness check: {} unique system_config_id (expected {}), "
        "{} unique attacker_config_id (expected {}), "
        "{} pairs (expected {})".format(
            n_i, expected_i, n_j, expected_j, n_pairs, expected_pairs
        )
    )
    if n_pairs != expected_pairs:
        logger.write(
            "WARNING: pair count does not match the full crossed design. "
            "System-level aggregation will proceed using whatever pairs are "
            "present."
        )


# ---------------------------------------------------------------------------
# Stage 3: system-level aggregation (same logic as the former Step 2 script)
# ---------------------------------------------------------------------------

SYSTEM_PARAM_COLS = [
    "validator_count",
    "node_degree",
    "bandwidth",
    "block_creation_interval",
    "max_block_size",
]


def aggregate_system_level(merged, r_rounds, n_bootstrap, seed, logger):
    system_ids = sorted(merged["system_config_id"].unique())
    attacker_ids = sorted(merged["attacker_config_id"].unique())
    n_i = len(system_ids)
    n_j = len(attacker_ids)
    logger.write(
        "Stage 3: building (system_config_id x attacker_config_id) matrices: "
        "{} x {}".format(n_i, n_j)
    )

    p_matrix = merged.pivot(
        index="system_config_id", columns="attacker_config_id", values="P_ij"
    ).reindex(index=system_ids, columns=attacker_ids)
    b_matrix = merged.pivot(
        index="system_config_id", columns="attacker_config_id", values="B_ij"
    ).reindex(index=system_ids, columns=attacker_ids)
    n_success_matrix = merged.pivot(
        index="system_config_id", columns="attacker_config_id", values="n_success"
    ).reindex(index=system_ids, columns=attacker_ids)
    # D-bar-plus severity (status-based success, see process_one_file) --
    # pivoted the same way as the P_ij/B_ij/n_success matrices above. (No
    # separate P_status matrix anymore: P_ij/p_matrix above is already
    # status-based, so it doubles as P_status_bar_i's old data source.)
    dbar_plus_matrix = merged.pivot(
        index="system_config_id", columns="attacker_config_id", values="Dbar_plus_ij"
    ).reindex(index=system_ids, columns=attacker_ids)
    n_status_success_matrix = merged.pivot(
        index="system_config_id", columns="attacker_config_id", values="n_status_success"
    ).reindex(index=system_ids, columns=attacker_ids)

    n_missing_p = int(p_matrix.isna().sum().sum())
    if n_missing_p > 0:
        logger.write(
            "WARNING: {} missing P_ij cells in the pivoted matrix "
            "(incomplete crossed design); excluded via nan-aware "
            "averaging.".format(n_missing_p)
        )

    n_success_arr = n_success_matrix.fillna(0.0).to_numpy()
    b_arr = b_matrix.fillna(0.0).to_numpy()
    p_arr = p_matrix.to_numpy()
    # Raw (NaN-preserving) copy for nanmean-based aggregation (Dbar_plus_i
    # bootstrap CI) -- a 0.0-filled copy is used separately below only for
    # the pooled-sum V_reorg/Dbar_plus_i formula, where it's always paired
    # with n_status_success_arr (also 0 in the same cells), so the fill
    # can't corrupt that specific computation. nanmean must NOT use the
    # filled copy, or an undefined (NaN) pair would be miscounted as a
    # measured zero depth.
    dbar_plus_arr_raw = dbar_plus_matrix.to_numpy()
    dbar_plus_arr = dbar_plus_matrix.fillna(0.0).to_numpy()
    n_status_success_arr = n_status_success_matrix.fillna(0.0).to_numpy()

    j_per_row = (~np.isnan(p_arr)).sum(axis=1)

    p_bar = np.nanmean(p_arr, axis=1)

    sum_success = n_success_arr.sum(axis=1)
    sum_success_times_b = (n_success_arr * b_arr).sum(axis=1)
    with np.errstate(invalid="ignore", divide="ignore"):
        b_bar = np.where(sum_success > 0, sum_success_times_b / sum_success, np.nan)

    j_actual = j_per_row.astype(float)
    with np.errstate(invalid="ignore", divide="ignore"):
        v_bar = sum_success_times_b / (j_actual * r_rounds)

    n_zero_success_configs = int((sum_success == 0).sum())
    if n_zero_success_configs > 0:
        logger.write(
            "Note: {} system configurations had zero successful rounds across "
            "all sampled attacker-context configs.".format(n_zero_success_configs)
        )

    # D-bar-plus / V_reorg: same pooled-round-level pattern as B_bar_i/V_bar_i
    # above, just substituting the status-based success/depth accumulators.
    # (P_bar_i above is already status-based -- see module docstring -- so
    # there's no separate P_status_bar_i here anymore; p_bar/ci_low/ci_high
    # already serve that role.)
    sum_status_success = n_status_success_arr.sum(axis=1)
    sum_status_success_times_d = (n_status_success_arr * dbar_plus_arr).sum(axis=1)
    with np.errstate(invalid="ignore", divide="ignore"):
        dbar_plus_i = np.where(
            sum_status_success > 0, sum_status_success_times_d / sum_status_success, np.nan
        )
    with np.errstate(invalid="ignore", divide="ignore"):
        v_reorg = sum_status_success_times_d / (j_actual * r_rounds)

    n_zero_status_success_configs = int((sum_status_success == 0).sum())
    if n_zero_status_success_configs > 0:
        logger.write(
            "Note: {} system configurations had zero status=='success' rounds "
            "across all sampled attacker-context configs (D-bar-plus "
            "undefined for these).".format(n_zero_status_success_configs)
        )

    logger.write(
        "Stage 3: running bootstrap for P_bar_i / Dbar_plus_i CIs: "
        "{} resamples over {} attacker-context configs "
        "(seed={})".format(n_bootstrap, n_j, seed)
    )
    rng = np.random.default_rng(seed)
    boot_means = np.empty((n_i, n_bootstrap), dtype=float)
    boot_means_d = np.empty((n_i, n_bootstrap), dtype=float)
    for b in range(n_bootstrap):
        col_idx = rng.integers(0, n_j, size=n_j)
        boot_means[:, b] = np.nanmean(p_arr[:, col_idx], axis=1)
        boot_means_d[:, b] = np.nanmean(dbar_plus_arr_raw[:, col_idx], axis=1)

    ci_low = np.percentile(boot_means, 2.5, axis=1)
    ci_high = np.percentile(boot_means, 97.5, axis=1)
    # Plain percentile (not nanpercentile): any NaN draw makes the whole
    # row's CI NaN, so an under-replicated system config (few attacker
    # configs sampled) reports "no CI available" rather than a spuriously
    # precise zero-width interval collapsing to the point estimate.
    dbar_plus_ci_low = np.percentile(boot_means_d, 2.5, axis=1)
    dbar_plus_ci_high = np.percentile(boot_means_d, 97.5, axis=1)

    sys_params = (
        merged[["system_config_id"] + SYSTEM_PARAM_COLS]
        .drop_duplicates(subset=["system_config_id"])
        .set_index("system_config_id")
        .reindex(system_ids)
    )

    result = pd.DataFrame(
        {
            "system_config_id": system_ids,
            "n_attacker_configs_used": j_per_row,
            "P_bar_i": p_bar,  # status-based (see module docstring)
            "P_bar_i_ci_low": ci_low,
            "P_bar_i_ci_high": ci_high,
            "B_bar_i": b_bar,
            "V_bar_i": v_bar,
            # D-bar-plus severity metrics (status-based success -- see
            # process_one_file). Shares P_bar_i above (no separate P here).
            "Dbar_plus_i": dbar_plus_i,
            "Dbar_plus_i_ci_low": dbar_plus_ci_low,
            "Dbar_plus_i_ci_high": dbar_plus_ci_high,
            "V_reorg_i": v_reorg,
        }
    ).set_index("system_config_id")

    result = sys_params.join(result).reset_index()

    logger.write("Stage 3 done: system-level table has {} rows".format(len(result)))
    return result


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main():
    parser = argparse.ArgumentParser(
        description="Single-job pipeline: extract + aggregate + system-level "
        "aggregate for the selfish mining strategy, parallelized internally "
        "with a process pool instead of a SLURM job array."
    )
    parser.add_argument("--json-dir", required=True, help="Directory containing the raw JSON result files")
    parser.add_argument("--json-pattern", default="*.json", help="Glob pattern for JSON files")
    parser.add_argument("--config-csv", required=True, help="Path to run_configurations_selfish.csv")
    parser.add_argument("--output-dir", required=True, help="Directory to write outputs and log")
    parser.add_argument(
        "--num-workers",
        type=int,
        default=None,
        help="Number of worker processes for Stage 1 (default: all available CPUs, "
        "or $SLURM_CPUS_PER_TASK if set)",
    )
    parser.add_argument("--expected-i", type=int, default=500, help="Expected number of system configurations")
    parser.add_argument("--expected-j", type=int, default=200, help="Expected number of attacker-context configurations")
    parser.add_argument("--r-rounds", type=int, default=500, help="Monte Carlo rounds per (i,j) pair")
    parser.add_argument("--n-bootstrap", type=int, default=1000, help="Bootstrap resamples for P_bar_i/Dbar_plus_i CIs")
    parser.add_argument("--seed", type=int, default=42, help="Random seed for bootstrap")
    args = parser.parse_args()

    os.makedirs(args.output_dir, exist_ok=True)
    os.makedirs(os.path.join(args.output_dir, "logs"), exist_ok=True)

    log_path = os.path.join(args.output_dir, "logs", "log_pipeline.txt")
    logger = TextLogger(log_path)

    t_start = time.time()
    logger.write("=== run_selfish_pipeline.py started ===")
    logger.write("Args: {}".format(vars(args)))

    num_workers = args.num_workers
    if num_workers is None:
        num_workers = int(os.environ.get("SLURM_CPUS_PER_TASK", os.cpu_count() or 1))
    logger.write("Using num_workers={}".format(num_workers))

    try:
        all_files = sorted(glob.glob(os.path.join(args.json_dir, args.json_pattern)))
        logger.write("Found {} JSON files matching pattern".format(len(all_files)))
        if not all_files:
            raise RuntimeError("No JSON files found in {}".format(args.json_dir))

        results, n_failed = run_stage1_parallel(all_files, num_workers, logger)
        if not results:
            raise RuntimeError("Stage 1 produced no valid results, aborting.")

        config_csv_df = pd.read_csv(args.config_csv)
        merged = build_merged_table(results, config_csv_df, logger)

        merged_path = os.path.join(args.output_dir, "agg_selfish_full.csv")
        merged.to_csv(merged_path, index=False)
        logger.write("Merged (i,j) table written: {} ({} rows)".format(merged_path, len(merged)))

        check_completeness(merged, args.expected_i, args.expected_j, logger)

        system_level = aggregate_system_level(merged, args.r_rounds, args.n_bootstrap, args.seed, logger)

        system_level_path = os.path.join(args.output_dir, "system_level_selfish.csv")
        system_level.to_csv(system_level_path, index=False)
        logger.write(
            "System-level table written: {} ({} rows)".format(system_level_path, len(system_level))
        )

        logger.write(
            "Summary: mean P_bar_i={:.4f}, mean B_bar_i={:.4f}, mean V_bar_i={:.4f}, "
            "min V_bar_i={:.4f} (system_config_id={}), max V_bar_i={:.4f} (system_config_id={})".format(
                system_level["P_bar_i"].mean(),
                system_level["B_bar_i"].mean(skipna=True),
                system_level["V_bar_i"].mean(),
                system_level["V_bar_i"].min(),
                system_level.loc[system_level["V_bar_i"].idxmin(), "system_config_id"],
                system_level["V_bar_i"].max(),
                system_level.loc[system_level["V_bar_i"].idxmax(), "system_config_id"],
            )
        )
        logger.write(
            "Summary (D-bar-plus severity): mean Dbar_plus_i={:.4f}, "
            "mean V_reorg_i={:.4f}, min V_reorg_i={:.4f} (system_config_id={}), "
            "max V_reorg_i={:.4f} (system_config_id={})".format(
                system_level["Dbar_plus_i"].mean(skipna=True),
                system_level["V_reorg_i"].mean(),
                system_level["V_reorg_i"].min(),
                system_level.loc[system_level["V_reorg_i"].idxmin(), "system_config_id"],
                system_level["V_reorg_i"].max(),
                system_level.loc[system_level["V_reorg_i"].idxmax(), "system_config_id"],
            )
        )

        elapsed = time.time() - t_start
        logger.write(
            "=== Finished OK: {} files failed in Stage 1, total elapsed {:.1f} min ===".format(
                n_failed, elapsed / 60.0
            )
        )

    except Exception:
        logger.write("FATAL ERROR:\n" + traceback.format_exc())
        logger.close()
        sys.exit(1)

    logger.close()


if __name__ == "__main__":
    main()
