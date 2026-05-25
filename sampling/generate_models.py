#!/usr/bin/env python3
"""
Generate per-configuration ATOSIM models from a base template and an LHS CSV.

For each row in the CSV, this creates a folder named ``atomsim-<config_id>``
containing a copy of the base model files with the row's values written into
the relevant XMI attributes.

Mapping (CSV column -> base file -> attribute):

  block_creation_interval  Net.blockchainsystem  MeanBlockTime
  max_block_size           Net.blockchainsystem  MaxBlockSize          (rounded to int)
  node_degree              Net.p2pnetwork        Subgraphs.Connectivity
  validator_count          Net.p2pnetwork        NodeTemplates.NumberOfNodeOccurences (every template)
  propagation_delay        Net.linkallocation    Latency                (all Values, both LinkAllocations)
  transaction_delay        Net.transactions      MeanTransactionCreationInterval

Other CSV columns (attacker_*, tie_breaking_parameter, transaction_acceleration)
are not written into the model files; they belong to the runtime configuration.

Usage:
    python generate_models.py
    python generate_models.py --csv path/to.csv --base path/to/threesim-net-base --out path/to/out
"""

import argparse
import csv
import re
import shutil
import sys
from pathlib import Path


BASE_FILES = (
    "Net.blockchainsystem",
    "Net.p2pnetwork",
    "Net.nodeallocation",
    "Net.blockchainsystemcomponentrepository",
    "Net.geographicalregions",
    "Net.linkallocation",
    "Net.transactions",
    "representations.aird",
)


def _replace_attr(text: str, attr: str, new_value: str) -> tuple[str, int]:
    pattern = re.compile(rf'(\b{re.escape(attr)}=")[^"]*(")')
    new_text, n = pattern.subn(lambda m: f'{m.group(1)}{new_value}{m.group(2)}', text)
    return new_text, n


def _require(text: str, attr: str, n: int, path: Path, expected_min: int = 1) -> None:
    if n < expected_min:
        raise RuntimeError(
            f"{path}: expected to replace attribute {attr!r} at least {expected_min} time(s), "
            f"but replaced {n}."
        )


def patch_blockchainsystem(path: Path, block_creation_interval: str, max_block_size: str) -> None:
    text = path.read_text(encoding="utf-8")
    text, n1 = _replace_attr(text, "MeanBlockTime", f"{float(block_creation_interval)}")
    _require(text, "MeanBlockTime", n1, path)
    text, n2 = _replace_attr(text, "MaxBlockSize", str(int(round(float(max_block_size)))))
    _require(text, "MaxBlockSize", n2, path)
    path.write_text(text, encoding="utf-8")


def patch_p2pnetwork(path: Path, node_degree: str, validator_count: str) -> None:
    text = path.read_text(encoding="utf-8")
    text, n1 = _replace_attr(text, "Connectivity", str(int(node_degree)))
    _require(text, "Connectivity", n1, path)
    text, n2 = _replace_attr(text, "NumberOfNodeOccurences", str(int(validator_count)))
    _require(text, "NumberOfNodeOccurences", n2, path, expected_min=8)
    path.write_text(text, encoding="utf-8")


def patch_linkallocation(path: Path, propagation_delay: str) -> None:
    text = path.read_text(encoding="utf-8")
    text, n = _replace_attr(text, "Latency", str(int(propagation_delay)))
    _require(text, "Latency", n, path, expected_min=1)
    path.write_text(text, encoding="utf-8")


def patch_transactions(path: Path, transaction_delay: str) -> None:
    text = path.read_text(encoding="utf-8")
    text, n = _replace_attr(text, "MeanTransactionCreationInterval", f"{float(transaction_delay)}")
    _require(text, "MeanTransactionCreationInterval", n, path)
    path.write_text(text, encoding="utf-8")


def generate_one(base_dir: Path, out_dir: Path, row: dict) -> Path:
    config_id = row["config_id"].strip()
    target = out_dir / f"atomsim-{config_id}"
    if target.exists():
        shutil.rmtree(target)
    target.mkdir(parents=True)

    for name in BASE_FILES:
        src = base_dir / name
        if not src.exists():
            continue
        shutil.copy2(src, target / name)

    patch_blockchainsystem(
        target / "Net.blockchainsystem",
        row["block_creation_interval"],
        row["max_block_size"],
    )
    patch_p2pnetwork(
        target / "Net.p2pnetwork",
        row["node_degree"],
        row["validator_count"],
    )
    patch_linkallocation(
        target / "Net.linkallocation",
        row["propagation_delay"],
    )
    patch_transactions(
        target / "Net.transactions",
        row["transaction_delay"],
    )

    return target


def main() -> int:
    script_dir = Path(__file__).resolve().parent

    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument(
        "--csv",
        type=Path,
        default=script_dir / "optimized_deterministic_lhs_configurations.csv",
        help="Path to the LHS configurations CSV.",
    )
    parser.add_argument(
        "--base",
        type=Path,
        default=script_dir / "threesim-net-base",
        help="Path to the base model template directory.",
    )
    parser.add_argument(
        "--out",
        type=Path,
        default=script_dir / "generated_models",
        help="Output directory for the generated atomsim-<config_id> folders.",
    )
    args = parser.parse_args()

    if not args.csv.is_file():
        print(f"CSV not found: {args.csv}", file=sys.stderr)
        return 1
    if not args.base.is_dir():
        print(f"Base template directory not found: {args.base}", file=sys.stderr)
        return 1

    args.out.mkdir(parents=True, exist_ok=True)

    with args.csv.open(newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        required = {
            "config_id",
            "validator_count",
            "node_degree",
            "propagation_delay",
            "block_creation_interval",
            "max_block_size",
            "transaction_delay",
        }
        missing = required - set(reader.fieldnames or [])
        if missing:
            print(f"CSV missing required columns: {sorted(missing)}", file=sys.stderr)
            return 1

        count = 0
        for row in reader:
            target = generate_one(args.base, args.out, row)
            count += 1
            print(f"  [{count:>4}] {target.relative_to(args.out.parent)}")

    print(f"\nGenerated {count} model(s) under {args.out}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
