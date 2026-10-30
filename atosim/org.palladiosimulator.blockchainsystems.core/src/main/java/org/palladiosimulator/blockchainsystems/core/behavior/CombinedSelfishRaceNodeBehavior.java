package org.palladiosimulator.blockchainsystems.core.behavior;

import org.jetbrains.annotations.NotNull;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeContext;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

import java.util.random.RandomGenerator;

public class CombinedSelfishRaceNodeBehavior extends BlockchainNodeObject
        implements BlockchainSystemNodeBehavior, BlockHashSeedable, UnpublishedBlockCounter {

    private final SelfishMiningNodeBehavior selfish = new SelfishMiningNodeBehavior();
    private final RaceMiningNodeBehavior race = new RaceMiningNodeBehavior();

    @Override
    public void setBlockHashGenerator(RandomGenerator generator) {
        selfish.setBlockHashGenerator(generator);
        race.setBlockHashGenerator(generator);
    }

    @Override
    public void onNodeInitialized(BlockchainSystemNodeContext context) {
        selfish.onNodeInitialized(context);
    }

    @Override
    public void onTransactionReceived(Transaction transaction, BlockchainSystemNodeContext context) {
        race.onTransactionReceived(transaction, context);
    }

    @Override
    public void onBlockReceived(Block block, BlockchainSystemNodeContext context) {
        selfish.onBlockReceived(block, context);
    }

    @Override
    public void onBlockValidated(Block block, boolean isValid, BlockchainSystemNodeContext context) {
        selfish.onBlockValidated(block, isValid, context);
        if (isValid) {
            race.onBlockValidated(block, true, context);
        }
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        selfish.onBlockMined(block, context);
        race.onBlockMined(block, context);
    }

    @Override
    public Block onCreatingBlock(long blockMinedAt, String previousBlockHash, BlockchainSystemNodeContext context) {
        return selfish.onCreatingBlock(blockMinedAt, previousBlockHash, context);
    }

    @NotNull
    @Override
    public String onPreviousBlockSelection(BlockchainSystemNodeContext context) {
        return selfish.onPreviousBlockSelection(context);
    }

    // UnpublishedBlockCounter: both sub-behaviours record every mined block (onBlockMined calls
    // both), so counts are combined with max, not summed. The round is held open if either
    // sub-behaviour holds it open.
    @Override
    public int getUnpublishedBlockCount() {
        return Math.max(selfish.getUnpublishedBlockCount(), race.getUnpublishedBlockCount());
    }

    @Override
    public long getBranchStartTime() {
        long x = selfish.getBranchStartTime(), y = race.getBranchStartTime();
        if (x == NO_BRANCH) return y;
        if (y == NO_BRANCH) return x;
        return Math.min(x, y);
    }

    @Override
    public boolean hasOpenContest() { return selfish.hasOpenContest() || race.hasOpenContest(); }

    @Override
    public int getTrailingDeficit() { return Math.max(selfish.getTrailingDeficit(), race.getTrailingDeficit()); }

    @Override
    public long getDroppedUnpublishableBlockCount() {
        return selfish.getDroppedUnpublishableBlockCount() + race.getDroppedUnpublishableBlockCount();
    }

    @Override
    public boolean holdsRoundOpen(long lastReorgTimestamp) {
        return selfish.holdsRoundOpen(lastReorgTimestamp) || race.holdsRoundOpen(lastReorgTimestamp);
    }

    @Override
    public void dispatchEvent(Event event) {
        selfish.dispatchEvent(event);
        race.dispatchEvent(event);
    }
}