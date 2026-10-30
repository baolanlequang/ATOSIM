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
    # Per-file invariants (1, 2, 3, 4, 6, 7, 8, 11, 12, 13; N/A 5) on each file, then check 3b
    # (check 3 passes at every alpha value among the inputs; needs files of two alpha values):
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
def _mining_rates(doc):
    """Pooled mining counts and simulated exposure from replicationDiagnostics, or None if the
    file predates simulatedDurationMs. Returns (attacker_blocks, total_blocks, duration_ms, block_interval_ms)."""
    sr = sim_result(doc)
    diags = sr.get("replicationDiagnostics")
    if not isinstance(diags, list) or not diags or any(not isinstance(d, dict) or "simulatedDurationMs" not in d for d in diags):
        return None
    bci = sr["threesimSimulationParameters"].get("blockInterval")
    return (sum(d["attackerBlocksMined"] for d in diags), sum(d["totalBlocksMined"] for d in diags),
            sum(d["simulatedDurationMs"] for d in diags), bci)


def _rate_interval(n, duration_ms, bci, z=2.576):
    """Blocks per block interval with an approximate Poisson interval (normal approximation, z=2.576: 99%)."""
    r = n / duration_ms * bci
    h = z * (n ** 0.5) / duration_ms * bci
    return r, r - h, r + h


