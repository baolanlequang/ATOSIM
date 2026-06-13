package org.palladiosimulator.blockchainsystems.core.behavior;

import org.jetbrains.annotations.NotNull;
import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;
import org.palladiosimulator.blockchainsystems.core.common.BlockchainNodeObject;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.Event;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeBehavior;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.BlockchainSystemNodeContext;
import org.palladiosimulator.blockchainsystems.core.transaction.abstractions.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Equal-fork stubborn mining, a.k.a. "F-stubborn" (Nayak et al., "Stubborn Mining", Section 3.2).
 *
 * F-stubborn's entire deviation from selfish mining is a single rule: when Alice wins a tied
 * race (state lead=0', i.e. she has no remaining hidden material and then mines the next block
 * herself), instead of revealing that block to win outright, she CONCEALS it and keeps mining
 * on it privately, landing back at a plain hidden lead of 1. Every other transition (lead=0
 * adopt; lead=1 reveal-one-to-tie; lead=2 reveal-all-to-win; lead&gt;2 reveal-one-to-pressure) is
 * unchanged from selfish mining.
 *
 * State:
 * - privateChain: attacker-mined blocks that are still hidden / unpublished
 * - ownTipHash: hash of the attacker's own most recently authored block, published or not
 * - inEqualForkContest: true when the attacker has revealed part of its private branch and is
 *   currently in a tied public contest
 */
public class EqualForkStubbornMiningNodeBehavior extends BlockchainNodeObject implements BlockchainSystemNodeBehavior {

    private final HonestBlockchainSystemNodeBehavior honest = new HonestBlockchainSystemNodeBehavior();

    /**
     * Hidden attacker blocks that have been mined but not yet published.
     * Index 0 is the next hidden block to reveal.
     */
    private final List<Block> privateChain = new ArrayList<>();

    /**
     * Hash of the attacker's own most recently authored block, published or not.
     * Kept separate from privateChain because privateChain empties out the moment a
     * block is published, even though the attacker must keep mining on top of that same
     * block rather than falling back to an ambiguous public tip.
     */
    private String ownTipHash = null;

    /**
     * True when the attacker is currently engaged in a tied public contest.
     */
    private boolean inEqualForkContest = false;

    /**
     * Number of attacker blocks already revealed in the current contest.
     */
    private int publishedInCurrentContest = 0;

    @Override
    public void onNodeInitialized(BlockchainSystemNodeContext context) {
        resetPrivateState();
        context.getMiningProcess().startMining();
    }

    @Override
    public void onTransactionReceived(Transaction transaction, BlockchainSystemNodeContext context) {
        context.getTrxMemPool().storeTransaction(transaction);
        context.getTransactionPropagationStrategy().distribute(transaction);
    }

    @Override
    public void onBlockReceived(Block block, BlockchainSystemNodeContext context) {
        if (context.getBlockchain().hasBlockWithHash(block.getHash())
                || context.getOrphanBlockPool().hasBlockWithHash(block.getHash())) {
            return;
        }

        context.getBlockValidator().validateBlock(block);
    }

    @Override
    public void onBlockValidated(Block block, boolean isValid, BlockchainSystemNodeContext context) {
        if (!isValid) {
            return;
        }

        if (inEqualForkContest) {
            handleBlockWhileInEqualForkContest(block, context);
            return;
        }

        int hiddenLead = hiddenLead();

        // No hidden branch -> behave honestly.
        if (hiddenLead == 0) {
            adoptPublicBlockAndAbandonPrivateState(block, context);
            return;
        }

        // lead=1: reveal one block to force a tie (unchanged from selfish mining).
        if (hiddenLead == 1) {
            boolean published = publishOneHiddenBlock(context);
            if (published) {
                inEqualForkContest = true;
                publishedInCurrentContest = 1;
            }
            return;
        }

        // lead=2: reveal everything and win outright (unchanged from selfish mining).
        if (hiddenLead == 2) {
            publishAllHiddenBlocks(context);
            clearContestStateOnly();
            return;
        }

        // lead>2: reveal one block to keep pressure while preserving the rest (unchanged).
        boolean published = publishOneHiddenBlock(context);
        if (published) {
            clearContestStateOnly();
        }
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        // F-stubborn's defining rule: winning an already-tied race outright (state 0', no
        // remaining hidden material) is NOT revealed. Conceal the new block and keep mining
        // on it privately instead, landing at a plain hidden lead of 1.
        boolean wasWinningRace = inEqualForkContest && privateChain.isEmpty();

        privateChain.add(block);
        ownTipHash = block.getHash();

        if (wasWinningRace) {
            clearContestStateOnly();
        }
    }

