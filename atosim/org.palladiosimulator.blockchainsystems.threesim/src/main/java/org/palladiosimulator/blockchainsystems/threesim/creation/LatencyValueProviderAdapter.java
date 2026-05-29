package org.palladiosimulator.blockchainsystems.threesim.creation;

import org.palladiosimulator.blockchainsystems.bscm.linkallocation.DynamicLinkLatencySpecification;
import org.palladiosimulator.blockchainsystems.bscm.linkallocation.DynamicLinkLatencySpecificationValue;
import org.palladiosimulator.blockchainsystems.core.network.LinkLatency;
import org.palladiosimulator.blockchainsystems.core.utils.RandomValueProvider;
import org.palladiosimulator.blockchainsystems.threesim.creation.abstractions.TemporalValueProviderAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

public class LatencyValueProviderAdapter extends TemporalValueProviderAdapter<Long, LinkLatency> {

    private LatencyValueProviderAdapter(RandomValueProvider<LinkLatency> randomValueProvider) {
        super(randomValueProvider);
    }

    public static LatencyValueProviderAdapter create(
            DynamicLinkLatencySpecification spec, RandomGenerator randomGenerator) {
        Map<LinkLatency, Double> map = new HashMap<>();
        for (DynamicLinkLatencySpecificationValue v : spec.getValues()) {
            map.put(new LinkLatency(v.getLatency(), v.getDuration()), v.getProbability());
        }
        return new LatencyValueProviderAdapter(RandomValueProvider.create(map, randomGenerator));
    }
}
