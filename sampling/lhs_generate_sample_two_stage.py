import numpy as np
import pandas as pd
from scipy.stats import qmc

# -----------------------------
# 1. Experimental setup
# -----------------------------

N_SYSTEM_SAMPLES   = 250   # number of core system configurations C_i
N_PD_VARIANTS      = 10    # propagation_delay variants applied to every core config
N_ATTACKER_SAMPLES = 100   # number of attacker-capability configurations A_ij per S_i
N_CANDIDATE_SEEDS  = 1000  # seeds to evaluate; best is picked at runtime


# -----------------------------
# 2. Parameter ranges (separated into three groups)
# -----------------------------

system_core_param_ranges = {
    "validator_count":          (20,      1_000),
    "node_degree":              (1,       8),
    "block_creation_interval":  (60_000,  1_200_000),
    "max_block_size":           (250_000, 8_000_000),
}

propagation_delay_range = (6_500, 40_000)

attacker_param_ranges = {
    "attacker_hash_power":      (0.2, 0.5),
    "tie_breaking_parameter":   (0.0, 1.0),
}

system_core_param_names = list(system_core_param_ranges.keys())
attacker_param_names    = list(attacker_param_ranges.keys())

system_core_integer_params = list(system_core_param_names)  # all 4 core params are integers

# -----------------------------
# 3. Build functions
# -----------------------------

def build_system_core_df(seed: int) -> pd.DataFrame:
    """Stage 1a: LHS over the 4 core system parameters."""
    dim = len(system_core_param_names)
    lhs_unit = qmc.LatinHypercube(d=dim, seed=seed).random(n=N_SYSTEM_SAMPLES)

    lhs_scaled = np.zeros_like(lhs_unit)
    for i, param in enumerate(system_core_param_names):
        low, high = system_core_param_ranges[param]
        lhs_scaled[:, i] = low + lhs_unit[:, i] * (high - low)

    df = pd.DataFrame(lhs_scaled, columns=system_core_param_names)

    for p in system_core_integer_params:
        df[p] = df[p].round().astype(int)

    # Constraints
    df["node_degree"] = np.minimum(df["node_degree"], df["validator_count"] - 1)
    df["node_degree"] = df["node_degree"].clip(lower=1).astype(int)

    return df


def build_propagation_delay_df(seed: int) -> pd.DataFrame:
    """Stage 1b: LHS over propagation_delay, shared across every core config."""
    lhs_unit = qmc.LatinHypercube(d=1, seed=seed).random(n=N_PD_VARIANTS)

    low, high = propagation_delay_range
    scaled = low + lhs_unit[:, 0] * (high - low)

    df = pd.DataFrame({"propagation_delay": scaled})
    df["propagation_delay"] = df["propagation_delay"].round().astype(int)

    return df


def build_attacker_df(seed: int) -> pd.DataFrame:
    """Stage 2: LHS over 2 attacker-capability parameters."""
    dim = len(attacker_param_names)
    lhs_unit = qmc.LatinHypercube(d=dim, seed=seed).random(n=N_ATTACKER_SAMPLES)

    lhs_scaled = np.zeros_like(lhs_unit)
    for i, param in enumerate(attacker_param_names):
        low, high = attacker_param_ranges[param]
        lhs_scaled[:, i] = low + lhs_unit[:, i] * (high - low)

    df = pd.DataFrame(lhs_scaled, columns=attacker_param_names)
    df["tie_breaking_parameter"] = df["tie_breaking_parameter"].clip(0.0, 1.0)

    return df


# -----------------------------
# 4. Score functions
# -----------------------------

def score_system_core_df(df: pd.DataFrame) -> float:
    """Centered discrepancy of the core system design (lower = better coverage)."""
    arr = df[system_core_param_names].to_numpy(dtype=float)
    for i, c in enumerate(system_core_param_names):
        low, high = system_core_param_ranges[c]
        arr[:, i] = np.clip((arr[:, i] - low) / (high - low), 0.0, 1.0)
    return qmc.discrepancy(arr, method="CD")


def score_propagation_delay_df(df: pd.DataFrame) -> float:
    """Centered discrepancy of the propagation_delay design (lower = better coverage)."""
    low, high = propagation_delay_range
    arr = df[["propagation_delay"]].to_numpy(dtype=float)
    arr[:, 0] = np.clip((arr[:, 0] - low) / (high - low), 0.0, 1.0)
    return qmc.discrepancy(arr, method="CD")


def score_attacker_df(df: pd.DataFrame) -> float:
    """Centered discrepancy of the attacker design (lower = better coverage)."""
    arr = df[attacker_param_names].to_numpy(dtype=float)
    for i, c in enumerate(attacker_param_names):
        low, high = attacker_param_ranges[c]
        arr[:, i] = np.clip((arr[:, i] - low) / (high - low), 0.0, 1.0)
    return qmc.discrepancy(arr, method="CD")


# -----------------------------
# 5. Seed selection — Stage 1a (system core)
# -----------------------------

best_core_seed  = -1
best_core_df    = None
best_core_score = float("inf")

for s in range(N_CANDIDATE_SEEDS):
    df_candidate = build_system_core_df(s)
    score = score_system_core_df(df_candidate)
    if score < best_core_score:
        best_core_seed, best_core_df, best_core_score = s, df_candidate, score

