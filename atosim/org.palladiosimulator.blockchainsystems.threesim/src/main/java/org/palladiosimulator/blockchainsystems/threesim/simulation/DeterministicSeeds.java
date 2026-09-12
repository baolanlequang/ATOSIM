package org.palladiosimulator.blockchainsystems.threesim.simulation;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * Deterministic seed derivation for reproducible Monte Carlo replications.
 *
 * topologySeed(configId, replicationId) is the single root seed for one replication. Every RNG
 * site within that replication (topology wiring, mining timing, transaction submission,
 * validation timing, ...) derives its own independent seed from the root seed plus a stable
 * per-site tag via deriveSeed, so unrelated random processes never share correlated draws --
 * seeding every site with the literal root seed would otherwise make e.g. every node's mining
 * process draw identical inter-block times.
 */
public final class DeterministicSeeds {

    private DeterministicSeeds() {}

    public static long topologySeed(String configId, long replicationId) {
        long a = mix64(stableHash(configId == null ? "" : configId));
        long b = mix64(replicationId ^ 0x9E3779B97F4A7C15L);
        return mix64(a ^ Long.rotateLeft(b, 31));
    }

    public static long deriveSeed(long rootSeed, String siteTag) {
        return mix64(rootSeed ^ mix64(stableHash(siteTag)));
    }

    /** A {@link RandomGenerator} backed by {@code java.util.Random}, seeded deterministically. */
    public static RandomGenerator seededGenerator(long seed) {
        return RandomGeneratorFactory.of("Random").create(seed);
    }

    public static RandomGenerator seededGenerator(long rootSeed, String siteTag) {
        return seededGenerator(deriveSeed(rootSeed, siteTag));
    }

    /** Deterministic replacement for {@code UUID.randomUUID().toString()} -- draws two longs
     * from the given generator and renders them as a fixed-width 32-char lowercase hex string.
     * Every block/transaction/node/system id in this codebase is treated as an opaque string,
     * never parsed back into a {@code UUID} or matched against the hyphenated format (confirmed
     * by a repo-wide format-dependency search), so this is a safe drop-in replacement. */
    public static String randomHexId(RandomGenerator generator) {
        return String.format("%016x%016x", generator.nextLong(), generator.nextLong());
    }

    // 64-bit FNV-1a. Deliberately not String.hashCode() (only 32 bits of entropy in, and while
    // its algorithm is Javadoc-specified/stable, keeping the hash local avoids any dependence on
    // JDK string-hashing behavior for a value that determines simulation reproducibility).
    private static long stableHash(String s) {
        long hash = 0xcbf29ce484222325L;
        for (int i = 0; i < s.length(); i++) {
            hash ^= s.charAt(i);
            hash *= 0x100000001b3L;
        }
        return hash;
    }

    // SplitMix64 finalizer -- cheap, well-distributed avalanche mixing.
    private static long mix64(long z) {
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
}
