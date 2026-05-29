package org.palladiosimulator.blockchainsystems.core.block;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.BlockValidator;
import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.ValueProvider;

import java.util.function.BiConsumer;

public class BlockValidatorImpl extends BlockchainNodeObject implements BlockValidator {

    private final ValueProvider<Long> _blockValidationDurationProvider;
    private BiConsumer<Block, Boolean> _onBlockValidatedCallback;

    public BlockValidatorImpl(ValueProvider<Long> blockValidationDurationProvider) {
        _blockValidationDurationProvider = blockValidationDurationProvider;
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
        BlockValidationFinishedEvent finished = new BlockValidationFinishedEvent(
                getSimulationContext().getSystemClock().getCurrentTime() + _blockValidationDurationProvider.getValue(),
                this,
                event.block()
        );
        getSimulationContext().getEventCoordinator().raiseEvent(finished);
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
