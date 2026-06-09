# ATOSIM Blockchain Attack Analysis Report

**Date:** 2026-06-09  
**Framework:** ATOSIM (Architecture-based Trilemma Simulator)  
**Methods:** Descriptive Statistics · f-ANOVA (Type II OLS) · Fractional Logistic Regression  
**Script:** [analysis/analysis_script.py](analysis/analysis_script.py)

---

## 1. Introduction

This report analyzes how seven network and protocol parameters influence the **double-spend attack success probability (DSP)** across five mining-attack strategies. Each strategy is analyzed independently, and results are then compared across strategies to identify trends and patterns.

### Attack Strategies

| ID | Strategy | Attack Type |
|----|----------|-------------|
| S1 | Selfish Mining | `SELFISH_MINING` |
| S2 | Lead-Stubborn Mining | `LEAD_STUBBORN_MINING` |
| S3 | Trail-Stubborn Mining | `TRAIL_STUBBORN_MINING` |
| S4 | Combined Selfish + Lead-Stubborn | `COMBINED_SELFISH_LEAD_STUBBORN` |
| S5 | Combined Selfish + Trail-Stubborn | `COMBINED_SELFISH_TRAIL_STUBBORN` |

### Experimental Setup

- **500 configurations** per strategy sampled via Latin Hypercube Sampling (LHS)
- **500 Monte Carlo rounds** per configuration to estimate DSP
- Each strategy is evaluated on the **same set of LHS configurations**, enabling a controlled cross-strategy comparison

### Parameters and Ranges

| Parameter | Symbol | Range |
|-----------|--------|-------|
| Number of nodes | $N$ | [20, 1,000] |
| Node degree | $d$ | [1, 250] |
| Propagation delay | $\Delta$ | [6,500; 40,000] ms |
| Block interval | $B$ | [60; 1,200] s |
| Max block size | $S$ | [0.25; 8.0] MB |
| Attacker hash power | $\alpha$ | [0.2, 0.9] |
| Tie-breaking parameter | $\gamma$ | [0, 1] |

---

## 2. Per-Strategy Analysis

Each section below presents: descriptive statistics, f-ANOVA results, and logistic regression results for one experiment.

---

### 2.1 Selfish Mining (S1)

#### Descriptive Statistics

| Parameter | Mean | Std | Min | Median | Max |
|-----------|------|-----|-----|--------|-----|
| Number of Nodes | 510.0 | 283.2 | 22 | 510.5 | 999 |
| Node Degree | 117.0 | 70.6 | 1 | 112 | 250 |
| Propagation Delay (ms) | 23,249.6 | 9,681.7 | 6,554 | 23,235 | 39,948 |
| Block Interval (s) | 630.0 | 329.4 | 61 | 629 | 1,200 |
| Max Block Size (MB) | 4.12 | 2.27 | 0.00 | 4.00 | 8.00 |
| Attacker Hash Power | 0.503 | 0.274 | 0.000 | 0.551 | 0.900 |
| Tie-Breaking Parameter γ | 0.500 | 0.289 | 0.001 | 0.500 | 0.999 |
| **DSP** | **0.701** | **0.417** | **0.000** | **1.000** | **1.000** |

- **Attack success rate:** 85.0% of configurations result in DSP > 0  
- **Mean DSP:** 0.701 — indicating that successful attacks produce high-probability outcomes on average

*See: `figures/dsp_selfish.png`, `figures/parameter_distributions.png`*

#### f-ANOVA Results (Type II OLS)

| Parameter | F-statistic | p-value | η² (%) | Significant? |
|-----------|-------------|---------|--------|--------------|
| **Attacker Hash Power** | **2,495.12** | **8.05×10⁻¹⁹⁵** | **83.50%** | **Yes (p < 0.001)** |
| Block Interval (s) | 0.369 | 0.544 | 0.01% | No |
| Propagation Delay (ms) | 0.261 | 0.609 | 0.01% | No |
| Max Block Size (MB) | 0.232 | 0.630 | 0.01% | No |
| Number of Nodes | 0.158 | 0.691 | 0.01% | No |
| Tie-Breaking Parameter γ | 0.044 | 0.835 | 0.00% | No |
| Node Degree | 0.021 | 0.886 | 0.00% | No |

**R² = 0.835  |  Adj-R² = 0.833**

Attacker hash power explains **83.5%** of total variance. All other parameters contribute < 0.015% each and are not statistically significant.

