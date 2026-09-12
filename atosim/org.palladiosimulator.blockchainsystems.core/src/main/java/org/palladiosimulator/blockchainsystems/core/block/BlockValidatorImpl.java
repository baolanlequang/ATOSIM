package org.palladiosimulator.blockchainsystems.core.block;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockValidator;
import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.ValueProvider;

import java.util.function.BiConsumer;

public class BlockValidatorImpl extends BlockchainNodeObject implements BlockValidator {

    // tau_proc(b) = 50ms * (n_tx(b) / 3500) -- per-hop local processing time before a
    // received block's chain view is updated and the block is forwarded, scaling with
    // block content; additive to (not a replacement for) the node's modeled validation speed.
    private static final double PROCESSING_TIME_MS_PER_REFERENCE_BLOCK = 50.0;
    private static final double REFERENCE_TRANSACTION_COUNT = 3500.0;

    private final ValueProvider<Long> _blockValidationDurationProvider;
    private final boolean _staticValidationDelayEnabled;
    private BiConsumer<Block, Boolean> _onBlockValidatedCallback;

    // Item 6: single-server FCFS validation queue for this node, represented as the simulated
    // time at which the node becomes free to start its next validation -- not an explicit
    // List/Queue<Block>. This is a complete FCFS queue, not merely a mutex: onBlockReceived (see
    // every *NodeBehavior class) calls validateBlock() unconditionally and synchronously as each
    // block arrives, and those calls happen in the node's guaranteed non-decreasing arrival-time
    // dispatch order (EventCoordinatorImpl's TreeMap<Long, EffectsTimeSlice> scheduling, already
    // confirmed correct by fix 7 -- not touched here). Each block's start/finish time is therefore
    // computed once, immediately, in call order, with no separate "wake up and pick next pending
    // block" step where order could be lost -- a monotonically-advancing free-time scalar is
    // sufficient to preserve FCFS ordering under that guarantee, and needs no unordered/ordered
    // collection of its own.
    private long _queueFreeTime = 0L;

    public BlockValidatorImpl(ValueProvider<Long> blockValidationDurationProvider, boolean staticValidationDelayEnabled) {
        _blockValidationDurationProvider = blockValidationDurationProvider;
        _staticValidationDelayEnabled = staticValidationDelayEnabled;
    }

    @Override
    public void validateBlock(Block block) {
        BlockValidationStartedEvent event = new BlockValidationStartedEvent(
                getSimulationContext().getSystemClock().getCurrentTime(),
                this,
                block
        );
        getSimulationContext().getEventCoordinator().raiseEvent(event);
    }

    @Override
    public void dispatchEvent(Event event) {
        if (BlockValidationStartedEvent.EVENT_TYPE.equals(event.getEventType())) {
            handleBlockValidationStartedEvent((BlockValidationStartedEvent) event);
        } else if (BlockValidationFinishedEvent.EVENT_TYPE.equals(event.getEventType())) {
            handleBlockValidationFinishedEvent((BlockValidationFinishedEvent) event);
        }
    }

    private void handleBlockValidationStartedEvent(BlockValidationStartedEvent event) {
        // event.getOccurrenceTime() is this block's arrival at the validator (unchanged meaning).
        // Its actual validation start is delayed to whenever the node's single validation slot
        // frees up, if that's later -- this is the FCFS queueing itself: a block that arrives
        // while another is still being validated waits for it to finish before its own delay
        // starts counting, rather than validating concurrently.
        long arrivalTime = event.getOccurrenceTime();
        long startTime = Math.max(arrivalTime, _queueFreeTime);

        long staticDelay = _staticValidationDelayEnabled ? _blockValidationDurationProvider.getValue() : 0L;
        long delay = staticDelay + processingDelay(event.block());
        long finishTime = startTime + delay;
        _queueFreeTime = finishTime;

        BlockValidationFinishedEvent finished = new BlockValidationFinishedEvent(
                finishTime,
                this,
                event.block()
        );
        getSimulationContext().getEventCoordinator().raiseEvent(finished);
    }

    private long processingDelay(Block block) {
        double nTx = block.getTransactions().size();
        return Math.round(PROCESSING_TIME_MS_PER_REFERENCE_BLOCK * (nTx / REFERENCE_TRANSACTION_COUNT));
    }

    private void handleBlockValidationFinishedEvent(BlockValidationFinishedEvent event) {
        if (_onBlockValidatedCallback != null) {
            _onBlockValidatedCallback.accept(event.block(), true);
        }
    }

    @Override
    public void setOnBlockValidatedCallback(BiConsumer<Block, Boolean> onBlockValidatedCallback) {
        _onBlockValidatedCallback = onBlockValidatedCallback;
    }
}
