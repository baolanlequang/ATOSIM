"""
ATOSIM Blockchain Attack Analysis
==================================
Analyzes the impact of network/protocol parameters on double-spend attack
success probability (DSP). Analysis is performed SEPARATELY for each
attack-strategy experiment, then compared across strategies.

Methods
-------
  - Descriptive statistics per strategy
  - f-ANOVA (Type II OLS, multi-parameter) per strategy
  - One-way f-ANOVA per parameter (Low/Medium/High levels) per strategy
  - Fractional logistic regression (GLM Binomial) with odds ratios
    and 95% confidence intervals per strategy
  - Cross-strategy comparison

Output
------
  analysis/figures/          – all plots (PNG, 300 dpi)
  analysis/tables/           – CSV tables
  analysis/analysis_report.md – written report
"""

import os
import json
import warnings
import numpy as np
import pandas as pd
import matplotlib
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
import matplotlib.patches as mpatches
import seaborn as sns
import statsmodels.api as sm
import statsmodels.formula.api as smf
from statsmodels.formula.api import ols
from matplotlib.lines import Line2D
from scipy import stats

matplotlib.use("Agg")
matplotlib.rcParams.update({
    "font.family": "DejaVu Sans",
    "font.size": 11,
    "axes.titlesize": 12,
    "axes.labelsize": 11,
    "figure.dpi": 300,
})
warnings.filterwarnings("ignore")

# ─────────────────────────────────────────────────────────────────────────────
# Paths
# ─────────────────────────────────────────────────────────────────────────────
SCRIPT_DIR  = os.path.dirname(os.path.abspath(__file__))
RESULTS_DIR = os.path.join(SCRIPT_DIR, "..", "results")
FIGURES_DIR = os.path.join(SCRIPT_DIR, "figures")
TABLES_DIR  = os.path.join(SCRIPT_DIR, "tables")
os.makedirs(FIGURES_DIR, exist_ok=True)
os.makedirs(TABLES_DIR,  exist_ok=True)

# ─────────────────────────────────────────────────────────────────────────────
# Configuration
# ─────────────────────────────────────────────────────────────────────────────
STRATEGIES = {
    "selfish":               "Selfish Mining",
    "lead_stubborn":         "Lead-Stubborn Mining",
    "trail_stubborn":        "Trail-Stubborn Mining",
    "selfish_lead_stubborn": "Combined Selfish + Lead-Stubborn",
    "selfish_trail_stubborn":"Combined Selfish + Trail-Stubborn",
}
STRATEGY_COLORS = {
    "selfish":               "#e74c3c",
    "lead_stubborn":         "#2980b9",
    "trail_stubborn":        "#27ae60",
    "selfish_lead_stubborn": "#8e44ad",
    "selfish_trail_stubborn":"#e67e22",
}

PARAM_COLS = [
    "num_nodes", "node_degree", "prop_delay",
    "block_interval", "max_block_size", "attacker_hp", "tie_breaking",
]
PARAM_LABELS = {
    "num_nodes":      "Number of Nodes",
    "node_degree":    "Node Degree",
    "prop_delay":     "Propagation Delay (ms)",
    "block_interval": "Block Interval (s)",
    "max_block_size": "Max Block Size (MB)",
    "attacker_hp":    "Attacker Hash Power",
    "tie_breaking":   "Tie-Breaking Parameter γ",
}

FORMULA = (
    "success_prob ~ num_nodes + node_degree + prop_delay + "
    "block_interval + max_block_size + attacker_hp + tie_breaking"
)


def sig_stars(p):
    if p < 0.001: return "***"
    if p < 0.01:  return "**"
    if p < 0.05:  return "*"
    if p < 0.1:   return "."
    return "ns"


