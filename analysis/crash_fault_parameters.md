# Crash-Fault-Related Parameters in 3SIM

**Caveat:** 3SIM does not simulate actual node crashes/recovery. Instead, it infers
"failures" indirectly from throughput degradation. There is no `Crash`/`NodeFailure`
behavior strategy class (unlike SelfishMining, StubbornMining, etc.), and the metamodel
(`bscm.ecore`) has no crash/fault attributes.

## Implemented Parameters

| Parameter | Where | Default | Role |
|---|---|---|---|
| `failureThroughputThreshold` | `ThreesimAttributes.java:7-8`, `configuration.json` | `1.0` (txn/min) | If throughput drops ≤ this, system is considered "in failure" |
| `reliabilityObservationTimespan` | `ThreesimAttributes.java:16-17`, `configuration.json` | `24.0` hours | Window over which `ReliabilityCalculator` evaluates survival probability |

## Detection Mechanism

In `ThreesimSimulationMonitor.java:240-244`, every confirmed block checks throughput
against `failureThroughputThreshold`:
- throughput ≤ threshold → `failureStarted()`
- throughput > threshold → `failureEnded()`

`BlockchainSystemFailureLog.java:19-40` logs these intervals and derives mean failure
duration (→ MTTR).

## Derived Metrics (fully wired, computed every round)

- **MTBF** = `simulationTime / numberOfFailureEvents`
  (`ThreesimSimulationMonitor.java:306-310`)
- **MTTR** = average failure-interval duration
  (`BlockchainSystemFailureLog.calculateMeanFailureDuration()`)
- **AvailabilitySecurity** = `MTBF / (MTBF + MTTR)`
  (`AvailabilitySecurityCalculator.java:12-26`)
- **Reliability** = `exp(-timespan / MTBF)`
  (`ReliabilityCalculator.java:12-26`)
- **FaultTolerance** = deltas in throughput/latency between normal vs. degraded periods
  (`FaultToleranceCalculator.java:12-36`)

## State Tracking

`ThreesimSimulationMonitorState.java:17-24` holds:
- `meanTimeBetweenFailures`, `meanTimeToRepair`
- `averageThroughputDuringFailure`, `averageThroughputDuringNormalOperation`
- `averageConfirmationLatencyDuringFailure`, `averageConfirmationLatencyDuringNormalOperation`

Populated in `ThreesimSimulationMonitor.getFinalState()`.

## UI Exposure

`ThreesimTab.java:50-55, 72-77` exposes `failureThroughputThresholdField` and
`reliabilityObservationTimespanField` as editable launch-configuration fields.

## Not Implemented

- No explicit node crash/recovery simulation
- No Byzantine fault tolerance
- No network partition handling
- No per-node downtime model

All current "crash-fault" metrics are observational, derived from throughput dips
rather than modeling actual faulty node behavior.
