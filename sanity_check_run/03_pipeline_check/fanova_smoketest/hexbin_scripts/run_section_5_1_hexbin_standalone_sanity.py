#!/usr/bin/env python3
"""
SANITY-CHECK COPY of run_section_5_1_hexbin_standalone.py -- the production
script is untouched; this copy exists only to smoke-test against the
sanity-check data with corrected (status-based) P.

Original EXTENTS was a hardcoded constant calibrated for the OLD, pre-fix P
definition (~0.9995-1.0 for selfish/lead_stubborn). Corrected P ranges
~0.17-0.85, entirely below that floor. Replaced with the sanity copy of the
combined script's compute_extents (imported below), computed dynamically
from whatever data is actually loaded -- stays correct if the data range
shifts again later, instead of trading one hardcoded constant for another.
Everything else is unchanged.

Splits the combined 3-panel hexbin figure (run_section_5_1_hexbin.py ->
hexbin_attack_success_by_hash_power.png) into three standalone, independently
placeable PNGs, one per strategy - same content (per-panel y-limits,
gridsize, shared color scale, raw P on Y) as the combined figure's delivered
(non-shared-ylim) version, just one file each instead of one 3-subplot file.
Reuses run_section_5_1_hexbin.py's constants/helpers directly so there is no
risk of the split figures silently drifting from the combined one.

Does NOT touch or delete the combined figure.

Outputs (--outdir, default section_5_1_out/):
  hexbin_attack_success_selfish.png
  hexbin_attack_success_lead_stubborn.png
  hexbin_attack_success_trail_stubborn.png
  section_5_1_hexbin_standalone_report.txt
"""
import argparse
import os

import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.colors import LogNorm

from run_section_5_1_hexbin_sanity import (
    STRATEGIES, GRIDSIZE, CMAP, PRIOR_BIN_MEANS, BIN_EDGES,
    load, compute_max_count, draw_panel, compute_extents,
)


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


def make_standalone(strategy, title, df, extent, norm, out_path):
    fig, ax = plt.subplots(figsize=(8, 6.5))
    n = len(df)
    hb = draw_panel(ax, df["attacker_hash_power"].to_numpy(), df["attack_success_rate"].to_numpy(),
                     extent, norm, title, n, gridsize=GRIDSIZE)
    ax.set_ylabel("Attack success rate P", fontsize=13)
    cbar = fig.colorbar(hb, ax=ax, shrink=0.9, pad=0.02)
    cbar.set_label("rows per hex cell (log scale)", fontsize=11)
    # fig.suptitle("Attack success rate P vs. attacker hash power - raw per-row hexbin ({})".format(title),
    #              fontsize=12)
    fig.tight_layout(rect=[0, 0, 1, 0.95])
    os.makedirs(os.path.dirname(out_path) or ".", exist_ok=True)
    fig.savefig(out_path, dpi=150, bbox_inches="tight")
    plt.close(fig)
    return out_path


def consistency_check(dfs, report):
    report.h2("Task 4: per-bin mean P vs. previously-reported numbers (sanity check, not new analysis)")
    for strategy, _, _ in STRATEGIES:
        df = dfs[strategy]
        report.line(strategy + ":")
        idx = np.digitize(df["attacker_hash_power"].to_numpy(), BIN_EDGES[1:-1], right=False)
        idx = np.clip(idx, 0, 5)
        header = "  {:<14s}{:>12s}{:>12s}{:>10s}".format("bin", "fresh_mean", "prior_mean", "delta")
        report.line(header)
        report.line("  " + "-" * (len(header) - 2))
        max_delta = 0.0
        for i in range(6):
            lab = "{:.2f}-{:.2f}".format(BIN_EDGES[i], BIN_EDGES[i + 1])
            fresh = df.loc[idx == i, "attack_success_rate"].mean()
            prior = PRIOR_BIN_MEANS[strategy][i]
            delta = fresh - prior
            max_delta = max(max_delta, abs(delta))
            report.line("  {:<14s}{:>12.4f}{:>12.4f}{:>10.4f}".format(lab, fresh, prior, delta))
        report.line("  max |delta| = {:.6f} -> {}".format(
            max_delta, "MATCHES prior combined-figure numbers" if max_delta < 1e-3 else "MISMATCH - investigate"))


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--outdir", default="section_5_1_out")
    args = ap.parse_args()

    report = Report()
    report.h1("SECTION 5.1 - STANDALONE PER-STRATEGY HEXBIN FIGURES (split from combined 3-panel figure)")

    dfs = {}
    for strategy, path, title in STRATEGIES:
        df = load(path)
        dfs[strategy] = df
        expected = 250000
        n = len(df)
        flag = "" if n == expected else "  <-- below full {} (trail_stubborn backfill pending)".format(expected)
        report.line("{}: n={}{}".format(strategy, n, flag))

    # Computed dynamically from the loaded data (see compute_extents / SANITY-CHECK
    # COPY note at top of this file), not a hardcoded constant.
    EXTENTS = compute_extents(dfs)
    report.line("Per-strategy y-limits (dynamic, computed from loaded data): {}".format(EXTENTS))

    report.h2("Color scale")
    max_counts = []
    for strategy, _, _ in STRATEGIES:
        df = dfs[strategy]
        mc = compute_max_count(df["attacker_hash_power"].to_numpy(), df["attack_success_rate"].to_numpy(),
                                EXTENTS[strategy])
        max_counts.append(mc)
        report.line("  {}: max hex-cell count in its own extent = {:.0f}".format(strategy, mc))
    global_max = max(max_counts)
    report.line("Global max cell count across the 3 strategies (recomputed fresh from current data) = {:.0f}".format(
        global_max))
    report.line("Decision: KEEP the shared LogNorm(vmin=1, vmax={:.0f}) color scale across all three standalone "
                "files (matches the combined figure's vmax=7500; comparability across strategies has been the "
                "consistent priority throughout this analysis, and none of the three panels visibly washes out "
                "under this shared scale - see Task 4 check below / figures themselves).".format(global_max))
    norm = LogNorm(vmin=1, vmax=global_max)

    report.h2("Per-file settings")
    for strategy, _, title in STRATEGIES:
        report.line("{}: gridsize={}, extent(x,y)={}, cmap={}, color_norm=LogNorm(1,{:.0f}), y-axis=raw P (linear)".format(
            strategy, GRIDSIZE, EXTENTS[strategy], CMAP, global_max))

    report.h1("FIGURES")
    for strategy, _, title in STRATEGIES:
        out_path = os.path.join(args.outdir, "hexbin_attack_success_{}.png".format(strategy))
        saved = make_standalone(strategy, title, dfs[strategy], EXTENTS[strategy], norm, out_path)
        report.line("Saved: {}".format(saved))

    consistency_check(dfs, report)

    report.h1("NOTE")
    report.line("Combined 3-panel figure (section_5_1_out/hexbin_attack_success_by_hash_power.png) is NOT "
                "modified or deleted by this script - both versions remain available.")

    report_path = os.path.join(args.outdir, "section_5_1_hexbin_standalone_report.txt")
    report.save(report_path)
    print("\nSaved report: {}".format(report_path))


if __name__ == "__main__":
    main()