# ─────────────────────────────────────────────────────────────────────────────
# Data loading
# ─────────────────────────────────────────────────────────────────────────────
def load_strategy(folder_name: str) -> pd.DataFrame:
    folder = os.path.join(RESULTS_DIR, folder_name)
    records = []
    for fname in sorted(os.listdir(folder)):
        if not (fname.startswith("result_run_") and fname.endswith(".json")):
            continue
        try:
            with open(os.path.join(folder, fname)) as fh:
                d = json.load(fh)
            p   = d["inputParameters"]
            avg = d["simulationResult"]["averageSimulationRoundResult"]
            dsp = next(
                (float(m["average"]) for m in avg
                 if m.get("name") == "DoubleSpendSuccessProbability"
                 and isinstance(m.get("average"), (int, float))),
                None,
            )
            if dsp is None:
                continue
            records.append({
                "run_id":         int(d["runId"]),
                "strategy":       folder_name,
                "num_nodes":      float(p["validator_count"]),
                "node_degree":    float(p["node_degree"]),
                "prop_delay":     float(p["propagation_delay"]),
                "block_interval": float(p["block_creation_interval"]),
                "max_block_size": float(p["max_block_size"]),
                "attacker_hp":    float(p["attacker_hash_power"]),
                "tie_breaking":   float(p["tie_breaking_parameter"]),
                "success_prob":   dsp,
                "attack_success": int(dsp > 0),
            })
        except Exception as e:
            print(f"  [WARN] {fname}: {e}")
    return pd.DataFrame(records)


def standardize(df: pd.DataFrame, cols: list) -> pd.DataFrame:
    df_s = df.copy()
    for c in cols:
        mu, sigma = df[c].mean(), df[c].std()
        df_s[c] = (df[c] - mu) / sigma if sigma > 0 else 0.0
    return df_s


# ─────────────────────────────────────────────────────────────────────────────
# 1. Load all strategies
# ─────────────────────────────────────────────────────────────────────────────
print("=" * 70)
print("LOADING DATA")
print("=" * 70)

strategy_data: dict[str, pd.DataFrame] = {}
for folder, title in STRATEGIES.items():
    df_s = load_strategy(folder)
    strategy_data[folder] = df_s
    pos = df_s["attack_success"].sum()
    print(f"  {title:42s}  n={len(df_s)}  "
          f"success={pos/len(df_s)*100:.1f}%  mean_DSP={df_s['success_prob'].mean():.4f}")


# ─────────────────────────────────────────────────────────────────────────────
# 2. Per-strategy analysis
#    For each strategy: descriptive stats, f-ANOVA, logistic regression
# ─────────────────────────────────────────────────────────────────────────────
anova_all    = {}   # folder -> anova table (Type II OLS, multi-parameter)
oneway_all   = {}   # folder -> one-way ANOVA-by-level table
logit_all    = {}   # folder -> {"raw": model, "std": model}
desc_all     = {}   # folder -> describe DataFrame