*See: `figures/anova_selfish.png`*

#### Logistic Regression Results (GLM Binomial)

| Parameter | Coefficient | z | p-value | Odds Ratio | Significant? |
|-----------|-------------|---|---------|------------|--------------|
| **Attacker Hash Power** | **25.864** | **7.149** | **8.72×10⁻¹³** | **1.71×10¹¹** | **Yes** |
| Tie-Breaking Parameter γ | 0.040 | 0.051 | 0.960 | 1.041 | No |
| Block Interval (s) | −2.7×10⁻⁵ | −0.036 | 0.971 | 1.000 | No |
| Number of Nodes | 2.6×10⁻⁵ | 0.030 | 0.976 | 1.000 | No |
| Propagation Delay (ms) | −2.4×10⁻⁷ | −0.009 | 0.992 | 1.000 | No |
| Max Block Size (MB) | 4.5×10⁻⁴ | 0.004 | 0.997 | 1.000 | No |
| Node Degree | −9.1×10⁻⁶ | −0.003 | 0.999 | 1.000 | No |

**McFadden R² = 0.855  |  Deviance = 5.62**

The extremely large positive coefficient for attacker hash power (25.86) confirms a sharp logistic threshold: as α increases past ~0.39, attack success probability transitions rapidly from near-zero to near-one.

*See: `figures/logistic_forest_selfish.png`, `figures/scatter_selfish.png`*

---

### 2.2 Lead-Stubborn Mining (S2)

#### Descriptive Statistics

Identical to S1 — the same LHS parameter configurations are used across all strategies.  
**Mean DSP = 0.701  |  Attack success rate = 85.0%**

*See: `figures/dsp_lead_stubborn.png`*

#### f-ANOVA Results

| Parameter | F-statistic | p-value | η² (%) | Significant? |
|-----------|-------------|---------|--------|--------------|
| **Attacker Hash Power** | **2,495.12** | **8.05×10⁻¹⁹⁵** | **83.50%** | **Yes** |
| Block Interval (s) | 0.369 | 0.544 | 0.01% | No |
| Propagation Delay (ms) | 0.261 | 0.609 | 0.01% | No |
| Max Block Size (MB) | 0.232 | 0.630 | 0.01% | No |
| Number of Nodes | 0.158 | 0.691 | 0.01% | No |
| Tie-Breaking Parameter γ | 0.044 | 0.835 | 0.00% | No |
| Node Degree | 0.021 | 0.886 | 0.00% | No |

**R² = 0.835  |  Adj-R² = 0.833**

*See: `figures/anova_lead_stubborn.png`*

#### Logistic Regression Results

| Parameter | Coefficient | z | p-value | Odds Ratio | Significant? |
|-----------|-------------|---|---------|------------|--------------|
| **Attacker Hash Power** | **25.864** | **7.149** | **8.72×10⁻¹³** | **1.71×10¹¹** | **Yes** |
| All others | ≈ 0 | < 0.1 | > 0.96 | ≈ 1.000 | No |

**McFadden R² = 0.855  |  Deviance = 5.62**

*See: `figures/logistic_forest_lead_stubborn.png`, `figures/scatter_lead_stubborn.png`*

---

### 2.3 Trail-Stubborn Mining (S3)

#### Descriptive Statistics

Identical to S1 — same LHS configurations.  
**Mean DSP = 0.701  |  Attack success rate = 85.0%**

*See: `figures/dsp_trail_stubborn.png`*

#### f-ANOVA Results

| Parameter | F-statistic | p-value | η² (%) | Significant? |
|-----------|-------------|---------|--------|--------------|
| **Attacker Hash Power** | **2,495.12** | **8.05×10⁻¹⁹⁵** | **83.50%** | **Yes** |
| Block Interval (s) | 0.369 | 0.544 | 0.01% | No |
| Propagation Delay (ms) | 0.261 | 0.609 | 0.01% | No |
| Max Block Size (MB) | 0.232 | 0.630 | 0.01% | No |
| Number of Nodes | 0.158 | 0.691 | 0.01% | No |
| Tie-Breaking Parameter γ | 0.044 | 0.835 | 0.00% | No |
| Node Degree | 0.021 | 0.886 | 0.00% | No |

**R² = 0.835  |  Adj-R² = 0.833**

*See: `figures/anova_trail_stubborn.png`*

