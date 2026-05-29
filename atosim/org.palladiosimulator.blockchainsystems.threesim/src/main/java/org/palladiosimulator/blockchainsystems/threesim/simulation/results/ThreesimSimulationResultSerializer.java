package org.palladiosimulator.blockchainsystems.threesim.simulation.results;

import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationResult;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationResultSerializer;
import org.palladiosimulator.blockchainsystems.threesim.serialization.ThreesimJsonSerializer;

public class ThreesimSimulationResultSerializer implements SimulationResultSerializer {

    @Override
    public String serialize(SimulationResult result) {
        if (result instanceof ThreesimMonteCarloSimulationResult r) {
            return ThreesimJsonSerializer.toJson(r);
        }
        if (result instanceof ThreesimSingleSimulationResult r) {
            return ThreesimJsonSerializer.toJson(r);
        }
        throw new IllegalArgumentException("Unsupported simulation result type: " +
                result.getClass().getSimpleName());
    }
}