for folder, title in STRATEGIES.items():
    df = strategy_data[folder]
    banner = f"  {title}  "
    print("\n" + "═" * 70)
    print(f"{banner.upper().center(70, '═')}")
    print("═" * 70)

    # ── Descriptive statistics ─────────────────────────────────────────────
    print("\n[Descriptive Statistics]")
    desc = df[PARAM_COLS + ["success_prob"]].describe().T
    desc.index = [PARAM_LABELS.get(c, c) for c in desc.index]
    desc_all[folder] = desc
    print(desc[["mean", "std", "min", "50%", "max"]].to_string())
    desc.to_csv(os.path.join(TABLES_DIR, f"desc_{folder}.csv"))

    # ── f-ANOVA ────────────────────────────────────────────────────────────
    print("\n[f-ANOVA Type II OLS]")
    model_ols = ols(FORMULA, data=df).fit()
    anova_tbl = sm.stats.anova_lm(model_ols, typ=2)
    ss_total  = anova_tbl["sum_sq"].sum()
    anova_tbl["eta_sq"] = anova_tbl["sum_sq"] / ss_total
    anova_all[folder] = anova_tbl

    print(f"  {'Factor':<28} {'F':>9} {'p-value':>12} {'η²(%)':>8} {'Sig':>5}")
    print("  " + "-" * 65)
    for idx, row in anova_tbl.iterrows():
        if idx == "Residual":
            continue
        label = PARAM_LABELS.get(idx, idx)
        p = row["PR(>F)"]
        print(f"  {label:<28} {row['F']:>9.3f} {p:>12.4e} "
              f"{row['eta_sq']*100:>7.2f}% {sig_stars(p):>5}")
    print(f"  R² = {model_ols.rsquared:.4f}  |  Adj-R² = {model_ols.rsquared_adj:.4f}")
    anova_tbl.to_csv(os.path.join(TABLES_DIR, f"anova_{folder}.csv"))

    # ── One-way f-ANOVA per parameter (Low/Medium/High levels) ──────────────
    # For each parameter independently, split its observed range into three
    # equal-frequency levels (tertiles) and test whether mean DSP differs
    # significantly across these levels using a one-way ANOVA F-test.
    print("\n[One-Way f-ANOVA per Parameter — Low/Medium/High Levels]")
    oneway_rows = []
    df_levels = df.copy()
    for p_col in PARAM_COLS:
        try:
            levels = pd.qcut(df[p_col], q=3, labels=["Low", "Medium", "High"], duplicates="drop")
        except ValueError:
            levels = pd.cut(df[p_col], bins=3, labels=["Low", "Medium", "High"])
        df_levels[f"{p_col}_level"] = levels

        groups = [df.loc[levels == lvl, "success_prob"].values
                  for lvl in levels.cat.categories]
        groups = [g for g in groups if len(g) > 0]
        f_stat, p_val = stats.f_oneway(*groups)

        row = {
            "Parameter":    PARAM_LABELS[p_col],
            "F_statistic":  f_stat,
            "p_value":      p_val,
            "Significance": sig_stars(p_val),
        }
        for lvl in levels.cat.categories:
            mask = levels == lvl
            row[f"Mean_DSP_{lvl}"] = df.loc[mask, "success_prob"].mean()
            row[f"N_{lvl}"]        = int(mask.sum())
        oneway_rows.append(row)

        print(f"  {PARAM_LABELS[p_col]:<28} F={f_stat:>9.3f}  p={p_val:>12.4e}  "
              f"{sig_stars(p_val):>4}  | Mean DSP (L/M/H) = "
              + " / ".join(f"{row.get(f'Mean_DSP_{lvl}', float('nan')):.3f}"
                            for lvl in ["Low", "Medium", "High"]))

    oneway_df = pd.DataFrame(oneway_rows)
    oneway_all[folder] = (oneway_df, df_levels)
    oneway_df.to_csv(os.path.join(TABLES_DIR, f"oneway_anova_{folder}.csv"), index=False)

    # ── Logistic regression ────────────────────────────────────────────────
    print("\n[Fractional Logistic Regression]")
    df_std   = standardize(df, PARAM_COLS)
    m_raw    = smf.glm(FORMULA, data=df,     family=sm.families.Binomial()).fit(disp=False)
    m_std    = smf.glm(FORMULA, data=df_std, family=sm.families.Binomial()).fit(disp=False)
    logit_all[folder] = {"raw": m_raw, "std": m_std}

    params = m_raw.params
    bse    = m_raw.bse
    zvals  = m_raw.tvalues
    pvals  = m_raw.pvalues
    conf   = m_raw.conf_int()

    print(f"  {'Parameter':<28} {'Coef':>10} {'z':>8} {'p-value':>12} "
          f"{'OR':>12} {'Sig':>5}")
    print("  " + "-" * 75)
    for v in params.index:
        label = PARAM_LABELS.get(v, v) if v != "Intercept" else "Intercept"
        OR = np.exp(params[v])
        print(f"  {label:<28} {params[v]:>10.4f} {zvals[v]:>8.3f} "
              f"{pvals[v]:>12.4e} {OR:>12.4f} {sig_stars(pvals[v]):>5}")
    print(f"  McFadden R² = {m_raw.pseudo_rsquared('mcf'):.4f}  |  "
          f"Deviance = {m_raw.deviance:.4f}")

    pd.DataFrame({
        "Parameter":      [PARAM_LABELS.get(v, v) if v != "Intercept" else "Intercept"
                           for v in params.index],
        "Coefficient":    params.values,
        "Std_Error":      bse.values,
        "z_statistic":    zvals.values,
        "p_value":        pvals.values,
        "Significance":   [sig_stars(p) for p in pvals],
        "Odds_Ratio":     np.exp(params.values),
        "OR_CI_lower_95": np.exp(conf[0].values),
        "OR_CI_upper_95": np.exp(conf[1].values),
    }).to_csv(os.path.join(TABLES_DIR, f"logistic_{folder}.csv"), index=False)


