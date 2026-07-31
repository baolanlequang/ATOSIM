#!/usr/bin/env python3
"""
Generate per-configuration ATOSIM models from a base template and two
LHS CSVs (output of lhs_generate_sample_two_stage.py).

Two-stage design
----------------
Stage 1 — System models (system_configurations.csv, 5,000 rows = 500 core
configs × 10 bandwidth variants each). Topology and block params are
identical within a core_config_id group, so they are generated once and
reused; only bandwidth is repatched per system_config_id:
    block_creation_interval  → Net.blockchainsystem  MeanBlockTime      (per core_config_id)
    max_block_size           → Net.blockchainsystem  MaxBlockSize       (per core_config_id)
    node_degree               → Net.p2pnetwork        Subgraphs.Connectivity  (per core_config_id)
    validator_count           → Net.p2pnetwork + Net.nodeallocation  (NodeTemplates rebuild, per core_config_id)
    bandwidth (Mbit/s)        → Net.linkallocation    Throughput (bps)  (per system_config_id)

Stage 2 — Attack models (attacker_configurations.csv, 100 rows):
  Two attacker parameters are patched per attacker_config_id × 3 strategies:
    attacker_hash_power      → Net.attackmodel  attackers.powerShare
    tie_breaking_parameter   → Net.attackmodel  attack.gamma

Folder layout
-------------
  <out>/
    system_models/
      sys-1/ ... sys-5000/         ← 5,000 folders, no Net.attackmodel
                                      (500 unique topologies × 10 bandwidth variants)
    attack_models/
      selfish/
        atk-1/ ... atk-100/        ← 100 folders, Net.attackmodel only
      lead_stubborn/
        atk-1/ ... atk-100/
      trail_stubborn/
        atk-1/ ... atk-100/

Usage
-----
    python generate_models_two_stage.py
    python generate_models_two_stage.py --system-csv path/to/system_configurations.csv
    python generate_models_two_stage.py --attacker-csv path/to/attacker_configurations.csv
    python generate_models_two_stage.py --base path/to/base --out path/to/out
    python generate_models_two_stage.py --mean-trx-interval 600000
"""

from __future__ import annotations

import argparse
import csv
import os
import random
import re
import shutil
import sys
import uuid
from pathlib import Path


ATTACK_STRATEGIES = ["selfish", "lead_stubborn", "trail_stubborn"]

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


# ─────────────────────────────────────────────
# Shared helpers
# ─────────────────────────────────────────────

def _replace_attr(text: str, attr: str, new_value: str) -> tuple[str, int]:
    pattern = re.compile(rf'(\b{re.escape(attr)}=")[^"]*(")')
    new_text, n = pattern.subn(lambda m: f'{m.group(1)}{new_value}{m.group(2)}', text)
    return new_text, n


def _require(text: str, attr: str, n: int, path: Path, expected_min: int = 1) -> None:
    if n < expected_min:
        raise RuntimeError(
            f"{path}: expected to replace attribute {attr!r} at least "
            f"{expected_min} time(s), but replaced {n}."
        )


def _new_id() -> str:
    return "_" + uuid.uuid4().hex


# ─────────────────────────────────────────────
# Stage 1 patches  (system parameters)
# ─────────────────────────────────────────────

def patch_blockchainsystem(path: Path, block_creation_interval: str, max_block_size: str) -> None:
    text = path.read_text(encoding="utf-8")
    text, n1 = _replace_attr(text, "MeanBlockTime", f"{float(block_creation_interval)}")
    _require(text, "MeanBlockTime", n1, path)
    text, n2 = _replace_attr(text, "MaxBlockSize", str(int(round(float(max_block_size)))))
    _require(text, "MaxBlockSize", n2, path)
    path.write_text(text, encoding="utf-8")


