package org.palladiosimulator.blockchainsystems.threesim.creation;

import org.palladiosimulator.blockchainsystems.bscm.linkallocation.DynamicLinkThroughputSpecification;
import org.palladiosimulator.blockchainsystems.bscm.linkallocation.DynamicLinkThroughputSpecificationValue;
import org.palladiosimulator.blockchainsystems.core.network.LinkThroughput;
import org.palladiosimulator.blockchainsystems.core.utils.RandomValueProvider;
import org.palladiosimulator.blockchainsystems.threesim.creation.abstractions.TemporalValueProviderAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

public class ThroughputValueProviderAdapter extends TemporalValueProviderAdapter<Long, LinkThroughput> {

    private ThroughputValueProviderAdapter(RandomValueProvider<LinkThroughput> randomValueProvider) {
        super(randomValueProvider);
    }

    public static ThroughputValueProviderAdapter create(
            DynamicLinkThroughputSpecification spec, RandomGenerator randomGenerator) {
        Map<LinkThroughput, Double> map = new HashMap<>();
        for (DynamicLinkThroughputSpecificationValue v : spec.getValues()) {
            map.put(new LinkThroughput(v.getThroughput(), v.getDuration()), v.getProbability());
        }
        return new ThroughputValueProviderAdapter(RandomValueProvider.create(map, randomGenerator));
    }
}