# ─────────────────────────────────────────────────────────────────────────────
# 3. Cross-strategy comparison tables
# ─────────────────────────────────────────────────────────────────────────────
print("\n" + "=" * 70)
print("CROSS-STRATEGY COMPARISON")
print("=" * 70)

# ANOVA eta_sq comparison
eta_rows = []
for p_col in PARAM_COLS:
    row = {"Parameter": PARAM_LABELS[p_col]}
    for folder, title in STRATEGIES.items():
        tbl = anova_all[folder]
        row[title] = tbl.loc[p_col, "eta_sq"] * 100 if p_col in tbl.index else np.nan
    eta_rows.append(row)
eta_compare = pd.DataFrame(eta_rows)
eta_compare.to_csv(os.path.join(TABLES_DIR, "cross_strategy_eta_sq.csv"), index=False)
print("\nEta-squared (%) by strategy:")
print(eta_compare.to_string(index=False))

# Logistic OR comparison
or_rows = []
for p_col in PARAM_COLS:
    row = {"Parameter": PARAM_LABELS[p_col]}
    for folder, title in STRATEGIES.items():
        m = logit_all[folder]["raw"]
        OR = np.exp(m.params.get(p_col, np.nan))
        row[title] = OR
    or_rows.append(row)
or_compare = pd.DataFrame(or_rows)
or_compare.to_csv(os.path.join(TABLES_DIR, "cross_strategy_odds_ratios.csv"), index=False)

# DSP summary per strategy
dsp_rows = []
for folder, title in STRATEGIES.items():
    df = strategy_data[folder]
    dsp_rows.append({
        "Strategy": title,
        "n": len(df),
        "Mean DSP": df["success_prob"].mean(),
        "Std DSP":  df["success_prob"].std(),
        "Attack Success Rate (%)": df["attack_success"].mean() * 100,
        "McFadden R²": logit_all[folder]["raw"].pseudo_rsquared("mcf"),
        "ANOVA R²":    anova_all[folder].loc[:, "eta_sq"].sum() - \
                       anova_all[folder].loc["Residual", "eta_sq"],
    })
dsp_summary = pd.DataFrame(dsp_rows)
dsp_summary.to_csv(os.path.join(TABLES_DIR, "cross_strategy_dsp_summary.csv"), index=False)
print("\nDSP summary per strategy:")
print(dsp_summary.to_string(index=False))


# ─────────────────────────────────────────────────────────────────────────────
# 4. Visualizations – per strategy + cross-strategy
# ─────────────────────────────────────────────────────────────────────────────
print("\n" + "=" * 70)
print("GENERATING VISUALIZATIONS")
print("=" * 70)

# ── 4a. Per-strategy: f-ANOVA F-statistic bar charts ─────────────────────
for folder, title in STRATEGIES.items():
    tbl    = anova_all[folder]
    params_list = [p for p in PARAM_COLS if p in tbl.index]
    f_vals = [tbl.loc[p, "F"] for p in params_list]
    p_vals = [tbl.loc[p, "PR(>F)"] for p in params_list]
    labels = [PARAM_LABELS[p] for p in params_list]

    sorted_idx = np.argsort(f_vals)
    colors = ["#e74c3c" if p_vals[i] < 0.05 else "#95a5a6" for i in sorted_idx]

    fig, ax = plt.subplots(figsize=(10, 5))
    bars = ax.barh(
        [labels[i] for i in sorted_idx],
        [f_vals[i] for i in sorted_idx],
        color=colors, edgecolor="grey", height=0.55,
    )
    for bar in bars:
        w = bar.get_width()
        ax.text(w + max(f_vals) * 0.01, bar.get_y() + bar.get_height() / 2,
                f"{w:.1f}", va="center", ha="left", fontsize=9, fontweight="bold")
    ax.set_xlabel("F-statistic (Type II ANOVA)")
    ax.set_title(f"f-ANOVA Feature Importance\n{title}", fontweight="bold")
    ax.grid(axis="x", linestyle="--", alpha=0.4)
    ax.legend(handles=[
        mpatches.Patch(color="#e74c3c", label="p < 0.05 (significant)"),
        mpatches.Patch(color="#95a5a6", label="p ≥ 0.05 (not significant)"),
    ], loc="lower right", fontsize=9)
    plt.tight_layout()
    fpath = os.path.join(FIGURES_DIR, f"anova_{folder}.png")
    plt.savefig(fpath, dpi=300, bbox_inches="tight")
    plt.close()
    print(f"  Saved → figures/anova_{folder}.png")