_NODE_ALLOC_BLOCK  = re.compile(r"<NodeAllocations\b[^>]*>.*?</NodeAllocations>", re.DOTALL)
_NODE_TEMPLATE_BLOCK = re.compile(r"<NodeTemplates\b[^>]*>.*?</NodeTemplates>", re.DOTALL)


def _clone_node_allocation(block: str) -> tuple[str, str]:
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
    nodealloc_path = target / "Net.nodeallocation"
    p2p_path       = target / "Net.p2pnetwork"

    nodealloc_text = nodealloc_path.read_text(encoding="utf-8")
    p2p_text       = p2p_path.read_text(encoding="utf-8")

    archetypes = _NODE_ALLOC_BLOCK.findall(nodealloc_text)
    if not archetypes:
        raise RuntimeError(f"{nodealloc_path}: no <NodeAllocations> blocks found.")

    total = int(validator_count)
    if total <= 0:
        raise ValueError(f"validator_count must be positive, got {validator_count!r}")
    n_full, remainder = divmod(total, 4)
    counts = [4] * n_full + ([remainder] if remainder else [])

    new_outer_ids: list[str] = []
    new_blocks: list[str]    = []
    for i, _ in enumerate(counts):
        archetype = archetypes[i % len(archetypes)]
        outer_id, cloned = _clone_node_allocation(archetype)
        new_outer_ids.append(outer_id)
        new_blocks.append(cloned)

    nodealloc_parts  = _NODE_ALLOC_BLOCK.split(nodealloc_text)
    new_nodealloc    = nodealloc_parts[0] + "\n  ".join(new_blocks) + nodealloc_parts[-1]
    nodealloc_path.write_text(new_nodealloc, encoding="utf-8")

    def make_template(outer_id: str, count: int) -> str:
        return (
            f'<NodeTemplates id="{_new_id()}" NumberOfNodeOccurences="{count}">\n'
            f'        <Allocation href="Net.nodeallocation#{outer_id}"/>\n'
            f"      </NodeTemplates>"
        )

    template_block = "\n      ".join(
        make_template(oid, c) for oid, c in zip(new_outer_ids, counts)
    )

    p2p_parts = _NODE_TEMPLATE_BLOCK.split(p2p_text)
    if len(p2p_parts) < 2:
        raise RuntimeError(f"{p2p_path}: no <NodeTemplates> blocks found.")
    new_p2p = p2p_parts[0] + template_block + p2p_parts[-1]
    new_p2p, n = _replace_attr(new_p2p, "Connectivity", str(int(node_degree)))
    _require(new_p2p, "Connectivity", n, p2p_path)
    p2p_path.write_text(new_p2p, encoding="utf-8")


def patch_linkallocation(path: Path, bandwidth_mbps: str) -> None:
    """Patch Throughput (bps) from a bandwidth value given in Mbit/s.

    Net.linkallocation's Throughput field is bps; P2PLink divides it by 8000
    to get bytes/ms, so the conversion here must match: Mbit/s * 1_000_000.
    """
    bps = int(round(float(bandwidth_mbps) * 1_000_000))
    text = path.read_text(encoding="utf-8")
    text, n = _replace_attr(text, "Throughput", str(bps))
    _require(text, "Throughput", n, path, expected_min=1)
    path.write_text(text, encoding="utf-8")


def patch_transactions(path: Path, mean_transaction_creation_interval: float) -> None:
    text = path.read_text(encoding="utf-8")
    text, n = _replace_attr(
        text, "MeanTransactionCreationInterval",
        f"{float(mean_transaction_creation_interval)}"
    )
    _require(text, "MeanTransactionCreationInterval", n, path)
    path.write_text(text, encoding="utf-8")


# ─────────────────────────────────────────────
# Stage 2 patches  (attacker-capability parameters)
# ─────────────────────────────────────────────

