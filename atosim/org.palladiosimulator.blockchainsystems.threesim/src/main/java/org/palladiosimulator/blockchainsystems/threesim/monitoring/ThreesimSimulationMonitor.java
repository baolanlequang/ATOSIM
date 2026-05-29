package org.palladiosimulator.blockchainsystems.threesim.monitoring;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockType;
import org.palladiosimulator.blockchainsystems.core.blockchain.BlockAppendedTraceEvent;
import org.palladiosimulator.blockchainsystems.core.blockchain.BlockTypeChangedTraceEvent;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogOrigin;
import org.palladiosimulator.blockchainsystems.core.geography.GeographicalRegions;
import org.palladiosimulator.blockchainsystems.core.mining.BlockMinedTraceEvent;
import org.palladiosimulator.blockchainsystems.core.monitoring.abstractions.SimulationMonitor;
import org.palladiosimulator.blockchainsystems.core.simulation.termination.LongestChainExceededMaxLengthCondition;
import org.palladiosimulator.blockchainsystems.core.transaction.TransactionSubmittedTraceEvent;
import org.palladiosimulator.blockchainsystems.core.utils.CounterMap;
import org.palladiosimulator.blockchainsystems.threesim.behavior.BlockUtils;
import org.palladiosimulator.blockchainsystems.threesim.metrics.calculators.TransactionThroughputCalculator;
import org.palladiosimulator.blockchainsystems.threesim.simulation.AttackType;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;
import org.palladiosimulator.blockchainsystems.threesim.utils.BlockchainSystemFailureLog;
import org.palladiosimulator.blockchainsystems.threesim.utils.BlocksMap;
import org.palladiosimulator.blockchainsystems.core.system.BlockchainSystem;
import org.palladiosimulator.blockchainsystems.core.system.BlockchainSystemNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ThreesimSimulationMonitor implements SimulationMonitor {

    private final LongestChainExceededMaxLengthCondition _maxBlockchainLengthCondition;
    private final double _failureThroughputThreshold;
    private final ThreesimSimulationParameters _simulationParameters;

    private Set<BlockchainSystemNode> _nodes;
    private GeographicalRegions _geographicalRegions;
    private int _numberOfSubmittedTransactions = 0;
    private Double _blockReward;

    private boolean _finneyAttackSucceeded = false;
    private Long _finneyCandidateMinedTime = null;
    private String _finneyCandidateBlockHash = null;
    private boolean _raceAttackSucceeded = false;

    private final Map<String, Set<String>> _confirmedByPrevHash = new HashMap<>();
    private final Map<String, Set<String>> _staleByPrevHash = new HashMap<>();
    private final Map<String, Set<String>> _confirmedChildrenByParentHash = new HashMap<>();

    private String _raceWinningPrevHash = null;
    private String _raceWinningAttackerBlockHash = null;
    private Long _raceProvisionalSuccessTime = null;

    private CounterMap<String> _blocksProposedPerNode;
    private BlocksMap _includedBlocks;
    private BlocksMap _confirmedBlocks;
    private BlocksMap _staleBlocks;
    private BlocksMap _forkedBlocks;

    private final BlockchainSystemFailureLog _failureLog = new BlockchainSystemFailureLog();
    private final BlockRewardMonitor _blockRewardMonitor = new BlockRewardMonitor();

    private final List<Double> _throughputsDuringFailure = new ArrayList<>();
    private final List<Long> _confirmationLatenciesDuringFailure = new ArrayList<>();
    private final List<Double> _throughputsWithoutFailure = new ArrayList<>();
    private final List<Long> _confirmationLatenciesWithoutFailure = new ArrayList<>();

    private long _lastThroughputCheckTimestamp = 0;
    private Long _attackSuccessTime = null;

    private Integer _lastTermDebugConfirmedBlocks = null;
    private Boolean _lastTermDebugRaceSucceeded = null;
    private Boolean _lastTermDebugReachedDepth = null;

    private final Set<String> _throughputAccountedHashes = new HashSet<>();
    private final Set<String> _rewardAccountedHashes = new HashSet<>();

    public ThreesimSimulationMonitor(
            LongestChainExceededMaxLengthCondition maxBlockchainLengthCondition,
            double failureThroughputThreshold,
            ThreesimSimulationParameters simulationParameters) {
        _maxBlockchainLengthCondition = maxBlockchainLengthCondition;
        _failureThroughputThreshold = failureThroughputThreshold;
        _simulationParameters = simulationParameters;
    }

    @Override
    public void initialize(BlockchainSystem blockchainSystem) {
        _nodes = blockchainSystem.getNodes();
        _geographicalRegions = blockchainSystem.getGeographicalRegions();
        _blockReward = blockchainSystem.getBlockReward();

        _blocksProposedPerNode = CounterMap.create(
                _nodes.stream().map(BlockchainSystemNode::getId).collect(Collectors.toList()));

        int majorityThreshold = (_nodes.size() / 2) + 1;
        _includedBlocks = new BlocksMap(majorityThreshold);
        _confirmedBlocks = new BlocksMap(majorityThreshold);
        _staleBlocks = new BlocksMap(majorityThreshold);
        _forkedBlocks = new BlocksMap(majorityThreshold);
    }

    public ThreesimSimulationMonitorState getFinalState(long finalSystemTime) {
        return new ThreesimSimulationMonitorState(
                _nodes.size(),
                _nodes.stream().map(BlockchainSystemNode::getResourcePower).collect(Collectors.toList()),
                _blocksProposedPerNode.getValues(),
                _geographicalRegions.getNumberOfRegions(),
                calculateNumberOfNodesPerRegion(),
                _numberOfSubmittedTransactions,
                calculateNumberOfConfirmedTransactions(),
                calculateTokensHeldPerNode(),
                calculateBlockProposalAndConfirmationTimes(),
                calculateMeanTimeBetweenFailures(finalSystemTime),
                _failureLog.calculateMeanFailureDuration(),
                _staleBlocks.getNumberOfValidBlocks(),
                _confirmedBlocks.getNumberOfValidBlocks(),
                calculateAverageThroughputDuringFailure(),
                calculateAverageThroughputWithoutFailure(),
                calculateAverageConfirmationLatencyDuringFailure(),
                calculateAverageConfirmationLatencyWithoutFailure()
        );
    }

    private List<long[]> calculateBlockProposalAndConfirmationTimes() {
        return _confirmedBlocks.getValidBlocks().stream()
                .map(e -> new long[]{e.getKey().getBlockMinedTimestamp(), e.getValue()})
                .collect(Collectors.toList());
    }

    @Override
    public void onTraceEventOccurred(TraceEvent event, TraceEventLogOrigin logOrigin) {
        if (BlockMinedTraceEvent.EVENT_TYPE.equals(event.getEventType())) {
            BlockMinedTraceEvent e = (BlockMinedTraceEvent) event;
            Block block = e.getBlock();
            if (_finneyCandidateBlockHash == null && isAttacker(block.getOriginId())) {
                _finneyCandidateBlockHash = block.getHash();
                _finneyCandidateMinedTime = e.getOccurrenceTime();
            }
            if (BlockUtils.isBlockForked(block)) {
                _forkedBlocks.addNodeToBlock(block, logOrigin.getId(), e.getOccurrenceTime());
            }
            _blocksProposedPerNode.increment(logOrigin.getId());

        } else if (BlockAppendedTraceEvent.EVENT_TYPE.equals(event.getEventType())) {
            BlockAppendedTraceEvent e = (BlockAppendedTraceEvent) event;
            addBlock(e.getAppendedBlockType(), e.getAppendedBlock(), logOrigin.getId(), e.getOccurrenceTime());
            if (e.getAppendedBlockType() == BlockType.ConfirmedBlock || e.getAppendedBlockType() == BlockType.StaleBlock) {
                updateRaceOutcomeIfRelevant(e.getAppendedBlock(), e.getAppendedBlockType(), e.getOccurrenceTime());
            }
            _maxBlockchainLengthCondition.onBlockAppended(e.getBlockPosition());
            if (e.getAppendedBlockType() == BlockType.ConfirmedBlock && _throughputAccountedHashes.add(e.getAppendedBlock().getHash())) {
                monitorThroughput(e.getAppendedBlock(), e.getOccurrenceTime());
            }
            if (e.getAppendedBlockType() == BlockType.ConfirmedBlock && _rewardAccountedHashes.add(e.getAppendedBlock().getHash())) {
                _blockRewardMonitor.recordBlockReward(e.getAppendedBlock());
            }
            if (e.getAppendedBlockType() == BlockType.ConfirmedBlock
                    && _simulationParameters.getAttackType() == AttackType.FINNEY
                    && e.getAppendedBlock().getHash().equals(_finneyCandidateBlockHash)
                    && _finneyCandidateMinedTime != null
                    && e.getOccurrenceTime() > _finneyCandidateMinedTime) {
                _finneyAttackSucceeded = true;
            }

        } else if (BlockTypeChangedTraceEvent.EVENT_TYPE.equals(event.getEventType())) {
            BlockTypeChangedTraceEvent e = (BlockTypeChangedTraceEvent) event;
            removeBlock(e.getOldBlockType(), e.getBlock().getHash(), logOrigin.getId());
            addBlock(e.getNewBlockType(), e.getBlock(), logOrigin.getId(), e.getOccurrenceTime());
            if (e.getNewBlockType() == BlockType.ConfirmedBlock || e.getNewBlockType() == BlockType.StaleBlock) {
                updateRaceOutcomeIfRelevant(e.getBlock(), e.getNewBlockType(), e.getOccurrenceTime());
            }
            if (e.getNewBlockType() == BlockType.ConfirmedBlock && _throughputAccountedHashes.add(e.getBlock().getHash())) {
                monitorThroughput(e.getBlock(), e.getOccurrenceTime());
            }
            if (e.getNewBlockType() == BlockType.ConfirmedBlock && _rewardAccountedHashes.add(e.getBlock().getHash())) {
                _blockRewardMonitor.recordBlockReward(e.getBlock());
            }
            if (e.getNewBlockType() == BlockType.ConfirmedBlock
                    && _simulationParameters.getAttackType() == AttackType.FINNEY
                    && e.getBlock().getHash().equals(_finneyCandidateBlockHash)
                    && _finneyCandidateMinedTime != null
                    && e.getOccurrenceTime() > _finneyCandidateMinedTime) {
                _finneyAttackSucceeded = true;
            }

        } else if (TransactionSubmittedTraceEvent.EVENT_TYPE.equals(event.getEventType())) {
            _numberOfSubmittedTransactions++;
        }
    }

    @Override
    public boolean shouldTerminate() {
        boolean maxExceeded = _maxBlockchainLengthCondition.hasLengthExceeded(); // computed but not used (faithful migration)
        
//        System.out.println("maxExceeded: " + maxExceeded);

        if (_simulationParameters.getAttackType() == AttackType.RACE) {
            int confirmedBlocks = _confirmedBlocks.getNumberOfValidBlocks();
            boolean reachedDepth = confirmedBlocks >= _simulationParameters.getConfirmationDepth();

            if (!Integer.valueOf(confirmedBlocks).equals(_lastTermDebugConfirmedBlocks)
                    || !Boolean.valueOf(_raceAttackSucceeded).equals(_lastTermDebugRaceSucceeded)
                    || !Boolean.valueOf(reachedDepth).equals(_lastTermDebugReachedDepth)) {
                _lastTermDebugConfirmedBlocks = confirmedBlocks;
                _lastTermDebugRaceSucceeded = _raceAttackSucceeded;
                _lastTermDebugReachedDepth = reachedDepth;
            }

            if (reachedDepth) return true;
        }

        return maxExceeded;
    }

    private void addBlock(BlockType type, Block block, String nodeId, long time) {
        getMap(type).addNodeToBlock(block, nodeId, time);
    }

    private void removeBlock(BlockType type, String hash, String nodeId) {
        getMap(type).removeNodeFromBlock(hash, nodeId);
    }

    private BlocksMap getMap(BlockType type) {
        return switch (type) {
            case IncludedBlock -> _includedBlocks;
            case ConfirmedBlock -> _confirmedBlocks;
            case StaleBlock -> _staleBlocks;
            case ForkingBlock -> _forkedBlocks;
        };
    }

    private void monitorThroughput(Block confirmedBlock, long occurrenceTime) {
        if (_lastThroughputCheckTimestamp == 0L) { _lastThroughputCheckTimestamp = occurrenceTime; return; }
        long observationTime = occurrenceTime - _lastThroughputCheckTimestamp;
        double throughput = new TransactionThroughputCalculator(confirmedBlock.getTransactions().size(), observationTime)
                .calculate().getValue();
        long confirmationLatency = occurrenceTime - confirmedBlock.getBlockMinedTimestamp();

        if (_failureLog.isFailureOngoing()) {
            if (throughput > _failureThroughputThreshold) _failureLog.failureEnded(occurrenceTime);
        } else {
            if (throughput <= _failureThroughputThreshold) _failureLog.failureStarted(occurrenceTime);
        }
        if (_failureLog.isFailureOngoing()) {
            _throughputsDuringFailure.add(throughput);
            _confirmationLatenciesDuringFailure.add(confirmationLatency);
        } else {
            _throughputsWithoutFailure.add(throughput);
            _confirmationLatenciesWithoutFailure.add(confirmationLatency);
        }
        _lastThroughputCheckTimestamp = occurrenceTime;
    }

    private void updateRaceOutcomeIfRelevant(Block block, BlockType newType, long occurrenceTime) {
        if (_raceAttackSucceeded || _simulationParameters.getAttackType() != AttackType.RACE) return;
        String hash = block.getHash(), prev = block.getPreviousHash();
        boolean attacker = isAttacker(block.getOriginId());

        if (newType == BlockType.ConfirmedBlock) {
            if (prev != null) addTo(_confirmedChildrenByParentHash, prev, hash);
            if (attacker && prev != null) addTo(_confirmedByPrevHash, prev, hash);
        } else if (newType == BlockType.StaleBlock) {
            if (!attacker && prev != null) addTo(_staleByPrevHash, prev, hash);
        }

        if (_raceWinningAttackerBlockHash == null && prev != null) {
            Set<String> ac = _confirmedByPrevHash.getOrDefault(prev, Set.of());
            Set<String> hs = _staleByPrevHash.getOrDefault(prev, Set.of());
            String winner = ac.stream().filter(ah -> hs.stream().anyMatch(hh -> !hh.equals(ah))).findFirst().orElse(null);
            if (winner != null) {
                _raceWinningPrevHash = prev;
                _raceWinningAttackerBlockHash = winner;
                _raceProvisionalSuccessTime = occurrenceTime;
            }
        }

        String winningHash = _raceWinningAttackerBlockHash;
        if (winningHash == null) return;
        if (countConfirmedDepthFrom(winningHash) >= _simulationParameters.getConfirmationDepth()) {
            _raceAttackSucceeded = true;
            if (_attackSuccessTime == null) _attackSuccessTime = _raceProvisionalSuccessTime != null ? _raceProvisionalSuccessTime : occurrenceTime;
        }
    }

    private int countConfirmedDepthFrom(String rootHash) {
        int depth = 1;
        String current = rootHash;
        while (true) {
            Set<String> children = _confirmedChildrenByParentHash.getOrDefault(current, Set.of());
            if (children.isEmpty()) break;
            current = children.iterator().next();
            depth++;
        }
        return depth;
    }

    private boolean isAttacker(String originId) {
        return originId != null && _simulationParameters.getAttackerNodeIds().contains(originId);
    }

    private void addTo(Map<String, Set<String>> map, String key, String value) {
        map.computeIfAbsent(key, k -> new HashSet<>()).add(value);
    }

    private double calculateMeanTimeBetweenFailures(long observationTime) {
        int n = _failureLog.getNumberOfFailures();
        if (n <= 0) return Double.POSITIVE_INFINITY;
        return (double) observationTime / n;
    }

    private int calculateNumberOfConfirmedTransactions() {
        return _confirmedBlocks.getValidBlocks().stream()
                .mapToInt(e -> e.getKey().getTransactions().size()).sum();
    }

    private List<Integer> calculateNumberOfNodesPerRegion() {
        Map<String, Integer> counts = new HashMap<>();
        for (BlockchainSystemNode node : _nodes) {
            String region = node.getGeographicalRegion().getRegion();
            counts.merge(region, 1, Integer::sum);
        }
        return new ArrayList<>(counts.values());
    }

    private List<Double> calculateTokensHeldPerNode() {
        if (_blockReward == null) throw new IllegalStateException("Block reward is not set");
        Map<String, List<Map.Entry<Block, Long>>> byOrigin = _confirmedBlocks.getValidBlocks().stream()
                .filter(e -> e.getKey().getOriginId() != null)
                .collect(Collectors.groupingBy(e -> e.getKey().getOriginId()));
        return _nodes.stream().map(node -> {
            List<Map.Entry<Block, Long>> blocks = byOrigin.get(node.getId());
            if (blocks == null) return 0.0;
            return blocks.stream().mapToDouble(e -> _blockReward + e.getKey().getTransactions().stream().mapToDouble(t -> t.getFee()).sum()).sum();
        }).collect(Collectors.toList());
    }

    private double avg(List<? extends Number> list) {
        if (list.isEmpty()) return -1.0;
        return list.stream().mapToDouble(Number::doubleValue).average().orElse(-1.0);
    }

    private double calculateAverageThroughputDuringFailure() { return avg(_throughputsDuringFailure); }
    private double calculateAverageConfirmationLatencyDuringFailure() { return avg(new ArrayList<>(_confirmationLatenciesDuringFailure)); }
    private double calculateAverageThroughputWithoutFailure() { return avg(_throughputsWithoutFailure); }
    private double calculateAverageConfirmationLatencyWithoutFailure() { return avg(new ArrayList<>(_confirmationLatenciesWithoutFailure)); }

    public void recordBlockReward(Block block) { _blockRewardMonitor.recordBlockReward(block); }
    public int getTotalBlockRewards() { return _blockRewardMonitor.getTotalRewards(); }
    public int getBlockRewardsForNode(String nodeId) { return _blockRewardMonitor.getRewardsForNode(nodeId); }
    public boolean hasFinneyAttackSucceeded() { return _finneyAttackSucceeded; }
    public boolean hasRaceAttackSucceeded() { return _raceAttackSucceeded; }
    public Long getAttackSuccessTime() { return _attackSuccessTime; }
}
