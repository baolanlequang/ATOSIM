package org.palladiosimulator.blockchainsystems.threesim.creation.abstractions;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationContext;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.SimulationLifecycleAwareValueProvider;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TemporalValue;
import org.palladiosimulator.blockchainsystems.core.utils.RandomValueProvider;

public abstract class TemporalValueProviderAdapter<V, T extends TemporalValue<V>>
        implements SimulationLifecycleAwareValueProvider<V> {

    private final RandomValueProvider<T> _randomValueProvider;
    private SimulationContext _simulationContext;
    private V _lastValue;
    private Long _lastValueValidUntil;

    protected TemporalValueProviderAdapter(RandomValueProvider<T> randomValueProvider) {
        _randomValueProvider = randomValueProvider;
    }

    @Override
    public V getValue() {
        V currentValue = _lastValue;
        Long currentValidUntil = _lastValueValidUntil;
        long now = _simulationContext.getSystemClock().getCurrentTime();

        if (currentValue != null && currentValidUntil != null && currentValidUntil >= now) {
            return currentValue;
        }

        T newValue = _randomValueProvider.getValue();
        _lastValue = newValue.getValue();
        _lastValueValidUntil = now + newValue.getDuration();
        return newValue.getValue();
    }

    @Override
    public void initialize(SimulationContext simulationContext) {
        _simulationContext = simulationContext;
    }

    @Override
    public void cleanup() {
        _lastValue = null;
        _lastValueValidUntil = null;
    }
}
