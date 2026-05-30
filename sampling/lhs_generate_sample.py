import numpy as np
import pandas as pd
from scipy.stats import qmc

# -----------------------------
# 1. Experimental setup
# -----------------------------

SEED = 42
N_SAMPLES = 500
ZERO_ATTACK_SHARE = 0.15          # 15% explicit non-adversarial control cases
EXACT_25_ATTACK_SHARE = 0.10      # 10% exact 0.25 hash-power reference cases

rng = np.random.default_rng(SEED)

# -----------------------------
# 2. Parameter ranges
# -----------------------------

param_ranges = {
    "validator_count":          (20,    1_000),
    "node_degree":              (1,     250),
    "propagation_delay":        (6_500, 40_000),
    "block_creation_interval":  (60,    1_200),
    "max_block_size":           (0.25,  8.0),
    "attacker_hash_power":      (0.2,   0.9),
    "tie_breaking_parameter":   (0.0,   1.0),
}

param_names = list(param_ranges.keys())
DIM = len(param_names)
attacker_col = param_names.index("attacker_hash_power")

# -----------------------------
# 3. Generate optimized LHS
# -----------------------------

# scipy < 1.8 does not expose optimization="random-cd"; fall back to a seed
# sweep that selects the unit design with the lowest centred discrepancy (CD).
try:
    sampler = qmc.LatinHypercube(d=DIM, seed=SEED, optimization="random-cd")
    lhs_unit = sampler.random(n=N_SAMPLES)
except TypeError:
    best_unit, best_cd = None, float("inf")
    for s in range(200):
        unit = qmc.LatinHypercube(d=DIM, seed=s).random(n=N_SAMPLES)
        cd   = qmc.discrepancy(unit, method="CD")
        if cd < best_cd:
            best_cd, best_unit = cd, unit
    lhs_unit = best_unit

# -----------------------------
# 4. Scale to parameter ranges
# -----------------------------

lhs_scaled = np.zeros_like(lhs_unit)
for i, param in enumerate(param_names):
    low, high = param_ranges[param]
    lhs_scaled[:, i] = low + lhs_unit[:, i] * (high - low)

df = pd.DataFrame(lhs_scaled, columns=param_names)

# -----------------------------
# 5. Integer parameters
# -----------------------------

integer_params = [
    "validator_count",
    "node_degree",
    "propagation_delay",
    "block_creation_interval",
    "max_block_size"
]

for p in integer_params:
    df[p] = df[p].round().astype(int)

# -----------------------------
# 6. Attacker assignment
# -----------------------------
# Rationale:
# - LHS samples attacker_hash_power continuously in [0.2, 0.9].
# - ZERO_ATTACK_SHARE rows (lowest hash-power strata) are overridden to 0.0
#   to include explicit non-adversarial control cases without distorting
#   upper-range coverage.
# - EXACT_25_ATTACK_SHARE rows closest to 0.25 are snapped to exactly 0.25
#   as calibrated security-threshold reference cases.
# - number_of_attackers is derived as round(attacker_hash_power * validator_count)
#   and capped strictly below majority (< 0.5).

n_zero     = int(round(N_SAMPLES * ZERO_ATTACK_SHARE))
n_exact_25 = int(round(N_SAMPLES * EXACT_25_ATTACK_SHARE))

if n_zero + n_exact_25 >= N_SAMPLES:
    raise ValueError("ZERO_ATTACK_SHARE + EXACT_25_ATTACK_SHARE must leave ordinary LHS rows.")

# Lowest attacker-power strata become zero-attacker control contexts.
order         = np.argsort(lhs_unit[:, attacker_col])
zero_idx      = order[:n_zero]
remaining_idx = np.setdiff1d(np.arange(N_SAMPLES), zero_idx, assume_unique=False)

# Rows closest to hash power 0.25 become exact reference cases.
closest_to_25 = remaining_idx[
    np.argsort(np.abs(df.loc[remaining_idx, "attacker_hash_power"].to_numpy() - 0.25))
]
exact_25_idx = closest_to_25[:n_exact_25]

# Default attacker counts for all rows.
df["number_of_attackers"] = (
    df["attacker_hash_power"] * df["validator_count"]
).round().astype(int)

# Cap strictly below majority.
max_attackers = np.floor(0.49 * df["validator_count"]).astype(int)
df["number_of_attackers"] = np.minimum(df["number_of_attackers"], max_attackers)

# Enforce explicit zero-attacker cases.
df.loc[zero_idx, "attacker_hash_power"]  = 0.0
df.loc[zero_idx, "number_of_attackers"]  = 0

# Enforce exact 0.25 hash-power reference cases.
df.loc[exact_25_idx, "attacker_hash_power"] = 0.25
df.loc[exact_25_idx, "number_of_attackers"] = (
    (0.25 * df.loc[exact_25_idx, "validator_count"]).round().astype(int)
)

# -----------------------------
# 7. Semantic constraints
# -----------------------------

df["number_of_attackers"] = np.minimum(
    df["number_of_attackers"],
    df["validator_count"]
)

df["node_degree"] = np.minimum(df["node_degree"], df["validator_count"] - 1)
df["node_degree"] = df["node_degree"].clip(lower=1).astype(int)

df["tie_breaking_parameter"] = df["tie_breaking_parameter"].clip(0.0, 1.0)
df["attacker_hash_power"]    = df["attacker_hash_power"].clip(0.0, 1.0)

df["attacker_hash_power_realized"] = (
    df["number_of_attackers"] / df["validator_count"]
)

# -----------------------------
# 8. Final checks
# -----------------------------

assert (df["number_of_attackers"] <= df["validator_count"]).all()
assert (df["number_of_attackers"] >= 0).all()
assert (df["node_degree"] >= 1).all()
assert (df["node_degree"] <= df["validator_count"] - 1).all()
assert (df["attacker_hash_power_realized"] < 0.5).all()
assert (df["tie_breaking_parameter"] >= 0.0).all() and (df["tie_breaking_parameter"] <= 1.0).all()
assert (df["number_of_attackers"] == 0).sum() == n_zero
assert np.isclose((df["attacker_hash_power"] == 0.25).sum(), n_exact_25)
assert (df["attacker_hash_power_realized"] > 0.25).any()

# -----------------------------
# 9. Cleanup + save
# -----------------------------

df.insert(0, "config_id", range(1, len(df) + 1))
outfile = "optimized_deterministic_lhs_configurations.csv"
df.to_csv(outfile, index=False)

print("LHS configurations generated.")
print(f"Total zero-attacker contexts  : {(df['number_of_attackers'] == 0).sum()}")
print(f"Exact 0.25 hash-power contexts: {(df['attacker_hash_power'] == 0.25).sum()}")
print(f"Realized attacker fraction > 0.25: {(df['attacker_hash_power_realized'] > 0.25).sum()}")
print(f"Max realized attacker fraction: {df['attacker_hash_power_realized'].max():.6f}")
print(df[[
    "config_id",
    "attacker_hash_power",
    "validator_count",
    "number_of_attackers",
    "attacker_hash_power_realized",
]].sort_values("attacker_hash_power_realized", ascending=False).head(10))
