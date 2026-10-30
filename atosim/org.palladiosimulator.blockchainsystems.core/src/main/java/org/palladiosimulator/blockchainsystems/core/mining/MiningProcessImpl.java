package org.palladiosimulator.blockchainsystems.core.mining;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.stochastics.PoissonProcess;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.MiningProcess;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

public class MiningProcessImpl extends BlockchainNodeObject implements MiningProcess {

    private final PoissonProcess _poissonProcess;

    private BiFunction<Long, String, Block> _onCreatingBlockCallback;
    private Supplier<String> _previousBlockSelectionCallback;
    private Consumer<Block> _onBlockMinedCallback;

    private boolean _isMining = false;

    // At most one pending BlockMinedEvent per mining process (AH_rate_depth_check): every schedule
    // cancels the pending event first, so a reschedule from inside the onBlockMined callback (a
    // publish -> restartMining()) followed by the reschedule after the callback can no longer leave
    // two exponential clocks running. _pendingEvent is the one live event; any other event of this
    // process that still fires (cancelEventsFor cannot remove an event due at the current time) is
    // stale and ignored.
    private BlockMinedEvent _pendingEvent;
    // Diagnostic: schedules that found an event still pending, i.e. not cancelled by the caller
    // first (restartMining cancels explicitly and is not counted). Before the fix each of these left
    // a second pending event; now each is a harmless resample of a memoryless clock.
    private long _schedulesWhilePending = 0;
    private long _staleEventsIgnored = 0;

    public MiningProcessImpl(double meanBlockTime, RandomGenerator randomGenerator) {
        _poissonProcess = new PoissonProcess(1.0 / meanBlockTime, randomGenerator);
    }

    @Override
    public void dispatchEvent(Event event) {
        if (!BlockMinedEvent.EVENT_TYPE.equals(event.getEventType())) return;
        if (event != _pendingEvent) {
            _staleEventsIgnored++;
            return;
        }
        _pendingEvent = null;
        if (!_isMining) return;

        BlockMinedEvent blockMinedEvent = (BlockMinedEvent) event;

        if (_onCreatingBlockCallback == null) return;
        Block block = _onCreatingBlockCallback.apply(
                blockMinedEvent.getOccurrenceTime(),
                blockMinedEvent.getPreviousBlockHash()
        );
        if (block == null) return;

        logBlockMined(block);
        notifyBlockMined(block);
        scheduleNewBlockMinedEvent();
    }

    private String scheduleNewBlockMinedEvent() {
        if (_previousBlockSelectionCallback == null) {
            throw new IllegalStateException("Previous block selection callback must be set before scheduling a new block mined event.");
        }
        if (_pendingEvent != null) _schedulesWhilePending++;
        cancelPendingEvent();
        String previousBlockHash = _previousBlockSelectionCallback.get();

        BlockMinedEvent event = new BlockMinedEvent(
                getNextBlockMinedEventOccurrenceTimestamp(),
                previousBlockHash,
                this
        );
        // set before raiseEvent: an event due now is dispatched inside raiseEvent and must be live
        _pendingEvent = event;
        getSimulationContext().getEventCoordinator().raiseEvent(event);
        return previousBlockHash;
    }

    private long getNextBlockMinedEventOccurrenceTimestamp() {
        long eventCurrentTimeOffset = _poissonProcess.nextPointDistance();
        return getSimulationContext().getSystemClock().getCurrentTime() + eventCurrentTimeOffset;
    }

    private void notifyBlockMined(Block block) {
        if (_onBlockMinedCallback != null) _onBlockMinedCallback.accept(block);
    }

    private void cancelPendingEvent() {
        getSimulationContext().getEventCoordinator().cancelEventsFor(this);
        _pendingEvent = null;
    }

    /** Schedules that found an event still pending (see _schedulesWhilePending). */
    public long getSchedulesWhilePending() { return _schedulesWhilePending; }

    /** Fired events of this process that were no longer the live one and were ignored. */
    public long getStaleEventsIgnored() { return _staleEventsIgnored; }

    @Override
    public void startMining() {
        if (_isMining) return;
        scheduleNewBlockMinedEvent();
        _isMining = true;
        logMiningStarted();
    }

    @Override
    public void restartMining() {
        if (!_isMining) {
            startMining();
            return;
        }
        cancelPendingEvent();
        String previousHash = scheduleNewBlockMinedEvent();
        logMiningRestarted(previousHash);
    }

    @Override
    public void stopMining() {
        if (!_isMining) return;
        cancelPendingEvent();
        _isMining = false;
        logMiningStopped();
    }

    @Override
    public void setOnCreatingBlockCallback(BiFunction<Long, String, Block> onCreatingBlockCallback) {
        _onCreatingBlockCallback = onCreatingBlockCallback;
    }

    @Override
    public void setPreviousBlockSelectionCallback(Supplier<String> previousBlockSelectionCallback) {
        _previousBlockSelectionCallback = previousBlockSelectionCallback;
    }

    @Override
    public void setOnBlockMinedCallback(Consumer<Block> onBlockMinedCallback) {
        _onBlockMinedCallback = onBlockMinedCallback;
    }

    private void logMiningStarted() {
        if (!getTraceEventLogger().isEventTypeEnabled(BlockMiningStartedTraceEvent.EVENT_TYPE)) return;
        getTraceEventLogger().logEvent(new BlockMiningStartedTraceEvent(
                getSimulationContext().getSystemClock().getCurrentTime()));
    }

    private void logBlockMined(Block block) {
        if (!getTraceEventLogger().isEventTypeEnabled(BlockMinedTraceEvent.EVENT_TYPE)) return;
        getTraceEventLogger().logEvent(new BlockMinedTraceEvent(
                getSimulationContext().getSystemClock().getCurrentTime(), block));
    }

    private void logMiningRestarted(String previousHash) {
        if (!getTraceEventLogger().isEventTypeEnabled(BlockMiningRestartedTraceEvent.EVENT_TYPE)) return;
        getTraceEventLogger().logEvent(new BlockMiningRestartedTraceEvent(
                getSimulationContext().getSystemClock().getCurrentTime(), previousHash));
    }

    private void logMiningStopped() {
        if (!getTraceEventLogger().isEventTypeEnabled(BlockMiningStoppedTraceEvent.EVENT_TYPE)) return;
        getTraceEventLogger().logEvent(new BlockMiningStoppedTraceEvent(
                getSimulationContext().getSystemClock().getCurrentTime()));
    }
}
