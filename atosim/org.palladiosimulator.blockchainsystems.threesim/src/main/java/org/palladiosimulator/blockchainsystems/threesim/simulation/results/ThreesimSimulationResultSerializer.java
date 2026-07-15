package org.palladiosimulator.blockchainsystems.threesim.simulation.results;

import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationResult;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationResultSerializer;
import org.palladiosimulator.blockchainsystems.threesim.serialization.ThreesimChainReorganizationsCsvSerializer;
import org.palladiosimulator.blockchainsystems.threesim.serialization.ThreesimJsonSerializer;

public class ThreesimSimulationResultSerializer implements SimulationResultSerializer {

    @Override
    public String serialize(SimulationResult result) {
        return serialize(result, true);
    }

    /**
     * Serializes to JSON, with chainReorganizations embedded (see ThreesimJsonSerializer) unless
     * includeChainReorganizations is false -- used when that detail is written to a separate CSV
     * instead (see serializeChainReorganizationsCsv), to avoid duplicating it in both files.
     */
    public String serialize(SimulationResult result, boolean includeChainReorganizations) {
        if (result instanceof ThreesimMonteCarloSimulationResult r) {
            return ThreesimJsonSerializer.toJson(r, includeChainReorganizations);
        }
        if (result instanceof ThreesimSingleSimulationResult r) {
            return ThreesimJsonSerializer.toJson(r, includeChainReorganizations);
        }
        throw new IllegalArgumentException("Unsupported simulation result type: " +
                result.getClass().getSimpleName());
    }

    /**
     * The distinct chain-reorganization events (attacker reveals) as CSV -- an alternative to
     * embedding them in the JSON, only used when explicitly requested (see
     * ThreesimChainReorganizationsCsvSerializer / the writeChainReorganizationsAsCsv config flag).
     */
    public String serializeChainReorganizationsCsv(SimulationResult result) {
        if (result instanceof ThreesimMonteCarloSimulationResult r) {
            return ThreesimChainReorganizationsCsvSerializer.toCsv(r);
        }
        if (result instanceof ThreesimSingleSimulationResult r) {
            return ThreesimChainReorganizationsCsvSerializer.toCsv(r);
        }
        throw new IllegalArgumentException("Unsupported simulation result type: " +
                result.getClass().getSimpleName());
    }
}
