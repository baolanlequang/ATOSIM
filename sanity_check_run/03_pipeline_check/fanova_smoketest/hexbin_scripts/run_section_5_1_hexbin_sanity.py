#!/usr/bin/env python3
"""
SANITY-CHECK COPY of run_section_5_1_hexbin.py -- the production script is
untouched; this copy exists only to smoke-test against the sanity-check
data with corrected (status-based) P.

Original per-panel y-limits (selfish/lead_stubborn zoomed to [0.975,1.002])
were hardcoded for the OLD, pre-fix P definition (~0.9995-1.0). Corrected P
ranges ~0.17-0.85, entirely below that floor, so those limits are replaced
here with limits computed dynamically from whatever data is actually loaded
(see compute_extents) -- stays correct if the data range shifts again (e.g.
once the full 250k dataset replaces this sanity sample), instead of trading
one hardcoded constant for another. Everything else (binning, colormap,
figure layout, trail_stubborn's real-data handling) is unchanged.

Section 5.1 follow-up: raw-data hexbin of P (attack success rate, D_r-based,
draft Eq. 1) vs. attacker_hash_power (alpha), no binning of alpha - a
complementary "raw dispersion" view alongside the Section 5.5 PDP (a fitted,
averaged curve). Supersedes the binned-boxplot and log-1-P-boxplot attempts
for this purpose (both rejected: binning imposes an arbitrary grouping on a
continuous variable and largely duplicates the PDP; see prior session notes).

Reads corrected_analysis/analysis_settings_<strategy>.parquet (same D_r-based
per-row P used throughout this Section 5.1 analysis).

Outputs (--outdir, default section_5_1_out/):
  hexbin_attack_success_by_hash_power_shared_ylim.png   diagnostic: shared [0,1] y-axis
  hexbin_attack_success_by_hash_power.png               delivered: per-panel y-limits
  section_5_1_hexbin_report.txt
"""
import argparse
import os

import numpy as np
import pandas as pd
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.colors import LogNorm

STRATEGIES = [
    ("selfish", "corrected_analysis/analysis_settings_selfish.parquet", "Selfish mining"),
    ("lead_stubborn", "corrected_analysis/analysis_settings_lead_stubborn.parquet", "Lead-stubborn mining"),
    ("trail_stubborn", "corrected_analysis/analysis_settings_trail_stubborn.parquet", "Trail-stubborn mining"),
]

GRIDSIZE = 60  # x-cells across [0.20,0.50] -> ~0.005 wide, matches the earlier bin-analysis resolution
CMAP = "viridis"

# Prior session's binned per-bin mean P (full population), used only for the Task 5
# consistency check - the hexbin's density pattern should not contradict these.
PRIOR_BIN_MEANS = {
    "selfish": [0.9995, 0.9999, 1.0000, 1.0000, 1.0000, 1.0000],
    "lead_stubborn": [0.9995, 0.9999, 1.0000, 1.0000, 1.0000, 1.0000],
    "trail_stubborn": [0.6565, 0.6949, 0.7320, 0.7670, 0.7999, 0.8303],
}
BIN_EDGES = [0.20, 0.25, 0.30, 0.35, 0.40, 0.45, 0.50]


class Report:
    def __init__(self):
        self.lines = []

    def line(self, text=""):
        print(text, flush=True)
        self.lines.append(str(text))

    def h1(self, title):
        self.line("\n" + "=" * 88)
        self.line(title)
        self.line("=" * 88)

    def h2(self, title):
        self.line("\n--- {} ---".format(title))

    def save(self, path):
        os.makedirs(os.path.dirname(path) or ".", exist_ok=True)
        with open(path, "w", encoding="utf-8") as f:
            f.write("\n".join(self.lines) + "\n")


def load(path):
    return pd.read_parquet(path, columns=["attack_success_rate", "attacker_hash_power"])


def compute_max_count(x, y, extent, gridsize=GRIDSIZE):
    fig, ax = plt.subplots()
    hb = ax.hexbin(x, y, gridsize=gridsize, extent=extent, mincnt=1)
    counts = hb.get_array()
    plt.close(fig)
    return float(counts.max()) if len(counts) else 1.0


