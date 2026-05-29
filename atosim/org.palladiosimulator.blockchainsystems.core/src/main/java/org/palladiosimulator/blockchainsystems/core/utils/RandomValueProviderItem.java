package org.palladiosimulator.blockchainsystems.core.utils;

public class RandomValueProviderItem<T> {

    private final double _selectionValueBorder;
    private final T _value;

    public RandomValueProviderItem(double selectionValueBorder, T value) {
        _selectionValueBorder = selectionValueBorder;
        _value = value;
    }

    public double getSelectionValueBorder() {
        return _selectionValueBorder;
    }

    public T getValue() {
        return _value;
    }
}