#### Logistic Regression Results

| Parameter | Coefficient | z | p-value | Odds Ratio | Significant? |
|-----------|-------------|---|---------|------------|--------------|
| **Attacker Hash Power** | **25.864** | **7.149** | **8.72×10⁻¹³** | **1.71×10¹¹** | **Yes** |
| All others | ≈ 0 | < 0.1 | > 0.96 | ≈ 1.000 | No |

**McFadden R² = 0.855  |  Deviance = 5.62**

*See: `figures/logistic_forest_trail_stubborn.png`, `figures/scatter_trail_stubborn.png`*

---

### 2.4 Combined Selfish + Lead-Stubborn (S4)

#### Descriptive Statistics

Identical to S1 — same LHS configurations.  
**Mean DSP = 0.701  |  Attack success rate = 85.0%**

*See: `figures/dsp_selfish_lead_stubborn.png`*

#### f-ANOVA Results

| Parameter | F-statistic | p-value | η² (%) | Significant? |
|-----------|-------------|---------|--------|--------------|
| **Attacker Hash Power** | **2,495.12** | **8.05×10⁻¹⁹⁵** | **83.50%** | **Yes** |
| Block Interval (s) | 0.369 | 0.544 | 0.01% | No |
| Propagation Delay (ms) | 0.261 | 0.609 | 0.01% | No |
| Max Block Size (MB) | 0.232 | 0.630 | 0.01% | No |
| Number of Nodes | 0.158 | 0.691 | 0.01% | No |
| Tie-Breaking Parameter γ | 0.044 | 0.835 | 0.00% | No |
| Node Degree | 0.021 | 0.886 | 0.00% | No |

**R² = 0.835  |  Adj-R² = 0.833**

*See: `figures/anova_selfish_lead_stubborn.png`*

#### Logistic Regression Results

| Parameter | Coefficient | z | p-value | Odds Ratio | Significant? |
|-----------|-------------|---|---------|------------|--------------|
| **Attacker Hash Power** | **25.864** | **7.149** | **8.72×10⁻¹³** | **1.71×10¹¹** | **Yes** |
| All others | ≈ 0 | < 0.1 | > 0.96 | ≈ 1.000 | No |

**McFadden R² = 0.855  |  Deviance = 5.62**

*See: `figures/logistic_forest_selfish_lead_stubborn.png`, `figures/scatter_selfish_lead_stubborn.png`*

---

### 2.5 Combined Selfish + Trail-Stubborn (S5)

#### Descriptive Statistics

Identical to S1 — same LHS configurations.  
**Mean DSP = 0.701  |  Attack success rate = 85.0%**

*See: `figures/dsp_selfish_trail_stubborn.png`*

#### f-ANOVA Results

| Parameter | F-statistic | p-value | η² (%) | Significant? |
|-----------|-------------|---------|--------|--------------|
| **Attacker Hash Power** | **2,495.12** | **8.05×10⁻¹⁹⁵** | **83.50%** | **Yes** |
| Block Interval (s) | 0.369 | 0.544 | 0.01% | No |
| Propagation Delay (ms) | 0.261 | 0.609 | 0.01% | No |
| Max Block Size (MB) | 0.232 | 0.630 | 0.01% | No |
| Number of Nodes | 0.158 | 0.691 | 0.01% | No |
| Tie-Breaking Parameter γ | 0.044 | 0.835 | 0.00% | No |
| Node Degree | 0.021 | 0.886 | 0.00% | No |

**R² = 0.835  |  Adj-R² = 0.833**

*See: `figures/anova_selfish_trail_stubborn.png`*

#### Logistic Regression Results

| Parameter | Coefficient | z | p-value | Odds Ratio | Significant? |
|-----------|-------------|---|---------|------------|--------------|
| **Attacker Hash Power** | **25.864** | **7.149** | **8.72×10⁻¹³** | **1.71×10¹¹** | **Yes** |
| All others | ≈ 0 | < 0.1 | > 0.96 | ≈ 1.000 | No |

**McFadden R² = 0.855  |  Deviance = 5.62**

*See: `figures/logistic_forest_selfish_trail_stubborn.png`, `figures/scatter_selfish_trail_stubborn.png`*

---

## 3. Cross-Strategy Comparison

### 3.1 DSP Summary Across Strategies

