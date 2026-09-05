#!/usr/bin/env python3
"""
SANITY-CHECK COPY of atosim_common.py -- the production script is untouched;
this copy exists only to smoke-test fANOVA with tie_breaking_parameter
excluded from the analyzed parameter set.

Per Lan's recorded design decision (checklist.md, Part 3, point 2): "we will
not use tie-breaking or gamma as an experimental input parameter." Removed
from PARAM_LABELS below -- confirmed (see fanova_smoketest README /
task history) that this is the ONLY place it needs removing: LABELS/KEYS are
both derived from PARAM_LABELS (not separate hardcoded lists), and every
downstream consumer (prepare_xy's feature-matrix construction,
fanova_first_order/sobol_total_order's dimensionality) derives d from the
matrix's actual shape rather than a hardcoded count, so dropping one
PARAM_LABELS entry alone drops it everywhere. Also empirically confirmed
near-zero across all three fANOVA outcomes in the original 7-parameter
sanity smoke test (S_Ti 0.002-0.003, first-order 0.2-0.7%) -- consistent
with, not contradicting, the design decision above. Everything else
(clustering -- not present in this file at all, see build_analysis_table.py
-- RF/Sobol/fANOVA logic, docstrings for the other params) is unchanged.

Shared helpers for the Step 3 scripts (fanova_per_metric.py, pdp_analysis.py,
analyze_strategy.py). All three operate on analysis_settings.parquet (small,
flat, one row per setting - produced by build_analysis_table.py), so unlike
the pre-existing scripts in ../parquet_old/ (which each duplicated a raw-JSON
streaming loader because they read 2 different heavy schemas), there is only
one trivial loader here and it is safe to share.

Modeling conventions (see CLAUDE.md's pipeline spec, Step 3):
  - Random Forest surrogate, fixed hyperparameters (no tuning grid was
    specified) - same n_estimators/min_samples_leaf/max_features convention
    already used throughout ../parquet_old/*.py.
  - Held-out performance: 5-fold CV grouped by system_config_id (GroupKFold),
    so no fold sees rows from a system config that also appears in another
    fold - avoids leaking the (validator_count, node_degree,
    block_creation_interval, max_block_size) combination across train/test.
    This grouped CV R^2 is reported as a diagnostic only -- see
    grouped_cv_r2's docstring. It is NOT used to gate/suppress fANOVA
    results (see fanova_first_order below): Hutter et al. (2014) have no
    train/test cross-validation step anywhere in the paper (Algorithm 2
    operates directly on a fitted tree's partition, no held-out data), and
    the paper's own experiments span n~=96 to n~=236,000 and report results
    for all of them without any minimum-sample-size or CV-based
    suppression. Their actual uncertainty measure is cross-tree variance
    (Corollary 8: "We use the variance across these individual tree
    predictions to express our model uncertainty.") -- implemented here by
    fanova_first_order, which reports mean +/- std across the trees of the
    fitted forest instead of a single point estimate.
  - The final model fANOVA/PDP is computed from is refit on ALL data (more
    trees than the CV models), not from the CV fold models.
"""
import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import GroupKFold

PARAM_LABELS = [
    ("Validating-node count", "validator_count"),
    ("Node degree", "node_degree"),
    ("Block creation interval", "block_creation_interval"),
    ("Max block size", "max_block_size"),
    ("Bandwidth", "bandwidth"),
    ("Attacker hash power", "attacker_hash_power"),
    # Tie-breaking parameter (gamma) intentionally excluded here -- see
    # SANITY-CHECK COPY note at top of this file.
]
LABELS = [p[0] for p in PARAM_LABELS]
KEYS = [p[1] for p in PARAM_LABELS]

