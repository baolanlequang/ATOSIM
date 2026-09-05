#!/usr/bin/env python3
"""
Generate two custom, hand-specified system configs (Fast, Slow, per the
colleague's exact review spec) plus their selfish/lead_stubborn attack
models, using the existing generate_models_two_stage.py patch functions
directly -- one row at a time, no LHS sampling, no CSV batch.

Does NOT modify generate_models_two_stage.py or anything under sampling/.
Output is entirely under sanity_check_run/custom_fast_slow/ -- these are
custom configs outside the live D6 LHS design, not part of it.

attacker_hash_power=0.35 and tie_breaking_parameter=0.5 are DEFAULT picks
(the colleague's spec didn't specify either) -- see Phase 0 of the task
report. Override CONFIGS below before regenerating if Lan wants different
values.
"""
import sys
from pathlib import Path

SCRIPT_DIR = Path(__file__).resolve().parent
SAMPLING_DIR = SCRIPT_DIR.parent.parent / "sampling"
sys.path.insert(0, str(SAMPLING_DIR))

import generate_models_two_stage as gen  # noqa: E402

BASE_DIR = SAMPLING_DIR / "threesim-net-2sc"
OUT_DIR = SCRIPT_DIR / "generated_models"

# Colleague's exact spec (Phase 4 review task) -- do not approximate.
# attacker_hash_power / tie_breaking_parameter are Phase 0 DEFAULTS, not
# part of the colleague's spec -- flagged for Lan to override.
CONFIGS = {
    "fast": dict(
        system_config_id="fast",
        validator_count="20",
        node_degree="8",
        bandwidth="125.0",
        block_creation_interval="1200000",
        max_block_size="250000",
        attacker_hash_power="0.35",       # DEFAULT (Phase 0) -- override if needed
        tie_breaking_parameter="0.5",     # DEFAULT (Phase 0), confirmed inert
    ),
    "slow": dict(
        system_config_id="slow",
        validator_count="1000",
        node_degree="2",
        bandwidth="5.0",
        block_creation_interval="60000",
        max_block_size="8000000",
        attacker_hash_power="0.35",       # DEFAULT (Phase 0) -- override if needed
        tie_breaking_parameter="0.5",     # DEFAULT (Phase 0), confirmed inert
    ),
}

STRATEGIES = ["selfish", "lead_stubborn"]


def main():
    if not BASE_DIR.is_dir():
        print(f"Base template directory not found: {BASE_DIR}", file=sys.stderr)
        return 1

    sys_models_dir = OUT_DIR / "system_models"
    atk_models_dir = OUT_DIR / "attack_models"
    sys_models_dir.mkdir(parents=True, exist_ok=True)
    atk_models_dir.mkdir(parents=True, exist_ok=True)

    for name, row in CONFIGS.items():
        print(f"\n=== {name} ===")
        sys_dir = sys_models_dir / f"sys-{name}"
        attacker_ns_id = gen.generate_system_model(BASE_DIR, sys_dir, row)
        print(f"  system model -> {sys_dir}  (attacker_ns_id={attacker_ns_id})")

        for strategy in STRATEGIES:
            strategy_dir = atk_models_dir / strategy
            strategy_dir.mkdir(parents=True, exist_ok=True)
            atk_dir = strategy_dir / f"atk-{name}"
            row_with_strategy = {**row, "attacker_config_id": name, "attack_strategy": strategy}
            gen.generate_pair_model(BASE_DIR, atk_dir, row_with_strategy, attacker_ns_id)
            print(f"  attack model ({strategy}) -> {atk_dir}")

    print(f"\nAll done -> {OUT_DIR}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
