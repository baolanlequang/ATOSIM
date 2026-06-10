"""
F-ANOVA Pipeline: Selfish Mining Simulation
============================================
Biến phụ thuộc : p_success = mean(DoubleSpendSuccessProbability) per run
Independent    : 7 simulation parameters
Pipeline       : load → transform (logit) → bin → ANOVA → post-hoc (Tukey)
"""

import os, glob, json, warnings
import numpy as np
import pandas as pd
from scipy import stats
from scipy.special import logit
import statsmodels.api as sm
import statsmodels.formula.api as smf
from statsmodels.stats.multicomp import pairwise_tukeyhsd
from statsmodels.stats.anova import anova_lm
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import matplotlib.gridspec as gridspec

warnings.filterwarnings("ignore")

# ─────────────────────────────────────────────
# 1. LOAD DATA
# ─────────────────────────────────────────────
JSON_FOLDER = "../results/selfish" 
CSV_PATH    = "../results/optimized_deterministic_lhs_configurations.csv"

# 1a. Đọc parameters từ CSV
cfg = pd.read_csv(CSV_PATH)
cfg = cfg.rename(columns={
    "validator_count"             : "number_of_nodes",
    "node_degree"                 : "node_degree",
    "propagation_delay"           : "propagation_delay",
    "block_creation_interval"     : "block_interval",
    "max_block_size"              : "max_block_size",
    "attacker_hash_power"         : "attacker_hash_power", 
    "tie_breaking_parameter"      : "tie_breaking",
})


# 1b. Đọc p_success từ JSON files
rows = []
files = glob.glob(os.path.join(JSON_FOLDER, "result_run_*.json"))
for fpath in files:
    with open(fpath, "r") as f:
        d = json.load(f)
    config_id = int(d["inputParameters"]["config_id"])
    rounds = d["simulationResult"]["simulationRoundResults"]
    dsps = [
        next((x["value"] for x in r if x["name"] == "DoubleSpendSuccessProbability"), None)
        for r in rounds
    ]
    dsps = [v for v in dsps if v is not None]
    rows.append({"config_id": config_id, "p_success": float(np.mean(dsps))})

results = pd.DataFrame(rows)

# 1c. Join theo config_id
df = cfg.merge(results, on="config_id")

print(f"  Loaded: {len(df)} runs")
print(f"  p_success: mean={df.p_success.mean():.3f}, "
      f"zero={(df.p_success==0).sum()}, one={(df.p_success==1).sum()}\n")

# ─────────────────────────────────────────────
# 2. LOGIT TRANSFORM
# ─────────────────────────────────────────────
print("=" * 60)
print("STEP 2 — Logit transform (Smithson & Verkuilen 2006)")
print("=" * 60)

# Trim boundary: y' = (y*(n-1) + 0.5) / n
n = len(df)
df["p_trimmed"] = (df["p_success"] * (n - 1) + 0.5) / n
df["p_logit"] = logit(df["p_trimmed"])

# Normality check before vs after
stat_raw,  p_raw  = stats.shapiro(df["p_success"].sample(200, random_state=0))
stat_logit, p_logit_sw = stats.shapiro(df["p_logit"].sample(200, random_state=0))

print(f"  Shapiro-Wilk raw    : W={stat_raw:.4f}, p={p_raw:.2e}")
print(f"  Shapiro-Wilk logit  : W={stat_logit:.4f}, p={p_logit_sw:.2e}")
if p_logit_sw < 0.05:
    print("  ⚠  Logit vẫn vi phạm normality → dùng Kruskal-Wallis song song\n")
else:
    print("  ✓  Logit đạt normality\n")

# ─────────────────────────────────────────────
# 3. BINNING
# ─────────────────────────────────────────────
print("=" * 60)
print("STEP 3 — Discretize parameters thành Low / Mid / High")
print("=" * 60)

# attacker_hash_power: domain-driven bins (0 = no attacker, ~0.25 = known threshold, ~0.49 = near 50%)
df["hp_cat"] = pd.cut(
    df["attacker_hash_power"],
    bins=[-0.001, 0.001, 0.33, 0.50],
    labels=["Zero", "Low", "High"]
)

