package org.palladiosimulator.blockchainsystems.threesim.creation;

import org.palladiosimulator.blockchainsystems.bscm.transactions.TransactionPropertiesSpecification;
import org.palladiosimulator.blockchainsystems.bscm.transactions.TransactionPropertiesSpecificationValue;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.ValueProvider;
import org.palladiosimulator.blockchainsystems.core.transaction.TransactionProperties;
import org.palladiosimulator.blockchainsystems.core.utils.RandomValueProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

public class TransactionPropertiesValueProviderAdapter implements ValueProvider<TransactionProperties> {

    private final RandomValueProvider<TransactionProperties> _randomValueProvider;

    private TransactionPropertiesValueProviderAdapter(RandomValueProvider<TransactionProperties> randomValueProvider) {
        _randomValueProvider = randomValueProvider;
    }

    @Override
    public TransactionProperties getValue() {
        return _randomValueProvider.getValue();
    }

    public static TransactionPropertiesValueProviderAdapter create(
            TransactionPropertiesSpecification spec, RandomGenerator randomGenerator) {
        Map<TransactionProperties, Double> map = new HashMap<>();
        for (TransactionPropertiesSpecificationValue v : spec.getValues()) {
            map.put(new TransactionProperties(v.getSize(), v.getFee(), v.getAmount()), v.getProbability());
        }
        return new TransactionPropertiesValueProviderAdapter(RandomValueProvider.create(map, randomGenerator));
    }
}
