#!/usr/bin/env python3
"""
SANITY-CHECK COPY of results_new/parquet/build_analysis_table.py -- the
production script is untouched; this copy exists only to fix and verify
the ASR/attack_severity denominator bug flagged by colleague review item
(the "ASR must exclude unresolved rounds from the denominator" fix)
before touching production.

Real, confirmed bug in the production script's `_derive_outcomes`:
    attack_success_rate = n_success / n          # n = ALL rounds (200), including unresolved
    attack_severity      = sum(success_depths) / n
This contradicts D3 ("unresolved episodes must never be counted as
failures") -- dividing by all rounds implicitly treats every unresolved
round as if it were a failure (it shrinks the rate/severity by exactly the
same amount a failure would, even though the round's true outcome is
unknown). conditional_reorg_depth was NOT affected -- it was already
correctly divided by n_success alone.

Fix: both denominators changed from `n` (total rounds) to `(n_success +
n_failure)` (resolved rounds only) -- unresolved rounds excluded from
numerator AND denominator entirely, per the colleague's explicit request.
attack_severity's numerator is unchanged (success rounds contribute their
depth, failure rounds contribute 0 -- unresolved rounds simply don't
appear in the resolved-only denominator at all, which is the "must not
receive zero severity" fix: previously they silently diluted severity
toward zero by inflating the denominator; now they are excluded, not
assigned a placeholder value).

New: n_status_success / n_status_failure / n_status_unresolved are now
their own explicit output columns (previously not tracked as separate
fields at all -- ASR alone doesn't distinguish "no episodes were
unresolved" from "many were, but they don't count against you either
way", per the colleague's explicit request to report all three counts).

Everything else (system_config_id clustering, attacker_blocks_mined,
network_reorg_rate, depth_min/max, secondary metrics) is unchanged from
the production script.
"""
import argparse
import json
import math
import time
from concurrent.futures import ProcessPoolExecutor

import pyarrow as pa
import pyarrow.parquet as pq

BASE_INPUT_COLUMNS = ["source_file", "config_id", "inputParameters_json",
                       "chainReorganizationDepths_json", "attackerCausedChainReorganizationDepths_json",
                       "status_json", "simulationRoundResults_json"]

ATTACKER_BLOCKS_METRIC_NAME = "Attacker Block Rewards"

SECONDARY_METRIC_NAMES = {
    "Total Block Rewards": "avg_total_block_rewards",
    "StaleBlockRate": "avg_stale_block_rate",
}

BASE_FIELDS = [
    ("source_file", pa.string()),
    ("config_id", pa.int64()),
    ("attack_strategy", pa.string()),
    ("system_config_id", pa.int64()),
    ("attacker_config_id", pa.int64()),
    ("validator_count", pa.int64()),
    ("node_degree", pa.int64()),
    ("block_creation_interval", pa.int64()),
    ("max_block_size", pa.int64()),
    ("propagation_delay", pa.int64()),
    ("bandwidth", pa.float64()),
    ("attacker_hash_power", pa.float64()),
    ("tie_breaking_parameter", pa.float64()),
    ("n_rounds", pa.int64()),
    # Corrected (resolved-rounds-only denominator) -- see module docstring.
    ("attack_success_rate", pa.float64()),
    ("conditional_reorg_depth", pa.float64()),
    ("attack_severity", pa.float64()),
    # New explicit status-count columns (colleague's explicit request).
    ("n_status_success", pa.int64()),
    ("n_status_failure", pa.int64()),
    ("n_status_unresolved", pa.int64()),
    ("attacker_blocks_mined", pa.float64()),
    ("network_reorg_rate", pa.float64()),
    ("depth_min", pa.int64()),
    ("depth_max", pa.int64()),
    ("depth_all_valid_int", pa.bool_()),
]

SECONDARY_FIELDS = [(col, pa.float64()) for col in SECONDARY_METRIC_NAMES.values()]

_SENTINELS = {"infinity": float("inf"), "+infinity": float("inf"), "-infinity": float("-inf"), "nan": float("nan")}


def parse_float(s):
    if s is None:
        return None
    key = s.strip().lower()
    if key in _SENTINELS:
        return _SENTINELS[key]
    return float(s)


def parse_int(s):
    try:
        return int(s)
    except ValueError:
        f = parse_float(s)
        if f is None or math.isnan(f) or math.isinf(f):
            return None
        return int(f)


