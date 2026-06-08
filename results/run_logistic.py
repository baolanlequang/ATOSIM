import os
import json
import pandas as pd
import numpy as np
import statsmodels.api as sm
import statsmodels.formula.api as smf
import matplotlib
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
from matplotlib.lines import Line2D

matplotlib.rcParams.update({
    "font.family": "DejaVu Sans",
    "font.size": 11,
    "axes.titlesize": 13,
    "axes.labelsize": 12,
})

# ─────────────────────────────────────────────────────────────────────────────
# Configuration
# ─────────────────────────────────────────────────────────────────────────────
FOLDERS = [
    "lead_stubborn",
    "selfish_lead_stubborn",
    "selfish_trail_stubborn",
    "trail_stubborn",
]

TITLES = {
    "lead_stubborn":          "Lead-Stubborn Mining",
    "selfish_lead_stubborn":  "Combined Selfish + Lead-Stubborn Mining",
    "selfish_trail_stubborn": "Combined Selfish + Trail-Stubborn Mining",
    "trail_stubborn":         "Trail-Stubborn Mining",
}

PREDICTORS = [
    "num_nodes", "node_degree", "prop_delay",
    "block_interval", "max_block_size", "attacker_hp", "tie_breaking"
]

FRIENDLY = {
    "Intercept":      "Intercept",
    "num_nodes":      "Number of Nodes",
    "node_degree":    "Node Degree",
    "prop_delay":     "Propagation Delay",
    "block_interval": "Block Interval",
    "max_block_size": "Max Block Size",
    "attacker_hp":    "Attacker Hash Power",
    "tie_breaking":   "Tie-Breaking Parameter",
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
    return ""

# ─────────────────────────────────────────────────────────────────────────────
# Main loop
# ─────────────────────────────────────────────────────────────────────────────
for folder in FOLDERS:
    title = TITLES[folder]
    banner = f"  {title.upper()}  "
    print("\n" + "═" * 80)
    print(f"{'═' * ((80 - len(banner)) // 2)}{banner}{'═' * ((80 - len(banner) + 1) // 2)}")
    print("═" * 80)

    if not os.path.isdir(folder):
        print(f"  ⚠  Directory '{folder}' not found. Skipping.\n")
        continue

    # ── 1. Load ──────────────────────────────────────────────────────────────
    run_files = [
        f for f in os.listdir(folder)
        if f.startswith("result_run_") and f.endswith(".json")
    ]
    print(f"Loading {len(run_files)} run files from '{folder}'…")

    records = []
    for fname in run_files:
        try:
            with open(os.path.join(folder, fname)) as fh:
                d = json.load(fh)
            p = d["inputParameters"]
            avg = d["simulationResult"]["averageSimulationRoundResult"]
            dsp = next(
                (float(m["average"]) for m in avg
                 if m.get("name") == "DoubleSpendSuccessProbability"),
                None
            )
            if dsp is not None:
                records.append({
                    "num_nodes":      float(p["validator_count"]),
                    "node_degree":    float(p["node_degree"]),
                    "prop_delay":     float(p["propagation_delay"]),
                    "block_interval": float(p["block_creation_interval"]),
                    "max_block_size": float(p["max_block_size"]),
                    "attacker_hp":    float(p["attacker_hash_power"]),
                    "tie_breaking":   float(p["tie_breaking_parameter"]),
                    "success_prob":   dsp,
                })
        except Exception as e:
            print(f"  ⚠  Could not parse {fname}: {e}")

    df = pd.DataFrame(records)
    print(f"Dataset: {len(df)} runs  |  "
          f"success_prob range: [{df.success_prob.min():.4f}, {df.success_prob.max():.4f}]")

    # ── 2. Standardise ───────────────────────────────────────────────────────
    df_std = df.copy()
    for col in PREDICTORS:
        mu, sigma = df[col].mean(), df[col].std()
        df_std[col] = (df[col] - mu) / sigma if sigma > 0 else 0.0

    # ── 3. Fit models ─────────────────────────────────────────────────────────
    model_raw = smf.glm(FORMULA, data=df,
                        family=sm.families.Binomial()).fit()
    model_std = smf.glm(FORMULA, data=df_std,
                        family=sm.families.Binomial()).fit()

    # ── 4. Full statsmodels summary ───────────────────────────────────────────
    print("\n" + model_raw.summary().as_text())

    # ── 5. Significance table ─────────────────────────────────────────────────
    params = model_raw.params
    bse    = model_raw.bse
    zvals  = model_raw.tvalues
    pvals  = model_raw.pvalues
    conf   = model_raw.conf_int()

    print("\n" + "=" * 95)
    print(f"{'Parameter':<28}  {'Coef':>10}  {'Std Err':>9}  "
          f"{'z':>8}  {'p-value':>10}  {'Sig':>4}  {'OR':>14}")
    print("-" * 95)
    for v in params.index:
        label = FRIENDLY.get(v, v)
        OR = np.exp(params[v])
        print(f"{label:<28}  {params[v]:>10.5f}  {bse[v]:>9.5f}  "
              f"{zvals[v]:>8.3f}  {pvals[v]:>10.4e}  {sig_stars(pvals[v]):>4}  {OR:>14.6f}")
    print("=" * 95)
    print("Significance codes:  *** p<0.001  ** p<0.01  * p<0.05  . p<0.10")
    print(f"\nPseudo R² (Cox-Snell): {model_raw.pseudo_rsquared('cs'):.4f}"
          f"   |  Deviance: {model_raw.deviance:.4f}"
          f"   |  Pearson χ²: {model_raw.pearson_chi2:.4f}")

    # ── 6. Save CSV ───────────────────────────────────────────────────────────
    csv_path = f"{folder}_logistic_results.csv"
    pd.DataFrame({
        "Parameter":      [FRIENDLY.get(v, v) for v in params.index],
        "Coefficient":    params.values,
        "Std_Error":      bse.values,
        "z_statistic":    zvals.values,
        "p_value":        pvals.values,
        "Significance":   [sig_stars(p) for p in pvals],
        "Odds_Ratio":     np.exp(params.values),
        "OR_CI_lower_95": np.exp(conf[0].values),
        "OR_CI_upper_95": np.exp(conf[1].values),
    }).to_csv(csv_path, index=False)
    print(f"\nSaved → {csv_path}")

    # ── 7. Forest plot ────────────────────────────────────────────────────────
    std_z       = model_std.tvalues.drop("Intercept").abs().reindex(PREDICTORS)
    sorted_vars = std_z.sort_values().index.tolist()   # most important on top

    labels = [FRIENDLY[v] for v in sorted_vars]
    ors    = [np.exp(params[v])     for v in sorted_vars]
    ci_lo  = [np.exp(conf.loc[v, 0]) for v in sorted_vars]
    ci_hi  = [np.exp(conf.loc[v, 1]) for v in sorted_vars]
    pv     = [pvals[v]               for v in sorted_vars]
    colors = ["#c0392b" if p < 0.05 else "#2c3e50" for p in pv]

    fig, ax = plt.subplots(figsize=(10, 5.5))
    y_pos = np.arange(len(sorted_vars))

    for i, (y, lo, hi, or_, col) in enumerate(zip(y_pos, ci_lo, ci_hi, ors, colors)):
        ax.hlines(y, lo, hi, colors=col, linewidth=2.4, zorder=3)
        ax.plot(or_, y, "o", color=col, markersize=8, zorder=4, clip_on=False)
        stars = sig_stars(pv[i])
        if stars:
            ax.text(hi * 1.02, y, stars, va="center",
                    fontsize=11, color="#c0392b", fontweight="bold")

    ax.axvline(1.0, color="#888", linewidth=1.2, linestyle="--", zorder=2)
    ax.set_yticks(y_pos)
    ax.set_yticklabels(labels, fontsize=11)
    ax.set_xscale("log")
    ax.xaxis.set_major_formatter(mticker.FuncFormatter(lambda x, _: f"{x:g}"))
    ax.set_xlabel("Odds Ratio (log scale)", fontweight="bold")
    ax.set_title(
        f"Fractional Logistic Regression — Forest Plot\n{title}",
        fontsize=12, fontweight="bold", pad=12
    )
    ax.spines[["top", "right"]].set_visible(False)
    ax.grid(axis="x", linestyle="--", alpha=0.4)

    ax.legend(handles=[
        Line2D([0], [0], marker="o", color="w", markerfacecolor="#c0392b",
               markersize=9, label="p < 0.05  (significant)"),
        Line2D([0], [0], marker="o", color="w", markerfacecolor="#2c3e50",
               markersize=9, label="p ≥ 0.05  (not significant)"),
    ], loc="lower right", framealpha=0.9)

    plt.tight_layout()
    plot_path = f"{folder}_logistic_forest_plot.png"
    plt.savefig(plot_path, dpi=300)
    plt.close()
    print(f"Saved → {plot_path}")

print("\n\nAll strategies processed.")