# Remaining variables: tertile-based
tertile_vars = {
    "number_of_nodes"  : "nodes_cat",
    "node_degree"      : "degree_cat",
    "propagation_delay": "delay_cat",
    "block_interval"   : "interval_cat",
    "max_block_size"   : "blocksize_cat",
    "tie_breaking"     : "tiebreak_cat",
}
for src, dst in tertile_vars.items():
    q33, q67 = df[src].quantile([0.333, 0.667])
    df[dst] = pd.cut(df[src], bins=[-np.inf, q33, q67, np.inf],
                     labels=["Low", "Mid", "High"])

cat_cols = ["hp_cat"] + list(tertile_vars.values())
for c in cat_cols:
    print(f"  {c}: {df[c].value_counts().to_dict()}")
print()

# ─────────────────────────────────────────────
# 4. ONE-WAY ANOVA / KRUSKAL-WALLIS (each factor)
# ─────────────────────────────────────────────
print("=" * 60)
print("STEP 4 — One-way tests (F-ANOVA + Kruskal-Wallis) per factor")
print("=" * 60)
print(f"  {'Factor':<20} {'F':>8} {'p(ANOVA)':>12} {'H(KW)':>8} {'p(KW)':>12} {'Sig':>5}")
print("  " + "-" * 70)

results_oneway = []
for cat, src in zip(cat_cols, ["attacker_hash_power"] + list(tertile_vars.keys())):
    groups_logit = [grp["p_logit"].values for _, grp in df.groupby(cat, observed=True)]
    groups_raw   = [grp["p_success"].values for _, grp in df.groupby(cat, observed=True)]

    F, p_f = stats.f_oneway(*groups_logit)
    H, p_kw = stats.kruskal(*groups_raw)
    sig = "***" if min(p_f, p_kw) < 0.001 else ("**" if min(p_f, p_kw) < 0.01
          else ("*" if min(p_f, p_kw) < 0.05 else "ns"))
    results_oneway.append(dict(factor=cat, F=F, p_anova=p_f, H=H, p_kw=p_kw, sig=sig))
    print(f"  {cat:<20} {F:>8.2f} {p_f:>12.4e} {H:>8.2f} {p_kw:>12.4e} {sig:>5}")

print()

# ─────────────────────────────────────────────
# 5. FACTORIAL ANOVA (top 3 factors: hp_cat + 2 others)
# ─────────────────────────────────────────────
print("=" * 60)
print("STEP 5 — Factorial ANOVA (Three-way: hp_cat × delay_cat × interval_cat)")
print("=" * 60)

formula = "p_logit ~ C(hp_cat) + C(delay_cat) + C(interval_cat) " \
          "+ C(hp_cat):C(delay_cat) + C(hp_cat):C(interval_cat)"

model  = smf.ols(formula, data=df.dropna(subset=["hp_cat","delay_cat","interval_cat"])).fit()
anova_table = anova_lm(model, typ=2)
print(anova_table.round(4).to_string())
print()

# Effect size: eta-squared
ss_total = anova_table["sum_sq"].sum()
anova_table["eta_sq"] = anova_table["sum_sq"] / ss_total
print("  Eta-squared (effect size):")
for idx, row in anova_table.iterrows():
    if idx != "Residual":
        print(f"    {idx:<45} η²={row['eta_sq']:.4f}")
print()

# ─────────────────────────────────────────────
# 6. POST-HOC TUKEY HSD (for hp_cat, the dominant factor)
# ─────────────────────────────────────────────
print("=" * 60)
print("STEP 6 — Post-hoc Tukey HSD (hp_cat → p_logit)")
print("=" * 60)

sub = df.dropna(subset=["hp_cat"])
tukey = pairwise_tukeyhsd(sub["p_logit"], sub["hp_cat"], alpha=0.05)
print(tukey.summary())
print()

# ─────────────────────────────────────────────
# 7. ASSUMPTION CHECKS
# ─────────────────────────────────────────────
print("=" * 60)
print("STEP 7 — Kiểm tra giả định ANOVA")
print("=" * 60)

resid = model.resid
stat_r, p_r = stats.shapiro(resid.sample(min(200, len(resid)), random_state=0))
print(f"  Normality of residuals (Shapiro-Wilk): W={stat_r:.4f}, p={p_r:.4e}")

# Levene test for homoscedasticity (hp_cat)
groups_resid = [resid[sub[sub["hp_cat"]==g].index].values
                for g in sub["hp_cat"].cat.categories]
lev_stat, lev_p = stats.levene(*[g for g in groups_resid if len(g) > 0])
print(f"  Homoscedasticity (Levene, hp_cat)      : stat={lev_stat:.4f}, p={lev_p:.4e}")
print()

