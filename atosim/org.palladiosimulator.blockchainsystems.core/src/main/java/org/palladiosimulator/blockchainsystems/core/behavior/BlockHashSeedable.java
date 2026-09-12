package org.palladiosimulator.blockchainsystems.core.behavior;

import java.util.random.RandomGenerator;

/**
 * Implemented by node behaviors that generate a block hash in {@code onCreatingBlock} (directly
 * or by delegating to an internal sub-behavior). The caller that constructs the behavior (see
 * {@code ThreesimBlockchainSystemNodeBehaviorFactory}) must call {@link #setBlockHashGenerator}
 * with a per-node seeded generator before the behavior mines its first block; otherwise the hash
 * would fall back to a non-reproducible source.
 */
public interface BlockHashSeedable {
    void setBlockHashGenerator(RandomGenerator generator);
}
