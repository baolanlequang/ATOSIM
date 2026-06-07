package org.palladiosimulator.blockchainsystems.threesim_plugin;

public final class ThreesimAttributes {

    private ThreesimAttributes() {}

    public static final String FAILURE_THROUGHPUT_THRESHOLD = "FailureThroughputThreshold";
    public static final String FAILURE_THROUGHPUT_THRESHOLD_DEFAULT = "1.0";

    public static final String SHANNON_ENTROPY_K = "ShannonEntropyK";
    public static final String SHANNON_ENTROPY_K_DEFAULT = "1.0";

    public static final String NAKAMOTO_COEFFICIENT_THRESHOLD = "NakamotoCoefficientThreshold";
    public static final String NAKAMOTO_COEFFICIENT_THRESHOLD_DEFAULT = "50.0";

    public static final String RELIABILITY_OBSERVATION_TIMESPAN = "ReliabilityObservationTimespan";
    public static final String RELIABILITY_OBSERVATION_TIMESPAN_DEFAULT = "24.0";

    public static final String BLOCK_INTERVAL = "BlockInterval";
    public static final String BLOCK_INTERVAL_DEFAULT = "600.0";

    public static final String PROPAGATION_DELAY = "PropagationDelay";
    public static final String PROPAGATION_DELAY_DEFAULT = "0.0";

    public static final String NODE_DEGREE = "NodeDegree";
    public static final String NODE_DEGREE_DEFAULT = "8";

    public static final String MAX_BLOCK_SIZE = "MaxBlockSize";
    public static final String MAX_BLOCK_SIZE_DEFAULT = "1000000";

    public static final String NETWORK_BANDWIDTH = "NetworkBandwidth";
    public static final String NETWORK_BANDWIDTH_DEFAULT = "100.0";

    public static final String COMBINED_ATTACK_MODE = "CombinedAttackMode";
    public static final String COMBINED_ATTACK_MODE_DEFAULT = "NONE";
    public static final String COMBINED_ATTACK_MODE_NONE = "NONE";
    public static final String COMBINED_ATTACK_MODE_SELFISH_RACE = "SELFISH_RACE";
    public static final String COMBINED_ATTACK_MODE_SELFISH_FINNEY = "SELFISH_FINNEY";
    public static final String COMBINED_ATTACK_MODE_SELFISH_LEAD_STUBBORN = "SELFISH_LEAD_STUBBORN";
    public static final String COMBINED_ATTACK_MODE_SELFISH_TRAIL_STUBBORN = "SELFISH_TRAIL_STUBBORN";

    public static final String TRANSACTION_A_DELAY = "TransactionADelay";
    public static final String TRANSACTION_A_DELAY_DEFAULT = "0";

    public static final String TRANSACTION_B_ACCELERATION = "TransactionBAcceleration";
    public static final String TRANSACTION_B_ACCELERATION_DEFAULT = "0";
}
