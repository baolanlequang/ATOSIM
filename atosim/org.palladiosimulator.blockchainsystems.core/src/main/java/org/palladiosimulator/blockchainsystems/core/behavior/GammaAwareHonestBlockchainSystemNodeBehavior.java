package org.palladiosimulator.blockchainsystems.core.behavior;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeContext;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GammaAwareHonestBlockchainSystemNodeBehavior extends BlockchainNodeObject implements BlockchainSystemNodeBehavior {

    private final Set<String> _attackerNodeIds;
    private final double _gamma;
    private final Random _random = new Random();

    public GammaAwareHonestBlockchainSystemNodeBehavior(Set<String> attackerNodeIds, double gamma) {
        _attackerNodeIds = attackerNodeIds;
        _gamma = gamma;
    }

    @Override
    public void onNodeInitialized(BlockchainSystemNodeContext context) {
        context.getMiningProcess().startMining();
    }

    @Override
    public void onTransactionReceived(Transaction transaction, BlockchainSystemNodeContext context) {
        context.getTrxMemPool().storeTransaction(transaction);
        context.getTransactionPropagationStrategy().distribute(transaction);
    }

    @Override
    public void onBlockReceived(Block block, BlockchainSystemNodeContext context) {
        if (context.getBlockchain().hasBlockWithHash(block.getHash()) ||
                context.getOrphanBlockPool().hasBlockWithHash(block.getHash())) {
            return;
        }
        context.getBlockValidator().validateBlock(block);
    }

    @Override
    public void onBlockValidated(Block block, boolean isValid, BlockchainSystemNodeContext context) {
        if (!isValid) return;

        AppendOutcome outcome = BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(block, context);
        if (outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING) {
            context.getTrxMemPool().removeTransactions(block.getTransactions());
            context.getMiningProcess().restartMining();
            context.getBlockPropagationStrategy().distribute(block);
        }
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        AppendOutcome outcome = BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(block, context);
        if (outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING) {
            context.getTrxMemPool().removeTransactions(block.getTransactions());
            context.getBlockPropagationStrategy().distribute(block);
        }
    }

    @Override
    public Block onCreatingBlock(long blockMinedAt, String previousBlockHash, BlockchainSystemNodeContext context) {
        var selected = context.getTransactionSelectionProcess().selectTransactionsForBlock(context);
        return context.getBlockFactory().createBlock(
                UUID.randomUUID().toString(),
                previousBlockHash,
                context.getId(),
                blockMinedAt,
                selected.getTotalSize(),
                selected.getTransactions()
        );
    }

    @Override
    public String onPreviousBlockSelection(BlockchainSystemNodeContext context) {
        Set<Block> tips = context.getBlockchain().getLastBlocksOfLongestChains();

        if (tips.size() <= 1) {
            return tips.iterator().next().getHash();
        }

        List<Block> attackerTips = tips.stream()
                .filter(b -> _attackerNodeIds.contains(b.getOriginId()))
                .collect(Collectors.toList());
        List<Block> honestTips = tips.stream()
                .filter(b -> !_attackerNodeIds.contains(b.getOriginId()))
                .collect(Collectors.toList());

        if (attackerTips.isEmpty() || honestTips.isEmpty()) {
            return tips.iterator().next().getHash();
        }

        Block chosen = (_random.nextDouble() < _gamma)
                ? attackerTips.get(_random.nextInt(attackerTips.size()))
                : honestTips.get(_random.nextInt(honestTips.size()));

        return chosen.getHash();
    }

    @Override
    public void dispatchEvent(Event event) {
    }
}
