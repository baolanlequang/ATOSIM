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
  validator_count          Net.p2pnetwork +      Rebuilds the NodeTemplates list with ceil(validator_count/4)
                           Net.nodeallocation    entries: floor(validator_count/4) templates with
                                                 NumberOfNodeOccurences=4 plus (when validator_count % 4 != 0)
                                                 one tail template carrying the remainder. Each template
                                                 references its own freshly-cloned NodeAllocation
                                                 (round-robin over the 8 archetypes, new ids throughout).
  propagation_delay        Net.linkallocation    Latency                (all Values, both LinkAllocations)
  attacker_hash_power      Net.attackmodel       attackers.powerShare    (only if Net.attackmodel exists)
  number_of_attackers      Net.attackmodel       Rebuilds the <attackers> list with
                                                 min(number_of_attackers, available NodeSystems)
                                                 AttackerNode elements. Each carries the full
                                                 attacker_hash_power as its powerShare and links a
                                                 distinct NodeSystem chosen at random (seeded by
                                                 config_id) from the regenerated Net.nodeallocation;
                                                 the attack's monitoredNodes is updated to match.

Other CSV columns (tie_breaking_parameter) are not written into the model
files; they belong to the runtime configuration.
Net.transactions is copied verbatim from the base.

Usage:
    python generate_models.py
    python generate_models.py --csv path/to.csv --base path/to/threesim-net-2sc --out path/to/out
