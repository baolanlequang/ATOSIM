"""
DSP Distribution Analysis
==========================
Characterizes the distribution of the Double-Spend Success Probability (DSP)
metric: normality tests, zero/one point-mass fractions, and a Beta fit for
the interior (0,1) values.

Output
------
  analysis/figures/dsp_distribution_fit.png – histogram with point masses
                                               and Beta fit overlay, plus
                                               Q-Q plot vs. normal
  analysis/tables/dsp_distribution_summary.csv – summary statistics
"""

import os
import json
import numpy as np
import pandas as pd
import matplotlib
import matplotlib.pyplot as plt
from scipy import stats

matplotlib.use("Agg")
matplotlib.rcParams.update({
    "font.family": "DejaVu Sans",
    "font.size": 11,
    "axes.titlesize": 12,
    "axes.labelsize": 11,
    "figure.dpi": 300,
})

SCRIPT_DIR  = os.path.dirname(os.path.abspath(__file__))
RESULTS_DIR = os.path.join(SCRIPT_DIR, "..", "results")
FIGURES_DIR = os.path.join(SCRIPT_DIR, "figures")
TABLES_DIR  = os.path.join(SCRIPT_DIR, "tables")
os.makedirs(FIGURES_DIR, exist_ok=True)
os.makedirs(TABLES_DIR, exist_ok=True)

STRATEGY = "selfish"  # all strategies share the same LHS sample / DSP values


def load_dsp(folder_name: str) -> np.ndarray:
    folder = os.path.join(RESULTS_DIR, folder_name)
    vals = []
    for fname in sorted(os.listdir(folder)):
        if not (fname.startswith("result_run_") and fname.endswith(".json")):
            continue
        with open(os.path.join(folder, fname)) as fh:
            d = json.load(fh)
        avg = d["simulationResult"]["averageSimulationRoundResult"]
        dsp = next(
            (float(m["average"]) for m in avg
             if m.get("name") == "DoubleSpendSuccessProbability"
             and isinstance(m.get("average"), (int, float))),
            None,
        )
        if dsp is not None:
            vals.append(dsp)
    return np.array(vals)


# ─────────────────────────────────────────────────────────────────────────────
# Load data
# ─────────────────────────────────────────────────────────────────────────────
vals = load_dsp(STRATEGY)
n = len(vals)
at_zero = vals == 0
at_one  = vals == 1
interior = vals[~at_zero & ~at_one]

# ─────────────────────────────────────────────────────────────────────────────
# Statistics
# ─────────────────────────────────────────────────────────────────────────────
shapiro = stats.shapiro(vals)
ks = stats.kstest((vals - vals.mean()) / vals.std(), "norm")

a, b, _, _ = stats.beta.fit(interior, floc=0, fscale=1)

summary = pd.DataFrame([
    {"Metric": "n",                          "Value": n},
    {"Metric": "mean",                       "Value": vals.mean()},
    {"Metric": "std",                        "Value": vals.std()},
    {"Metric": "skewness",                   "Value": stats.skew(vals)},
    {"Metric": "excess kurtosis",            "Value": stats.kurtosis(vals)},
    {"Metric": "fraction == 0",              "Value": at_zero.mean()},
    {"Metric": "fraction == 1",              "Value": at_one.mean()},
    {"Metric": "fraction in (0,1)",          "Value": len(interior) / n},
    {"Metric": "Shapiro-Wilk W",             "Value": shapiro.statistic},
    {"Metric": "Shapiro-Wilk p-value",       "Value": shapiro.pvalue},
    {"Metric": "KS D (vs. standard normal)", "Value": ks.statistic},
    {"Metric": "KS p-value",                 "Value": ks.pvalue},
    {"Metric": "Beta fit alpha (interior)",  "Value": a},
    {"Metric": "Beta fit beta (interior)",   "Value": b},
])
summary.to_csv(os.path.join(TABLES_DIR, "dsp_distribution_summary.csv"), index=False)
print(summary.to_string(index=False))

# ─────────────────────────────────────────────────────────────────────────────
# Plot: histogram with point masses + Beta fit, and Q-Q plot vs normal
# ─────────────────────────────────────────────────────────────────────────────
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(12, 5))

# Histogram of interior values + point masses at 0 and 1 as separate bars
bins = np.linspace(0, 1, 21)
ax1.hist(interior, bins=bins, color="#2980b9", edgecolor="white",
         alpha=0.85, density=False, label="Interior values (0,1)")
bar_w = bins[1] - bins[0]
ax1.bar(0, at_zero.sum(), width=bar_w, color="#e74c3c", edgecolor="white",
        align="edge", label=f"DSP = 0 (n={at_zero.sum()}, {at_zero.mean()*100:.1f}%)")
ax1.bar(1 - bar_w, at_one.sum(), width=bar_w, color="#27ae60", edgecolor="white",
        align="edge", label=f"DSP = 1 (n={at_one.sum()}, {at_one.mean()*100:.1f}%)")

# Beta fit overlay (scaled so its area matches the interior histogram area)
x = np.linspace(0.001, 0.999, 200)
pdf = stats.beta.pdf(x, a, b)
pdf_scaled = pdf * len(interior) * bar_w
ax1.plot(x, pdf_scaled, color="black", linewidth=2,
         label=f"Beta fit (α={a:.2f}, β={b:.2f})")

ax1.set_xlabel("Double-Spend Success Probability (DSP)")
ax1.set_ylabel("Count")
ax1.set_title(f"DSP Distribution — {STRATEGY}\n"
               f"mean={vals.mean():.3f}, std={vals.std():.3f}, "
               f"Shapiro p={shapiro.pvalue:.1e}", fontweight="bold")
ax1.legend(fontsize=8)
ax1.grid(axis="y", linestyle="--", alpha=0.4)

# Q-Q plot vs normal
stats.probplot(vals, dist="norm", plot=ax2)
ax2.set_title("Q-Q Plot vs. Normal Distribution", fontweight="bold")
ax2.get_lines()[0].set_markerfacecolor("#2980b9")
ax2.get_lines()[0].set_markeredgecolor("none")
ax2.get_lines()[1].set_color("#e74c3c")
ax2.grid(linestyle="--", alpha=0.4)

fig.suptitle("Is DSP Normally Distributed? — Zero-One-Inflated Bounded Distribution",
              fontsize=13, fontweight="bold", y=1.02)
plt.tight_layout()
fpath = os.path.join(FIGURES_DIR, "dsp_distribution_fit.png")
plt.savefig(fpath, dpi=300, bbox_inches="tight")
plt.close()
print(f"\nSaved -> figures/dsp_distribution_fit.png")
print(f"Saved -> tables/dsp_distribution_summary.csv")
