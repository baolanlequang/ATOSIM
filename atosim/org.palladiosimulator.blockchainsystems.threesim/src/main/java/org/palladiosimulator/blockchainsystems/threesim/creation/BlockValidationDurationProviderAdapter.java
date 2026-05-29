package org.palladiosimulator.blockchainsystems.threesim.creation;

import org.palladiosimulator.blockchainsystems.bscm.blockchainsystemComponentRepository.BlockValiationDurationSpecification;
import org.palladiosimulator.blockchainsystems.bscm.blockchainsystemComponentRepository.BlockValidationDurationValue;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.ValueProvider;
import org.palladiosimulator.blockchainsystems.core.utils.RandomValueProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

public class BlockValidationDurationProviderAdapter implements ValueProvider<Long> {

    private final RandomValueProvider<Long> _randomValueProvider;

    private BlockValidationDurationProviderAdapter(RandomValueProvider<Long> randomValueProvider) {
        _randomValueProvider = randomValueProvider;
    }

    @Override
    public Long getValue() {
        return _randomValueProvider.getValue();
    }

    public static BlockValidationDurationProviderAdapter create(
            BlockValiationDurationSpecification spec, RandomGenerator randomGenerator) {
        Map<Long, Double> map = new HashMap<>();
        for (BlockValidationDurationValue v : spec.getValues()) {
            map.put(v.getDuration(), v.getProbability());
        }
        return new BlockValidationDurationProviderAdapter(RandomValueProvider.create(map, randomGenerator));
    }
}
