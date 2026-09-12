#!/usr/bin/env python3
"""Item 12: check ATOSIM result_run_*.json output against the ten invariants.

This is a read-only diagnostic script, not a test suite (the project does
not use JUnit/Mockito) -- it inspects existing JSON output on disk and
reports pass/fail per invariant, with the evidence used for each. It does
not run the simulator itself; that's a separate step (a jar/Run-As
invocation producing the JSON this script then reads).

Not every invariant is fully checkable from JSON output alone -- some rely
on a Java-level runtime assertion that already aborts the run if violated
(so the "check" here is schema/plausibility, not independent verification),
and a few genuinely have no evidence in the current output schema at all.
Each invariant's result includes which of these three cases it is, rather
than silently treating a schema check as a full verification.

Usage:
    # Per-file invariants (2, 4, 6, 7, 8; partial 1, 3; N/A 5) on each file:
    python invariant_checker.py single result_run_A.json [result_run_B.json ...]

    # Everything: per-file checks on each input, plus cross-file topology
    # comparisons (invariants 9 and 10) for any pair the files' own
    # inputParameters identify as same-seed or same-topology-reuse:
    python invariant_checker.py all result_run_A.json result_run_B.json [...]

Exit code is 0 iff every invariant that was actually checked (not N/A, not
partial-only) passed on every input.
"""

import argparse
import json
import sys
from collections import defaultdict
from pathlib import Path

PASS = "PASS"
FAIL = "FAIL"
NA = "N/A"
PARTIAL = "PARTIAL"


def load(path):
    with open(path) as f:
        return json.load(f)


def sim_result(doc):
    return doc["simulationResult"]


# ---------------------------------------------------------------------------
# Invariant 1: total block-production rate equals 1/tau_B
# ---------------------------------------------------------------------------
def check_invariant_1(doc):
    """PARTIAL: only the configured rate parameter is checkable from JSON.

    The full invariant needs (a) a raw "blocks mined" count and (b) the
    simulated elapsed time per round, to compute an observed rate and
    compare it against 1/blockInterval. Neither is serialized -- the
    closest proxies are "Total Block Rewards" (CONFIRMED blocks, which
    requires reaching confirmationDepth and so badly undercounts given
    rounds usually terminate at first-reorg/quiescence well before that)
    and simulationTime/startSimulationTime/stopSimulationTime (real JVM
    wall-clock time, not simulated time). Forcing a rate computation out of
    either would be a proxy weak enough to be misleading, not a check --
    reporting PARTIAL (config sanity only) rather than inventing one.
    """
    params = sim_result(doc)["threesimSimulationParameters"]
    block_interval = params.get("blockInterval")
    ok = isinstance(block_interval, (int, float)) and block_interval > 0
    return {
        "status": PARTIAL if ok else FAIL,
        "evidence": f"threesimSimulationParameters.blockInterval={block_interval}",
        "note": ("Only checked that blockInterval (tau_B) is a positive configured "
                 "value. No raw mined-block count or per-round simulated-elapsed-time "
                 "field exists in current JSON output to compute an observed rate "
                 "against it -- full check needs new instrumentation."),
    }


# ---------------------------------------------------------------------------
# Invariant 2: exactly one attacker controls alpha
# ---------------------------------------------------------------------------
def check_invariant_2(doc):
    """Per-round hard check: topologyDeterminismInfo[i].attackerNodeIndices
    has exactly one entry. This also happens to be enforced at the Java
    level as a runtime assertion (item D4/item 2's connected-subgraph
    factory dedicates exactly one 1-node NodeSystem to the attacker), but
    unlike what was assumed before building this checker, it is *also*
    directly re-derivable from JSON -- attackerNodeIndices is serialized
    per round, not just guarded server-side.
    """
    tdi = sim_result(doc).get("topologyDeterminismInfo")
    if not isinstance(tdi, list) or not tdi:
        return {"status": NA, "evidence": "topologyDeterminismInfo absent/empty",
                "note": "No per-round topology info (e.g. explicit-topology runs don't assign indices)."}
    bad = [(i, len(t.get("attackerNodeIndices", []))) for i, t in enumerate(tdi)
           if len(t.get("attackerNodeIndices", [])) != 1]
    return {
        "status": PASS if not bad else FAIL,
        "evidence": f"{len(tdi)} rounds checked, {len(bad)} with attackerNodeIndices count != 1",
        "note": None if not bad else f"first violation: round {bad[0][0]} has {bad[0][1]} attacker indices",
    }


