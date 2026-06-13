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
 * Lead-stubborn mining, a.k.a. "L-stubborn" (Nayak et al., "Stubborn Mining", Section 3.1).
 *
 * L-stubborn's entire deviation from selfish mining is a single rule: whenever the public
 * chain catches up to any hidden lead of 1 or more, reveal exactly ONE block (matching the
 * public chain's new length) instead of selfish mining's behavior of revealing everything at
 * lead=2 or nothing at lead&gt;2. This keeps a tied fork alive with whatever hidden material
 * remains, rather than winning outright or silently conceding ground. Every other transition
 * (lead=0 adopt; own-mining behavior; the lead=0' win-on-own-block case) is unchanged from
 * selfish mining.
 *
 * State:
 * - privateChain: attacker-mined blocks that are still hidden / unpublished
 * - ownTipHash: hash of the attacker's own most recently authored block, published or not
 * - inContestState: true when the attacker has revealed part of its private branch and is
 *   currently in a tied public contest
 */
public class LeadStubbornMiningNodeBehavior extends BlockchainNodeObject implements BlockchainSystemNodeBehavior {

    private final HonestBlockchainSystemNodeBehavior honest = new HonestBlockchainSystemNodeBehavior();

    /**
     * Hidden attacker blocks that have been mined but not yet published.
     * Index 0 is the next hidden block that would be published first.
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
     * True when the attacker has already revealed part of its private branch and is currently
     * managing an ongoing public contest.
     */
    private boolean inContestState = false;

    /**
     * Number of attacker blocks revealed in the current contest.
     * This is not the same thing as hidden lead.
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

        int hiddenLead = hiddenLead();

        // No hidden advantage -> behave honestly and reset.
        if (hiddenLead == 0) {
            adoptPublicBlockAndAbandonPrivateState(block, context);
            return;
        }

        // L-stubborn's defining rule: for ANY hidden lead >= 1, reveal exactly one block to
        // match the public chain's new length and keep the tied contest alive, instead of
        // selfish mining's reveal-all-at-2 / reveal-nothing-beyond-2 behavior.
        boolean published = publishOneHiddenBlock(context);
        if (published) {
            inContestState = true;
            publishedInCurrentContest++;
        }
    }

    @Override
    public void onBlockMined(Block block, BlockchainSystemNodeContext context) {
        // Selfish mining's own-mining rule is unchanged by L-stubborn: only the special case of
        // winning an already-tied race outright (state 0', no remaining hidden material) reveals;
        // otherwise Alice always keeps mining privately without revealing.
        boolean wasWinningRace = inContestState && privateChain.isEmpty();

        privateChain.add(block);
        ownTipHash = block.getHash();

        if (wasWinningRace) {
            publishAllHiddenBlocks(context);
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
        inContestState = false;
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
