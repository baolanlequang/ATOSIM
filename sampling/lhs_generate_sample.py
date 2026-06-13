import numpy as np
import pandas as pd
from scipy.stats import qmc

# -----------------------------
# 1. Experimental setup
# -----------------------------

N_SAMPLES = 500
N_CANDIDATE_SEEDS = 200           # seeds to evaluate; best is picked at runtime

# -----------------------------
# 2. Parameter ranges
# -----------------------------

param_ranges = {
    "validator_count":          (20,    1_000),
    "node_degree":              (1,     8),
    "propagation_delay":        (6_500, 40_000),
    "block_creation_interval":  (60_000, 1_200_000),
    "max_block_size":           (250_000, 8_000_000),
    "attacker_hash_power":      (0.2,   0.5),
    "tie_breaking_parameter":   (0.0,   1.0),
}

param_names = list(param_ranges.keys())
DIM = len(param_names)

integer_params = [
    "validator_count",
    "node_degree",
    "propagation_delay",
    "block_creation_interval",
    "max_block_size",
]


# -----------------------------
# 3. Pipeline
# -----------------------------

def build_dataframe(seed: int) -> pd.DataFrame:
    """Run the LHS -> scale -> integer cast -> constraints pipeline for the
    given seed and return the final DataFrame (without config_id).
    """
    lhs_unit = qmc.LatinHypercube(d=DIM, seed=seed).random(n=N_SAMPLES)

    lhs_scaled = np.zeros_like(lhs_unit)
    for i, param in enumerate(param_names):
        low, high = param_ranges[param]
        lhs_scaled[:, i] = low + lhs_unit[:, i] * (high - low)

    df = pd.DataFrame(lhs_scaled, columns=param_names)

    for p in integer_params:
        df[p] = df[p].round().astype(int)

    df["node_degree"]            = np.minimum(df["node_degree"], df["validator_count"] - 1)
    df["node_degree"]            = df["node_degree"].clip(lower=1).astype(int)
    df["tie_breaking_parameter"] = df["tie_breaking_parameter"].clip(0.0, 1.0)

    return df


def score_dataframe(df: pd.DataFrame) -> float:
    """Centered discrepancy of the post-processed design, with each LHS-sampled
    parameter rescaled to [0, 1] against its declared range. Lower is better
    coverage.
    """
    cols = list(param_ranges.keys())
    arr = df[cols].to_numpy(dtype=float)
    for i, c in enumerate(cols):
        low, high = param_ranges[c]
        arr[:, i] = np.clip((arr[:, i] - low) / (high - low), 0.0, 1.0)
    return qmc.discrepancy(arr, method="CD")


# -----------------------------
# 4. Seed selection
# -----------------------------
# Sweep candidate seeds and pick the one whose post-processed design has the
# lowest centered discrepancy on parameter-range-normalized values. This makes
# the chosen seed sensitive to the actual parameter ranges and to the
# downstream constraints (integer rounding, node_degree cap) rather than only
# to the raw unit-cube design.

best_seed = -1
best_df = None
best_score = float("inf")

for s in range(N_CANDIDATE_SEEDS):
    df_candidate = build_dataframe(s)
    score = score_dataframe(df_candidate)
    if score < best_score:
        best_seed, best_df, best_score = s, df_candidate, score

assert best_df is not None
df = best_df
SEED = best_seed

# -----------------------------
# 5. Final checks
# -----------------------------

assert (df["node_degree"] >= 1).all()
assert (df["node_degree"] <= df["validator_count"] - 1).all()
assert (df["attacker_hash_power"] >= 0.2).all() and (df["attacker_hash_power"] < 0.5).all()
assert (df["tie_breaking_parameter"] >= 0.0).all() and (df["tie_breaking_parameter"] <= 1.0).all()

# -----------------------------
# 6. Cleanup + save
# -----------------------------

df.insert(0, "config_id", range(1, len(df) + 1))
outfile = "optimized_deterministic_lhs_configurations.csv"
df.to_csv(outfile, index=False)

print("LHS configurations generated.")
print(f"Selected seed              : {SEED}  (CD={best_score:.6f}, "
      f"swept {N_CANDIDATE_SEEDS} candidates)")
print(f"attacker_hash_power range  : [{df['attacker_hash_power'].min():.6f}, "
      f"{df['attacker_hash_power'].max():.6f}]")
print(df[[
    "config_id",
    "attacker_hash_power",
    "validator_count",
]].sort_values("attacker_hash_power", ascending=False).head(10))
