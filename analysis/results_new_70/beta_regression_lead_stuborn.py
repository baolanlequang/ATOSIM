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
JSON_FOLDER  = "./lead_stubborn"
CSV_PATH     = "./optimized_deterministic_lhs_configurations_70.csv"
ATTACK_LABEL = "Lead Stubborn Mining"  # ← đổi theo dataset

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
    dsps = [
        next((x["value"] for x in r if x["name"] == "DoubleSpendSuccessProbability"), None)
        for r in rounds
    ]
    rows.append({
        "config_id": int(d["inputParameters"]["config_id"]),
        "p_success": float(np.mean([v for v in dsps if v is not None])),
    })

df = cfg.merge(pd.DataFrame(rows), on="config_id")
n  = len(df)
print(f"Loaded: n={n}")
print(f"p_success: mean={df.p_success.mean():.4f}, "
      f"zero={(df.p_success==0).sum()}, one={(df.p_success==1).sum()}")

# ─────────────────────────────────────────────
# 2. TRANSFORM
# ─────────────────────────────────────────────
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
# HELPER
# ─────────────────────────────────────────────
def fmt_p(p):
    return "$<$0.001" if p < 0.001 else f"{p:.3f}"

PRED_LABELS = {
    "Intercept"              : "Intercept",
    "attacker_hash_power_z"  : "Attacker hash power",
    "number_of_nodes_z"      : "Number of nodes",
    "node_degree_z"          : "Node degree",
    "propagation_delay_z"    : "Propagation delay",
    "block_interval_z"       : "Block interval",
    "max_block_size_z"       : "Max block size",
    "tie_breaking_z"         : "Tie-breaking parameter",
}

# ─────────────────────────────────────────────
# 4. LATEX OUTPUT
# ─────────────────────────────────────────────
p_mean = df["p_trim"].mean()
scale  = p_mean * (1 - p_mean)
ci     = model.conf_int()

me_df = pd.DataFrame({
    "dy/dx"  : model.params * scale,
    "std_err": model.bse * scale,
    "z"      : model.params / model.bse,
    "p"      : model.pvalues,
})
or_df = pd.DataFrame({
    "OR"      : np.exp(model.params),
    "CI_lower": np.exp(ci[0]),
    "CI_upper": np.exp(ci[1]),
    "p"       : model.pvalues,
})

df["p_pred"] = model.predict()
pseudo_r2 = 1 - model.llf / model.llnull
rmse      = np.sqrt(np.mean((df.p_success - df.p_pred) ** 2))
mae       = np.mean(np.abs(df.p_success - df.p_pred))
corr      = np.corrcoef(df.p_success, df.p_pred)[0, 1]

print("% ── BETA REGRESSION TABLE ───────────────────────────────")
print(r"\begin{table}[h]")
print(r"\centering")
print(f"\\caption{{Beta Regression Results --- {ATTACK_LABEL} ($n={n}$)}}")
print(r"\label{tab:beta_regression}")
print(r"\begin{tabular}{lrrr}")
print(r"\toprule")
print(r"\textbf{Predictor} & \textbf{OR} & \textbf{95\% CI} & \textbf{$p$} \\")
print(r"\midrule")

for param in model.params.index:
    if param == "Intercept":
        continue
    label  = PRED_LABELS.get(param, param)
    p      = model.pvalues[param]
    OR     = or_df.loc[param, "OR"]
    ci_lo  = or_df.loc[param, "CI_lower"]
    ci_hi  = or_df.loc[param, "CI_upper"]

    print(f"{label} & {OR:.3f} & [{ci_lo:.3f},\\ {ci_hi:.3f}] & {fmt_p(p)} \\\\")

print(r"\bottomrule")
print(r"\end{tabular}")
print(r"\end{table}")