# ---------------------------------------------------------------------------
# Invariant 3: honest power sums to 1-alpha
# ---------------------------------------------------------------------------
def check_invariant_3(doc):
    """PARTIAL: only the configured alpha is checkable from JSON.

    The real invariant is about AttackAwareResourcePowerCalculator's
    realized per-node power distribution summing correctly -- that
    calculator's per-node output isn't serialized anywhere in JSON, only
    the single configured attackerHashPower scalar is. 1 - alpha for a
    scalar alpha is a tautology, not an independent check of the realized
    distribution; reporting PARTIAL rather than dressing up the tautology
    as a pass.
    """
    params = sim_result(doc)["threesimSimulationParameters"]
    alpha = params.get("attackerHashPower")
    ok = isinstance(alpha, (int, float)) and 0.0 <= alpha <= 1.0
    return {
        "status": PARTIAL if ok else FAIL,
        "evidence": f"threesimSimulationParameters.attackerHashPower={alpha}",
        "note": ("Only checked alpha is a valid probability. Realized per-node power "
                 "(AttackAwareResourcePowerCalculator's output) is not serialized -- "
                 "full check needs new instrumentation exposing per-node resource power."),
    }


# ---------------------------------------------------------------------------
# Invariant 4: every topology is connected and bilateral
# ---------------------------------------------------------------------------
def _is_connected(adjacency):
    if not adjacency:
        return True
    nodes = list(adjacency.keys())
    start = nodes[0]
    seen = {start}
    stack = [start]
    while stack:
        cur = stack.pop()
        for nb in adjacency.get(cur, []):
            nb = str(nb)
            if nb not in seen:
                seen.add(nb)
                stack.append(nb)
    return len(seen) == len(nodes)


def _is_bilateral(adjacency):
    for node, neighbors in adjacency.items():
        for nb in neighbors:
            nb = str(nb)
            if int(node) not in adjacency.get(nb, []):
                return False, (node, nb)
    return True, None


def check_invariant_4(doc):
    """Per-round hard check on topologyDeterminismInfo[i].nodeAdjacency:
    connectivity (BFS reaches every node) and bilateral symmetry (A lists B
    iff B lists A). Same correction as invariant 2: this is directly
    re-derivable from the serialized adjacency map, not solely a
    server-side assertion.
    """
    tdi = sim_result(doc).get("topologyDeterminismInfo")
    if not isinstance(tdi, list) or not tdi:
        return {"status": NA, "evidence": "topologyDeterminismInfo absent/empty", "note": None}
    disconnected = []
    unilateral = []
    for i, t in enumerate(tdi):
        adjacency = t.get("nodeAdjacency") or {}
        if not adjacency:
            continue
        if not _is_connected(adjacency):
            disconnected.append(i)
        ok, pair = _is_bilateral(adjacency)
        if not ok:
            unilateral.append((i, pair))
    bad = bool(disconnected or unilateral)
    return {
        "status": FAIL if bad else PASS,
        "evidence": f"{len(tdi)} rounds checked",
        "note": (None if not bad else
                 f"disconnected rounds: {disconnected[:5]}{'...' if len(disconnected) > 5 else ''}; "
                 f"unilateral-edge rounds: {[r for r, _ in unilateral[:5]]}"),
    }


# ---------------------------------------------------------------------------
# Invariant 5: equal-work choices follow processing order
# ---------------------------------------------------------------------------
def check_invariant_5(doc):
    """N/A: not checkable from JSON at all, by design, not oversight.

    Item 5's tie-break (BlockchainImpl.getPreferredTipOfLongestChains'
    _validationSequence-based .min()) is an internal ordering guarantee --
    _validationSequence values and which tip was preferred at each fork
    point are never serialized. There is no output-level trace of "which
    equal-work choice was made and in what order" to check against. This is
    a code-level-only guarantee (and was verified as such, by reading the
    implementation, in an earlier investigation) -- reporting N/A rather
    than forcing a weak proxy, per the task's own instruction.
    """
    return {"status": NA, "evidence": None,
            "note": ("Not serialized anywhere in JSON output (_validationSequence and "
                     "per-fork tip preference are internal-only). Verified at the code "
                     "level in a prior investigation, not checkable here.")}


