package org.palladiosimulator.blockchainsystems.threesim_plugin

object ThreesimAttributes {
  const val FAILURE_THROUGHPUT_THRESHOLD: String = "FailureThroughputThreshold"
  const val FAILURE_THROUGHPUT_THRESHOLD_DEFAULT: String = "1.0" // transactions per minute

  const val SHANNON_ENTROPY_K: String = "ShannonEntropyK"
  const val SHANNON_ENTROPY_K_DEFAULT: String = "1.0"

  const val NAKAMOTO_COEFFICIENT_THRESHOLD: String = "NakamotoCoefficientThreshold"
  const val NAKAMOTO_COEFFICIENT_THRESHOLD_DEFAULT: String = "50.0" // in percent

  const val RELIABILITY_OBSERVATION_TIMESPAN: String = "ReliabilityObservationTimespan"
  const val RELIABILITY_OBSERVATION_TIMESPAN_DEFAULT: String = "24.0" // in h => 24 hours, one day

  const val BLOCK_INTERVAL = "BlockInterval"
  const val BLOCK_INTERVAL_DEFAULT = "600.0"

  const val PROPAGATION_DELAY = "PropagationDelay"
  const val PROPAGATION_DELAY_DEFAULT = "0.0"

  const val NODE_DEGREE = "NodeDegree"
  const val NODE_DEGREE_DEFAULT = "8"

  const val MAX_BLOCK_SIZE = "MaxBlockSize"
  const val MAX_BLOCK_SIZE_DEFAULT = "1000000"

  const val NETWORK_BANDWIDTH = "NetworkBandwidth"
  const val NETWORK_BANDWIDTH_DEFAULT = "100.0"

  const val COMBINED_ATTACK_MODE = "CombinedAttackMode"
  const val COMBINED_ATTACK_MODE_DEFAULT = "NONE"

  const val COMBINED_ATTACK_MODE_NONE = "NONE"
  const val COMBINED_ATTACK_MODE_SELFISH_RACE = "SELFISH_RACE"
  const val COMBINED_ATTACK_MODE_SELFISH_FINNEY = "SELFISH_FINNEY"

  const val TRANSACTION_A_DELAY = "TransactionADelay"
  const val TRANSACTION_A_DELAY_DEFAULT = "0"

  const val TRANSACTION_B_ACCELERATION = "TransactionBAcceleration"
  const val TRANSACTION_B_ACCELERATION_DEFAULT = "0"
}