# Draft Eq. 1-3 (Sections 5.1/5.5/5.6): network-wide-reorg definition, D_r
# read directly off chainReorganizationDepths_json, NOT the attacker-attributed
# "Selfish Mining Attack Success" flag used by build_analysis_table.py / the
# results/<strategy>/analysis_settings_<strategy>.parquet tables (a separate,
# since-superseded analysis - do not conflate the two).
OUTCOMES = ["attack_success_rate", "conditional_reorg_depth", "attack_severity"]
OUTCOME_DESC = {
    "attack_success_rate": "Attack success rate P = (1/R) sum_r 1[D_r > 0]",
    "conditional_reorg_depth": "Conditional reorg depth D-bar+ = mean(D_r | D_r > 0), undefined if P=0",
    "attack_severity": "Attack severity V = (1/R) sum_r D_r = P * D-bar+ (when P>0)",
}
# conditional_reorg_depth is null whenever a setting had zero successful
# rounds (undefined by definition) - those rows are dropped, not zero-filled.
NAN_SUBSET_OUTCOMES = {"conditional_reorg_depth"}

CV_RF_KWARGS = dict(n_estimators=300, min_samples_leaf=20, max_features=0.7, n_jobs=-1)
FINAL_RF_KWARGS = dict(n_estimators=500, min_samples_leaf=20, max_features=0.7, n_jobs=-1)


def load_settings(path, strategies=None):
    df = pd.read_parquet(path)
    if strategies:
        df = df[df["attack_strategy"].isin(strategies)]
    return df


def prepare_xy(df, outcome, labels=None):
    labels = labels or LABELS
    keys = [k for lab, k in PARAM_LABELS if lab in labels]
    d = df.dropna(subset=[outcome])
    X = d[keys].to_numpy(dtype=float)
    y = d[outcome].to_numpy(dtype=float)
    groups = d["system_config_id"].to_numpy()
    return X, y, groups, d


def grouped_cv_r2(X, y, groups, rf_kwargs=None, n_splits=5, seed=0):
    """Held-out predictive performance (R^2, MAE, RMSE -- all on the
    outcome's natural, unrescaled scale) via GroupKFold(system_config_id).
    DIAGNOSTIC ONLY: none of these are part of the Hutter et al. (2014)
    fANOVA method (which has no train/test step) and, as of this file's
    fANOVA implementation, none are used to gate/suppress fANOVA results
    -- see the module docstring and fanova_first_order. They remain useful
    as a separate signal of the RF surrogate's general predictive quality.

    Computed as an explicit per-fold loop rather than sklearn's
    cross_val_score (which only returns a scalar score per fold and
    discards the underlying predictions), so MAE/RMSE are derived from
    the exact same held-out predicted/true pairs already used for R^2 --
    not a separate model refit or a separate CV pass.

    Returns (per-fold r2 scores, mean r2, mean MAE, mean RMSE) -- mean
    only across folds for all three metrics, matching the convention
    already used for r2 before this function reported anything else (its
    per-fold scores were computed but never reported with a std; MAE/RMSE
    follow the same mean-only convention for consistency). n_splits is
    capped at the number of distinct groups."""
    from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score

    rf_kwargs = rf_kwargs or CV_RF_KWARGS
    n_groups = len(np.unique(groups))
    n_splits_eff = max(2, min(n_splits, n_groups))
    gkf = GroupKFold(n_splits=n_splits_eff)

    r2_scores, mae_scores, rmse_scores = [], [], []
    for train_idx, test_idx in gkf.split(X, y, groups):
        rf = RandomForestRegressor(random_state=seed, **rf_kwargs)
        rf.fit(X[train_idx], y[train_idx])
        pred = rf.predict(X[test_idx])
        y_true = y[test_idx]
        r2_scores.append(r2_score(y_true, pred))
        mae_scores.append(mean_absolute_error(y_true, pred))
        rmse_scores.append(np.sqrt(mean_squared_error(y_true, pred)))

    r2_scores = np.array(r2_scores)
    return r2_scores, float(r2_scores.mean()), float(np.mean(mae_scores)), float(np.mean(rmse_scores))


# Small hyperparameter grid for nested CV (kept intentionally small for
# tractability at 250k-row scale within an interactive session - the outer
# loop still gives a genuine held-out estimate; the inner loop is a real,
# nested, non-leaking search, just over few candidates rather than a large
# grid). max_features is fixed at CV_RF_KWARGS's 0.7 throughout (not tuned).
NESTED_RF_GRID = [
    dict(n_estimators=300, min_samples_leaf=ml, max_features=0.7, n_jobs=-1)
    for ml in (5, 20, 50)
]


