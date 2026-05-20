package org.palladiosimulator.blockchainsystems.threesim.simulation

enum class AttackType {
    NONE,
    SELFISH_MINING,
    LEAD_STUBBORN_MINING,
    EQUAL_FORK_STUBBORN_MINING,
    TRAIL_STUBBORN_MINING,
    FINNEY,
    RACE,
    MAJORITY,
    COMBINED_SELFISH_RACE,
    COMBINED_SELFISH_FINNEY
}