# ── 4a-bis. Per-strategy: one-way ANOVA boxplots (DSP by parameter level) ──
for folder, title in STRATEGIES.items():
    oneway_df, df_levels = oneway_all[folder]

    fig, axes = plt.subplots(2, 4, figsize=(20, 9))
    axes = axes.flatten()
    for i, p_col in enumerate(PARAM_COLS):
        ax = axes[i]
        level_col = f"{p_col}_level"
        order = ["Low", "Medium", "High"]
        present = [lvl for lvl in order if lvl in df_levels[level_col].cat.categories]
        sns.boxplot(data=df_levels, x=level_col, y="success_prob", order=present,
                    ax=ax, palette="Blues", hue=level_col, legend=False)
        row = oneway_df[oneway_df["Parameter"] == PARAM_LABELS[p_col]].iloc[0]
        ax.set_title(f"{PARAM_LABELS[p_col]}\nF={row['F_statistic']:.2f}, "
                      f"p={row['p_value']:.2e} {row['Significance']}",
                      fontweight="bold", fontsize=10)
        ax.set_xlabel("Level")
        ax.set_ylabel("DSP" if i % 4 == 0 else "")
        ax.grid(axis="y", linestyle="--", alpha=0.3)
    axes[-1].axis("off")
    fig.suptitle(f"One-Way f-ANOVA: DSP by Parameter Level (Low/Medium/High)\n{title}",
                 fontsize=14, fontweight="bold", y=1.02)
    plt.tight_layout()
    fpath = os.path.join(FIGURES_DIR, f"oneway_anova_{folder}.png")
    plt.savefig(fpath, dpi=300, bbox_inches="tight")
    plt.close()
    print(f"  Saved → figures/oneway_anova_{folder}.png")

# ── 4b. Per-strategy: logistic regression forest plot ────────────────────
for folder, title in STRATEGIES.items():
    m_raw = logit_all[folder]["raw"]
    m_std = logit_all[folder]["std"]
    params  = m_raw.params
    conf_lr = m_raw.conf_int()
    pvals_  = m_raw.pvalues
    std_z   = m_std.tvalues.drop("Intercept").abs().reindex(PARAM_COLS)
    sorted_vars = std_z.sort_values().index.tolist()

    labels_fp = [PARAM_LABELS[v] for v in sorted_vars]
    ors       = [np.exp(params[v]) for v in sorted_vars]
    ci_lo     = [np.exp(conf_lr.loc[v, 0]) for v in sorted_vars]
    ci_hi     = [np.exp(conf_lr.loc[v, 1]) for v in sorted_vars]
    pv_fp     = [pvals_[v] for v in sorted_vars]
    col_fp    = ["#c0392b" if p < 0.05 else "#2c3e50" for p in pv_fp]

    fig, ax = plt.subplots(figsize=(10, 5))
    y_pos = np.arange(len(sorted_vars))
    for i, (y, lo, hi, or_, col) in enumerate(zip(y_pos, ci_lo, ci_hi, ors, col_fp)):
        ax.hlines(y, lo, hi, colors=col, linewidth=2.4, zorder=3)
        ax.plot(or_, y, "o", color=col, markersize=8, zorder=4, clip_on=False)
        s = sig_stars(pv_fp[i])
        if s not in ("ns", ""):
            ax.text(hi * 1.03, y, s, va="center", fontsize=10,
                    color="#c0392b", fontweight="bold")
    ax.axvline(1.0, color="#888", linewidth=1.2, linestyle="--", zorder=2)
    ax.set_yticks(y_pos)
    ax.set_yticklabels(labels_fp)
    ax.set_xscale("log")
    ax.xaxis.set_major_formatter(mticker.FuncFormatter(lambda x, _: f"{x:g}"))
    ax.set_xlabel("Odds Ratio (log scale)", fontweight="bold")
    ax.set_title(f"Logistic Regression — Forest Plot\n{title}", fontweight="bold")
    ax.spines[["top", "right"]].set_visible(False)
    ax.grid(axis="x", linestyle="--", alpha=0.4)
    ax.legend(handles=[
        Line2D([0], [0], marker="o", color="w", markerfacecolor="#c0392b",
               markersize=8, label="p < 0.05 (significant)"),
        Line2D([0], [0], marker="o", color="w", markerfacecolor="#2c3e50",
               markersize=8, label="p ≥ 0.05 (not significant)"),
    ], loc="lower right", framealpha=0.9, fontsize=9)
    plt.tight_layout()
    fpath = os.path.join(FIGURES_DIR, f"logistic_forest_{folder}.png")
    plt.savefig(fpath, dpi=300, bbox_inches="tight")
    plt.close()
    print(f"  Saved → figures/logistic_forest_{folder}.png")

