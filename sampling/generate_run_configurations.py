#!/usr/bin/env python3
"""
Build the per-strategy run CSVs consumed by run_selfish.sh / run_stubborn_lead.sh /
run_stubborn_trail.sh from the two-stage LHS outputs (system_configurations.csv,
attacker_configurations.csv).

Pairing scheme
--------------
Full Cartesian product: every one of the 2,500 system configs (250 core configs x
10 propagation_delay variants each) is paired with every one of the 100 attacker
configs, for 2,500 * 100 = 250,000 rows per strategy.
Row order is system-major (all 100 attacker configs for system_config_id=1, then
all 100 for system_config_id=2, ...), and config_id is the 1-based row number, so
--row-index (0-indexed) maps directly to a SLURM_ARRAY_TASK_ID in --array=0-249999.

Output
------
  run_configurations_selfish.csv        (attack_strategy=selfish)
  run_configurations_lead_stubborn.csv  (attack_strategy=lead_stubborn)
  run_configurations_trail_stubborn.csv (attack_strategy=trail_stubborn)

Each row carries the columns ATOSIMSimulator.validateCsvColumns requires:
config_id, system_config_id, validator_count, node_degree, propagation_delay,
block_creation_interval, max_block_size, attacker_config_id, attack_strategy,
plus attacker_hash_power/tie_breaking_parameter for traceability in the output JSON.
"""

from __future__ import annotations

import csv
from pathlib import Path

STRATEGIES = ["selfish", "lead_stubborn", "trail_stubborn"]

SYSTEM_COLUMNS = [
    "validator_count", "node_degree", "propagation_delay",
    "block_creation_interval", "max_block_size",
]
ATTACKER_COLUMNS = ["attacker_hash_power", "tie_breaking_parameter"]


def main() -> int:
    script_dir = Path(__file__).resolve().parent

    with (script_dir / "system_configurations.csv").open(newline="", encoding="utf-8") as f:
        system_rows = list(csv.DictReader(f))

    with (script_dir / "attacker_configurations.csv").open(newline="", encoding="utf-8") as f:
        attacker_rows = list(csv.DictReader(f))

    fieldnames = (
        ["config_id", "system_config_id"] + SYSTEM_COLUMNS
        + ["attacker_config_id", "attack_strategy"] + ATTACKER_COLUMNS
    )

    total = len(system_rows) * len(attacker_rows)

    for strategy in STRATEGIES:
        out_path = script_dir / f"run_configurations_{strategy}.csv"
        with out_path.open("w", newline="", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=fieldnames)
            writer.writeheader()

            config_id = 0
            for sys_row in system_rows:
                for atk_row in attacker_rows:
                    config_id += 1

                    row = {"config_id": config_id, "system_config_id": sys_row["system_config_id"]}
                    row.update({c: sys_row[c] for c in SYSTEM_COLUMNS})
                    row["attacker_config_id"] = atk_row["attacker_config_id"]
                    row["attack_strategy"] = strategy
                    row.update({c: atk_row[c] for c in ATTACKER_COLUMNS})

                    writer.writerow(row)

        print(f"Wrote {total} rows -> {out_path}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