def design_key(ip):
    return (
        parse_int(ip["validator_count"]),
        parse_int(ip["node_degree"]),
        parse_int(ip["block_creation_interval"]),
        parse_int(ip["max_block_size"]),
    )


def build_design_map(parquet_path):
    pf = pq.ParquetFile(parquet_path)
    total = pf.metadata.num_rows
    design_set = set()
    seen = 0
    t0 = time.time()
    for i, batch in enumerate(pf.iter_batches(batch_size=20000, columns=["inputParameters_json"])):
        for row in batch.to_pylist():
            ip = json.loads(row["inputParameters_json"])
            design_set.add(design_key(ip))
        seen += batch.num_rows
        print(f"[Step1 pass1/2] scanned {seen}/{total} rows ({seen / total * 100:.1f}%) "
              f"elapsed={time.time() - t0:.1f}s, {len(design_set)} unique design configs so far", flush=True)
    return {d: i + 1 for i, d in enumerate(sorted(design_set))}


def _init_worker(design_map):
    global _DESIGN_MAP
    _DESIGN_MAP = design_map


def _round_blocks(round_metrics):
    for m in round_metrics:
        if m.get("name") == ATTACKER_BLOCKS_METRIC_NAME:
            return m["value"]
    raise ValueError(f"'{ATTACKER_BLOCKS_METRIC_NAME}' metric missing from a round")


def _derive_outcomes(depths, status, attacker_depths, rounds):
    """Pair each round's status with that round's attackerCausedChainReorganizationDepths.
    Returns (attack_success_rate, conditional_reorg_depth, attack_severity,
    n_status_success, n_status_failure, n_status_unresolved, attacker_blocks_mined,
    network_reorg_rate, depth_min, depth_max, depth_all_valid_int).

    FIX (this sanity copy only): attack_success_rate and attack_severity are now
    divided by (n_status_success + n_status_failure) -- resolved rounds only --
    instead of by n (all rounds, including unresolved). Both are None (not 0 or
    a value computed against an empty denominator) when there are zero resolved
    rounds. conditional_reorg_depth is unchanged (already n_status_success-only).
    """
    n = len(depths)
    if n == 0:
        return None, None, None, 0, 0, 0, None, None, None, None, False
    if len(rounds) != n:
        raise ValueError(f"chainReorganizationDepths has {n} rounds but simulationRoundResults has {len(rounds)}")
    if len(status) != n or len(attacker_depths) != n:
        raise ValueError(
            f"status has {len(status)} rounds and attackerCausedChainReorganizationDepths has "
            f"{len(attacker_depths)} rounds but chainReorganizationDepths has {n}"
        )

    blocks = [_round_blocks(r) for r in rounds]
    all_valid = all(isinstance(d, int) and 0 <= d <= 25 for d in depths)
    depth_min = int(min(depths))
    depth_max = int(max(depths))

    n_status_success = sum(1 for s in status if s == "success")
    n_status_failure = sum(1 for s in status if s == "failure")
    n_status_unresolved = sum(1 for s in status if s == "unresolved")
    n_resolved = n_status_success + n_status_failure

    success_depths = [d for d, s in zip(attacker_depths, status) if s == "success"]

    attack_success_rate = (n_status_success / n_resolved) if n_resolved > 0 else None
    conditional_reorg_depth = (sum(success_depths) / n_status_success) if n_status_success else None
    attack_severity = (sum(success_depths) / n_resolved) if n_resolved > 0 else None

    attacker_blocks_mined = sum(blocks) / n
    network_reorg_rate = sum(1 for d in depths if d > 0) / n

    return (attack_success_rate, conditional_reorg_depth, attack_severity,
            n_status_success, n_status_failure, n_status_unresolved,
            attacker_blocks_mined, network_reorg_rate, depth_min, depth_max, all_valid)


def _secondary_stats(rounds):
    sums = {name: 0.0 for name in SECONDARY_METRIC_NAMES}
    counts = {name: 0 for name in SECONDARY_METRIC_NAMES}
    for round_metrics in rounds:
        for m in round_metrics:
            name = m.get("name")
            if name in SECONDARY_METRIC_NAMES:
                sums[name] += m["value"]
                counts[name] += 1
    out = {}
    for name, col in SECONDARY_METRIC_NAMES.items():
        out[col] = (sums[name] / counts[name]) if counts[name] else None
    return out