# ── 4c. Per-strategy: DSP distribution ───────────────────────────────────
for folder, title in STRATEGIES.items():
    df  = strategy_data[folder]
    col = STRATEGY_COLORS[folder]

    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(12, 4.5))

    ax1.hist(df["success_prob"], bins=40, color=col, edgecolor="white", alpha=0.85)
    ax1.axvline(df["success_prob"].mean(), color="black", linestyle="--",
                linewidth=1.8, label=f"Mean = {df['success_prob'].mean():.3f}")
    ax1.set_xlabel("Double-Spend Success Probability")
    ax1.set_ylabel("Count")
    ax1.set_title("DSP Distribution", fontweight="bold")
    ax1.legend(fontsize=9)
    ax1.grid(axis="y", linestyle="--", alpha=0.4)

    counts = df["attack_success"].value_counts().sort_index()
    ax2.pie(counts,
            labels=["No Success\n(DSP=0)", "Success\n(DSP>0)"],
            colors=["#ecf0f1", col], autopct="%1.1f%%",
            startangle=90, pctdistance=0.72,
            wedgeprops=dict(edgecolor="white", linewidth=2))
    ax2.set_title("Attack Success Rate", fontweight="bold")

    fig.suptitle(f"Attack Success Probability — {title}", fontsize=12, fontweight="bold")
    plt.tight_layout()
    fpath = os.path.join(FIGURES_DIR, f"dsp_{folder}.png")
    plt.savefig(fpath, dpi=300, bbox_inches="tight")
    plt.close()
    print(f"  Saved → figures/dsp_{folder}.png")

# ── 4d. Per-strategy: scatter panel (all params vs DSP) ──────────────────
for folder, title in STRATEGIES.items():
    df  = strategy_data[folder]
    col = STRATEGY_COLORS[folder]

    fig, axes = plt.subplots(2, 4, figsize=(20, 9))
    axes = axes.flatten()
    for i, p_col in enumerate(PARAM_COLS):
        ax = axes[i]
        ax.scatter(df[p_col], df["success_prob"],
                   alpha=0.35, s=18, color=col, edgecolors="none")
        m_, b_, r_, p_, _ = stats.linregress(df[p_col], df["success_prob"])
        x_line = np.linspace(df[p_col].min(), df[p_col].max(), 200)
        ax.plot(x_line, m_ * x_line + b_, color="black", linewidth=1.8,
                label=f"r={r_:.3f}  p={p_:.2e}")
        ax.set_xlabel(PARAM_LABELS[p_col], fontsize=9)
        ax.set_ylabel("DSP" if i % 4 == 0 else "", fontsize=9)
        ax.set_title(PARAM_LABELS[p_col], fontweight="bold", fontsize=9)
        ax.legend(fontsize=7)
        ax.grid(linestyle="--", alpha=0.3)
    axes[-1].axis("off")
    fig.suptitle(f"Parameter vs. DSP — {title}", fontsize=13, fontweight="bold", y=1.01)
    plt.tight_layout()
    fpath = os.path.join(FIGURES_DIR, f"scatter_{folder}.png")
    plt.savefig(fpath, dpi=300, bbox_inches="tight")
    plt.close()
    print(f"  Saved → figures/scatter_{folder}.png")