def draw_panel(ax, x, y, extent, norm, title, n, gridsize=GRIDSIZE):
    hb = ax.hexbin(x, y, gridsize=gridsize, extent=extent, mincnt=1, cmap=CMAP, norm=norm)
    ax.set_xlim(extent[0], extent[1])
    ax.set_ylim(extent[2], extent[3])
    # ax.set_title("{}\n(n={})".format(title, n), fontsize=13)
    ax.set_xlabel("Attacker Hash Power", fontsize=16)
    ax.grid(True, linestyle="--", alpha=0.3)
    return hb


def compute_extents(dfs):
    """Per-strategy y-axis window (x stays fixed at [0.20,0.50], attacker_hash_power's
    known fixed range). Computed from each strategy's own loaded attack_success_rate
    min/max + 5% padding (floor 0.01, so a near-constant column still gets a visible
    axis span), clipped to [0,1] since P can't leave that range. Dynamic rather than a
    hardcoded constant so this keeps working correctly if the data's P range shifts
    again later (e.g. once the full 250k dataset replaces this sanity sample)."""
    extents = {}
    for strategy, _, _ in STRATEGIES:
        y = dfs[strategy]["attack_success_rate"].to_numpy()
        y_min, y_max = float(np.min(y)), float(np.max(y))
        pad = max(0.05 * (y_max - y_min), 0.01)
        extents[strategy] = (0.20, 0.50, max(0.0, y_min - pad), min(1.0, y_max + pad))
    return extents


def make_figure(dfs, out_path, shared_ylim, report):
    fig, axes = plt.subplots(1, 3, figsize=(19, 6.5))

    if shared_ylim:
        extents = {s: (0.20, 0.50, 0.0, 1.0) for s, _, _ in STRATEGIES}
    else:
        extents = compute_extents(dfs)

    max_counts = []
    for strategy, _, _ in STRATEGIES:
        df = dfs[strategy]
        mc = compute_max_count(df["attacker_hash_power"].to_numpy(), df["attack_success_rate"].to_numpy(),
                                extents[strategy])
        max_counts.append(mc)
    global_max = max(max_counts)
    norm = LogNorm(vmin=1, vmax=global_max)
    report.line("Shared color scale: LogNorm(vmin=1, vmax={:.0f}) (max hex-cell count across all 3 panels, "
                "so cell darkness is directly comparable across strategies)".format(global_max))

    hb_last = None
    for ax, (strategy, _, title) in zip(axes, STRATEGIES):
        df = dfs[strategy]
        n = len(df)
        hb_last = draw_panel(ax, df["attacker_hash_power"].to_numpy(), df["attack_success_rate"].to_numpy(),
                              extents[strategy], norm, title, n)

    axes[0].set_ylabel("Attack success rate P", fontsize=13)
    cbar = fig.colorbar(hb_last, ax=axes, shrink=0.85, pad=0.02)
    cbar.set_label("rows per hex cell (log scale)", fontsize=11)

    mode = "shared y-axis [0,1] across all 3 panels" if shared_ylim else "per-panel y-limits (zoomed for selfish/lead_stubborn)"
    # fig.suptitle("Attack success rate P vs. attacker hash power - raw per-row hexbin, no alpha-binning ({})".format(mode),
    #              fontsize=13)

    os.makedirs(os.path.dirname(out_path) or ".", exist_ok=True)
    fig.savefig(out_path, dpi=150, bbox_inches="tight")
    plt.close(fig)
    return out_path, extents


