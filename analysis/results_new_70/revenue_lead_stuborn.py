import os, glob, json, warnings
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.gridspec as gridspec

warnings.filterwarnings("ignore")

# ─────────────────────────────────────────────
# CẤU HÌNH
# ─────────────────────────────────────────────
JSON_FOLDER  = "./lead_stubborn"
CSV_PATH     = "./optimized_deterministic_lhs_configurations_70.csv"
ATTACK_LABEL = "Lead Stubborn Mining"
OUTPUT_PATH  = "./figures/scatter_revenue_share_lead_stubborn.png"

# ─────────────────────────────────────────────
# 1. LOAD DATA
# ─────────────────────────────────────────────
def read_json(fpath):
    with open(fpath, "rb") as f:
        raw = f.read()
    for enc in ["utf-8", "utf-8-sig", "utf-16", "latin-1"]:
        try:
            return json.loads(raw.decode(enc))
        except:
            continue
    return None

cfg = pd.read_csv(CSV_PATH).rename(columns={
    "validator_count"        : "number_of_nodes",
    "block_creation_interval": "block_interval",
    "tie_breaking_parameter" : "tie_breaking",
})

rows = []
for fpath in glob.glob(os.path.join(JSON_FOLDER, "result_run_*.json")):
    d = read_json(fpath)
    if not d:
        continue
    rounds = d["simulationResult"]["simulationRoundResults"]
    rev = [
        next((x["value"] for x in r if x["name"] == "Attacker Revenue Share"), None)
        for r in rounds
    ]
    rows.append({
        "config_id"    : int(d["inputParameters"]["config_id"]),
        "revenue_share": float(np.mean([v for v in rev if v is not None])),
    })

df = cfg.merge(pd.DataFrame(rows), on="config_id")
print(f"Loaded: n={len(df)}, revenue_share mean={df.revenue_share.mean():.4f}")

# ─────────────────────────────────────────────
# 2. SCATTER PLOTS
# ─────────────────────────────────────────────
params = {
    "attacker_hash_power": "Attacker Hash Power",
    "number_of_nodes"    : "Number of Nodes",
    "node_degree"        : "Node Degree",
    "propagation_delay"  : "Propagation Delay",
    "block_interval"     : "Block Interval",
    "max_block_size"     : "Max Block Size",
    "tie_breaking"       : "Tie-breaking Parameter",
}

fig = plt.figure(figsize=(18, 10))
fig.suptitle(f"Effect of Parameters on Attacker Revenue Share\n{ATTACK_LABEL}",
             fontsize=13, fontweight="bold", y=1.01)

gs = gridspec.GridSpec(2, 4, figure=fig, hspace=0.45, wspace=0.35)

for i, (col, label) in enumerate(params.items()):
    row_idx = i // 4
    col_idx = i % 4
    ax = fig.add_subplot(gs[row_idx, col_idx])

    # Scatter
    ax.scatter(df[col], df["revenue_share"],
               color="#378ADD", alpha=0.7, s=40, edgecolors="white", linewidth=0.5)

    # Trend line (linear fit)
    z    = np.polyfit(df[col], df["revenue_share"], 1)
    p    = np.poly1d(z)
    x_   = np.linspace(df[col].min(), df[col].max(), 100)
    ax.plot(x_, p(x_), color="#E24B4A", linewidth=1.5, linestyle="--", label="Trend")

    # Correlation
    r, pval = __import__("scipy").stats.pearsonr(df[col], df["revenue_share"])
    sig = "***" if pval < 0.001 else ("**" if pval < 0.01 else ("*" if pval < 0.05 else "ns"))
    ax.set_title(label, fontsize=9, fontweight="bold")
    ax.set_xlabel(label, fontsize=8)
    ax.set_ylabel("Revenue Share (%)" if col_idx == 0 else "", fontsize=8)
    ax.tick_params(labelsize=7)
    ax.annotate(f"r={r:.3f} ({sig})", xy=(0.05, 0.92),
                xycoords="axes fraction", fontsize=7.5,
                color="#E24B4A" if pval < 0.05 else "gray")

# Ẩn subplot thừa (vị trí [1,3])
fig.add_subplot(gs[1, 3]).set_visible(False)

plt.savefig(OUTPUT_PATH, dpi=150, bbox_inches="tight", facecolor="white")
print(f"Saved: {OUTPUT_PATH}")