# ─────────────────────────────────────────────
# 8. PLOTS
# ─────────────────────────────────────────────
fig = plt.figure(figsize=(16, 12))
fig.suptitle("F-ANOVA Pipeline — Selfish Mining (DoubleSpendSuccessProbability)",
             fontsize=13, fontweight="bold", y=0.98)
gs = gridspec.GridSpec(2, 3, figure=fig, hspace=0.45, wspace=0.35)

# ── 8a. Raw vs logit distribution
ax0 = fig.add_subplot(gs[0, 0])
ax0.hist(df["p_success"], bins=30, color="#378ADD", alpha=0.7, edgecolor="white")
ax0.set_title("Raw p_success", fontsize=10)
ax0.set_xlabel("p_success"); ax0.set_ylabel("Count")

ax1 = fig.add_subplot(gs[0, 1])
ax1.hist(df["p_logit"], bins=30, color="#1D9E75", alpha=0.7, edgecolor="white")
ax1.set_title("Logit(p_success) — after trim", fontsize=10)
ax1.set_xlabel("logit(p)")

# ── 8b. QQ-plot of residuals
ax2 = fig.add_subplot(gs[0, 2])
sm.qqplot(resid, line="s", ax=ax2, alpha=0.5, markersize=3)
ax2.set_title("Q-Q plot: model residuals", fontsize=10)

# ── 8c. Boxplot: hp_cat (dominant factor)
ax3 = fig.add_subplot(gs[1, 0])
data_hp = [sub[sub["hp_cat"] == g]["p_success"].values
           for g in sub["hp_cat"].cat.categories]
bp = ax3.boxplot(data_hp, labels=sub["hp_cat"].cat.categories,
                 patch_artist=True, medianprops=dict(color="black", linewidth=2))
colors = ["#F7C1C1", "#FAC775", "#9FE1CB"]
for patch, color in zip(bp["boxes"], colors):
    patch.set_facecolor(color)
ax3.set_title("p_success by attacker_hash_power", fontsize=10)
ax3.set_xlabel("hp_cat"); ax3.set_ylabel("p_success (raw)")

# ── 8d. One-way F and KW p-values
ax4 = fig.add_subplot(gs[1, 1])
df_res = pd.DataFrame(results_oneway)
short_names = [c.replace("_cat","") for c in df_res["factor"]]
x = np.arange(len(short_names))
bars = ax4.bar(x, -np.log10(df_res["p_anova"].clip(1e-300)), color="#378ADD", alpha=0.8, label="ANOVA −log10(p)")
ax4.bar(x, -np.log10(df_res["p_kw"].clip(1e-300)), color="#1D9E75", alpha=0.5, label="Kruskal-Wallis", width=0.4)
ax4.axhline(-np.log10(0.05), color="red", linestyle="--", linewidth=1, label="p=0.05")
ax4.set_xticks(x); ax4.set_xticklabels(short_names, rotation=30, ha="right", fontsize=8)
ax4.set_title("Significance per factor (−log10 p)", fontsize=10)
ax4.set_ylabel("−log10(p)")
ax4.legend(fontsize=7)

# ── 8e. Eta-squared bar chart
ax5 = fig.add_subplot(gs[1, 2])
eta = anova_table[anova_table.index != "Residual"]["eta_sq"]
short = [s.replace("C(","").replace(")","").replace("hp_cat","hp")
          .replace("delay_cat","delay").replace("interval_cat","interval")
         for s in eta.index]
colors_eta = ["#CECBF6" if ":" not in s else "#AFA9EC" for s in short]
ax5.barh(short, eta.values, color=colors_eta, edgecolor="white")
ax5.axvline(0.01, color="orange", linestyle="--", linewidth=1, label="η²=0.01 (small)")
ax5.axvline(0.06, color="red",    linestyle="--", linewidth=1, label="η²=0.06 (medium)")
ax5.set_title("Effect size (η²) — factorial model", fontsize=10)
ax5.set_xlabel("η²")
ax5.legend(fontsize=7)

plt.savefig("./figures/anova_selfish_results.png",
            dpi=150, bbox_inches="tight", facecolor="white")
print("  Đã lưu biểu đồ: anova_selfish_results.png")
print()
print("=" * 60)
print("STEP 8 — FINISHED: Results Summary")
print(anova_table.round(4))
print(anova_table["eta_sq"].round(4))
print(tukey.summary())
print()
print("=" * 60)
print("DONE")
print("=" * 60)