def consistency_check(dfs, report):
    report.line("Per-bin mean P computed fresh from the raw per-row data used in this hexbin, vs. the "
                "prior session's already-reported binned means (should match closely - same source rows, "
                "same D_r-based P, just re-aggregated here as a sanity check):")
    for strategy, _, _ in STRATEGIES:
        df = dfs[strategy]
        report.h2(strategy)
        idx = np.digitize(df["attacker_hash_power"].to_numpy(), BIN_EDGES[1:-1], right=False)
        idx = np.clip(idx, 0, 5)
        header = "{:<14s}{:>12s}{:>12s}{:>10s}".format("bin", "fresh_mean", "prior_mean", "delta")
        report.line(header)
        report.line("-" * len(header))
        for i in range(6):
            lab = "{:.2f}-{:.2f}".format(BIN_EDGES[i], BIN_EDGES[i + 1])
            fresh = df.loc[idx == i, "attack_success_rate"].mean()
            prior = PRIOR_BIN_MEANS[strategy][i]
            report.line("{:<14s}{:>12.4f}{:>12.4f}{:>10.4f}".format(lab, fresh, prior, fresh - prior))


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--outdir", default="section_5_1_out")
    args = ap.parse_args()

    report = Report()
    report.h1("SECTION 5.1 FOLLOW-UP - RAW HEXBIN OF P vs. ATTACKER_HASH_POWER (no alpha-binning)")

    report.h2("Task 1: raw P vs. 1-P/log decision")
    report.line(
        "Decision: raw P, linear Y-axis, NOT 1-P/log.\n"
        "Reasoning: the earlier log-1-P transform was needed for the BOXPLOT because a box/whisker\n"
        "summary of a near-constant value has no visible shape unless the residual variation is\n"
        "stretched out on a log axis. A hexbin does not have that problem - overplotting is handled by\n"
        "the color/count dimension, not by spreading points along Y. Raw P lets the saturated band show\n"
        "up honestly as a dense dark region at P~1.0 while sparse low-P outliers still get their own\n"
        "(lighter) hex cells at their true P value. 1-P/log would in fact be WORSE here: P=1.0 rows\n"
        "(98% of selfish/lead_stubborn) give 1-P=0, undefined on a log axis, forcing the same exclusion\n"
        "problem as the boxplot version right back into a plot type that was supposed to avoid needing it."
    )

    dfs = {}
    for strategy, path, title in STRATEGIES:
        df = load(path)
        dfs[strategy] = df
        report.line("{}: n={}, P range [{:.4f}, {:.4f}], P 1st pct={:.4f}".format(
            strategy, len(df), df["attack_success_rate"].min(), df["attack_success_rate"].max(),
            df["attack_success_rate"].quantile(0.01)))

    report.h2("Task 2: gridsize")
    report.line("gridsize={} (x-cells across [0.20,0.50] -> ~{:.4f} wide per cell), matching the resolution "
                "of the earlier binned analysis (~40-42k rows per 0.05-wide alpha region here spread over "
                "~{} bins). mincnt=1 so every non-empty cell is drawn (no cell is silently dropped); log "
                "color norm shared across panels (see below) so cell shading is cross-strategy comparable, "
                "not just within-panel relative.".format(GRIDSIZE, 0.30 / GRIDSIZE, GRIDSIZE))

    report.h2("Task 3: axis comparability - shared y-limits diagnostic")
    shared_path, _ = make_figure(dfs, os.path.join(args.outdir, "hexbin_attack_success_by_hash_power_shared_ylim.png"),
                                  shared_ylim=True, report=report)
    report.line("Saved diagnostic (shared [0,1] y-axis): {}".format(shared_path))
    report.line("Expected/observed: selfish/lead_stubborn P sits in [{:.3f},{:.3f}] - on a full [0,1] axis "
                "this may be too narrow a band for structure to be visible depending on the data's actual "
                "spread. Decision: do NOT use shared y-limits for the delivered figure.".format(
                    dfs["selfish"]["attack_success_rate"].min(), dfs["selfish"]["attack_success_rate"].max()))

    report.h2("Task 3: axis comparability - delivered choice")
    report.line(
        "Delivered figure uses PER-PANEL y-limits, computed dynamically per strategy from that strategy's "
        "own loaded P min/max + 5% padding (see compute_extents; SANITY-CHECK COPY note at top of this file) "
        "rather than a hardcoded constant, so the figure stays correct as the data's P range shifts. Exact "
        "values used are printed below. This sacrifices literal shared-range comparability, but is the "
        "only way to keep both panel types individually readable (matching the same trade-off already "
        "made and accepted for the log-1-P boxplot). Partial comparability is preserved via: (a) identical "
        "x-axis [0.20,0.50] and gridsize across all 3 panels, (b) a single shared log color scale "
        "(LogNorm over the same [1, global_max_count] range) so hex darkness means the same row-count "
        "across panels, (c) gridlines at the same visual density. The y-axis numbers must be read "
        "explicitly per panel, not compared by eye position."
    )
    final_path, extents = make_figure(dfs, os.path.join(args.outdir, "hexbin_attack_success_by_hash_power.png"),
                                       shared_ylim=False, report=report)
    report.line("Saved (delivered): {}".format(final_path))
    report.line("Per-panel y-limits used: {}".format(extents))

    report.h2("Task 5: consistency check vs. prior binned means")
    consistency_check(dfs, report)

    report_path = os.path.join(args.outdir, "section_5_1_hexbin_report.txt")
    report.save(report_path)
    print("\nSaved report: {}".format(report_path))


if __name__ == "__main__":
    main()