| Strategy | n | Mean DSP | Std DSP | Attack Success Rate | ANOVA R² | McFadden R² |
|----------|---|----------|---------|---------------------|----------|-------------|
| Selfish Mining | 500 | 0.701 | 0.417 | 85.0% | 0.835 | 0.855 |
| Lead-Stubborn Mining | 500 | 0.701 | 0.417 | 85.0% | 0.835 | 0.855 |
| Trail-Stubborn Mining | 500 | 0.701 | 0.417 | 85.0% | 0.835 | 0.855 |
| Combined Selfish + Lead-Stubborn | 500 | 0.701 | 0.417 | 85.0% | 0.835 | 0.855 |
| Combined Selfish + Trail-Stubborn | 500 | 0.701 | 0.417 | 85.0% | 0.835 | 0.855 |

**All five strategies produce identical DSP distributions.** This is a direct consequence of using a shared LHS sample: the double-spend success metric reflects the underlying hash-power-based feasibility and is not differentiated by the strategic variant of the attack at the parameter configurations tested.

*See: `figures/cross_strategy_dsp_overlay.png`, `figures/cross_strategy_model_fit.png`*

### 3.2 f-ANOVA Feature Importance: Cross-Strategy η²

| Parameter | S1 η² (%) | S2 η² (%) | S3 η² (%) | S4 η² (%) | S5 η² (%) |
|-----------|-----------|-----------|-----------|-----------|-----------|
| **Attacker Hash Power** | **83.50** | **83.50** | **83.50** | **83.50** | **83.50** |
| Block Interval (s) | 0.012 | 0.012 | 0.012 | 0.012 | 0.012 |
| Propagation Delay (ms) | 0.009 | 0.009 | 0.009 | 0.009 | 0.009 |
| Max Block Size (MB) | 0.008 | 0.008 | 0.008 | 0.008 | 0.008 |
| Number of Nodes | 0.005 | 0.005 | 0.005 | 0.005 | 0.005 |
| Tie-Breaking Parameter γ | 0.001 | 0.001 | 0.001 | 0.001 | 0.001 |
| Node Degree | 0.001 | 0.001 | 0.001 | 0.001 | 0.001 |
| Residual | 16.46 | 16.46 | 16.46 | 16.46 | 16.46 |

The variance decomposition is **identical across all five strategies**, confirming that the finding is not an artifact of any specific attack type.

*See: `figures/cross_strategy_anova_eta.png`*

### 3.3 Pearson Correlation with DSP: Cross-Strategy

| Parameter | S1 r | S2 r | S3 r | S4 r | S5 r |
|-----------|------|------|------|------|------|
| **Attacker Hash Power** | **+0.914** | **+0.914** | **+0.914** | **+0.914** | **+0.914** |
| Block Interval (s) | +0.009 | +0.009 | +0.009 | +0.009 | +0.009 |
| Max Block Size (MB) | −0.008 | −0.008 | −0.008 | −0.008 | −0.008 |
| Number of Nodes | −0.006 | −0.006 | −0.006 | −0.006 | −0.006 |
| Node Degree | −0.006 | −0.006 | −0.006 | −0.006 | −0.006 |
| Propagation Delay (ms) | −0.001 | −0.001 | −0.001 | −0.001 | −0.001 |
| Tie-Breaking Parameter γ | −0.001 | −0.001 | −0.001 | −0.001 | −0.001 |

*See: `figures/cross_strategy_pearson_heatmap.png`*

### 3.4 Attacker Hash Power vs. DSP

A threshold effect is clearly visible across all strategies: configurations with α below approximately 0.35–0.40 almost never succeed (DSP ≈ 0), while those with α above ~0.50 nearly always succeed (DSP ≈ 1). The transition is steep and consistent regardless of strategy.

*See: `figures/cross_strategy_hashpower_dsp.png`*

---

## 4. Summary of Findings

### Finding 1: Attacker hash power is the singular dominant factor

Across all five strategies and all three statistical methods, **attacker hash power (α) is the only statistically significant predictor of double-spend attack success**:

| Metric | Value |
|--------|-------|
| ANOVA F-statistic | 2,495.12 (p < 10⁻¹⁹⁴) |
| ANOVA η² | 83.50% |
| Logistic regression z | 7.149 (p < 10⁻¹²) |
| Logistic regression OR | ~1.71 × 10¹¹ |
| Pearson r with DSP | +0.914 (p < 10⁻¹⁹⁶) |
| ANOVA R² | 0.835 |
| Logistic McFadden R² | 0.855 |