assert best_core_df is not None
core_df   = best_core_df
CORE_SEED = best_core_seed

# -----------------------------
# 6. Seed selection — Stage 1b (propagation_delay)
# -----------------------------
# One shared propagation_delay LHS design is selected (lowest CD).
# The same design is reused for every core config, preserving a
# matched/paired structure: every core config gets the identical
# set of propagation_delay variants.

best_pd_seed  = -1
best_pd_df    = None
best_pd_score = float("inf")

# Offset seed range to avoid overlap with core seeds
for s in range(N_CANDIDATE_SEEDS, 2 * N_CANDIDATE_SEEDS):
    df_candidate = build_propagation_delay_df(s)
    score = score_propagation_delay_df(df_candidate)
    if score < best_pd_score:
        best_pd_seed, best_pd_df, best_pd_score = s, df_candidate, score

assert best_pd_df is not None
pd_df   = best_pd_df
PD_SEED = best_pd_seed

# -----------------------------
# 7. Seed selection — Stage 2 (attacker)
# -----------------------------
# One shared attacker LHS design is selected (lowest CD).
# The same attacker design is reused for every system config, preserving
# the matched/paired structure: every (S_i, A_ij) pair uses
# the identical attacker-capability grid.

best_attacker_seed  = -1
best_attacker_df    = None
best_attacker_score = float("inf")

# Offset seed range to avoid overlap with core/propagation_delay seeds
for s in range(2 * N_CANDIDATE_SEEDS, 3 * N_CANDIDATE_SEEDS):
    df_candidate = build_attacker_df(s)
    score = score_attacker_df(df_candidate)
    if score < best_attacker_score:
        best_attacker_seed, best_attacker_df, best_attacker_score = s, df_candidate, score

assert best_attacker_df is not None
attacker_df   = best_attacker_df
ATTACKER_SEED = best_attacker_seed

# -----------------------------
# 8. Assign IDs and combine core x propagation_delay
# -----------------------------

core_df.insert(0, "core_config_id", range(1, N_SYSTEM_SAMPLES + 1))
pd_df.insert(0, "pd_variant_id", range(1, N_PD_VARIANTS + 1))
attacker_df.insert(0, "attacker_config_id", range(1, N_ATTACKER_SAMPLES + 1))

# Cartesian product: every core config paired with every propagation_delay variant
system_df = core_df.merge(pd_df, how="cross")
system_df.insert(0, "system_config_id", range(1, len(system_df) + 1))
system_df = system_df[[
    "system_config_id", "core_config_id", "pd_variant_id",
    "validator_count", "node_degree", "propagation_delay",
    "block_creation_interval", "max_block_size",
]]

# -----------------------------
# 9. Final checks
# -----------------------------

assert (system_df["node_degree"] >= 1).all()
assert (system_df["node_degree"] <= system_df["validator_count"] - 1).all()

assert (system_df["propagation_delay"] >= propagation_delay_range[0]).all()
assert (system_df["propagation_delay"] <= propagation_delay_range[1]).all()

assert len(system_df) == N_SYSTEM_SAMPLES * N_PD_VARIANTS

assert (attacker_df["attacker_hash_power"] >= 0.2).all()
assert (attacker_df["attacker_hash_power"] <  0.5).all()
assert (attacker_df["tie_breaking_parameter"] >= 0.0).all()
assert (attacker_df["tie_breaking_parameter"] <= 1.0).all()

# -----------------------------
# 10. Save outputs
# -----------------------------

system_df.to_csv("system_configurations.csv", index=False)
attacker_df.to_csv("attacker_configurations.csv", index=False)

# -----------------------------
# 11. Summary
# -----------------------------

print("=" * 60)
print("TWO-STAGE LHS — CONFIGURATIONS GENERATED")
print("=" * 60)
print(f"  Core seed              : {CORE_SEED}   (CD={best_core_score:.6f})")
print(f"  Propagation-delay seed : {PD_SEED}   (CD={best_pd_score:.6f})")
print(f"  Attacker seed          : {ATTACKER_SEED}   (CD={best_attacker_score:.6f})")
print(f"  Core configurations     : {N_SYSTEM_SAMPLES}")
print(f"  Propagation-delay variants per core config: {N_PD_VARIANTS}")
print(f"  System configurations   : {len(system_df)}")
print(f"  Attacker configurations : {N_ATTACKER_SAMPLES}")
print()
print("Output files:")
print(f"  system_configurations.csv   — Stage 1 LHS ({len(system_df)} rows)")
print(f"  attacker_configurations.csv — Stage 2 LHS ({N_ATTACKER_SAMPLES} rows)")
print()
print("Sample — system_configurations.csv:")
print(system_df[[
    "system_config_id", "core_config_id", "pd_variant_id", "validator_count", "node_degree",
    "propagation_delay", "block_creation_interval", "max_block_size"
]].head(5).to_string(index=False))
print()
print("Sample — attacker_configurations.csv:")
print(attacker_df[[
    "attacker_config_id", "attacker_hash_power", "tie_breaking_parameter"
]].head(5).to_string(index=False))