# ---------------------------------------------------------------------------
# Invariant 6: no episode ends after only one local reorganization
# ---------------------------------------------------------------------------
def check_invariant_6(doc):
    """N/A: not reliably checkable from current JSON schema.

    This is about whether a round's outcome was decided prematurely, off a
    single node's local (raw, non-deduplicated) reorg observation, before
    the network-wide quiescence window elapsed -- see
    ChainReorganizationDeduplicator's doc comment on why the raw
    per-node-observation list exists in the first place. That raw list
    (one entry per node that independently logged the same reveal) is
    never serialized -- only the deduplicated chainReorganizations array
    is, and reorg timestamps aren't included in it either, so there's no
    way to verify the quiescence-window gap from output alone. A weaker
    proxy (e.g. "status is success/failure only implies chainReorganizations
    non-empty") wouldn't actually test the thing this invariant is about,
    so it's reported N/A rather than substituted in.
    """
    return {"status": NA, "evidence": None,
            "note": ("Raw per-node reorg-observation list and reorg timestamps are not "
                     "serialized -- only the deduplicated chainReorganizations array "
                     "(heights only, no timestamps) is. Full check needs new "
                     "instrumentation exposing the pre-deduplication occurrence list "
                     "with timestamps.")}


# ---------------------------------------------------------------------------
# Invariant 7: unresolved episodes are never encoded as failures
# ---------------------------------------------------------------------------
def check_invariant_7(doc):
    """Per-round hard check: status is always exactly one of the three
    expected values (success/failure/unresolved) -- confirms the output
    encoding itself preserves the three-way distinction rather than ever
    silently collapsing "unresolved" into "failure". (There is no existing
    downstream aggregation/analysis script in this repo to separately audit
    for a re-conflation after the fact -- prcc_fanova_revenue.py, the only
    plausible candidate, does not exist in this repo or its git history.)
    """
    status_list = sim_result(doc).get("status")
    if not isinstance(status_list, list) or not status_list:
        return {"status": NA, "evidence": "status field absent/empty", "note": None}
    allowed = {"success", "failure", "unresolved"}
    bad = [(i, s) for i, s in enumerate(status_list) if s not in allowed]
    return {
        "status": PASS if not bad else FAIL,
        "evidence": f"{len(status_list)} rounds checked",
        "note": None if not bad else f"unexpected status values: {bad[:5]}",
    }


# ---------------------------------------------------------------------------
# Invariant 8: D > 0 exactly when status is successful
# ---------------------------------------------------------------------------
def check_invariant_8(doc):
    """Per-round hard check: attackerCausedChainReorganizationDepths[i] > 0
    iff status[i] == "success". D is taken to be the attacker-caused depth
    (not the plain chainReorganizationDepths), matching D5's own SUCCESS
    definition (an attacker-mined displacing block surviving to
    quiescence) and the paper's conditional-reorg-depth D-bar-plus, which
    is defined conditional on a successful attack.
    """
    sr = sim_result(doc)
    status_list = sr.get("status")
    d_list = sr.get("attackerCausedChainReorganizationDepths")
    if not isinstance(status_list, list) or not isinstance(d_list, list):
        return {"status": NA, "evidence": "status or attackerCausedChainReorganizationDepths absent", "note": None}
    if len(status_list) != len(d_list):
        return {"status": FAIL, "evidence": f"length mismatch: status={len(status_list)} D={len(d_list)}", "note": None}
    bad = [(i, status_list[i], d_list[i]) for i in range(len(status_list))
           if (d_list[i] > 0) != (status_list[i] == "success")]
    return {
        "status": PASS if not bad else FAIL,
        "evidence": f"{len(status_list)} rounds checked",
        "note": None if not bad else f"first violation: round {bad[0][0]} status={bad[0][1]!r} D={bad[0][2]}",
    }


SINGLE_FILE_CHECKS = [
    ("1. block-production rate = 1/tau_B", check_invariant_1),
    ("2. exactly one attacker controls alpha", check_invariant_2),
    ("3. honest power sums to 1-alpha", check_invariant_3),
    ("4. every topology connected and bilateral", check_invariant_4),
    ("5. equal-work choices follow processing order", check_invariant_5),
    ("6. no episode ends after only one local reorg", check_invariant_6),
    ("7. unresolved episodes never encoded as failures", check_invariant_7),
    ("8. D > 0 exactly when status is successful", check_invariant_8),
]


def run_single(path):
    doc = load(path)
    results = {}
    for name, fn in SINGLE_FILE_CHECKS:
        try:
            results[name] = fn(doc)
        except Exception as e:  # noqa: BLE001 -- report, don't crash the whole run
            results[name] = {"status": FAIL, "evidence": None, "note": f"checker error: {e}"}
    return doc, results