_ATTACKERS_BLOCK = re.compile(r"<attackers\b.*?</attackers>", re.DOTALL)
_NODE_SYSTEM_ID  = re.compile(r'<NodeSystem\b[^>]*\bid="([^"]+)"')
_ATTACK_TAG      = re.compile(r"<attack\b.*?>", re.DOTALL)
_MONITORED_NODES = re.compile(r'\s+monitoredNodes="[^"]*"')

# The base template models a SelfishMiningAttack. lead_stubborn/trail_stubborn
# need their own xsi:type, which doesn't have a monitoredNodes feature.
ATTACK_XSI_TYPE = {
    "selfish": "attackmodel:SelfishMiningAttack",
    "lead_stubborn": "attackmodel:LeadStubbornAttack",
    "trail_stubborn": "attackmodel:TrailStubbornAttack",
}


def _set_attack_strategy_type(text: str, strategy: str) -> tuple[str, int]:
    new_type = ATTACK_XSI_TYPE[strategy]
    text, n = re.subn(
        r'xsi:type="attackmodel:SelfishMiningAttack"', f'xsi:type="{new_type}"', text, count=1
    )
    if strategy != "selfish":
        text = _MONITORED_NODES.sub("", text, count=1)
    return text, n


def _set_attack_gamma(text: str, gamma_value: str) -> tuple[str, int]:
    match = _ATTACK_TAG.search(text)
    if match is None:
        return text, 0
    tag = match.group(0)
    if "gamma=" in tag:
        new_tag = re.sub(r'gamma="[^"]*"', f'gamma="{gamma_value}"', tag)
    elif tag.endswith("/>"):
        new_tag = f'{tag[:-2]} gamma="{gamma_value}"/>'
    else:
        new_tag = f'{tag[:-1]} gamma="{gamma_value}">'
    return text[:match.start()] + new_tag + text[match.end():], 1


def _node_system_ids(nodealloc_path: Path) -> list[str]:
    text = nodealloc_path.read_text(encoding="utf-8")
    ids  = _NODE_SYSTEM_ID.findall(text)
    if not ids:
        raise RuntimeError(f"{nodealloc_path}: no <NodeSystem> elements found.")
    return ids


def patch_attackmodel(
    path: Path,
    attacker_hash_power: str,
    node_system_ids: list[str],
    rng: random.Random,
    gamma: str,
) -> None:
    """Patch Net.attackmodel with attacker-capability parameters (Stage 2)."""
    text = path.read_text(encoding="utf-8")

    if _ATTACKERS_BLOCK.search(text) is None:
        raise RuntimeError(f"{path}: no <attackers> block found.")

    # Always use exactly 1 attacker node (two-stage design assumption)
    chosen_ns  = rng.sample(node_system_ids, 1)
    attacker_id = _new_id()
    power_share = f"{float(attacker_hash_power)}"

    attacker_block = (
        f'<attackers id="{attacker_id}" powerShare="{power_share}">\n'
        f'    <linkedNodeSystem href="Net.nodeallocation#{chosen_ns[0]}"/>\n'
        f"  </attackers>"
    )

    parts = _ATTACKERS_BLOCK.split(text)
    text  = parts[0] + attacker_block + parts[-1]

    if "monitoredNodes=" in text:
        text, n = _replace_attr(text, "monitoredNodes", attacker_id)
        _require(text, "monitoredNodes", n, path)

    text, n = _set_attack_gamma(text, f"{float(gamma)}")
    _require(text, "gamma", n, path)

    path.write_text(text, encoding="utf-8")


# ─────────────────────────────────────────────
# Two-stage generation
# ─────────────────────────────────────────────