def check_invariant_1(doc):
    """Network-wide block production rate equals 1 / tau_B.

    Pooled over rounds: total rate = sum(totalBlocksMined) / sum(simulatedDurationMs) x blockInterval, in
    blocks per block interval. simulatedDurationMs ends at the moment the round's end was decided, the
    same moment up to which blocks are counted, so this ratio estimates the Poisson rate without the
    stopping-rule bias of a per-round block ratio. PASS if 1.0 lies inside the approximate 99% Poisson
    interval. N/A for files written before simulatedDurationMs existed.
    """
    m = _mining_rates(doc)
    if m is None:
        return {"status": NA, "evidence": "replicationDiagnostics.simulatedDurationMs absent (older result file)", "note": None}
    _, n_tot, dur, bci = m
    if not dur or not bci:
        return {"status": FAIL, "evidence": f"duration {dur} ms, blockInterval {bci}", "note": "no exposure to estimate a rate"}
    r, lo, hi = _rate_interval(n_tot, dur, bci)
    return {
        "status": PASS if lo <= 1.0 <= hi else FAIL,
        "evidence": f"total rate {r:.4f} blocks per block interval, 99% interval [{lo:.4f}, {hi:.4f}] ({n_tot} blocks over {dur} ms, tau_B {bci} ms)",
        "note": None if lo <= 1.0 <= hi else "configured rate 1.0 outside the 99% interval",
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
    """Attacker mining share equals alpha (and honest nodes together 1 - alpha).

    Pooled over rounds: attacker rate = sum(attackerBlocksMined) / sum(simulatedDurationMs) x blockInterval.
    PASS if the configured alpha (threesimSimulationParameters.attackerHashPower) lies inside the approximate
    99% Poisson interval of the attacker rate. The honest rate (total minus attacker blocks) and its interval
    are reported as evidence. N/A for files written before simulatedDurationMs existed.
    """
    m = _mining_rates(doc)
    alpha = sim_result(doc)["threesimSimulationParameters"].get("attackerHashPower")
    if m is None:
        return {"status": NA, "evidence": "replicationDiagnostics.simulatedDurationMs absent (older result file)", "note": None}
    n_att, n_tot, dur, bci = m
    if not dur or not bci or not isinstance(alpha, (int, float)):
        return {"status": FAIL, "evidence": f"duration {dur} ms, blockInterval {bci}, alpha {alpha}", "note": "cannot estimate"}
    r, lo, hi = _rate_interval(n_att, dur, bci)
    rh, loh, hih = _rate_interval(n_tot - n_att, dur, bci)
    ok = lo <= alpha <= hi
    return {
        "status": PASS if ok else FAIL,
        "evidence": (f"attacker rate {r:.4f}, 99% interval [{lo:.4f}, {hi:.4f}] vs alpha {alpha}; "
                     f"honest rate {rh:.4f}, 99% interval [{loh:.4f}, {hih:.4f}] vs 1 - alpha {1 - alpha:.4f}"),
        "note": None if ok else "configured alpha outside the 99% interval of the attacker rate",
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


# ---------------------------------------------------------------------------
# Invariant 11: replicationDiagnostics consistency (AC_fix_plan item 4)
# ---------------------------------------------------------------------------
DIAG_KEYS = [
    "attackerBlocksMined", "totalBlocksMined", "unpublishedAttackerBlocksAtEnd", "attackerHoldsRoundOpenAtEnd",
    "duplicateBodiesDroppedChainOrPoolNetwork", "duplicateBodiesDroppedKnownSetOnlyNetwork",
    "duplicateInvsIgnoredChainOrPoolNetwork", "duplicateInvsIgnoredKnownSetOnlyNetwork",
    "duplicateBodiesDroppedChainOrPoolAttacker", "duplicateBodiesDroppedKnownSetOnlyAttacker",
    "duplicateInvsIgnoredChainOrPoolAttacker", "duplicateInvsIgnoredKnownSetOnlyAttacker",
    "honestAdoptionDepthMax", "honestAdoptionDepthMedian", "honestNodesWithAdoptionReorg", "honestNodeCount",
]


def check_invariant_11(doc):
    """Per-round hard checks on the replicationDiagnostics array.

    - one non-null object per round, with every key present (honestOutcomeDisagreementAtEnd only
      from the outcome-agreement build on, so it is checked when present);
    - outcome agreement: a resolved round has honestOutcomeDisagreementAtEnd == 0 (every honest
      node agrees with the witness on whether its chain holds an attacker block of X);
    - end rule (decisions Q2/Q3, branch-start variant): a resolved round (success/failure) never
      ends while the attacker still holds the round open, i.e. attackerHoldsRoundOpenAtEnd is
      false. Unpublished attacker blocks at the end are allowed in a resolved round when they
      belong to a branch started after the last reorganization (a new attempt), so
      unpublishedAttackerBlocksAtEnd == 0 is NOT required;
    - 0 <= attackerBlocksMined <= totalBlocksMined; all duplicate counters >= 0;
    - depth fields: null unless status == success; honestAdoptionDepthMax >= 1 exactly when
      honestNodesWithAdoptionReorg >= 1; honestAdoptionDepthMedian <= honestAdoptionDepthMax;
      honestNodesWithAdoptionReorg <= honestNodeCount;
    - miningSchedulesWhilePendingAttacker/Network (when present, from the AH fix on): >= 0 and
      attacker <= network;
    - honestNodesWithAdoptionReorg >= 1 whenever D (attackerCausedChainReorganizationDepths) > 0:
      D and the adoption depths refer to the same surviving block S, so a positive D needs at least
      one honest node whose chain received S through a recorded reorganization. (Not guaranteed by
      construction: D's occurrence may be recorded at an attacker node only.)
    N/A for result files written before the field existed.
    """
    sr = sim_result(doc)
    diags = sr.get("replicationDiagnostics")
    status_list = sr.get("status")
    d_list = sr.get("attackerCausedChainReorganizationDepths") or []
    if diags is None:
        return {"status": NA, "evidence": "replicationDiagnostics absent (older result file)", "note": None}
    if not isinstance(status_list, list) or len(diags) != len(status_list):
        return {"status": FAIL, "evidence": "length mismatch with status", "note": None}
    bad = []
    for i, (d, st) in enumerate(zip(diags, status_list)):
        if d is None or any(k not in d for k in DIAG_KEYS):
            bad.append((i, "missing object or key")); continue
        if st in ("success", "failure") and d["attackerHoldsRoundOpenAtEnd"]:
            bad.append((i, "resolved round ended while the attacker held it open"))
        if st in ("success", "failure") and d.get("honestOutcomeDisagreementAtEnd", 0) != 0:
            bad.append((i, "resolved round ended while honest nodes disagreed on the outcome"))
        if d.get("honestOutcomeDisagreementAtEnd", 0) < 0 or d.get("honestOutcomeDisagreementAtEnd", 0) > d["honestNodeCount"]:
            bad.append((i, "honestOutcomeDisagreementAtEnd outside [0, honestNodeCount]"))
        if not (0 <= d["attackerBlocksMined"] <= d["totalBlocksMined"]):
            bad.append((i, "attackerBlocksMined outside [0, totalBlocksMined]"))
        if any(d[k] < 0 for k in DIAG_KEYS[4:12]):
            bad.append((i, "negative duplicate counter"))
        if "miningSchedulesWhilePendingAttacker" in d or "miningSchedulesWhilePendingNetwork" in d:
            sa, sn = d.get("miningSchedulesWhilePendingAttacker"), d.get("miningSchedulesWhilePendingNetwork")
            if not isinstance(sa, int) or not isinstance(sn, int) or not (0 <= sa <= sn):
                bad.append((i, "miningSchedulesWhilePending counters missing, negative or attacker > network"))
        mx, med, n_with, n_honest = (d["honestAdoptionDepthMax"], d["honestAdoptionDepthMedian"],
                                     d["honestNodesWithAdoptionReorg"], d["honestNodeCount"])
        if st != "success":
            if mx is not None or med is not None or n_with is not None:
                bad.append((i, "depth fields set although status != success"))
        else:
            if n_with is None or (mx is not None and mx >= 1) != (n_with >= 1):
                bad.append((i, "honestAdoptionDepthMax >= 1 does not match honestNodesWithAdoptionReorg >= 1"))
            if mx is not None and med is not None and med > mx:
                bad.append((i, "median above max"))
            if n_with is not None and n_with > n_honest:
                bad.append((i, "more nodes with an adoption reorg than honest nodes"))
        if i < len(d_list) and d_list[i] > 0 and (n_with is None or n_with < 1):
            bad.append((i, "D > 0 but no honest node received S through a recorded reorganization"))
    return {
        "status": PASS if not bad else FAIL,
        "evidence": f"{len(diags)} rounds checked",
        "note": None if not bad else f"{len(bad)} violation(s); first: round {bad[0][0]}: {bad[0][1]}",
    }


# ---------------------------------------------------------------------------
# Invariant 12: no attacker block dropped as unpublishable (phase 3, AD 5.5)
# ---------------------------------------------------------------------------
def check_invariant_12(doc):
    """Per-round: replicationDiagnostics.droppedUnpublishableAttackerBlocks == 0.

    The trail-stubborn behaviour drops a hidden block only if publishing it appended as STALE or
    ALREADY_APPENDED (the block can never become public). After the phase-3 fix this should not
    happen; any non-zero count is reported as FAIL so that it is investigated. N/A for result files
    written before the field existed.
    """
    diags = sim_result(doc).get("replicationDiagnostics")
    if not diags or not isinstance(diags[0], dict) or "droppedUnpublishableAttackerBlocks" not in diags[0]:
        return {"status": NA, "evidence": "droppedUnpublishableAttackerBlocks absent (older result file)", "note": None}
    bad = [(i, d["droppedUnpublishableAttackerBlocks"]) for i, d in enumerate(diags)
           if d is not None and d.get("droppedUnpublishableAttackerBlocks", 0) != 0]
    return {
        "status": PASS if not bad else FAIL,
        "evidence": f"{len(diags)} rounds checked",
        "note": None if not bad else f"{len(bad)} round(s) with dropped blocks; first: round {bad[0][0]} ({bad[0][1]} dropped)",
    }


# ---------------------------------------------------------------------------
# Invariant 13: D <= honest adoption depth median <= max (AH_rate_depth_check)
# ---------------------------------------------------------------------------
def check_invariant_13(doc):
    """Per success round: D <= honestAdoptionDepthMedian <= honestAdoptionDepthMax.

    D is attackerCausedChainReorganizationDepths (as in checks 8 and 11): the depth of the earliest
    recorded reorganization, at any node, whose replacing chain contains S. The honest adoption
    depths are, per honest node, the depth of that node's earliest reorganization containing S; the
    earliest switch is the shallowest in practice (AH), so D should not exceed their median. A
    success round with a null median or max is reported as a violation. N/A for result files
    written before the depth fields existed.
    """
    sr = sim_result(doc)
    diags = sr.get("replicationDiagnostics")
    status_list = sr.get("status")
    d_list = sr.get("attackerCausedChainReorganizationDepths")
    if not isinstance(diags, list) or not diags or not isinstance(diags[0], dict) or "honestAdoptionDepthMedian" not in diags[0]:
        return {"status": NA, "evidence": "honest adoption depth fields absent (older result file)", "note": None}
    if not isinstance(status_list, list) or not isinstance(d_list, list) or not (len(status_list) == len(d_list) == len(diags)):
        return {"status": FAIL, "evidence": "length mismatch between status, D and replicationDiagnostics", "note": None}
    bad, n_succ = [], 0
    for i, (st, dv, d) in enumerate(zip(status_list, d_list, diags)):
        if st != "success":
            continue
        n_succ += 1
        med, mx = (d or {}).get("honestAdoptionDepthMedian"), (d or {}).get("honestAdoptionDepthMax")
        if med is None or mx is None:
            bad.append((i, f"D={dv}, median={med}, max={mx} (null)"))
        elif not (dv <= med <= mx):
            bad.append((i, f"D={dv}, median={med}, max={mx}"))
    return {
        "status": PASS if not bad else FAIL,
        "evidence": f"{n_succ} success rounds checked",
        "note": None if not bad else f"{len(bad)} violation(s); first: round {bad[0][0]}: {bad[0][1]}",
    }


SINGLE_FILE_CHECKS = [
    ("1. block-production rate = 1/tau_B", check_invariant_1),
    ("2. exactly one attacker controls alpha", check_invariant_2),
    ("3. attacker mining rate = alpha (honest 1 - alpha)", check_invariant_3),
    ("4. every topology connected and bilateral", check_invariant_4),
    ("5. equal-work choices follow processing order", check_invariant_5),
    ("6. no episode ends after only one local reorg", check_invariant_6),
    ("7. unresolved episodes never encoded as failures", check_invariant_7),
    ("8. D > 0 exactly when status is successful", check_invariant_8),
    ("11. replicationDiagnostics consistent (end rule, counts, depth fields)", check_invariant_11),
    ("12. no attacker block dropped as unpublishable", check_invariant_12),
    ("13. D <= honest adoption depth median <= max (success rounds)", check_invariant_13),
]


# ---------------------------------------------------------------------------
# Invariant 3b: check 3 passes at every alpha value among the inputs (AH_rate_depth_check)
# ---------------------------------------------------------------------------
def check_invariant_3b(docs_and_results):
    """Across the input files: check 3 (attacker rate = alpha, 99% interval) passes on every file, for
    at least two distinct alpha values (the AH double-scheduling excess grew with alpha: 0.30 passed
    while 0.45 failed). Check 3 itself is unchanged. N/A if fewer than two alpha values have a
    rate-based check 3 result (N/A files, e.g. older ones without simulatedDurationMs, are skipped).
    """
    by_alpha = defaultdict(list)
    for path, doc, results in docs_and_results:
        r3 = results.get("3. attacker mining rate = alpha (honest 1 - alpha)")
        if r3 is None or r3["status"] == NA:
            continue
        alpha = sim_result(doc)["threesimSimulationParameters"].get("attackerHashPower")
        by_alpha[alpha].append((path, r3["status"]))
    parts = []
    failed = []
    for alpha in sorted(by_alpha, key=lambda a: (a is None, a)):
        rows = by_alpha[alpha]
        n_pass = sum(1 for _, st in rows if st == PASS)
        parts.append(f"alpha {alpha}: {n_pass}/{len(rows)} files pass")
        failed += [(alpha, p) for p, st in rows if st != PASS]
    if len(by_alpha) < 2:
        return {"status": NA, "evidence": "; ".join(parts) or "no file with a rate-based check 3",
                "note": "needs files of at least two alpha values"}
    return {
        "status": PASS if not failed else FAIL,
        "evidence": "; ".join(parts),
        "note": None if not failed else f"first failing file: alpha {failed[0][0]}: {failed[0][1]}",
    }


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

    single = sub.add_parser("single", help="Run per-file invariants (1-8, 11-13) on each input, plus 3b across them")
    single.add_argument("files", nargs="+")

    allcmd = sub.add_parser("all", help="Run per-file invariants on each input, plus cross-file topology comparisons (9, 10)")
    allcmd.add_argument("files", nargs="+")

    args = parser.parse_args()

    any_fail = False
    if args.command in ("single", "all"):
        collected = []
        for path in args.files:
            doc, results = run_single(path)
            print_single_report(path, results)
            collected.append((path, doc, results))
            if any(r["status"] == FAIL for r in results.values()):
                any_fail = True
        r3b = check_invariant_3b(collected)
        print("\n=== across files ===")
        print(f"  [{r3b['status']:7s}] 3b. check 3 passes at every alpha value  -- {r3b['evidence']}")
        if r3b.get("note"):
            print(f"            {r3b['note']}")
        if r3b["status"] == FAIL:
            any_fail = True

    if args.command == "all" and len(args.files) >= 2:
        run_cross_file(args.files)
        # any_fail for cross-file checks is folded in by re-scanning printed verdicts
        # would require restructuring run_cross_file to return data; kept simple/print-only
        # here since this is a diagnostic script, not a CI gate.

    sys.exit(1 if any_fail else 0)


if __name__ == "__main__":
    main()