def nested_grouped_cv(X, y, groups, grid=None, n_outer=5, n_inner=3, seed=0):
    """Nested grouped CV: outer GroupKFold gives an unbiased held-out
    R^2/MAE estimate; for each outer training fold, an inner GroupKFold
    selects the best hyperparameter setting (by mean inner R^2) from `grid`,
    refit on the full outer-training fold, then scored on the outer test
    fold. Returns a dict with per-fold r2/mae, means, and the
    per-outer-fold chosen hyperparameters (for transparency)."""
    from sklearn.metrics import r2_score, mean_absolute_error

    grid = grid or NESTED_RF_GRID
    n_groups = len(np.unique(groups))
    n_outer_eff = max(2, min(n_outer, n_groups))
    outer = GroupKFold(n_splits=n_outer_eff)

    fold_r2, fold_mae, chosen = [], [], []
    for train_idx, test_idx in outer.split(X, y, groups):
        Xtr, ytr, gtr = X[train_idx], y[train_idx], groups[train_idx]
        Xte, yte = X[test_idx], y[test_idx]

        n_inner_groups = len(np.unique(gtr))
        n_inner_eff = max(2, min(n_inner, n_inner_groups))
        inner = GroupKFold(n_splits=n_inner_eff)
        best_kwargs, best_score = None, -np.inf
        for kwargs in grid:
            scores = []
            for itr, ite in inner.split(Xtr, ytr, gtr):
                rf = RandomForestRegressor(random_state=seed, **kwargs)
                rf.fit(Xtr[itr], ytr[itr])
                scores.append(r2_score(ytr[ite], rf.predict(Xtr[ite])))
            mean_inner = float(np.mean(scores))
            if mean_inner > best_score:
                best_score, best_kwargs = mean_inner, kwargs

        rf = RandomForestRegressor(random_state=seed, **best_kwargs)
        rf.fit(Xtr, ytr)
        pred = rf.predict(Xte)
        fold_r2.append(r2_score(yte, pred))
        fold_mae.append(mean_absolute_error(yte, pred))
        chosen.append(best_kwargs)

    return {
        "fold_r2": fold_r2, "mean_r2": float(np.mean(fold_r2)),
        "fold_mae": fold_mae, "mean_mae": float(np.mean(fold_mae)),
        "chosen_hyperparams": chosen,
    }


def fit_final_rf(X, y, rf_kwargs=None, seed=0):
    rf_kwargs = rf_kwargs or FINAL_RF_KWARGS
    rf = RandomForestRegressor(random_state=seed, **rf_kwargs)
    rf.fit(X, y)
    return rf


# ---------------------------------------------------------------------------
# Exact first-order fANOVA (Hutter et al. 2014) on a fitted RF's leaf
# partition. Adapted from ../parquet_old/fanova_per_metric.py's
# _leaf_boxes/_tree_fanova, generalized to operate on an already-fitted RF
# (the shared final model) instead of fitting fresh forests per seed.
#
# Uncertainty (Corollary 8): Algorithm 2 is exact and deterministic given a
# single fitted tree -- there's no sampling error to quantify from a single
# tree. The paper's model-uncertainty measure instead comes from applying
# Algorithm 2 to each of the B trees in the forest individually and
# reporting the mean +/- std of the resulting B per-tree fraction estimates
# (the grey bands in the paper's Figures 1/3/7), not from any held-out
# cross-validation. fanova_first_order below computes exactly that: one
# fraction vector per tree, then the cross-tree mean and std per parameter.
# ---------------------------------------------------------------------------

def _leaf_boxes(tree, d, lo, hi):
    t = tree.tree_
    boxes = []

    def rec(node, box):
        if t.children_left[node] == t.children_right[node]:
            boxes.append((float(t.value[node].ravel()[0]), [iv[:] for iv in box]))
            return
        f = t.feature[node]
        thr = t.threshold[node]
        lb = [iv[:] for iv in box]
        lb[f][1] = min(lb[f][1], thr)
        rec(t.children_left[node], lb)
        rb = [iv[:] for iv in box]
        rb[f][0] = max(rb[f][0], thr)
        rec(t.children_right[node], rb)

    rec(0, [[float(lo[k]), float(hi[k])] for k in range(d)])
    return boxes


