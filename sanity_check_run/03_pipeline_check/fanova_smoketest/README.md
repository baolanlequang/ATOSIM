# fANOVA / hexbin smoke-test outputs

Pipeline-correctness smoke-test outputs only — **not scientific findings**.
Generated against the 1000-row/strategy sanity check (**selfish +
lead_stubborn only**; trail_stubborn excluded — no local sanity JSON for it
at the time this was run), using
`sanity_check_run/03_pipeline_check/parquet/analysis_settings_{selfish,lead_stubborn}_sanity.parquet`
(the corrected, `status`-based `attack_success_rate`/`conditional_reorg_depth`/
`attack_severity`, with the `bandwidth` column fix applied).

Real results require the full 250k-row design — these exist purely to confirm
`fanova_per_metric.py` and the hexbin scripts run end-to-end against the
corrected pipeline output with zero code changes to those scripts.

## Contents

- `fanova/` — `fanova_per_metric.py` output: `fanova_sobol_bar_{attack_success_rate,conditional_reorg_depth,attack_severity}.png`
  + `fanova_per_metric_report.txt`. Run against selfish+lead_stubborn combined
  (2000 rows), both per-strategy and pooled models.
- `hexbin/out/`, `hexbin/out_standalone/` — first-pass smoke test of the real,
  unmodified `run_section_5_1_hexbin.py`/`run_section_5_1_hexbin_standalone.py`.
  **These panels are empty for selfish/lead_stubborn** — the scripts hardcode a
  y-axis window (`[0.975, 1.002]`) calibrated for the old, pre-fix P
  (~0.9995–1.0); corrected P (~0.17–0.85) falls entirely below it, so there's
  nothing in-frame to draw. Kept for the record, not because they're useful —
  see `hexbin_fixed/` for the working version.
- `hexbin_scripts/` — `run_section_5_1_hexbin_sanity.py` /
  `run_section_5_1_hexbin_standalone_sanity.py`: copies of the two production
  scripts (which remain untouched) with the hardcoded y-limits replaced by
  `compute_extents()` — computed dynamically per strategy from that strategy's
  own loaded P min/max + 5% padding, so it stays correct as the data's P range
  shifts (e.g. once the full 250k dataset replaces this sanity sample) instead
  of trading one hardcoded constant for another. Everything else (binning,
  colormap, figure layout, the trail_stubborn real-data handling below) is
  unchanged. `PRIOR_BIN_MEANS`-based consistency-check deltas in these
  scripts' reports are expected to show "MISMATCH" for selfish/lead_stubborn —
  that reference is a separate, still-stale artifact of the old P definition,
  not fixed here (doesn't crash, just prints an expected mismatch).
- `hexbin_fixed/out/`, `hexbin_fixed/out_standalone/` — output of the two
  scripts above. Selfish/lead_stubborn panels now show real, non-empty hexbin
  density. Y-axis ranges actually used: selfish `[0.136, 0.884]`,
  lead_stubborn `[0.179, 0.866]`, trail_stubborn `[0.218, 1.0]` (all computed
  from the loaded data, not hardcoded).

**Note on the trail_stubborn panel** (applies to both `hexbin/` and
`hexbin_fixed/`): both hexbin scripts hardcode needing all three strategies
present. Since no trail_stubborn sanity data exists, the trail_stubborn panel
in these charts was produced from the **real, existing, unmodified**
`corrected_analysis/analysis_settings_trail_stubborn.parquet` (read-only
reference, not sanity data) — only to satisfy the script's 3-strategy
requirement so selfish/lead_stubborn could be smoke-tested at all. It is not
part of this sanity check and should not be read as a trail_stubborn sanity
result.

**Reminder for whoever picks this up next**: the real production
`run_section_5_1_hexbin.py`/`run_section_5_1_hexbin_standalone.py` under
`results_new/parquet/` still have the stale hardcoded y-axis problem — this
task only fixed *copies* for the smoke test. Before those scripts can produce
a meaningful chart on the full, corrected 250k dataset, the same
`compute_extents`-style fix needs to be applied to the real scripts (or the
production data needs its own axis recalibration), not just this copy.
