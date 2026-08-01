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
        long staticDelay = _staticValidationDelayEnabled ? _blockValidationDurationProvider.getValue() : 0L;
        long delay = staticDelay + processingDelay(event.block());
        BlockValidationFinishedEvent finished = new BlockValidationFinishedEvent(
                getSimulationContext().getSystemClock().getCurrentTime() + delay,
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