def _tree_fanova(tree, d, lo, hi, W):
    boxes = _leaf_boxes(tree, d, lo, hi)
    nb = len(boxes)
    vals = np.array([b[0] for b in boxes])
    box_lo = np.array([[boxes[i][1][k][0] for k in range(d)] for i in range(nb)])
    box_hi = np.array([[boxes[i][1][k][1] for k in range(d)] for i in range(nb)])
    widths = (box_hi - box_lo) / W[None, :]

    vols = np.prod(widths, axis=1)
    f0 = float(np.sum(vols * vals))
    Vtot = float(np.sum(vols * vals * vals) - f0 * f0)

    Vj = np.zeros(d)
    for j in range(d):
        with np.errstate(divide="ignore", invalid="ignore"):
            cv = np.where(widths[:, j] > 0, vols / widths[:, j], 0.0)
        edges = np.unique(np.concatenate([box_lo[:, j], box_hi[:, j]]))
        if len(edges) < 2:
            continue
        seg_lo = edges[:-1]
        seg_hi = edges[1:]
        seg_w = seg_hi - seg_lo
        keep = seg_w > 0
        seg_lo, seg_hi, seg_w = seg_lo[keep], seg_hi[keep], seg_w[keep]
        mid = 0.5 * (seg_lo + seg_hi)
        mask = (box_lo[None, :, j] <= mid[:, None]) & (mid[:, None] <= box_hi[None, :, j])
        contrib = mask @ (vals * cv)
        Ea2 = float(np.sum((seg_w / W[j]) * contrib * contrib))
        Vj[j] = Ea2 - f0 * f0
    return Vj, Vtot


def fanova_first_order(rf, X, labels=None):
    """First-order variance fractions + interaction residual, decomposed
    per-tree (Algorithm 2, exact/deterministic per tree) from the trees of
    an already-fitted RandomForestRegressor `rf`, then reduced across trees
    to mean +/- std (Corollary 8's model-uncertainty measure).

    Returns (fractions_mean, fractions_std, interaction_mean, interaction_std):
      fractions_mean/std: {label: float} -- per-parameter mean/std of the
        first-order fraction across the B trees.
      interaction_mean/std: float -- mean/std across trees of each tree's
        own interaction residual (1 - sum of that tree's fractions).
    """
    labels = labels or LABELS
    d = X.shape[1]
    lo = X.min(0)
    hi = X.max(0)
    W = hi - lo
    W = np.where(W == 0, 1.0, W)

    B = len(rf.estimators_)
    frac_per_tree = np.zeros((B, d))
    interaction_per_tree = np.zeros(B)
    for b, est in enumerate(rf.estimators_):
        Vj, Vt = _tree_fanova(est, d, lo, hi, W)
        frac = Vj / Vt if Vt > 0 else np.zeros(d)
        frac_per_tree[b] = frac
        interaction_per_tree[b] = max(0.0, 1.0 - frac.sum())

    frac_mean = frac_per_tree.mean(axis=0)
    frac_std = frac_per_tree.std(axis=0)
    fractions_mean = {labels[k]: float(frac_mean[k]) for k in range(d)}
    fractions_std = {labels[k]: float(frac_std[k]) for k in range(d)}
    interaction_mean = float(interaction_per_tree.mean())
    interaction_std = float(interaction_per_tree.std())
    return fractions_mean, fractions_std, interaction_mean, interaction_std


