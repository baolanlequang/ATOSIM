package org.palladiosimulator.blockchainsystems.threesim.creation.network;

import org.palladiosimulator.blockchainsystems.bscm.linkallocation.DynamicLinkLatencySpecification;
import org.palladiosimulator.blockchainsystems.bscm.linkallocation.DynamicLinkThroughputSpecification;
import org.palladiosimulator.blockchainsystems.bscm.linkallocation.LinkLatencySpecification;
import org.palladiosimulator.blockchainsystems.bscm.linkallocation.LinkThroughputSpecification;
import org.palladiosimulator.blockchainsystems.bscm.linkallocation.StaticLinkLatencySpecification;
import org.palladiosimulator.blockchainsystems.bscm.linkallocation.StaticLinkThroughputSpecification;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationLifecycleAwareValueProvider;
import org.palladiosimulator.blockchainsystems.core.system.abstractions.P2PNetworkFactory;
import org.palladiosimulator.blockchainsystems.threesim.creation.LatencyValueProviderAdapter;
import org.palladiosimulator.blockchainsystems.threesim.creation.StaticLatencyValueProvider;
import org.palladiosimulator.blockchainsystems.threesim.creation.StaticThroughputValueProvider;
import org.palladiosimulator.blockchainsystems.threesim.creation.ThroughputValueProviderAdapter;
import org.palladiosimulator.blockchainsystems.threesim.simulation.DeterministicSeeds;
import org.palladiosimulator.blockchainsystems.threesim.simulation.ThreesimSimulationParameters;

public abstract class AbstractThreesimP2PNetworkFactory implements P2PNetworkFactory {

    protected final ThreesimSimulationParameters simulationParameters;
    protected final long rootSeed;

    protected AbstractThreesimP2PNetworkFactory(ThreesimSimulationParameters simulationParameters, long rootSeed) {
        this.simulationParameters = simulationParameters;
        this.rootSeed = rootSeed;
    }

    // siteTag must be a stable identifier for this specific call (e.g. the model element's own
    // id) so that repeated calls within one replication (once per subgraph, once per subgraph
    // link, ...) get independent seeds instead of all drawing from the same stream.
    protected SimulationLifecycleAwareValueProvider<Long> createLatencyValueProvider(
            LinkLatencySpecification latencySpecification, String siteTag) {
        if (latencySpecification instanceof StaticLinkLatencySpecification s) {
            return new StaticLatencyValueProvider(s.getLatency());
        }
        if (latencySpecification instanceof DynamicLinkLatencySpecification d) {
            return LatencyValueProviderAdapter.create(d, DeterministicSeeds.seededGenerator(rootSeed, siteTag));
        }
        throw new IllegalArgumentException("Unsupported latency specification type: " + latencySpecification.getClass().getName());
    }

    protected SimulationLifecycleAwareValueProvider<Long> createThroughputValueProvider(
            LinkThroughputSpecification throughputSpecification, String siteTag) {
        if (simulationParameters.getNetworkBandwidth() > 0.0) {
            long bps = (long) (simulationParameters.getNetworkBandwidth() * 1_000_000.0);
            return new StaticThroughputValueProvider(bps);
        }
        if (throughputSpecification instanceof StaticLinkThroughputSpecification s) {
            return new StaticThroughputValueProvider(s.getThroughput());
        }
        if (throughputSpecification instanceof DynamicLinkThroughputSpecification d) {
            return ThroughputValueProviderAdapter.create(d, DeterministicSeeds.seededGenerator(rootSeed, siteTag));
        }
        throw new IllegalArgumentException("Unsupported throughput specification type: " + throughputSpecification.getClass().getName());
    }
}