def generate_system_model(
    base_dir: Path,
    sys_dir: Path,
    row: dict,
) -> None:
    """Stage 1: copy base files (excluding Net.attackmodel) and apply system-parameter patches.

    sys_dir is created (or reused if it already exists — idempotent).
    Net.attackmodel is intentionally excluded — it lives in attack_models/ only.
    """
    sys_dir.mkdir(parents=True, exist_ok=True)

    for name in BASE_FILES:
        if name == "Net.attackmodel":
            continue                      # attack model is generated separately
        src = base_dir / name
        if not src.exists():
            continue
        shutil.copy2(src, sys_dir / name)

    patch_blockchainsystem(
        sys_dir / "Net.blockchainsystem",
        row["block_creation_interval"],
        row["max_block_size"],
    )
    patch_topology(sys_dir, row["node_degree"], row["validator_count"])
    patch_linkallocation(sys_dir / "Net.linkallocation", row["bandwidth"])


def generate_system_model_variant(
    core_dir: Path,
    sys_dir: Path,
    bandwidth_mbps: str,
) -> None:
    """Stage 1, repeat variant: reuse an already-patched core topology (same
    core_config_id) and only repatch the bandwidth-varying file.

    Avoids repeating the expensive random NodeAllocation cloning in
    patch_topology for every one of the 10 bandwidth variants that
    share an identical topology.
    """
    sys_dir.mkdir(parents=True, exist_ok=True)

    for name in BASE_FILES:
        if name == "Net.attackmodel":
            continue
        src = core_dir / name
        if not src.exists():
            continue
        shutil.copy2(src, sys_dir / name)

    patch_linkallocation(sys_dir / "Net.linkallocation", bandwidth_mbps)


def generate_pair_model(
    base_dir: Path,
    atk_dir: Path,
    row: dict,
    sys_ns_ids: list[str],
) -> None:
    """Stage 2: copy Net.attackmodel from base and patch with attacker-capability params.

    Only Net.attackmodel is written into atk_dir — system files live in system_models/
    and are not duplicated here.

    atk_dir is always recreated from scratch to ensure reproducibility.
    """
    if atk_dir.exists():
        shutil.rmtree(atk_dir)
    atk_dir.mkdir(parents=True)

    src = base_dir / "Net.attackmodel"
    if not src.exists():
        return                            # no attack model in base template — skip

    dst = atk_dir / "Net.attackmodel"
    shutil.copy2(src, dst)

    strategy = row.get("attack_strategy", "selfish")
    text, n = _set_attack_strategy_type(dst.read_text(encoding="utf-8"), strategy)
    _require(text, "xsi:type", n, dst)
    dst.write_text(text, encoding="utf-8")

    if row.get("attacker_hash_power"):
        rng = random.Random(f"{row['attacker_config_id']}-{row.get('attack_strategy', '')}")
        patch_attackmodel(
            dst,
            row["attacker_hash_power"],
            sys_ns_ids,
            rng,
            row.get("tie_breaking_parameter", "0.0"),
        )


# ─────────────────────────────────────────────
# Main
# ─────────────────────────────────────────────

