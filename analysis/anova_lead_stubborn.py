import os, glob, json, warnings
import numpy as np
import pandas as pd
from scipy import stats
from scipy.special import logit
import statsmodels.formula.api as smf
from statsmodels.stats.multicomp import pairwise_tukeyhsd
from statsmodels.stats.anova import anova_lm

warnings.filterwarnings("ignore")

# ─────────────────────────────────────────────
# CẤU HÌNH
# ─────────────────────────────────────────────
JSON_FOLDER = "../results/lead_stubborn"
CSV_PATH    = "../results/optimized_deterministic_lhs_configurations.csv"

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
    "validator_count"             : "number_of_nodes",
    "block_creation_interval"     : "block_interval",
    "attacker_hash_power_realized": "attacker_hp",
    "attacker_hash_power"         : "attacker_hp_nominal",
    "tie_breaking_parameter"      : "tie_breaking",
})

rows = []
for fpath in glob.glob(os.path.join(JSON_FOLDER, "result_run_*.json")):
    d = read_json(fpath)
    if not d:
        continue
    rounds = d["simulationResult"]["simulationRoundResults"]
    dsps = [
        next((x["value"] for x in r if x["name"] == "DoubleSpendSuccessProbability"), None)
        for r in rounds
    ]
    rows.append({
        "config_id": int(d["inputParameters"]["config_id"]),
        "p_success": float(np.mean([v for v in dsps if v is not None])),
    })

df = cfg.merge(pd.DataFrame(rows), on="config_id")
print(f"Loaded: {len(df)} runs | p_success mean={df.p_success.mean():.3f} "
      f"| zero={(df.p_success==0).sum()} | one={(df.p_success==1).sum()}")

# ─────────────────────────────────────────────
# 2. TRANSFORM
# ─────────────────────────────────────────────
n = len(df)
df["p_trim"]  = (df["p_success"] * (n - 1) + 0.5) / n
df["p_logit"] = logit(df["p_trim"])

# ─────────────────────────────────────────────
# 3. BINNING
# ─────────────────────────────────────────────
df["hp_cat"] = pd.cut(
    df["attacker_hp"],
    bins=[-0.001, 0.001, 0.33, 0.50],
    labels=["Zero", "Low", "High"]
)
for src, dst in [("propagation_delay", "delay_cat"),
                 ("block_interval",    "interval_cat"),
                 ("number_of_nodes",   "nodes_cat"),
                 ("node_degree",       "degree_cat"),
                 ("max_block_size",    "blocksize_cat"),
                 ("tie_breaking",      "tiebreak_cat")]:
    q33, q67 = df[src].quantile([0.333, 0.667])
    df[dst] = pd.cut(df[src], bins=[-np.inf, q33, q67, np.inf],
                     labels=["Low", "Mid", "High"])

# ─────────────────────────────────────────────
# 4. FACTORIAL ANOVA
# ─────────────────────────────────────────────
formula = ("p_logit ~ C(hp_cat) + C(nodes_cat) + C(degree_cat)"
           " + C(delay_cat) + C(interval_cat) + C(blocksize_cat) + C(tiebreak_cat)"
           " + C(hp_cat):C(delay_cat) + C(hp_cat):C(interval_cat)")

model = smf.ols(formula, data=df.dropna()).fit()
anova_table = anova_lm(model, typ=2)
anova_table["eta_sq"] = anova_table["sum_sq"] / anova_table["sum_sq"].sum()

print("\n" + "=" * 60)
print("FACTORIAL ANOVA")
print("=" * 60)
print(anova_table.round(4).to_string())

# ─────────────────────────────────────────────
# 5. TUKEY HSD
# ─────────────────────────────────────────────
sub   = df.dropna(subset=["hp_cat"])
tukey = pairwise_tukeyhsd(sub["p_logit"], sub["hp_cat"], alpha=0.05)

print("\n" + "=" * 60)
print("TUKEY HSD (hp_cat)")
print("=" * 60)
print(tukey.summary())

# ─────────────────────────────────────────────
# 6. ONE-WAY ANOVA PER FACTOR
# ─────────────────────────────────────────────
print("\n" + "=" * 60)
print("ONE-WAY ANOVA PER FACTOR")
print("=" * 60)
print(f"{'Factor':<20} {'F':>8} {'p(ANOVA)':>12} {'H(KW)':>8} {'p(KW)':>12} {'Sig':>5}")
print("-" * 65)

cat_map = {
    "hp_cat"       : "attacker_hp",
    "nodes_cat"    : "number_of_nodes",
    "degree_cat"   : "node_degree",
    "delay_cat"    : "propagation_delay",
    "interval_cat" : "block_interval",
    "blocksize_cat": "max_block_size",
    "tiebreak_cat" : "tie_breaking",
}
for cat, src in cat_map.items():
    groups_logit = [g["p_logit"].values for _, g in df.groupby(cat, observed=True)]
    groups_raw   = [g["p_success"].values for _, g in df.groupby(cat, observed=True)]
    F,  p_f  = stats.f_oneway(*groups_logit)
    H,  p_kw = stats.kruskal(*groups_raw)
    sig = "***" if min(p_f, p_kw) < 0.001 else (
          "**"  if min(p_f, p_kw) < 0.01  else (
          "*"   if min(p_f, p_kw) < 0.05  else "ns"))
    print(f"{cat:<20} {F:>8.2f} {p_f:>12.4e} {H:>8.2f} {p_kw:>12.4e} {sig:>5}")
    
    
print()
print("=" * 60)
print("STEP 7 — FINISHED: Results Summary")
print(anova_table.round(4))
print(anova_table["eta_sq"].round(4))
print(tukey.summary())
print()
print("=" * 60)
print("DONE")
print("=" * 60)