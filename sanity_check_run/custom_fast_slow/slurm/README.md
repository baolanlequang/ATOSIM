# Custom Fast/Slow SLURM submission

## Why this exists

A local run of row-index 2 (Slow/selfish) was killed after running >8 hours
with no result produced — far outside this project's established baseline
(16-204s/row across the live sampled design). `jstack` on the running
process confirmed it was **not hung**: worker threads showed real, changing
progress through legitimate code (`SimulationRound.run` → block validation →
`TrxMemPoolImpl.removeTransactions`, which fires a full trace-event
notification per removed transaction). Slow's `max_block_size=8,000,000`
bytes under `deterministicFullBlock` mode fills every block to capacity,
across `validator_count=1000` separate node-local mempools — plausibly a
very large, if not effectively unbounded for practical purposes,
transaction-count-driven cost. This is a genuine scaling/performance
characteristic of this specific parameter combination, not a bug to fix
here (out of scope — execute-only, no jar/source changes).

This SLURM script exists to move these 4 runs off a local machine and let
them run in parallel (one array task per row, not batched) rather than
sequentially — that's the actual speed benefit: wall-clock parallelism
across the 4 rows, not a faster per-row computation. The underlying
per-transaction trace-event cost is CPU-bound single-node work and will
still take however long it takes on whatever hardware runs it.

## Prerequisites

- `atosim_results` workspace already allocated (`ws_allocate atosim_results <days>`
  if not — same as every other script in this project).
- Repo synced to the cluster with `atosim.jar`, `sampling/configuration.json`,
  and `sanity_check_run/custom_fast_slow/{run_configurations.csv,generated_models/}`
  all present at their existing relative paths.
- Submit from the repo root (paths in the script are relative to it, same
  convention as `run_selfish.sh` etc.).

## Submit

```bash
sbatch sanity_check_run/custom_fast_slow/slurm/run_custom_fast_slow.sh
```

Runs all 4 array tasks (Fast×{selfish,lead_stubborn}, Slow×{selfish,lead_stubborn}).
To resubmit just one row (e.g. if Slow/selfish times out at 72h and needs a
longer budget):

```bash
sbatch --array=2 --time=120:00:00 sanity_check_run/custom_fast_slow/slurm/run_custom_fast_slow.sh
```

## Output

`${WORKSPACE_PATH}/sanity_check_run/custom_fast_slow/{fast_selfish,fast_lead_stubborn,slow_selfish,slow_lead_stubborn}/result_run_<N>.json`
— same naming/location convention as the other sanity_check_run SLURM scripts.