def main() -> int:
    script_dir = Path(__file__).resolve().parent

    parser = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument(
        "--system-csv",
        type=Path,
        default=script_dir / "system_configurations.csv",
        help="Path to the system configurations CSV (Stage 1, 5,000 rows = 500 core configs x 10 bandwidth variants).",
    )
    parser.add_argument(
        "--attacker-csv",
        type=Path,
        default=script_dir / "attacker_configurations.csv",
        help="Path to the attacker configurations CSV (Stage 2, 100 rows).",
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
        help="Root output directory.",
    )
    parser.add_argument(
        "--mean-trx-interval",
        type=float,
        default=None,
        help="If set, overwrites MeanTransactionCreationInterval (ms) in Net.transactions.",
    )
    args = parser.parse_args()

    for p in (args.system_csv, args.attacker_csv):
        if not p.is_file():
            print(f"CSV not found: {p}", file=sys.stderr)
            return 1
    if not args.base.is_dir():
        print(f"Base template directory not found: {args.base}", file=sys.stderr)
        return 1

    sys_models_dir = args.out / "system_models"
    atk_models_dir = args.out / "attack_models"
    sys_models_dir.mkdir(parents=True, exist_ok=True)
    atk_models_dir.mkdir(parents=True, exist_ok=True)

    # ── Stage 1: generate system models ───────────────────────────────────
    required_sys = {
        "system_config_id", "validator_count", "node_degree",
        "bandwidth", "block_creation_interval", "max_block_size",
    }
    sys_rows: list[dict] = []
    with args.system_csv.open(newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        missing = required_sys - set(reader.fieldnames or [])
        if missing:
            print(f"system CSV missing columns: {sorted(missing)}", file=sys.stderr)
            return 1
        has_core_config_id = "core_config_id" in (reader.fieldnames or [])
        sys_rows = list(reader)

    print(f"Stage 1 — generating {len(sys_rows)} system model(s)...")
    # sys_id -> list of NodeSystem IDs (cached for Stage 2)
    sys_ns_ids: dict[str, list[str]] = {}
    # core_config_id -> reference sys_dir whose topology has already been patched;
    # reused (copied, not regenerated) by every other bandwidth variant
    # sharing that core config.
    core_dirs: dict[str, Path] = {}
    n_topologies = 0
    for i, row in enumerate(sys_rows, 1):
        sys_id  = row["system_config_id"].strip()
        sys_dir = sys_models_dir / f"sys-{sys_id}"
        core_id = row["core_config_id"].strip() if has_core_config_id else sys_id

        core_dir = core_dirs.get(core_id)
        if core_dir is None:
            generate_system_model(args.base, sys_dir, row)
            core_dirs[core_id] = sys_dir
            n_topologies += 1
        else:
            generate_system_model_variant(core_dir, sys_dir, row["bandwidth"])

        sys_ns_ids[sys_id] = _node_system_ids(sys_dir / "Net.nodeallocation")
        if i % 100 == 0 or i <= 3:
            print(f"  [{i:>4}] sys-{sys_id}")

    print(
        f"  Done: {len(sys_rows)} system model(s) "
        f"({n_topologies} unique topolog{'y' if n_topologies == 1 else 'ies'}) → {sys_models_dir}"
    )

    # ── Stage 2: generate attacker × strategy attack models ────────────────
    required_atk = {
        "attacker_config_id", "attacker_hash_power", "tie_breaking_parameter",
    }
    atk_rows: list[dict] = []
    with args.attacker_csv.open(newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        missing = required_atk - set(reader.fieldnames or [])
        if missing:
            print(f"attacker CSV missing columns: {sorted(missing)}", file=sys.stderr)
            return 1
        atk_rows = list(reader)

    # Pick any system config's ns_ids as the representative set for attack models.
    # All system configs share the same attacker NodeSystem slot structure;
    # we use sys-1 as the canonical reference.
    representative_ns_ids = sys_ns_ids[sys_rows[0]["system_config_id"].strip()]

    print(f"\nStage 2 — generating {len(atk_rows)} × {len(ATTACK_STRATEGIES)} attack model(s)...")
    atk_count = 0
    for strategy in ATTACK_STRATEGIES:
        strategy_dir = atk_models_dir / strategy
        strategy_dir.mkdir(parents=True, exist_ok=True)
        for row in atk_rows:
            atk_id  = row["attacker_config_id"].strip()
            atk_dir = strategy_dir / f"atk-{atk_id}"
            # Pass strategy name in row so patch_attackmodel can use it if needed
            row_with_strategy = {**row, "attack_strategy": strategy}
            generate_pair_model(
                args.base, atk_dir, row_with_strategy,
                representative_ns_ids,
            )
            atk_count += 1
            if atk_count % 100 == 0 or atk_count <= 3:
                print(f"  [{atk_count:>4}] {strategy}/atk-{atk_id}")

    print(
        f"  Done: {atk_count} attack model(s) → {atk_models_dir}"
        f"\n        ({len(ATTACK_STRATEGIES)} strategies × {len(atk_rows)} attacker configs)"
    )
    print(f"\nAll done → {args.out}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
