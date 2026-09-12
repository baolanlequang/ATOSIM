package org.palladiosimulator.blockchainsystems.threesim.simulation.results;

/**
 * D5 episode-resolution classification for one simulation round, determined once the round
 * has terminated (see ThreesimSimulationMonitor.getEpisodeStatus()):
 * <ul>
 *   <li>SUCCESS -- quiescence was reached, and an attacker-mined block that displaced a
 *       previously-public branch (i.e. was part of a recorded chain reorganization) survives
 *       on the canonical chain at quiescence.</li>
 *   <li>FAILURE -- quiescence was reached, but no such surviving attacker-mined displacing
 *       block exists (this covers both "no reorg ever happened" and "an attacker block sits
 *       on the chain but never displaced a competing public branch").</li>
 *   <li>UNRESOLVED -- the round terminated via the H_max cap without ever reaching
 *       quiescence.</li>
 * </ul>
 */
public enum EpisodeStatus {
    SUCCESS, FAILURE, UNRESOLVED;

    public String toJsonValue() {
        return name().toLowerCase();
    }
}
