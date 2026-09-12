#!/usr/bin/env python3
"""
SANITY-CHECK COPY of fanova_per_metric.py -- the production script is
untouched. Only change from the original: imports atosim_common_sanity
(tie_breaking_parameter excluded from PARAM_LABELS) instead of
atosim_common. No other changes.

Step 3 (standalone, cross-strategy view): for each outcome
(attack_success_rate, conditional_reorg_depth, attack_severity), fit one RF
per attack strategy PLUS one pooled RF across all strategies, and plot the
first-order fANOVA importance fractions (Hutter et al. 2014 -- the primary,
canonical fANOVA result) side by side as a bar chart - this is the "per
metric, across all attacks" comparison that analyze_strategy.py (which
reports per strategy) does not produce. The total-order Sobol index S_Ti
(Jansen 1999, RF-surrogate) is also reported as a supplementary,
NOT-fANOVA metric alongside it (see atosim_common_sanity.py's
sobol_total_order docstring) since it additionally captures interactions
that the first-order fractions do not.

Same modeling conventions as analyze_strategy.py / atosim_common.py: 5-fold
CV grouped by system_config_id; final RF refit on all its data. Held-out
R^2 is reported as a diagnostic for every group but no longer gates
whether a group's fANOVA result is reported (see atosim_common_sanity.py's
module docstring / fanova_first_order -- Hutter et al. 2014 has no
held-out-data step; its own uncertainty measure is cross-tree std, which
is what's plotted as an error bar and printed here instead).
"""
import argparse
import os
from datetime import datetime

import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

import atosim_common_sanity as common

POOLED_LABEL = "All strategies (pooled)"


def bar_chart(results_by_group, outcome, outdir):
    groups = list(results_by_group.keys())
    labels = common.LABELS
    n_groups = len(groups)
    n_params = len(labels)

    fig, ax = plt.subplots(figsize=(10, 0.5 * n_params + 2))
    bar_h = 0.8 / n_groups
    y_base = np.arange(n_params)
    colors = plt.cm.tab10(np.linspace(0, 1, max(10, n_groups)))

    for gi, g in enumerate(groups):
        res = results_by_group[g]
        vals = [res["fractions_mean"][lab] for lab in labels]
        errs = [res["fractions_std"][lab] for lab in labels]
        offset = (gi - (n_groups - 1) / 2) * bar_h
        ax.barh(y_base + offset, vals, height=bar_h * 0.9, xerr=errs,
               error_kw=dict(elinewidth=1, capsize=2),
               label="{} (n_trees={}, held-out R2={:.2f})".format(g, res["n_trees"], res["cv_r2"]),
               color=colors[gi])

    ax.set_yticks(y_base)
    ax.set_yticklabels(labels)
    ax.invert_yaxis()
    ax.set_xlabel("First-order fANOVA importance fraction, mean +/- 1 across-tree standard deviation - {}".format(
        common.OUTCOME_DESC[outcome]))
    ax.set_title("fANOVA importance (Hutter et al. 2014, Corollary 8 cross-tree uncertainty) - {} - per strategy and pooled".format(outcome))
    ax.legend(fontsize=8, loc="lower right")
    ax.grid(axis="x", linestyle="--", alpha=0.4)
    fig.tight_layout()
    path = os.path.join(outdir, "fanova_sobol_bar_{}.png".format(outcome))
    fig.savefig(path, dpi=150)
    plt.close(fig)
    return path


