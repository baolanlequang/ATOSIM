package org.palladiosimulator.blockchainsystems.threesim.behavior;

import org.palladiosimulator.blockchainsystems.core.block.abstractions.Block;

public class BlockUtils {

    public static final BlockUtils INSTANCE = new BlockUtils();
    public static final String FORKED_BLOCK_NAME = "ForkedBlock";

    public static boolean isBlockForked(Block block) {
        return block.hasTag(FORKED_BLOCK_NAME);
    }
}