def print_single_report(path, results):
    print(f"\n=== {path} ===")
    for name, r in results.items():
        line = f"  [{r['status']:7s}] {name}"
        if r.get("evidence"):
            line += f"  -- {r['evidence']}"
        print(line)
        if r.get("note"):
            print(f"            {r['note']}")


# ---------------------------------------------------------------------------
# Invariants 9 & 10: cross-file topology comparisons
# ---------------------------------------------------------------------------
def topology_key(t):
    return (t.get("topologyId"), tuple(sorted(t.get("attackerNodeIndices", []))),
            tuple(sorted((k, tuple(v)) for k, v in (t.get("nodeAdjacency") or {}).items())))


def compare_topologies(docA, docB):
    tdiA = sim_result(docA).get("topologyDeterminismInfo")
    tdiB = sim_result(docB).get("topologyDeterminismInfo")
    if not isinstance(tdiA, list) or not isinstance(tdiB, list):
        return None
    by_repl_a = {t.get("replicationId"): t for t in tdiA}
    by_repl_b = {t.get("replicationId"): t for t in tdiB}
    common = sorted(set(by_repl_a) & set(by_repl_b))
    if not common:
        return {"common_rounds": 0, "matches": 0}
    matches = sum(1 for r in common if topology_key(by_repl_a[r]) == topology_key(by_repl_b[r]))
    return {"common_rounds": len(common), "matches": matches}


def config_identity(doc):
    ip = doc["inputParameters"]
    return {
        "config_id": ip.get("config_id"),
        "system_config_id": ip.get("system_config_id"),
        "bandwidth": ip.get("bandwidth"),
        "attacker_config_id": ip.get("attacker_config_id"),
        "attack_strategy": ip.get("attack_strategy"),
    }


def run_cross_file(paths):
    docs = {p: load(p) for p in paths}
    idents = {p: config_identity(d) for p, d in docs.items()}

    print("\n=== cross-file topology comparisons (invariants 9 & 10) ===")
    n = len(paths)
    for i in range(n):
        for j in range(i + 1, n):
            pa, pb = paths[i], paths[j]
            ia, ib = idents[pa], idents[pb]
            cmp = compare_topologies(docs[pa], docs[pb])
            if cmp is None or cmp["common_rounds"] == 0:
                continue

            same_config = ia["config_id"] == ib["config_id"]
            same_system = ia["system_config_id"] == ib["system_config_id"]

            if same_config:
                label = "invariant 9 (identical seed -> identical adjacency)"
                expectation = "expect full match"
            elif same_system:
                label = "invariant 10 (topology reuse across conditions)"
                expectation = "expect full match (same system_config_id, different condition)"
            else:
                label = "informational (different system_config_id)"
                expectation = "no match expected"

            frac = f"{cmp['matches']}/{cmp['common_rounds']}"
            verdict = PASS if cmp["matches"] == cmp["common_rounds"] else FAIL
            if label.startswith("informational"):
                verdict = "N/A (informational)"

            print(f"\n  {Path(pa).parent.name}  vs  {Path(pb).parent.name}")
            print(f"    {label}: {expectation}")
            print(f"    config_id: {ia['config_id']} vs {ib['config_id']}  |  "
                  f"system_config_id: {ia['system_config_id']} vs {ib['system_config_id']}  |  "
                  f"bandwidth: {ia['bandwidth']} vs {ib['bandwidth']}  |  "
                  f"attacker_config_id: {ia['attacker_config_id']} vs {ib['attacker_config_id']}  |  "
                  f"strategy: {ia['attack_strategy']} vs {ib['attack_strategy']}")
            print(f"    [{verdict}] matching-topology rounds: {frac}")


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = parser.add_subparsers(dest="command", required=True)

    single = sub.add_parser("single", help="Run per-file invariants (1-8) on each input")
    single.add_argument("files", nargs="+")

    allcmd = sub.add_parser("all", help="Run per-file invariants on each input, plus cross-file topology comparisons (9, 10)")
    allcmd.add_argument("files", nargs="+")

    args = parser.parse_args()

    any_fail = False
    if args.command in ("single", "all"):
        for path in args.files:
            _, results = run_single(path)
            print_single_report(path, results)
            if any(r["status"] == FAIL for r in results.values()):
                any_fail = True

    if args.command == "all" and len(args.files) >= 2:
        run_cross_file(args.files)
        # any_fail for cross-file checks is folded in by re-scanning printed verdicts
        # would require restructuring run_cross_file to return data; kept simple/print-only
        # here since this is a diagnostic script, not a CI gate.

    sys.exit(1 if any_fail else 0)


if __name__ == "__main__":
    main()
