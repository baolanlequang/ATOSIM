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
JSON_FOLDER  = "./lead_stubborn"
CSV_PATH     = "./optimized_deterministic_lhs_configurations_70.csv"
ATTACK_LABEL = "Lead Stubborn Mining"

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

df  = cfg.merge(pd.DataFrame(rows), on="config_id")
n   = len(df)
df["p_trim"]  = (df["p_success"] * (n - 1) + 0.5) / n
df["p_logit"] = logit(df["p_trim"])

# ─────────────────────────────────────────────
# 2. BINNING
# ─────────────────────────────────────────────
df["hp_cat"] = pd.cut(
    df["attacker_hash_power"],
    bins=[-0.001, 0.001, 0.33, 0.50],
    labels=["Zero", "Low", "High"]
)
for src, dst in [
    ("propagation_delay", "delay_cat"),
    ("block_interval",    "interval_cat"),
    ("number_of_nodes",   "nodes_cat"),
    ("node_degree",       "degree_cat"),
    ("max_block_size",    "blocksize_cat"),
    ("tie_breaking",      "tiebreak_cat"),
]:
    q33, q67 = df[src].quantile([0.333, 0.667])
    df[dst] = pd.cut(df[src], bins=[-np.inf, q33, q67, np.inf],
                     labels=["Low", "Mid", "High"])

cat_map = {
    "hp_cat"       : "attacker_hash_power",
    "nodes_cat"    : "number_of_nodes",
    "degree_cat"   : "node_degree",
    "delay_cat"    : "propagation_delay",
    "interval_cat" : "block_interval",
    "blocksize_cat": "max_block_size",
    "tiebreak_cat" : "tie_breaking",
}

FACTOR_LABELS = {
    "hp_cat"       : "Hash power",
    "nodes_cat"    : "Number of nodes",
    "degree_cat"   : "Node degree",
    "delay_cat"    : "Propagation delay",
    "interval_cat" : "Block interval",
    "blocksize_cat": "Max block size",
    "tiebreak_cat" : "Tie-breaking",
}

# ─────────────────────────────────────────────
# HELPER: format p-value
# ─────────────────────────────────────────────
def fmt_p(p):
    if p < 0.001:
        return "$<$0.001"
    return f"{p:.3f}"

def sig_stars(p):
    if p < 0.001: return "***"
    if p < 0.01:  return "**"
    if p < 0.05:  return "*"
    return "ns"

# ─────────────────────────────────────────────
# 3. ONE-WAY ANOVA — LaTeX
# ─────────────────────────────────────────────
oneway_rows = []
for cat, src in cat_map.items():
    groups_logit = [g["p_logit"].values for _, g in df.groupby(cat, observed=True) if len(g) > 0]
    groups_raw   = [g["p_success"].values for _, g in df.groupby(cat, observed=True) if len(g) > 0]
    if len(groups_logit) < 2:
        continue
    F, p_f = stats.f_oneway(*groups_logit)
    oneway_rows.append((FACTOR_LABELS[cat], F, p_f))

print("% ── ONE-WAY ANOVA TABLE ──────────────────────────────────")
print(r"\begin{table}[h]")
print(r"\centering")
print(f"\\caption{{One-way ANOVA per Factor --- {ATTACK_LABEL} ($n={n}$)}}")
print(r"\label{tab:oneway_anova}")
print(r"\begin{tabular}{lrr}")
print(r"\toprule")
print(r"\textbf{Parameter} & \textbf{F-statistic} & \textbf{$p$-value} \\")
print(r"\midrule")
for label, F, p_f in oneway_rows:
    print(f"{label} & {F:.3f} & {fmt_p(p_f)} \\\\")
print(r"\bottomrule")
print(r"\end{tabular}")
print(r"\end{table}")
print()

# ─────────────────────────────────────────────
# 4. FACTORIAL ANOVA — LaTeX
# ─────────────────────────────────────────────
formula = (
    "p_logit ~ C(hp_cat) + C(nodes_cat) + C(degree_cat)"
    " + C(delay_cat) + C(interval_cat) + C(blocksize_cat) + C(tiebreak_cat)"
)
model       = smf.ols(formula, data=df.dropna()).fit()
anova_table = anova_lm(model, typ=2)
anova_table["eta_sq"] = anova_table["sum_sq"] / anova_table["sum_sq"].sum()