def format_report_block(out, group_label, outcome, res):
    if res is None:
        out("{}: SKIPPED (insufficient data: fewer than 2 system_config_id groups or fewer than 50 rows)".format(
            group_label))
        return
    out("{} (n={})".format(group_label, res["n"]))
    out("  Held-out predictive performance (diagnostic, not part of Hutter 2014 method, not used to gate results):")
    out("    CV R^2 (mean across folds)   = {:.3f}".format(res["cv_r2"]))
    out("    CV MAE (mean across folds)   = {:.6f}  [outcome's natural scale]".format(res["cv_mae"]))
    out("    CV RMSE (mean across folds)  = {:.6f}  [outcome's natural scale]".format(res["cv_rmse"]))
    out("  fANOVA importance (Hutter et al. 2014, mean +/- across-tree standard deviation across {} trees,".format(
        res["n_trees"]
    ))
    out("  Corollary 8 uncertainty). Note: these are NOT confidence intervals for the true")
    out("  importance -- they reflect variation across the Random Forest's {} trees, per".format(res["n_trees"]))
    out("  Corollary 8 (Hutter et al. 2014):")
    for lab, mean_frac in sorted(res["fractions_mean"].items(), key=lambda kv: kv[1], reverse=True):
        std_frac = res["fractions_std"][lab]
        out("    {:<26s}{:>7.1f}% +/- {:.1f}% (across-tree std)".format(lab, 100 * mean_frac, 100 * std_frac))
    out("  interaction residual (mean +/- across-tree standard deviation): {:.1f}% +/- {:.1f}%".format(
        100 * res["interaction_mean"], 100 * res["interaction_std"]
    ))
    out("  Total-order Sobol S_Ti (Jansen 1999, RF-surrogate) - supplementary, not fANOVA:")
    for lab, val in sorted(res["S_Ti"].items(), key=lambda kv: kv[1], reverse=True):
        out("    {:<26s}{:>8.3f}".format(lab, val))
    out("  Second-order interaction effects (4 requested pairs, Hutter et al. 2014 Theorem 3,")
    out("  mean +/- across-tree standard deviation across {} trees; interaction only, main".format(
        res["n_trees"]
    ))
    out("  effects already subtracted). Note: not confidence intervals -- variation across the")
    out("  Random Forest's {} trees, per Corollary 8 (Hutter et al. 2014):".format(res["n_trees"]))
    for (lab_a, lab_b), pair_res in res["second_order"].items():
        pair_name = "{} x {}".format(lab_a, lab_b)
        if {lab_a, lab_b} == {"Bandwidth", "Block creation interval"}:
            pair_name += " [substituted for 'propagation time x BCI' -- propagation" \
                          " time is not an input feature, confirm this substitution]"
        out("    {:<95s}{:>6.1f}% +/- {:.1f}% (across-tree std)".format(
            pair_name, 100 * pair_res["frac_mean"], 100 * pair_res["frac_std"]
        ))


def fit_and_score(df, outcome, seed):
    X, y, groups, d = common.prepare_xy(df, outcome)
    if len(np.unique(groups)) < 2 or len(d) < 50:
        return None
    scores, mean_r2, mean_mae, mean_rmse = common.grouped_cv_r2(X, y, groups, seed=seed)
    rf = common.fit_final_rf(X, y, seed=seed)
    fractions_mean, fractions_std, interaction_mean, interaction_std = common.fanova_first_order(rf, X)
    s_t, _ = common.sobol_total_order(rf, X, seed=seed)
    second_order = common.fanova_second_order(rf, X)
    return {"fractions_mean": fractions_mean, "fractions_std": fractions_std,
            "interaction_mean": interaction_mean, "interaction_std": interaction_std,
            "S_Ti": s_t, "second_order": second_order,
            "cv_r2": mean_r2, "cv_mae": mean_mae, "cv_rmse": mean_rmse,
            "n": len(d), "n_trees": len(rf.estimators_)}


def main():
    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("analysis_parquet", help="Path to analysis_settings.parquet")
    ap.add_argument("--strategy", action="append", default=None,
                    help="Attack strategy to include (repeatable). Default: all strategies present.")
    ap.add_argument("--outcomes", nargs="+", default=common.OUTCOMES, choices=common.OUTCOMES)
    ap.add_argument("--seed", type=int, default=0)
    ap.add_argument("--outdir", required=True)
    args = ap.parse_args()

    os.makedirs(args.outdir, exist_ok=True)
    df_all = common.load_settings(args.analysis_parquet, strategies=args.strategy)
    strategies = args.strategy or sorted(df_all["attack_strategy"].unique())

    report_lines = []

    def out(s=""):
        print(s)
        report_lines.append(s)

    out("Generated: {}".format(datetime.now().strftime("%Y-%m-%d %H:%M:%S")))
    out("Strategies: {}".format(", ".join(strategies)))
    out("n rows total: {}".format(len(df_all)))

    for outcome in args.outcomes:
        out("\n" + "#" * 78)
        out("OUTCOME: {} ({})".format(outcome, common.OUTCOME_DESC[outcome]))
        out("#" * 78)

        results_by_group = {}
        for strat in strategies:
            res = fit_and_score(df_all[df_all["attack_strategy"] == strat], outcome, args.seed)
            format_report_block(out, strat, outcome, res)
            if res is not None:
                results_by_group[strat] = res

        if len(strategies) > 1:
            pooled_res = fit_and_score(df_all, outcome, args.seed)
            format_report_block(out, POOLED_LABEL, outcome, pooled_res)
            if pooled_res is not None:
                results_by_group[POOLED_LABEL] = pooled_res

        if results_by_group:
            chart_path = bar_chart(results_by_group, outcome, args.outdir)
            out("\nChart saved: {}".format(chart_path))
        else:
            out("\nNo models for this outcome (all groups had fewer than 2 system_config_id groups or "
                "fewer than 50 rows) - no chart produced.")

    report_path = os.path.join(args.outdir, "fanova_per_metric_report.txt")
    with open(report_path, "w") as f:
        f.write("\n".join(report_lines) + "\n")
    print("\nSaved report: {}".format(report_path))


if __name__ == "__main__":
    main()
