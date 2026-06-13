#!/usr/bin/env python3
"""
merge_and_aggregate_system_level.py

Step 2 of the severity-analysis pipeline for the selfish mining strategy.

Merges the per-range result CSVs produced by
extract_and_aggregate_selfish.py (Step 1) into a single
(system_config_id x attacker_config_id) table, then aggregates over the
J=200 attacker-context configurations to produce, for each of the
I=500 system configurations:

    P_bar_i = mean_j(P_ij)
    B_bar_i = weighted mean_j(B_ij), weighted by n_success_ij
    V_bar_i = sum_j(B_ij * n_success_ij) / (J * R)

A bootstrap confidence interval for P_bar_i is also computed by
resampling the J=200 attacker-context configurations with replacement
(the same resample is applied across all system configurations at once,
since all S_i share the same set of A_j by design).

Python 3.9
"""

import argparse
import glob
import os
import sys
import time
import traceback
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
# Merge step
# ---------------------------------------------------------------------------

def merge_result_files(results_dir, pattern, logger):
    paths = sorted(glob.glob(os.path.join(results_dir, pattern)))
    logger.write("Found {} Step 1 result files matching '{}'".format(len(paths), pattern))
    if not paths:
        raise RuntimeError("No result files found in {}".format(results_dir))

    frames = [pd.read_csv(p) for p in paths]
    merged = pd.concat(frames, ignore_index=True)
    logger.write("Merged raw row count: {}".format(len(merged)))

    n_before = len(merged)
    merged = merged.drop_duplicates(subset=["system_config_id", "attacker_config_id"])
    n_dupes = n_before - len(merged)
    if n_dupes > 0:
        logger.write(
            "WARNING: dropped {} duplicate (system_config_id, attacker_config_id) rows "
            "(likely overlapping file ranges from Step 1)".format(n_dupes)
        )

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
            "Downstream aggregation will proceed using whatever pairs are present, "
            "but P_bar_i / B_bar_i / V_bar_i may be based on fewer than J={} "
            "attacker-context configs for some system configs.".format(expected_j)
        )
    return n_pairs == expected_pairs


# ---------------------------------------------------------------------------
# System-level aggregation
# ---------------------------------------------------------------------------

SYSTEM_PARAM_COLS = [
    "validator_count",
    "node_degree",
    "propagation_delay",
    "block_creation_interval",
    "max_block_size",
]


def aggregate_system_level(merged, r_rounds, n_bootstrap, seed, logger):
    system_ids = sorted(merged["system_config_id"].unique())
    attacker_ids = sorted(merged["attacker_config_id"].unique())
    n_i = len(system_ids)
    n_j = len(attacker_ids)
    logger.write(
        "Building (system_config_id x attacker_config_id) matrices: "
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

    n_missing_p = int(p_matrix.isna().sum().sum())
    if n_missing_p > 0:
        logger.write(
            "WARNING: {} missing P_ij cells in the pivoted matrix "
            "(incomplete crossed design); these are excluded from the "
            "per-row mean via nan-aware averaging.".format(n_missing_p)
        )

    n_success_arr = n_success_matrix.fillna(0.0).to_numpy()
    b_arr = b_matrix.fillna(0.0).to_numpy()  # weight is 0 where n_success is 0, so value doesn't matter
    p_arr = p_matrix.to_numpy()

    j_per_row = (~np.isnan(p_arr)).sum(axis=1)

    # --- point estimates ---
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
            "all sampled attacker-context configs (B_bar_i is undefined, "
            "V_bar_i = 0 for these).".format(n_zero_success_configs)
        )

    # --- bootstrap CI for P_bar_i (resample j, same resample applied to all i) ---
    logger.write(
        "Running bootstrap for P_bar_i CI: {} resamples over {} attacker-context "
        "configs (seed={})".format(n_bootstrap, n_j, seed)
    )
    rng = np.random.default_rng(seed)
    boot_means = np.empty((n_i, n_bootstrap), dtype=float)
    for b in range(n_bootstrap):
        col_idx = rng.integers(0, n_j, size=n_j)
        boot_means[:, b] = np.nanmean(p_arr[:, col_idx], axis=1)

    ci_low = np.percentile(boot_means, 2.5, axis=1)
    ci_high = np.percentile(boot_means, 97.5, axis=1)

    # --- assemble output table, carrying over system parameters ---
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
            "P_bar_i": p_bar,
            "P_bar_i_ci_low": ci_low,
            "P_bar_i_ci_high": ci_high,
            "B_bar_i": b_bar,
            "V_bar_i": v_bar,
        }
    ).set_index("system_config_id")

    result = sys_params.join(result).reset_index()

    logger.write("System-level aggregation done: {} rows".format(len(result)))
    return result


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main():
    parser = argparse.ArgumentParser(
        description="Merge Step 1 result files and aggregate to system-config level "
        "(P_bar_i, B_bar_i, V_bar_i) for the selfish mining strategy."
    )
    parser.add_argument(
        "--results-dir", required=True, help="Directory containing Step 1 agg_selfish_*.csv files"
    )
    parser.add_argument(
        "--results-pattern", default="agg_selfish_*.csv", help="Glob pattern for Step 1 result files"
    )
    parser.add_argument("--output-dir", required=True, help="Directory to write merged/system-level CSVs and log")
    parser.add_argument("--expected-i", type=int, default=500, help="Expected number of system configurations")
    parser.add_argument("--expected-j", type=int, default=200, help="Expected number of attacker-context configurations")
    parser.add_argument("--r-rounds", type=int, default=500, help="Monte Carlo rounds per (i,j) pair")
    parser.add_argument("--n-bootstrap", type=int, default=1000, help="Number of bootstrap resamples for P_bar_i CI")
    parser.add_argument("--seed", type=int, default=42, help="Random seed for bootstrap")
    args = parser.parse_args()

    os.makedirs(args.output_dir, exist_ok=True)
    os.makedirs(os.path.join(args.output_dir, "logs"), exist_ok=True)

    log_path = os.path.join(args.output_dir, "logs", "log_step2_merge_aggregate.txt")
    logger = TextLogger(log_path)

    t_start = time.time()
    logger.write("=== merge_and_aggregate_system_level.py started ===")
    logger.write("Args: {}".format(vars(args)))

    try:
        merged = merge_result_files(args.results_dir, args.results_pattern, logger)
        check_completeness(merged, args.expected_i, args.expected_j, logger)

        merged_path = os.path.join(args.output_dir, "merged_agg_selfish.csv")
        merged.to_csv(merged_path, index=False)
        logger.write("Merged (i,j) table written: {} ({} rows)".format(merged_path, len(merged)))

        system_level = aggregate_system_level(
            merged, args.r_rounds, args.n_bootstrap, args.seed, logger
        )

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

        elapsed = time.time() - t_start
        logger.write("=== Finished OK, elapsed {:.1f}s ===".format(elapsed))

    except Exception:
        logger.write("FATAL ERROR:\n" + traceback.format_exc())
        logger.close()
        sys.exit(1)

    logger.close()


if __name__ == "__main__":
    main()