# ---------------------------------------------------------------------------
# Second-order (pairwise) fANOVA (Hutter et al. 2014, Algorithm 2's K=2
# case / Theorem 3), for a fixed, small set of requested parameter pairs
# -- NOT an exhaustive C(6,2)=15-pair sweep, by design (scoped to exactly
# the pairs asked for).
#
# _tree_fanova above is hardcoded to K=1: its single "for j in range(d)"
# loop partitions the domain along ONE axis at a time (edges = box
# boundaries projected onto that one axis) and is not generic over
# arbitrary subsets U -- extending to a pair U={i,j} needs a genuinely 2D
# partition (edges projected onto BOTH axes i and j, forming a grid of
# axis-aligned cells), which _tree_fanova_pair below implements from
# scratch rather than by generalizing the existing loop in place (kept
# separate so the already-verified K=1 code path is untouched).
#
# Correctness of the interaction estimate -- i.e., that main effects are
# subtracted, not conflated into a raw joint marginal -- follows the
# paper's Eq. 4 recursion (f_hat_U = a_hat_U - sum_{W subsetneq U}
# f_hat_W) algebraically rearranged into Theorem 3's more efficient
# inclusion-exclusion identity over SQUARED marginals:
#     V_U = sum_{W subseteq U} (-1)^(|U|-|W|) E[a_hat_W(theta_W)^2]
# For U = {i,j}, W ranges over {}, {i}, {j}, {i,j}:
#     V_ij = E[a_ij^2] - E[a_i^2] - E[a_j^2] + E[a_empty^2]
# and since E[a_i^2] = V_i + f0^2 (that is exactly what _tree_fanova's Vj
# computation already returns before the f0^2 subtraction), E[a_j^2] =
# V_j + f0^2, and a_empty = f0 (the constant), this reduces to:
#     V_ij = E[a_ij^2] - V_i - V_j - f0^2
# i.e. the single-parameter main-effect variances V_i, V_j are explicitly
# subtracted out -- this IS the recursive subtraction, just in the
# algebraically simplified form the paper itself uses for efficiency, not
# a shortcut that skips it. V_i/V_j are recomputed here from scratch
# (independently of fanova_first_order's per-tree loop above) so this
# addition cannot perturb the already-verified first-order code path;
# verify() cross-checks that they agree.
# ---------------------------------------------------------------------------

SECOND_ORDER_PAIRS = [
    ("node_degree", "validator_count"),
    ("max_block_size", "bandwidth"),
    ("bandwidth", "block_creation_interval"),  # substituted for "propagation
    # time x block_creation_interval" -- propagation time is not an input
    # feature (it's measured/derived, and not even serialized in current
    # output); see fanova_second_order's docstring for the flagged
    # substitution rationale.
    ("attacker_hash_power", "node_degree"),
]


def _marginal_sq_1d(k, vals, box_lo, box_hi, widths, vols, W, f0):
    """E[a_hat_k(theta_k)^2] for a single tree along axis k, from the same
    leaf-box arrays _tree_fanova uses. Returns (Ea2, edges_ok)."""
    with np.errstate(divide="ignore", invalid="ignore"):
        cv = np.where(widths[:, k] > 0, vols / widths[:, k], 0.0)
    edges = np.unique(np.concatenate([box_lo[:, k], box_hi[:, k]]))
    if len(edges) < 2:
        return f0 * f0, False
    seg_lo, seg_hi = edges[:-1], edges[1:]
    seg_w = seg_hi - seg_lo
    keep = seg_w > 0
    seg_lo, seg_hi, seg_w = seg_lo[keep], seg_hi[keep], seg_w[keep]
    mid = 0.5 * (seg_lo + seg_hi)
    mask = (box_lo[None, :, k] <= mid[:, None]) & (mid[:, None] <= box_hi[None, :, k])
    contrib = mask @ (vals * cv)
    Ea2 = float(np.sum((seg_w / W[k]) * contrib * contrib))
    return Ea2, True