# ── 4e. Cross-strategy: η² comparison bar chart ──────────────────────────
fig, ax = plt.subplots(figsize=(12, 6))
n_strats = len(STRATEGIES)
x        = np.arange(len(PARAM_COLS))
width    = 0.15
palette  = list(STRATEGY_COLORS.values())

for k, (folder, title) in enumerate(STRATEGIES.items()):
    tbl    = anova_all[folder]
    eta_sq = [tbl.loc[p, "eta_sq"] * 100 if p in tbl.index else 0.0 for p in PARAM_COLS]
    ax.bar(x + k * width, eta_sq, width=width, label=title,
           color=palette[k], edgecolor="white", alpha=0.9)

ax.set_xticks(x + width * (n_strats - 1) / 2)
ax.set_xticklabels([PARAM_LABELS[p] for p in PARAM_COLS], rotation=18, ha="right")
ax.set_ylabel("Eta-Squared η² (%)")
ax.set_title("f-ANOVA Feature Importance — Cross-Strategy Comparison", fontweight="bold")
ax.legend(fontsize=9, loc="upper right")
ax.grid(axis="y", linestyle="--", alpha=0.4)
plt.tight_layout()
plt.savefig(os.path.join(FIGURES_DIR, "cross_strategy_anova_eta.png"), dpi=300, bbox_inches="tight")
plt.close()
print("  Saved → figures/cross_strategy_anova_eta.png")

# ── 4f. Cross-strategy: DSP distributions overlay ────────────────────────
fig, ax = plt.subplots(figsize=(10, 5.5))
for folder, title in STRATEGIES.items():
    df = strategy_data[folder]
    ax.hist(df["success_prob"], bins=35, alpha=0.45, label=title,
            color=STRATEGY_COLORS[folder], edgecolor="none")
    ax.axvline(df["success_prob"].mean(), color=STRATEGY_COLORS[folder],
               linestyle="--", linewidth=1.5)
ax.set_xlabel("Double-Spend Success Probability")
ax.set_ylabel("Count")
ax.set_title("DSP Distribution — All Strategies Overlaid", fontweight="bold")
ax.legend(fontsize=9)
ax.grid(axis="y", linestyle="--", alpha=0.3)
plt.tight_layout()
plt.savefig(os.path.join(FIGURES_DIR, "cross_strategy_dsp_overlay.png"), dpi=300, bbox_inches="tight")
plt.close()
print("  Saved → figures/cross_strategy_dsp_overlay.png")

# ── 4g. Cross-strategy: attacker hash power vs DSP overlay ───────────────
fig, ax = plt.subplots(figsize=(10, 6))
for folder, title in STRATEGIES.items():
    df = strategy_data[folder]
    ax.scatter(df["attacker_hp"], df["success_prob"],
               alpha=0.25, s=18, color=STRATEGY_COLORS[folder],
               edgecolors="none", label=title)
ax.axvline(0.5, color="#2c3e50", linestyle="--", linewidth=1.5, label="50% hash power")
ax.set_xlabel(PARAM_LABELS["attacker_hp"], fontweight="bold")
ax.set_ylabel("Double-Spend Success Probability")
ax.set_title("Attacker Hash Power vs. DSP — All Strategies", fontweight="bold")
ax.legend(fontsize=9)
ax.grid(linestyle="--", alpha=0.3)
plt.tight_layout()
plt.savefig(os.path.join(FIGURES_DIR, "cross_strategy_hashpower_dsp.png"), dpi=300, bbox_inches="tight")
plt.close()
print("  Saved → figures/cross_strategy_hashpower_dsp.png")