    @Override
    public Block onCreatingBlock(long blockMinedAt, String previousBlockHash, BlockchainSystemNodeContext context) {
        var selection = context.getTransactionSelectionProcess().selectTransactionsForBlock(context);

        return context.getBlockFactory().createBlock(
                UUID.randomUUID().toString(),
                previousBlockHash,
                context.getId(),
                blockMinedAt,
                selection.getTotalSize(),
                selection.getTransactions()
        );
    }

    @NotNull
    @Override
    public String onPreviousBlockSelection(BlockchainSystemNodeContext context) {
        // Always continue mining on top of my own last-authored block, published or not.
        // Falling back to the generic public-tip lookup here would pick arbitrarily between
        // my own tip and a tied honest tip during a contest.
        if (ownTipHash != null) {
            return ownTipHash;
        }

        return honest.onPreviousBlockSelection(context);
    }

    /**
     * Contest-state handler (unchanged from selfish mining's tie handling): reveal everything
     * and win once any hidden material remains; otherwise the contest is lost, adopt.
     */
    private void handleBlockWhileInEqualForkContest(Block block, BlockchainSystemNodeContext context) {
        if (hiddenLead() > 0) {
            publishAllHiddenBlocks(context);
            clearContestStateOnly();
            return;
        }

        adoptPublicBlockAndAbandonPrivateState(block, context);
    }

    /**
     * Append a public block and fully abandon any no-longer-viable private strategy state.
     */
    private void adoptPublicBlockAndAbandonPrivateState(Block block, BlockchainSystemNodeContext context) {
        AppendOutcome outcome = BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(block, context);

        if (outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING) {
            context.getTrxMemPool().removeTransactions(block.getTransactions());
            context.getMiningProcess().restartMining();
            context.getBlockPropagationStrategy().distribute(block);
            resetPrivateState();
        }
    }

    /**
     * Publish the oldest hidden attacker block.
     *
     * @return true iff the block was meaningfully appended and removed from hidden state
     */
    private boolean publishOneHiddenBlock(BlockchainSystemNodeContext context) {
        if (privateChain.isEmpty()) {
            return false;
        }

        Block publish = privateChain.get(0);

        AppendOutcome outcome = BehaviorUtils.INSTANCE.appendBlockToBlockchainDetailed(publish, context);

        if (outcome == AppendOutcome.INCLUDED || outcome == AppendOutcome.FORKING) {
            privateChain.remove(0);
            context.getTrxMemPool().removeTransactions(publish.getTransactions());
            context.getMiningProcess().restartMining();
            context.getBlockPropagationStrategy().distribute(publish);
            return true;
        }

        return false;
    }

    /**
     * Publish all remaining hidden attacker blocks in order.
     */
    private void publishAllHiddenBlocks(BlockchainSystemNodeContext context) {
        while (!privateChain.isEmpty()) {
            int sizeBefore = privateChain.size();
            boolean published = publishOneHiddenBlock(context);

            if (!published || privateChain.size() == sizeBefore) {
                break;
            }
        }
    }

    private int hiddenLead() {
        return privateChain.size();
    }

    private void clearContestStateOnly() {
        inEqualForkContest = false;
        publishedInCurrentContest = 0;
    }

    private void resetPrivateState() {
        privateChain.clear();
        ownTipHash = null;
        clearContestStateOnly();
    }

    @Override
    public void dispatchEvent(Event event) {
        // no-op
    }
}
