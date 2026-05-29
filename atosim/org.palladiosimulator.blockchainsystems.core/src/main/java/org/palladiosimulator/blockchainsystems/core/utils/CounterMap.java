package org.palladiosimulator.blockchainsystems.core.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CounterMap<T> {

    private final Map<T, Integer> _map = new HashMap<>();

    public void initialize(Collection<T> keys) {
        for (T key : keys) {
            _map.put(key, 0);
        }
    }

    public synchronized void increment(T key) {
        _map.put(key, _map.getOrDefault(key, 0) + 1);
    }

    public synchronized void decrement(T key) {
        if (_map.containsKey(key)) {
            int newValue = _map.get(key) - 1;
            if (newValue <= 0) {
                _map.remove(key);
            } else {
                _map.put(key, newValue);
            }
        }
    }

    public void put(T key, int value) {
        if (value <= 0) {
            _map.remove(key);
        } else {
            _map.put(key, value);
        }
    }

    public int get(T key) {
        return _map.getOrDefault(key, 0);
    }

    public Map<T, Integer> getAll() {
        return _map;
    }

    public Collection<Integer> getValues() {
        return _map.values();
    }

    public Set<T> getKeys() {
        return _map.keySet();
    }

    public static <T> CounterMap<T> create(Collection<T> keys) {
        CounterMap<T> counterMap = new CounterMap<>();
        counterMap.initialize(keys);
        return counterMap;
    }
}