def _tree_fanova_pair(tree, i, j, d, lo, hi, W):
    """Per-tree second-order variance V_ij (interaction only, main effects
    subtracted) for feature indices i, j -- see module comment above for
    the identity used. Returns (Vij, Vi, Vj, Vtot) for this tree; Vi/Vj
    are returned too so callers can cross-check them against
    fanova_first_order's independently-computed values for the same
    tree/feature (they must match exactly -- same deterministic box
    partition, same formula)."""
    boxes = _leaf_boxes(tree, d, lo, hi)
    nb = len(boxes)
    vals = np.array([b[0] for b in boxes])
    box_lo = np.array([[boxes[b_][1][k][0] for k in range(d)] for b_ in range(nb)])
    box_hi = np.array([[boxes[b_][1][k][1] for k in range(d)] for b_ in range(nb)])
    widths = (box_hi - box_lo) / W[None, :]
    vols = np.prod(widths, axis=1)
    f0 = float(np.sum(vols * vals))
    Vtot = float(np.sum(vols * vals * vals) - f0 * f0)

    Ea2_i, ok_i = _marginal_sq_1d(i, vals, box_lo, box_hi, widths, vols, W, f0)
    Ea2_j, ok_j = _marginal_sq_1d(j, vals, box_lo, box_hi, widths, vols, W, f0)
    Vi = Ea2_i - f0 * f0
    Vj = Ea2_j - f0 * f0
    if not (ok_i and ok_j):
        return 0.0, Vi, Vj, Vtot

    with np.errstate(divide="ignore", invalid="ignore"):
        denom = widths[:, i] * widths[:, j]
        cv2 = np.where(denom > 0, vols / denom, 0.0)
    edges_i = np.unique(np.concatenate([box_lo[:, i], box_hi[:, i]]))
    edges_j = np.unique(np.concatenate([box_lo[:, j], box_hi[:, j]]))
    if len(edges_i) < 2 or len(edges_j) < 2:
        return 0.0, Vi, Vj, Vtot

    seg_lo_i, seg_hi_i = edges_i[:-1], edges_i[1:]
    seg_w_i = seg_hi_i - seg_lo_i
    keep_i = seg_w_i > 0
    seg_lo_i, seg_hi_i, seg_w_i = seg_lo_i[keep_i], seg_hi_i[keep_i], seg_w_i[keep_i]
    mid_i = 0.5 * (seg_lo_i + seg_hi_i)

    seg_lo_j, seg_hi_j = edges_j[:-1], edges_j[1:]
    seg_w_j = seg_hi_j - seg_lo_j
    keep_j = seg_w_j > 0
    seg_lo_j, seg_hi_j, seg_w_j = seg_lo_j[keep_j], seg_hi_j[keep_j], seg_w_j[keep_j]
    mid_j = 0.5 * (seg_lo_j + seg_hi_j)

    mask_i = (box_lo[None, :, i] <= mid_i[:, None]) & (mid_i[:, None] <= box_hi[None, :, i])  # (P, nb)
    mask_j = (box_lo[None, :, j] <= mid_j[:, None]) & (mid_j[:, None] <= box_hi[None, :, j])  # (Q, nb)
    weighted_vals = vals * cv2  # (nb,)
    contrib = mask_i.astype(float) @ (weighted_vals[:, None] * mask_j.astype(float).T)  # (P, Q)
    cell_w = np.outer(seg_w_i / W[i], seg_w_j / W[j])  # (P, Q)
    Ea2_ij = float(np.sum(cell_w * contrib * contrib))

    Vij = Ea2_ij - Vi - Vj - f0 * f0
    return Vij, Vi, Vj, Vtot


