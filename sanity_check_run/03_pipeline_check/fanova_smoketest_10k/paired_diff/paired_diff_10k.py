#!/usr/bin/env python3
"""
Paired-difference analysis (lead_stubborn - selfish), 10k-row matched
sample, all three outcomes (attack_success_rate, conditional_reorg_depth,
attack_severity). Extends the existing 1000-row success_rate_diff/
analysis (which covered attack_success_rate only) to 10k scale and to the
other two outcomes. Read-only: only reads analysis_settings_{selfish,
lead_stubborn}_10k.parquet, already produced by build_analysis_table.py.
Does not touch that pipeline script, the 1000-row output, or any jar.

Matching key: the prior 1000-row analysis matched pairs on
(system_config_id, attacker_config_id) alone. Checking that against the
actual two-stage sampling design (see lhs_generate_sample_two_stage.py /
the latin-hyper-cube-sampling skill) shows system_config_id is the
CORE system config id (250 distinct values), with bandwidth varied as a
separate stage on top of it (10 fixed values per core config) -- so
(system_config_id, attacker_config_id) alone is NOT a unique key; it
collides across the 10 bandwidth variants of the same core config. In
this 10k sample that 2-key match has 1578 duplicate-key rows per
strategy file (verified directly against the loaded parquet), and even
the existing 1000-row analysis's own source data has 19 such duplicate
keys (981 unique matches out of the 1000 it reported, i.e. some of its
"1000 matched pairs" were built by joining SQL-style on a non-unique key,
silently mispairing across bandwidth variants for the colliding rows).
This script matches on (system_config_id, attacker_config_id, bandwidth)
instead, which is confirmed unique per strategy (0 duplicate keys) and
achieves a full 10000/10000 1:1 match with no unmatched rows either side.
"""
import numpy as np
import pandas as pd
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

SEL_PATH = "../../parquet_10k/analysis_settings_selfish_10k.parquet"
LEAD_PATH = "../../parquet_10k/analysis_settings_lead_stubborn_10k.parquet"
OUTDIR = "."
KEY = ["system_config_id", "attacker_config_id", "bandwidth"]
OUTCOMES = ["attack_success_rate", "conditional_reorg_depth", "attack_severity"]
THRESHOLDS = [0.02, 0.05, 0.10]
N_BOOTSTRAP = 10000
SEED = 0


def bootstrap_ci_mean(x, n_boot=N_BOOTSTRAP, seed=SEED):
    rng = np.random.default_rng(seed)
    n = len(x)
    boot_means = np.empty(n_boot)
    for b in range(n_boot):
        idx = rng.integers(0, n, size=n)
        boot_means[b] = x[idx].mean()
    lo, hi = np.percentile(boot_means, [2.5, 97.5])
    return float(lo), float(hi)