"""

import argparse
import csv
import random
import re
import shutil
import sys
import uuid
from pathlib import Path


BASE_FILES = (
    "Net.blockchainsystem",
    "Net.p2pnetwork",
    "Net.nodeallocation",
    "Net.bscmrepository",
    "Net.blockchainsystemcomponentrepository",
    "Net.geographicalregions",
    "Net.linkallocation",
    "Net.transactions",
    "Net.attackmodel",
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


_NODE_ALLOC_BLOCK = re.compile(r"<NodeAllocations\b[^>]*>.*?</NodeAllocations>", re.DOTALL)
_NODE_TEMPLATE_BLOCK = re.compile(r"<NodeTemplates\b[^>]*>.*?</NodeTemplates>", re.DOTALL)


def _new_id() -> str:
    return "_" + uuid.uuid4().hex


def _clone_node_allocation(block: str) -> tuple[str, str]:
    """Clone a <NodeAllocations> block, regenerating every locally-defined id.

    Returns (new_outer_id, new_block_text). External hrefs (Net.bscmrepository#...,
    Net.geographicalregions#...) are untouched because their target ids are not
    defined inside the block.
    """
    defined_ids = set(re.findall(r'\bid="([^"]+)"', block))
    mapping = {old: _new_id() for old in defined_ids}

    def repl(m: "re.Match[str]") -> str:
        value = m.group(1)
        return f'"{mapping[value]}"' if value in mapping else m.group(0)

    new_block = re.sub(r'"([^"]+)"', repl, block)
    outer = re.search(r'<NodeAllocations\b[^>]*\bid="([^"]+)"', new_block)
    if outer is None:
        raise RuntimeError("Cloned NodeAllocations block lost its outer id.")
    return outer.group(1), new_block


def patch_topology(target: Path, node_degree: str, validator_count: str) -> None:
    """Rebuild Net.p2pnetwork's NodeTemplates list and Net.nodeallocation."""
    nodealloc_path = target / "Net.nodeallocation"
    p2p_path = target / "Net.p2pnetwork"

    nodealloc_text = nodealloc_path.read_text(encoding="utf-8")
    p2p_text = p2p_path.read_text(encoding="utf-8")

    archetypes = _NODE_ALLOC_BLOCK.findall(nodealloc_text)
    if not archetypes:
        raise RuntimeError(f"{nodealloc_path}: no <NodeAllocations> blocks found.")

    total = int(validator_count)
    if total <= 0:
        raise ValueError(f"validator_count must be positive, got {validator_count!r}")
    n_full, remainder = divmod(total, 4)
    counts = [4] * n_full + ([remainder] if remainder else [])

    new_outer_ids: list[str] = []
    new_blocks: list[str] = []
    for i, _ in enumerate(counts):
        archetype = archetypes[i % len(archetypes)]
        outer_id, cloned = _clone_node_allocation(archetype)
        new_outer_ids.append(outer_id)
        new_blocks.append(cloned)

    nodealloc_parts = _NODE_ALLOC_BLOCK.split(nodealloc_text)
    new_nodealloc = nodealloc_parts[0] + "\n  ".join(new_blocks) + nodealloc_parts[-1]
    nodealloc_path.write_text(new_nodealloc, encoding="utf-8")

    def make_template(outer_id: str, count: int) -> str:
        return (
            f'<NodeTemplates id="{_new_id()}" NumberOfNodeOccurences="{count}">\n'
            f'        <Allocation href="Net.nodeallocation#{outer_id}"/>\n'
            f"      </NodeTemplates>"
        )

    template_block = "\n      ".join(
        make_template(outer_id, count) for outer_id, count in zip(new_outer_ids, counts)
    )

    p2p_parts = _NODE_TEMPLATE_BLOCK.split(p2p_text)
    if len(p2p_parts) < 2:
        raise RuntimeError(f"{p2p_path}: no <NodeTemplates> blocks found.")
    new_p2p = p2p_parts[0] + template_block + p2p_parts[-1]
    new_p2p, n = _replace_attr(new_p2p, "Connectivity", str(int(node_degree)))
    _require(new_p2p, "Connectivity", n, p2p_path)
    p2p_path.write_text(new_p2p, encoding="utf-8")


def patch_linkallocation(path: Path, propagation_delay: str) -> None:
    text = path.read_text(encoding="utf-8")
    text, n = _replace_attr(text, "Latency", str(int(propagation_delay)))
    _require(text, "Latency", n, path, expected_min=1)
    path.write_text(text, encoding="utf-8")


_ATTACKERS_BLOCK = re.compile(r"<attackers\b.*?</attackers>", re.DOTALL)
_NODE_SYSTEM_ID = re.compile(r'<NodeSystem\b[^>]*\bid="([^"]+)"')


def _node_system_ids(nodealloc_path: Path) -> list[str]:
    text = nodealloc_path.read_text(encoding="utf-8")
    ids = _NODE_SYSTEM_ID.findall(text)
    if not ids:
        raise RuntimeError(f"{nodealloc_path}: no <NodeSystem> elements found.")
    return ids


def patch_attackmodel(
    path: Path,
    attacker_hash_power: str,
    number_of_attackers: int,
    node_system_ids: list[str],
    rng: random.Random,
) -> None:
    """Rebuild the <attackers> list with one AttackerNode per attacker.

    Each AttackerNode carries the full ``attacker_hash_power`` as its powerShare
    and links a *distinct* NodeSystem picked at random from ``node_system_ids``.
    The number of attackers is capped at the number of available NodeSystems.
    The attack's ``monitoredNodes`` attribute is rewritten to reference every
    newly created AttackerNode.
    """
    text = path.read_text(encoding="utf-8")

    if _ATTACKERS_BLOCK.search(text) is None:
        raise RuntimeError(f"{path}: no <attackers> block found.")

    count = max(1, min(number_of_attackers, len(node_system_ids)))
    chosen_node_systems = rng.sample(node_system_ids, count)
    attacker_ids = [_new_id() for _ in range(count)]
    power_share = f"{float(attacker_hash_power)}"

    def make_attacker(attacker_id: str, node_system_id: str) -> str:
        return (
            f'<attackers id="{attacker_id}" powerShare="{power_share}">\n'
            f'    <linkedNodeSystem href="Net.nodeallocation#{node_system_id}"/>\n'
            f"  </attackers>"
        )

    attackers_block = "\n  ".join(
        make_attacker(aid, nsid) for aid, nsid in zip(attacker_ids, chosen_node_systems)
    )

    parts = _ATTACKERS_BLOCK.split(text)
    text = parts[0] + attackers_block + parts[-1]

    if 'monitoredNodes=' in text:
        text, n = _replace_attr(text, "monitoredNodes", " ".join(attacker_ids))
        _require(text, "monitoredNodes", n, path)

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
    patch_topology(
        target,
        row["node_degree"],
        row["validator_count"],
    )
    patch_linkallocation(
        target / "Net.linkallocation",
        row["propagation_delay"],
    )

    attackmodel_path = target / "Net.attackmodel"
    if attackmodel_path.exists() and row.get("attacker_hash_power"):
        number_of_attackers = int(row.get("number_of_attackers") or 1)
        node_system_ids = _node_system_ids(target / "Net.nodeallocation")
        rng = random.Random(config_id)
        patch_attackmodel(
            attackmodel_path,
            row["attacker_hash_power"],
            number_of_attackers,
            node_system_ids,
            rng,
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
        default=script_dir / "threesim-net-2sc",
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
        }
        if (args.base / "Net.attackmodel").exists():
            required.add("attacker_hash_power")
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
