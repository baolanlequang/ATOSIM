import os, glob, json, warnings
import numpy as np
import pandas as pd
from scipy.special import logit
import statsmodels.formula.api as smf
from statsmodels.genmod import families

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
    "validator_count"        : "number_of_nodes",
    "block_creation_interval": "block_interval",
    "tie_breaking_parameter" : "tie_breaking",
})
cfg = cfg.drop(columns=["attacker_hash_power_realized"], errors="ignore")

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
df["p_trim"] = (df["p_success"] * (n - 1) + 0.5) / n

for col in ["number_of_nodes", "node_degree", "propagation_delay",
            "block_interval", "max_block_size", "attacker_hash_power", "tie_breaking"]:
    df[col + "_z"] = (df[col] - df[col].mean()) / df[col].std()

# ─────────────────────────────────────────────
# 3. BETA REGRESSION
# ─────────────────────────────────────────────
formula = ("p_trim ~ attacker_hash_power_z + number_of_nodes_z + node_degree_z"
           " + propagation_delay_z + block_interval_z + max_block_size_z + tie_breaking_z")

model = smf.glm(formula, data=df, family=families.Binomial()).fit()

# ─────────────────────────────────────────────
# 4. KẾT QUẢ
# ─────────────────────────────────────────────
print("\n" + "=" * 60)
print("COEFFICIENTS")
print("=" * 60)
print(model.summary().tables[1])

print("\n" + "=" * 60)
print("MARGINAL EFFECTS")
print("=" * 60)
p_mean = df["p_trim"].mean()
scale  = p_mean * (1 - p_mean)
me_df  = pd.DataFrame({
    "dy/dx"  : model.params * scale,
    "std_err": model.bse * scale,
    "z"      : model.params / model.bse,
    "p"      : model.pvalues,
}).round(4)
print(me_df)

print("\n" + "=" * 60)
print("ODDS RATIOS")
print("=" * 60)
ci = model.conf_int()
or_df = pd.DataFrame({
    "OR"      : np.exp(model.params),
    "CI_lower": np.exp(ci[0]),
    "CI_upper": np.exp(ci[1]),
    "p"       : model.pvalues,
}).round(4)
print(or_df)

print("\n" + "=" * 60)
print("MODEL FIT")
print("=" * 60)
df["p_pred"]  = model.predict()
rmse      = np.sqrt(np.mean((df["p_success"] - df["p_pred"]) ** 2))
mae       = np.mean(np.abs(df["p_success"] - df["p_pred"]))
corr      = np.corrcoef(df["p_success"], df["p_pred"])[0, 1]
pseudo_r2 = 1 - model.llf / model.llnull
print(f"  Pseudo R² (McFadden) : {pseudo_r2:.4f}")
print(f"  RMSE                 : {rmse:.4f}")
print(f"  MAE                  : {mae:.4f}")
print(f"  Corr(actual, pred)   : {corr:.4f}")