SOURCE_LABELS = {
    "C(hp_cat)"      : "Hash power ($\\alpha_\\text{hp}$)",
    "C(nodes_cat)"   : "Number of nodes ($\\alpha_\\text{nodes}$)",
    "C(degree_cat)"  : "Node degree ($\\alpha_\\text{degree}$)",
    "C(delay_cat)"   : "Propagation delay ($\\alpha_\\text{delay}$)",
    "C(interval_cat)": "Block interval ($\\alpha_\\text{interval}$)",
    "C(blocksize_cat)": "Max block size ($\\alpha_\\text{blocksize}$)",
    "C(tiebreak_cat)": "Tie-breaking ($\\alpha_\\text{tiebreak}$)",
    "Residual"       : "Residual",
}

print("% ── FACTORIAL ANOVA TABLE ───────────────────────────────")
print(r"\begin{table}[h]")
print(r"\centering")
print(f"\\caption{{Factorial ANOVA Results --- {ATTACK_LABEL} (Dependent variable: logit($p_{{\\text{{success}}}}$), $n={n}$)}}")
print(r"\label{tab:factorial_anova}")
print(r"\begin{tabular}{lrrc}")
print(r"\toprule")
print(r"\textbf{Source} & \textbf{F-statistic} & \textbf{$p$-value} & \textbf{$\eta^2$ (effect size)} \\")
print(r"\midrule")
for idx, row in anova_table.iterrows():
    label = SOURCE_LABELS.get(idx, idx)
    if idx == "Residual":
        print(r"\midrule")
        print(f"{label} & & & {row['eta_sq']:.4f} \\\\")
    else:
        F_val = row["F"]
        p_val = row["PR(>F)"]
        print(f"{label} & {F_val:.3f} & {fmt_p(p_val)} & {row['eta_sq']:.4f} \\\\")
print(r"\bottomrule")
print(r"\end{tabular}")
print(r"\end{table}")
print()

# ─────────────────────────────────────────────
# 5. TUKEY HSD — LaTeX
# ─────────────────────────────────────────────
sub   = df.dropna(subset=["hp_cat"])
tukey = pairwise_tukeyhsd(sub["p_logit"], sub["hp_cat"], alpha=0.05)
tdf   = pd.DataFrame(
    data    = tukey._results_table.data[1:],
    columns = tukey._results_table.data[0]
)

print("% ── TUKEY HSD TABLE ─────────────────────────────────────")
print(r"\begin{table}[h]")
print(r"\centering")
print(f"\\caption{{Post-hoc Tukey HSD: Hash Power Groups --- {ATTACK_LABEL} (FWER $= 0.05$)}}")
print(r"\label{tab:tukey_hsd}")
print(r"\begin{tabular}{llrrrr}")
print(r"\toprule")
print(r"\textbf{Group 1} & \textbf{Group 2} & \textbf{Mean diff} & \textbf{$p$-adj} & \textbf{95\% CI lower} & \textbf{95\% CI upper} \\")
print(r"\midrule")
for _, row in tdf.iterrows():
    md    = float(row["meandiff"])
    lower = float(row["lower"])
    upper = float(row["upper"])
    padj  = float(row["p-adj"])
    g1, g2 = row["group1"], row["group2"]
    md_str    = f"$-${abs(md):.4f}" if md < 0 else f"{md:.4f}"
    lower_str = f"$-${abs(lower):.4f}" if lower < 0 else f"{lower:.4f}"
    upper_str = f"$-${abs(upper):.4f}" if upper < 0 else f"{upper:.4f}"
    p_str = fmt_p(padj)
    print(f"{g1} & {g2} & {md_str} & {p_str} & {lower_str} & {upper_str} \\\\")
print(r"\bottomrule")
print(r"\begin{tablenotes}")
print(r"\small")
print(r"\item Mean differences on logit scale. FWER $= 0.05$.")
print(r"\item Hash power bins: Zero $\leq 0.001$, Low $\leq 0.33$, High $\leq 0.50$.")
print(r"\end{tablenotes}")
print(r"\end{tabular}")
print(r"\end{table}")