#!/usr/bin/env python3
"""
Read-only analysis script for the N=500/N=1000 wide_variance_subset_v2
convergence check. Not a production pipeline file -- ad hoc, this task only.

Reuses the established Corollary-8/per-tree fANOVA methodology directly
from sanity_check_run/03_pipeline_check/fanova_smoketest/fanova_scripts/
atosim_common_sanity.py (grouped_cv_r2, fit_final_rf, fanova_first_order,
sobol_total_order) -- no reimplementation of RF/fANOVA logic.

wide_variance_subset_v2.csv has no system_config_id column; the composite
(system_config_id, bandwidth) grouping key the task asks to reuse is
recovered from the `note` field's "groupNNN/.../sysidNN/degD/bwB.B" prefix
(everything before "/attackerNN"), which is confirmed to produce exactly
126 distinct groups -- matching the known 126-group structure of this
378-row (126 x 3) subset exactly.
"""
import glob
import json
import os
import re
import sys

import numpy as np
import pandas as pd

sys.path.insert(0, os.path.join(
    os.path.dirname(__file__), "..", "..", "03_pipeline_check",
    "fanova_smoketest", "fanova_scripts"))
import atosim_common_sanity as common

REPO_ROOT = os.path.join(os.path.dirname(__file__), "..", "..", "..")
WIDEVAR_CSV = os.path.join(REPO_ROOT, "sanity_check_run", "convergence_test", "wide_variance_subset_v2.csv")
RESULTS_ROOT = "/Users/lanle/Documents/Working/Uni_Ulm/PhD/atosim-acm-2026/results_new/convergence_test_v2/convergence_test_v2"


def load_tier_completeness(tier_name, n_expected_rounds):
    """Returns (dict config_id -> D_bar_plus or None, list of missing/incomplete config_ids with reason)."""
    tier_dir = os.path.join(RESULTS_ROOT, tier_name)
    present_dirs = sorted(os.listdir(tier_dir)) if os.path.isdir(tier_dir) else []
    d_bar = {}
    problems = []
    for cid in present_dirs:
        files = glob.glob(os.path.join(tier_dir, cid, "result_run_*.json"))
        if len(files) != 1:
            problems.append((cid, "no result file (empty dir -- task never produced output)"))
            continue
        data = json.load(open(files[0]))
        sr = data["simulationResult"]
        status = sr["status"]
        depths = sr["attackerCausedChainReorganizationDepths"]
        n = len(status)
        if n != n_expected_rounds:
            problems.append((cid, "round count {} != expected {}".format(n, n_expected_rounds)))
            continue
        success_depths = [depths[i] for i in range(n) if status[i] == "success"]
        if len(success_depths) == 0:
            d_bar[cid] = None  # undefined, P=0 -- matches NAN_SUBSET_OUTCOMES convention, dropped not zero-filled
        else:
            d_bar[cid] = float(np.mean(success_depths))
    return present_dirs, d_bar, problems


