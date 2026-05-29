package org.palladiosimulator.blockchainsystems.core.utils;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.ValueProvider;

import java.util.Map;
import java.util.TreeMap;
import java.util.random.RandomGenerator;

public class RandomValueProvider<T> implements ValueProvider<T> {

    private static final double RANDOM_VALUES_SUM_MAX_DEVIATION = 0.005;

    private final TreeMap<Integer, RandomValueProviderItem<T>> _items;
    private final RandomGenerator _randomGenerator;

    private RandomValueProvider(TreeMap<Integer, RandomValueProviderItem<T>> items, RandomGenerator randomGenerator) {
        _items = items;
        _randomGenerator = randomGenerator;
    }

    @Override
    public T getValue() {
        double selectionValue = _randomGenerator.nextDouble();

        int index = 0;
        double currentLowerBorder = 0.0;
        double currentUpperBorder = _items.get(0).getSelectionValueBorder();

        while (!isInRange(selectionValue, currentLowerBorder, currentUpperBorder) && indexIsInValidRange(index)) {
            currentLowerBorder = currentUpperBorder;
            currentUpperBorder = _items.get(index + 1).getSelectionValueBorder();
            index++;
        }

        return _items.get(index).getValue();
    }

    private boolean indexIsInValidRange(int index) {
        return index < _items.size() - 1;
    }

    private boolean isInRange(double value, double lowerBorder, double upperBorder) {
        return lowerBorder <= value && value < upperBorder;
    }

    public static <T> RandomValueProvider<T> create(Map<T, Double> randomValues, RandomGenerator generator) {
        if (randomValues.isEmpty()) {
            throw new IllegalArgumentException("The provided probabilities map must not be empty.");
        }

        double probabilitiesSum = randomValues.values().stream().mapToDouble(Double::doubleValue).sum();

        if (probabilitiesSum > 1.0) {
            throw new IllegalArgumentException("The sum of the provided probabilities is greater than one.");
        }

        double deviation = 1.0 - probabilitiesSum;
        if (deviation >= RANDOM_VALUES_SUM_MAX_DEVIATION) {
            throw new IllegalArgumentException(
                    "The sum of the provided probabilities deviates more than allowed deviation maximum (0.005).");
        }

        TreeMap<Integer, RandomValueProviderItem<T>> items = new TreeMap<>();
        int index = 0;
        double acc = 0.0;
        for (Map.Entry<T, Double> entry : randomValues.entrySet()) {
            acc += entry.getValue();
            items.put(index, new RandomValueProviderItem<>(acc, entry.getKey()));
            index++;
        }

        return new RandomValueProvider<>(items, generator);
    }
}
