"""
Simple DSP Distribution Plot
=============================
Plots a histogram of the Double-Spend Success Probability (DSP) for each
attack strategy.

Output
------
  analysis/figures/dsp_distributions_simple.png
"""

import os
import json
import numpy as np
import matplotlib
import matplotlib.pyplot as plt

matplotlib.use("Agg")

SCRIPT_DIR  = os.path.dirname(os.path.abspath(__file__))
RESULTS_DIR = os.path.join(SCRIPT_DIR, "..", "results")
FIGURES_DIR = os.path.join(SCRIPT_DIR, "figures")
os.makedirs(FIGURES_DIR, exist_ok=True)

STRATEGIES = {
    "selfish":               "Selfish Mining",
    "lead_stubborn":         "Lead-Stubborn Mining",
    "trail_stubborn":        "Trail-Stubborn Mining",
    "selfish_lead_stubborn": "Combined Selfish + Lead-Stubborn",
    "selfish_trail_stubborn":"Combined Selfish + Trail-Stubborn",
}


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


fig, axes = plt.subplots(2, 3, figsize=(15, 8))
axes = axes.flatten()

for ax, (folder, title) in zip(axes, STRATEGIES.items()):
    vals = load_dsp(folder)
    ax.hist(vals, bins=20, range=(0, 1), color="#2980b9", edgecolor="white")
    ax.set_title(title, fontweight="bold")
    ax.set_xlabel("Success Probability")
    ax.set_ylabel("Count")

axes[-1].axis("off")
fig.suptitle("Distribution per Attack", fontsize=14, fontweight="bold")
plt.tight_layout()
fpath = os.path.join(FIGURES_DIR, "dsp_distributions_simple.png")
plt.savefig(fpath, dpi=150, bbox_inches="tight")
plt.close()
print(f"Saved -> {fpath}")