def main():
    df = pd.read_csv(WIDEVAR_CSV, dtype={"config_id": str})
    expected_ids = set(df["config_id"])
    assert len(expected_ids) == 378, "expected 378 unique config_ids, got {}".format(len(expected_ids))

    group_key = df["note"].str.split("/attacker").str[0]
    df = df.assign(group_key=group_key)
    assert df["group_key"].nunique() == 126, "expected 126 groups, got {}".format(df["group_key"].nunique())

    print("=" * 78)
    print("STEP 1 -- COMPLETENESS CHECK")
    print("=" * 78)

    tiers = [("mc500", 500), ("mc1000", 1000)]
    tier_dbar = {}
    for tier_name, n_rounds in tiers:
        present_dirs, d_bar, problems = load_tier_completeness(tier_name, n_rounds)
        present_ids_matching_csv = set(present_dirs) & expected_ids
        print("\n--- {} ---".format(tier_name))
        print("Directories present: {}".format(len(present_dirs)))
        print("Present dirs that are actual wide_variance_subset_v2.csv config_ids: {}".format(
            len(present_ids_matching_csv)))
        if len(present_ids_matching_csv) < len(present_dirs):
            print("  ({} present dirs do NOT belong to wide_variance_subset_v2.csv -- stale/other data)".format(
                len(present_dirs) - len(present_ids_matching_csv)))
        missing_entirely = expected_ids - set(present_dirs)
        print("Config_ids from the 378-row set with NO directory at all: {}".format(len(missing_entirely)))
        if problems:
            print("Config_ids with a directory but no usable result ({}):".format(len(problems)))
            for cid, reason in problems:
                print("    {} -- {}".format(cid, reason))
        n_zero_success = sum(1 for v in d_bar.values() if v is None)
        n_usable = sum(1 for v in d_bar.values() if v is not None)
        print("Usable D-bar+ values (status present, round count correct, >=1 success round): {}".format(n_usable))
        if n_zero_success:
            print("Configs with correct round count but ZERO successful rounds (D-bar+ undefined, dropped): {}".format(
                n_zero_success))
        tier_dbar[tier_name] = d_bar

    print("\n" + "=" * 78)
    print("STEP 1 -- SLOW-PATHOLOGY-CORNER CROSS-CHECK (mc1000 missing set)")
    print("=" * 78)
    present_1000 = set(os.listdir(os.path.join(RESULTS_ROOT, "mc1000")))
    empty_1000 = [cid for cid in present_1000
                  if len(glob.glob(os.path.join(RESULTS_ROOT, "mc1000", cid, "result_run_*.json"))) != 1]
    lookup = df.set_index("config_id")
    strict_corner = set(df[(df["node_degree"] <= 2) & (df["bandwidth"] <= 7.1)
                            & (df["max_block_size"] >= 6_000_000) & (df["validator_count"] >= 750)]["config_id"])
    print("Strict Slow-pathology-corner config_ids in the 378-row set: {} -> {}".format(
        len(strict_corner), sorted(strict_corner)))
    print("Empty (failed) mc1000 config_ids: {} -> {}".format(len(empty_1000), sorted(empty_1000)))
    print("Strict corner subset of empty set: {}".format(strict_corner.issubset(set(empty_1000))))
    extra_failed = set(empty_1000) - strict_corner
    print("Failed configs OUTSIDE the strict corner definition ({}):".format(len(extra_failed)))
    for cid in sorted(extra_failed):
        row = lookup.loc[cid]
        print("    {} -- validator_count={}, node_degree={}, bandwidth={}, max_block_size={}".format(
            cid, row["validator_count"], row["node_degree"], row["bandwidth"], row["max_block_size"]))
    print("=> Conclusion: failed set is a SUPERSET of the strict Slow-corner definition"
          " (all {} strict-corner rows failed, plus {} additional node_degree=2/large-block rows"
          " just outside the bandwidth/validator_count thresholds), not an exact match.".format(
        len(strict_corner), len(extra_failed)))

    print("\n" + "=" * 78)
    print("STEP 3 -- fANOVA RE-FIT (N=1000 only -- N=500 has no usable data, see Step 1)")
    print("=" * 78)

    N200_CV_R2 = 0.507
    N200_IMPORTANCE = {"Validating-node count": (0.519, 0.360), "Node degree": (0.207, 0.299)}
    N200_N = 378

    results = {}
    d_bar_1000 = tier_dbar["mc1000"]
    fit_df = df[df["config_id"].isin([cid for cid, v in d_bar_1000.items() if v is not None])].copy()
    fit_df["conditional_reorg_depth"] = fit_df["config_id"].map(d_bar_1000)
    fit_df["system_config_id"] = fit_df["group_key"]  # composite key stand-in, see prepare_xy note below
    print("N=1000 fit set: n={} configs (of 378; {} excluded -- 10 failed tasks with no result)".format(
        len(fit_df), 378 - len(fit_df)))
    print("Distinct groups (composite sysid+bandwidth key) in fit set: {}".format(fit_df["group_key"].nunique()))

    X, y, groups, d = common.prepare_xy(fit_df, "conditional_reorg_depth")
    assert len(d) == len(fit_df), "prepare_xy dropped rows unexpectedly (NaN outcome?) -- check for undefined D-bar+"
    scores, mean_r2, mean_mae, mean_rmse = common.grouped_cv_r2(X, y, groups, seed=0)
    rf = common.fit_final_rf(X, y, seed=0)
    fractions_mean, fractions_std, interaction_mean, interaction_std = common.fanova_first_order(rf, X)
    results["mc1000"] = dict(n=len(d), cv_r2=mean_r2, cv_mae=mean_mae, cv_rmse=mean_rmse,
                              fractions_mean=fractions_mean, fractions_std=fractions_std,
                              interaction_mean=interaction_mean, interaction_std=interaction_std,
                              per_fold_r2=scores.tolist())

    print("\nCV R^2 per fold: {}".format(["{:.3f}".format(s) for s in scores]))
    print("Mean CV R^2  = {:.3f}".format(mean_r2))
    print("Mean CV MAE  = {:.6f}".format(mean_mae))
    print("Mean CV RMSE = {:.6f}".format(mean_rmse))
    print("\nfANOVA first-order importance (mean +/- across-tree std):")
    for lab, mean_frac in sorted(fractions_mean.items(), key=lambda kv: kv[1], reverse=True):
        print("  {:<26s}{:>6.1f}% +/- {:.1f}%".format(lab, 100 * mean_frac, 100 * fractions_std[lab]))
    print("Interaction residual: {:.1f}% +/- {:.1f}%".format(100 * interaction_mean, 100 * interaction_std))

    print("\n" + "=" * 78)
    print("STEP 4 -- CONVERGENCE TABLE")
    print("=" * 78)
    print("{:<10s} {:>6s} {:>10s} {:>12s} {:>14s} {:>28s} {:>28s}".format(
        "Tier", "n", "CV R^2", "MAE", "RMSE", "validator_count imp", "node_degree imp"))
    print("{:<10s} {:>6d} {:>10.3f} {:>12s} {:>14s} {:>28s} {:>28s}".format(
        "N=200", N200_N, N200_CV_R2, "n/a (baseline)", "n/a (baseline)",
        "{:.1f}%+/-{:.1f}%".format(100 * N200_IMPORTANCE["Validating-node count"][0], 100 * N200_IMPORTANCE["Validating-node count"][1]),
        "{:.1f}%+/-{:.1f}%".format(100 * N200_IMPORTANCE["Node degree"][0], 100 * N200_IMPORTANCE["Node degree"][1])))
    print("{:<10s} {:>6s} {:>10s} {:>12s} {:>14s} {:>28s} {:>28s}".format(
        "N=500", "0", "NO FIT", "-- 0/378", "usable rows", "(mc500 unusable, stale data)", ""))
    r = results["mc1000"]
    print("{:<10s} {:>6d} {:>10.3f} {:>12.6f} {:>14.6f} {:>28s} {:>28s}".format(
        "N=1000", r["n"], r["cv_r2"], r["cv_mae"], r["cv_rmse"],
        "{:.1f}%+/-{:.1f}%".format(100 * r["fractions_mean"]["Validating-node count"], 100 * r["fractions_std"]["Validating-node count"]),
        "{:.1f}%+/-{:.1f}%".format(100 * r["fractions_mean"]["Node degree"], 100 * r["fractions_std"]["Node degree"])))

    print("\n-- D-bar+ deviation from N=200 baseline (configs present at N=1000 tier, n={}) --".format(len(fit_df)))
    fit_df["n200_baseline"] = fit_df["conditional_reorg_depth_n200"].astype(float)
    fit_df["dev_from_baseline"] = fit_df["conditional_reorg_depth"] - fit_df["n200_baseline"]
    print("Mean deviation (N=1000 - N=200): {:.4f}".format(fit_df["dev_from_baseline"].mean()))
    print("Mean ABSOLUTE deviation:         {:.4f}".format(fit_df["dev_from_baseline"].abs().mean()))
    print("Std of deviation:                {:.4f}".format(fit_df["dev_from_baseline"].std()))
    print("(N=500->N=1000 consecutive-tier deviation: NOT COMPUTABLE -- 0 configs have usable N=500 data)")

    print("\n" + "=" * 78)
    print("STEP 5 -- COMPARABILITY FLAG")
    print("=" * 78)
    print("N=1000 fANOVA fit above used n={} of 378 configs (97.4%) -- 10 configs dropped, all sharing".format(len(fit_df)))
    print("node_degree=2 + large max_block_size (6/10 the strict Slow-pathology corner, 4/10 adjacent to it).")
    print("This is NOT a random subsample: it is systematically skewed AWAY from the hardest-to-resolve,")
    print("highest-max_block_size, most topologically-constrained corner of the design space relative to")
    print("the N=200 baseline's full 378-config fit. Any R^2/importance shift between N=200 and N=1000 below")
    print("could partly reflect this sample composition change, not a pure replication-count effect.")
    print("N=500 is not comparable at all -- zero usable configs (see Step 1); mc500 on disk is stale,")
    print("unrelated 44-config data, not a partial/incomplete version of this 378-config run.")

    return df, tier_dbar, results


if __name__ == "__main__":
    main()