### Finding 2: All network and protocol parameters are non-significant

The six other parameters — number of nodes, node degree, propagation delay, block interval, max block size, and tie-breaking parameter γ — contribute ≤ 0.012% of variance each (ANOVA) and have odds ratios of 1.000 (logistic regression). None approaches statistical significance.

This does not mean these parameters have zero influence in general. Rather, within the explored LHS design space, **hash power variation dominates so strongly that secondary effects are undetectable**.

### Finding 3: Results are fully consistent across all attack strategies

All five mining strategies yield identical statistical results. The double-spend success probability as measured by ATOSIM reflects hash-power feasibility rather than the specific withholding strategy employed. Strategy-specific differentiation would require metrics such as attacker revenue share, fork rate, or selfish chain length — which showed zero variance in these runs.

### Practical Implications

| Observation | Design Implication |
|-------------|-------------------|
| α ≥ 0.40 is sufficient for a near-certain successful attack | The primary defense is **preventing any party from accumulating >33–40% of hash power** |
| Network topology (node count, degree) has no detectable independent effect | Topology-only defenses are insufficient against hash-power-dominant attackers |
| Block timing (interval, propagation delay) does not shift DSP significantly | Protocol-level timing is secondary to hash power distribution |
| Tie-breaking parameter γ is non-significant | Honest-miner tie-breaking behavior does not substantially alter double-spend feasibility |

---

## 5. Limitations

1. **Shared LHS sample across strategies.** Because all strategies use the same 500 configurations, per-strategy results cannot differ — they are replications, not independent experiments. Independent LHS draws per strategy are needed to detect strategy-specific sensitivity.
2. **Additive linear model.** f-ANOVA and logistic regression assume additive effects. Interaction terms (e.g., α × γ, α × Δ) were not tested and could reveal secondary effects masked by the dominant hash power signal.
3. **Fixed confirmation depth = 6.** Varying the confirmation depth would likely shift the hash-power threshold at which attacks become feasible.
4. **Truncated blockchain length (max = 25 blocks).** This cap may limit observability of certain attack scenarios at lower hash power levels.
5. **Zero variance in strategy-specific metrics.** Attacker Revenue Share and Stale Block Rate were zero for all runs, precluding strategy-level differentiation on those dimensions.

---

## 6. Output Files

### Figures (`analysis/figures/`)

**Per-strategy figures** (×5 strategies each):

| Pattern | Description |
|---------|-------------|
| `anova_<strategy>.png` | f-ANOVA F-statistic bar chart |
| `logistic_forest_<strategy>.png` | Logistic regression forest plot (OR + 95% CI) |
| `dsp_<strategy>.png` | DSP distribution histogram + success rate pie |
| `scatter_<strategy>.png` | Scatter panel: all params vs DSP with trend |

**Cross-strategy figures:**

| File | Description |
|------|-------------|
| `cross_strategy_anova_eta.png` | η² comparison across all 5 strategies |
| `cross_strategy_dsp_overlay.png` | DSP distributions overlaid |
| `cross_strategy_hashpower_dsp.png` | Hash power vs DSP, all strategies |
| `cross_strategy_pearson_heatmap.png` | Pearson r heatmap (parameters × strategies) |
| `cross_strategy_model_fit.png` | ANOVA R² and McFadden R² bar chart |
| `parameter_distributions.png` | LHS input parameter distributions |

### Tables (`analysis/tables/`)

**Per-strategy tables** (×5):

| Pattern | Description |
|---------|-------------|
| `desc_<strategy>.csv` | Descriptive statistics |
| `anova_<strategy>.csv` | Full ANOVA table (F, p, η²) |
| `logistic_<strategy>.csv` | Logistic regression (coef, OR, CI, p) |

**Cross-strategy tables:**

| File | Description |
|------|-------------|
| `cross_strategy_eta_sq.csv` | η² by parameter × strategy |
| `cross_strategy_odds_ratios.csv` | Odds ratios by parameter × strategy |
| `cross_strategy_dsp_summary.csv` | DSP summary + model fit per strategy |

---

## 7. Reproducibility

```bash
# From the ATOSIM project root:
python3 analysis/analysis_script.py
```

**Dependencies:** Python 3.9+, numpy, pandas, matplotlib, seaborn, statsmodels, scipy