def fanova_second_order(rf, X, pairs=None):
    """Second-order (pairwise) fANOVA interaction fractions for a fixed
    list of (key_a, key_b) pairs (default SECOND_ORDER_PAIRS), computed
    per-tree then reduced to cross-tree mean +/- std -- the same
    Corollary-8 convention as fanova_first_order, not a single point
    estimate. Also returns, per pair, the mean absolute difference between
    this function's independently-recomputed Vi/Vj-derived first-order
    fractions and nothing external -- see verify script for the
    cross-check against fanova_first_order's own output.

    Returns {(label_a, label_b): {"frac_mean": float, "frac_std": float,
                                   "vi_frac_mean": float, "vj_frac_mean": float}}
    keyed by the pair's display labels (in the same order as `pairs`).
    """
    pairs = pairs or SECOND_ORDER_PAIRS
    key_to_idx = {k: idx for idx, k in enumerate(KEYS)}
    label_by_key = {k: lab for lab, k in PARAM_LABELS}

    d = X.shape[1]
    lo = X.min(0)
    hi = X.max(0)
    W = hi - lo
    W = np.where(W == 0, 1.0, W)

    B = len(rf.estimators_)
    results = {}
    for ka, kb in pairs:
        i, j = key_to_idx[ka], key_to_idx[kb]
        frac_ij = np.zeros(B)
        frac_i = np.zeros(B)
        frac_j = np.zeros(B)
        for b, est in enumerate(rf.estimators_):
            Vij, Vi, Vj, Vt = _tree_fanova_pair(est, i, j, d, lo, hi, W)
            if Vt > 0:
                frac_ij[b] = Vij / Vt
                frac_i[b] = Vi / Vt
                frac_j[b] = Vj / Vt
        results[(label_by_key[ka], label_by_key[kb])] = {
            "frac_mean": float(frac_ij.mean()),
            "frac_std": float(frac_ij.std()),
            "vi_frac_mean": float(frac_i.mean()),
            "vj_frac_mean": float(frac_j.mean()),
        }
    return results


# ---------------------------------------------------------------------------
# Total-order Sobol (Jansen 1999, RF-surrogate) -- SUPPLEMENTARY, NOT fANOVA.
#
# S_Ti is a different, separately-citable technique (a radial Monte Carlo
# estimator treating the fitted RF as a black-box surrogate) from the exact
# analytic fanova_first_order decomposition above, and is NOT part of the
# Hutter et al. (2014) fANOVA method despite being computed from the same
# fitted RF. Do not label this "fANOVA" in any chart/report -- it is
# reported alongside fanova_first_order only because it additionally
# captures each parameter's interactions (sum S_Ti >= 1, with equality only
# for a purely additive model), which the first-order fractions do not.
#
# Resampling: for continuous, near-uniformly-sampled features
# (validator_count, block_creation_interval, max_block_size,
# attacker_hash_power) the A/B matrices are drawn continuous-uniform over
# the observed [min, max] range, matching how those features were actually
# sampled (LHS over a continuous range). For DISCRETE_KEYS features
# (bandwidth: exactly 10 fixed Mbit/s values; node_degree: 7 observed
# integers 2-8 with non-uniform empirical frequencies -- edge values 2/8
# are under-represented at ~8.4% density vs ~16.4-16.8% for interior values
# 3-7, a rounding-boundary artifact of the LHS-then-round design) a
# continuous-uniform draw would evaluate the RF at values that never occur
# in the data (e.g. bandwidth=37.2 Mbit/s) and would flatten node_degree's
# real frequency skew. Instead these two columns are resampled via
# rng.choice() with replacement directly from the observed values in X,
# preserving the true empirical marginal (both values and frequencies)
# actually seen by the fitted RF.
# ---------------------------------------------------------------------------

DISCRETE_KEYS = {"bandwidth", "node_degree"}

def sobol_total_order(rf, X, labels=None, n_base=2048, seed=0):
    labels = labels or LABELS
    key_by_label = dict(PARAM_LABELS)
    rng = np.random.default_rng(seed)
    n, d = X.shape
    lo = X.min(axis=0)
    hi = X.max(axis=0)

    def resample(rng_):
        M = np.empty((n_base, d))
        for j in range(d):
            if key_by_label.get(labels[j]) in DISCRETE_KEYS:
                M[:, j] = rng_.choice(X[:, j], size=n_base, replace=True)
            else:
                M[:, j] = lo[j] + (hi[j] - lo[j]) * rng_.random(n_base)
        return M

    A = resample(rng)
    B = resample(rng)
    fA = rf.predict(A)
    fB = rf.predict(B)
    var_y = float(np.var(np.concatenate([fA, fB])))
    if var_y <= 0:
        return {lab: 0.0 for lab in labels}, var_y
    S_T = {}
    for i, lab in enumerate(labels):
        AB_i = A.copy()
        AB_i[:, i] = B[:, i]
        f_AB_i = rf.predict(AB_i)
        S_T[lab] = float(np.mean((fA - f_AB_i) ** 2) / (2 * var_y))
    return S_T, var_y