# ── 4h. Cross-strategy: Pearson r heatmap (params × strategies) ──────────
corr_data = {}
for folder in STRATEGIES:
    df = strategy_data[folder]
    corr_data[folder] = {
        p: stats.pearsonr(df[p], df["success_prob"])[0] for p in PARAM_COLS
    }
corr_df = pd.DataFrame(corr_data, index=PARAM_COLS).T
corr_df.index   = list(STRATEGIES.values())
corr_df.columns = [PARAM_LABELS[p] for p in PARAM_COLS]

fig, ax = plt.subplots(figsize=(11, 4))
sns.heatmap(corr_df, annot=True, fmt=".3f", cmap="RdBu_r", center=0,
            vmin=-1, vmax=1, linewidths=0.5, ax=ax, annot_kws={"size": 9})
ax.set_title("Pearson Correlation (r) with DSP — Cross-Strategy", fontweight="bold")
ax.set_xticklabels(ax.get_xticklabels(), rotation=20, ha="right")
plt.tight_layout()
plt.savefig(os.path.join(FIGURES_DIR, "cross_strategy_pearson_heatmap.png"), dpi=300, bbox_inches="tight")
plt.close()
print("  Saved → figures/cross_strategy_pearson_heatmap.png")

# ── 4i. Parameter distributions (shared across strategies, one plot) ──────
df_any = strategy_data["selfish"]
fig, axes = plt.subplots(2, 4, figsize=(18, 8))
axes = axes.flatten()
for i, p_col in enumerate(PARAM_COLS):
    ax = axes[i]
    ax.hist(df_any[p_col], bins=30, color="#2980b9", edgecolor="white", alpha=0.85)
    ax.set_title(PARAM_LABELS[p_col], fontweight="bold")
    ax.set_xlabel("Value")
    ax.set_ylabel("Count")
    ax.grid(axis="y", linestyle="--", alpha=0.4)
axes[-1].axis("off")
fig.suptitle("Input Parameter Distributions (LHS Sampling, n=500)", fontsize=14,
             fontweight="bold", y=1.01)
plt.tight_layout()
plt.savefig(os.path.join(FIGURES_DIR, "parameter_distributions.png"), dpi=300, bbox_inches="tight")
plt.close()
print("  Saved → figures/parameter_distributions.png")

# ── 4j. Cross-strategy: model quality summary bar chart ──────────────────
fig, ax = plt.subplots(figsize=(9, 4.5))
strat_labels = list(STRATEGIES.values())
mcfadden_r2  = [logit_all[f]["raw"].pseudo_rsquared("mcf") for f in STRATEGIES]
anova_r2     = [anova_all[f].loc["Residual", "eta_sq"]     for f in STRATEGIES]
anova_r2_exp = [1 - r for r in anova_r2]

x_bar = np.arange(len(STRATEGIES))
w     = 0.35
ax.bar(x_bar - w/2, anova_r2_exp,  width=w, color="#2980b9", alpha=0.85, label="ANOVA R²")
ax.bar(x_bar + w/2, mcfadden_r2,   width=w, color="#27ae60", alpha=0.85, label="McFadden R² (logistic)")
ax.set_xticks(x_bar)
ax.set_xticklabels([s.replace(" + ", "\n+\n") for s in strat_labels], fontsize=9)
ax.set_ylabel("R²")
ax.set_title("Model Fit (R²) Across Attack Strategies", fontweight="bold")
ax.set_ylim(0, 1.05)
ax.legend(fontsize=9)
ax.grid(axis="y", linestyle="--", alpha=0.4)
plt.tight_layout()
plt.savefig(os.path.join(FIGURES_DIR, "cross_strategy_model_fit.png"), dpi=300, bbox_inches="tight")
plt.close()
print("  Saved → figures/cross_strategy_model_fit.png")

print("\n" + "=" * 70)
print("ANALYSIS COMPLETE")
print(f"  Figures: analysis/figures/  ({len(os.listdir(FIGURES_DIR))} files)")
print(f"  Tables:  analysis/tables/   ({len(os.listdir(TABLES_DIR))} files)")
print("=" * 70)