def main():
    sel = pd.read_parquet(SEL_PATH)
    lead = pd.read_parquet(LEAD_PATH)

    assert sel.duplicated(subset=KEY).sum() == 0, "selfish key not unique"
    assert lead.duplicated(subset=KEY).sum() == 0, "lead_stubborn key not unique"

    merged = sel.merge(lead, on=KEY, suffixes=("_selfish", "_lead_stubborn"),
                        how="inner")
    n_selfish_only = len(sel) - len(sel.merge(lead, on=KEY, how="inner"))
    n_lead_only = len(lead) - len(lead.merge(sel, on=KEY, how="inner"))

    report = []

    def out(s=""):
        print(s)
        report.append(s)

    out("=" * 90)
    out("Paired-difference analysis (lead_stubborn - selfish), 10k matched sample")
    out("Matched on (system_config_id, attacker_config_id, bandwidth) -- see script docstring")
    out("for why this 3-key match is used instead of the 1000-row analysis's 2-key match.")
    out("=" * 90)
    out("selfish rows: {}, lead_stubborn rows: {}".format(len(sel), len(lead)))
    out("Matched pairs: {}  (unmatched: {} selfish-only, {} lead_stubborn-only)".format(
        len(merged), n_selfish_only, n_lead_only))
    out("")

    diffs = {}
    for outcome in OUTCOMES:
        col_s = "{}_selfish".format(outcome)
        col_l = "{}_lead_stubborn".format(outcome)
        sub = merged[[*KEY, col_s, col_l]].copy()
        n_before = len(sub)
        sub = sub.dropna(subset=[col_s, col_l])
        n_excluded = n_before - len(sub)
        sub["diff"] = sub[col_l] - sub[col_s]
        diffs[outcome] = sub

        out("-" * 90)
        out("Outcome: {}".format(outcome))
        if n_excluded:
            out("  Excluded {} pairs where {} was undefined (NaN, i.e. zero successful".format(
                n_excluded, outcome))
            out("  rounds) in at least one strategy -- D-bar+ is undefined by definition when")
            out("  P=0, so these pairs are dropped from this outcome's diff, not zero-filled.")
        else:
            out("  0 pairs excluded -- {} was defined (non-NaN) for all {} matched pairs".format(
                outcome, n_before))
            out("  in both strategies at this data scale.")

        d = sub["diff"].to_numpy()
        ci_lo, ci_hi = bootstrap_ci_mean(d)
        mean_d = float(d.mean())
        std_d = float(d.std())
        median_d = float(np.median(d))
        sem = std_d / np.sqrt(len(d))
        analytic_lo, analytic_hi = mean_d - 1.96 * sem, mean_d + 1.96 * sem

        out("  n = {}".format(len(d)))
        out("  mean = {:.6f}   (95% bootstrap CI: [{:.6f}, {:.6f}], {} resamples)".format(
            mean_d, ci_lo, ci_hi, N_BOOTSTRAP))
        out("  mean = {:.6f}   (95% analytic CI:  [{:.6f}, {:.6f}], normal approx, for comparison)".format(
            mean_d, analytic_lo, analytic_hi))
        out("  std = {:.6f}, median = {:.6f}, min = {:.6f}, max = {:.6f}".format(
            std_d, median_d, float(d.min()), float(d.max())))
        for t in THRESHOLDS:
            frac = float(np.mean(np.abs(d) > t))
            out("  fraction |diff| > {:.2f}: {:.4f}  ({}/{})".format(
                t, frac, int(np.sum(np.abs(d) > t)), len(d)))
        out("  distinguishable from zero (bootstrap CI excludes 0)? {}".format(
            "YES" if (ci_lo > 0 or ci_hi < 0) else "NO"))

    # -------------------------------------------------------------------
    # Threshold-appropriateness note: 0.02/0.05/0.10 are absolute
    # thresholds carried over from the ASR analysis (ASR ranges ~[0.15,
    # 0.95] at this scale). attack_severity has a comparable dynamic range
    # (~[0.3, 2.1]) so the same absolute thresholds remain informative
    # there. conditional_reorg_depth's *entire* observed range is ~[2.0,
    # 2.4] (std ~0.015-0.020, see the fANOVA diagnostics for this same
    # data) -- an absolute threshold of 0.02 is comparable to the outcome's
    # ENTIRE standard deviation, so "fraction exceeding 0.02" on this
    # outcome should be read as "fraction of pairs differing by roughly
    # one full outcome-std or more", not as a small perturbation the way
    # it reads for the other two outcomes. Reported as-is below for
    # comparability, but this scale mismatch is flagged explicitly rather
    # than presented as directly comparable across outcomes.
    # -------------------------------------------------------------------
    out("-" * 90)
    out("Threshold-appropriateness note:")
    out("  0.02/0.05/0.10 are absolute thresholds. For attack_success_rate and")
    out("  attack_severity, whose natural ranges are ~[0.15,0.95] and ~[0.3,2.1] respectively,")
    out("  these thresholds represent genuinely small-to-moderate perturbations, comparable")
    out("  in spirit to the original 1000-row ASR analysis. For conditional_reorg_depth,")
    out("  whose ENTIRE observed range is ~[2.0, 2.4] (std ~0.015-0.02), a 0.02 absolute")
    out("  threshold is comparable to the outcome's entire standard deviation -- the same")
    out("  thresholds are NOT on a comparable relative scale for this outcome. Reported below")
    out("  for consistency/comparability across outcomes, but should not be read as 'similarly")
    out("  small' across all three.")

    # -------------------------------------------------------------------
    # Plot: one figure, 3 subplots (one per outcome) -- histogram of
    # per-pair diffs (shows the SPREAD, since the whole point of the prior
    # 1000-row finding was that aggregate near-zero-mean can hide large
    # per-pair spread) with the mean and its 95% bootstrap CI overlaid, so
    # "is the strategy difference distinguishable from zero" is visible
    # at a glance (CI band position relative to the dashed zero line) at
    # the same time as the per-pair distribution shape.
    # -------------------------------------------------------------------
    fig, axes = plt.subplots(1, 3, figsize=(16, 4.5))
    for ax, outcome in zip(axes, OUTCOMES):
        d = diffs[outcome]["diff"].to_numpy()
        mean_d = float(d.mean())
        ci_lo, ci_hi = bootstrap_ci_mean(d)
        ax.hist(d, bins=60, color="#4C72B0", alpha=0.75, edgecolor="none")
        ax.axvline(0, color="black", linestyle="--", linewidth=1, label="zero")
        ax.axvline(mean_d, color="#C44E52", linestyle="-", linewidth=1.5,
                   label="mean = {:.4f}".format(mean_d))
        ax.axvspan(ci_lo, ci_hi, color="#C44E52", alpha=0.2,
                  label="95% CI [{:.4f}, {:.4f}]".format(ci_lo, ci_hi))
        ax.set_title("{}\n(lead_stubborn - selfish), n={}".format(outcome, len(d)))
        ax.set_xlabel("per-pair diff")
        ax.set_ylabel("count")
        ax.legend(fontsize=7, loc="upper right")
        ax.grid(axis="y", linestyle="--", alpha=0.3)
    fig.tight_layout()
    plot_path = "{}/paired_diff_10k.png".format(OUTDIR)
    fig.savefig(plot_path, dpi=150)
    plt.close(fig)
    out("")
    out("Saved plot: {}".format(plot_path))

    # Save per-pair CSV (all three outcomes' diffs, one row per matched pair)
    csv_cols = KEY + ["attack_success_rate_selfish", "attack_success_rate_lead_stubborn"]
    csv_df = merged[csv_cols].copy()
    csv_df["ASR_diff"] = diffs["attack_success_rate"].set_index(KEY)["diff"].reindex(
        pd.MultiIndex.from_frame(csv_df[KEY])).to_numpy()
    d_diff_indexed = diffs["conditional_reorg_depth"].set_index(KEY)["diff"]
    csv_df["D_diff"] = d_diff_indexed.reindex(pd.MultiIndex.from_frame(csv_df[KEY])).to_numpy()
    v_diff_indexed = diffs["attack_severity"].set_index(KEY)["diff"]
    csv_df["V_diff"] = v_diff_indexed.reindex(pd.MultiIndex.from_frame(csv_df[KEY])).to_numpy()
    csv_path = "{}/paired_diff_10k.csv".format(OUTDIR)
    csv_df.to_csv(csv_path, index=False)
    out("Saved per-pair data: {}".format(csv_path))

    report_path = "{}/paired_diff_10k_report.txt".format(OUTDIR)
    with open(report_path, "w") as f:
        f.write("\n".join(report) + "\n")
    out("Saved report: {}".format(report_path))


if __name__ == "__main__":
    main()