def process_row(row, include_secondary):
    ip = json.loads(row["inputParameters_json"])
    dkey = design_key(ip)
    depths = json.loads(row["chainReorganizationDepths_json"])
    status = json.loads(row["status_json"])
    attacker_depths = json.loads(row["attackerCausedChainReorganizationDepths_json"])
    rounds = json.loads(row["simulationRoundResults_json"])
    (success_rate, cond_depth, severity, n_succ, n_fail, n_unres,
     blocks_mined, network_reorg_rate, dmin, dmax, all_valid) = _derive_outcomes(
        depths, status, attacker_depths, rounds)

    out = {
        "source_file": row["source_file"],
        "config_id": int(row["config_id"]),
        "attack_strategy": ip["attack_strategy"],
        "system_config_id": _DESIGN_MAP[dkey],
        "attacker_config_id": parse_int(ip["attacker_config_id"]),
        "validator_count": dkey[0],
        "node_degree": dkey[1],
        "block_creation_interval": dkey[2],
        "max_block_size": dkey[3],
        "propagation_delay": parse_int(ip["propagation_delay"]) if "propagation_delay" in ip else None,
        "bandwidth": parse_float(ip["bandwidth"]),
        "attacker_hash_power": parse_float(ip["attacker_hash_power"]),
        "tie_breaking_parameter": parse_float(ip["tie_breaking_parameter"]),
        "n_rounds": len(depths),
        "attack_success_rate": success_rate,
        "conditional_reorg_depth": cond_depth,
        "attack_severity": severity,
        "n_status_success": n_succ,
        "n_status_failure": n_fail,
        "n_status_unresolved": n_unres,
        "attacker_blocks_mined": blocks_mined,
        "network_reorg_rate": network_reorg_rate,
        "depth_min": dmin,
        "depth_max": dmax,
        "depth_all_valid_int": all_valid,
    }
    if include_secondary:
        out.update(_secondary_stats(rounds))
    return out


def process_chunk(rows, include_secondary):
    return [process_row(r, include_secondary) for r in rows]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("input", help="Path to the lossless source parquet file")
    parser.add_argument("output", help="Path to write analysis_settings.parquet to")
    parser.add_argument("--batch-size", type=int, default=5000)
    parser.add_argument("--workers", type=int, default=None, help="Default: os.cpu_count()")
    parser.add_argument("--include-secondary-metrics", action="store_true")
    args = parser.parse_args()

    print(f"[Step1] Pass 1/2: deriving design-parameter-only system_config_id from {args.input}", flush=True)
    design_map = build_design_map(args.input)
    print(f"[Step1] Found {len(design_map)} unique design configurations", flush=True)

    schema = pa.schema(BASE_FIELDS + (SECONDARY_FIELDS if args.include_secondary_metrics else []))
    columns = BASE_INPUT_COLUMNS

    pf = pq.ParquetFile(args.input)
    total_rows = pf.metadata.num_rows

    print(f"[Step1] Pass 2/2: computing corrected outcomes and writing {args.output}", flush=True)
    writer = pq.ParquetWriter(args.output, schema)
    processed = 0
    t0 = time.time()
    try:
        with ProcessPoolExecutor(max_workers=args.workers, initializer=_init_worker, initargs=(design_map,)) as ex:
            for batch_num, batch in enumerate(pf.iter_batches(batch_size=args.batch_size, columns=columns), start=1):
                rows = batch.to_pylist()
                n_workers = ex._max_workers if args.workers is None else args.workers
                n_workers = n_workers or 1
                chunk_size = max(1, len(rows) // n_workers)
                chunks = [rows[i:i + chunk_size] for i in range(0, len(rows), chunk_size)]
                results = []
                for chunk_result in ex.map(process_chunk, chunks, [args.include_secondary_metrics] * len(chunks)):
                    results.extend(chunk_result)
                table = pa.Table.from_pylist(results, schema=schema)
                writer.write_table(table)
                processed += len(rows)
                elapsed = time.time() - t0
                rate = processed / elapsed if elapsed > 0 else 0
                print(f"[Step1] batch {batch_num}: {processed}/{total_rows} rows "
                      f"({processed / total_rows * 100:.1f}%) elapsed={elapsed:.1f}s rate={rate:.0f} rows/s",
                      flush=True)
    finally:
        writer.close()

    print(f"[Step1] Done. Wrote {processed} rows to {args.output}", flush=True)


if __name__ == "__main__":
    main()
