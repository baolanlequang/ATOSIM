package org.palladiosimulator.blockchainsystems.threesim.creation;

import org.palladiosimulator.blockchainsystems.core.system.abstractions.ResourcePowerCalculator;

import java.util.Set;

public class AttackAwareResourcePowerCalculator implements ResourcePowerCalculator {

    private final ResourcePowerCalculator _delegate;
    private final Set<String> _attackerNodeIds;
    private final double _attackerHashPower;

    private Double _totalBasePower;
    private Double _attackerBasePower;
    private Double _honestBasePower;

    public AttackAwareResourcePowerCalculator(ResourcePowerCalculator delegate,
            Set<String> attackerNodeIds, double attackerHashPower) {
        if (attackerHashPower < 0.0 || attackerHashPower > 1.0)
            throw new IllegalArgumentException("attackerHashPower must be in [0,1]");
        _delegate = delegate;
        _attackerNodeIds = attackerNodeIds;
        _attackerHashPower = attackerHashPower;
    }

    private double totalBasePower() {
        if (_totalBasePower == null) _totalBasePower = _delegate.calculateGlobalResourcePower();
        return _totalBasePower;
    }

    private double attackerBasePower() {
        if (_attackerBasePower == null) {
            double sum = 0;
            for (String id : _attackerNodeIds) {
                Double p = _delegate.getResourcePowerOfNode(id);
                if (p != null) sum += p;
            }
            _attackerBasePower = sum;
        }
        return _attackerBasePower;
    }

    private double honestBasePower() {
        if (_honestBasePower == null) _honestBasePower = totalBasePower() - attackerBasePower();
        return _honestBasePower;
    }

    @Override
    public Double getResourcePowerOfNode(String nodeId) {
        Double basePower = _delegate.getResourcePowerOfNode(nodeId);
        if (basePower == null) return null;
        boolean isAttacker = _attackerNodeIds.contains(nodeId);

        if (totalBasePower() == 0.0) return 0.0;

        if (_attackerHashPower == 0.0) {
            return isAttacker ? 0.0 : (honestBasePower() == 0.0 ? 0.0 : basePower * (totalBasePower() / honestBasePower()));
        }
        if (_attackerHashPower == 1.0) {
            return isAttacker ? (attackerBasePower() == 0.0 ? 0.0 : basePower * (totalBasePower() / attackerBasePower())) : 0.0;
        }

        if (_attackerHashPower > 0.0 && attackerBasePower() == 0.0)
            throw new IllegalStateException("Configured attackerHashPower=" + _attackerHashPower + " but attacker nodes have zero base resource power");
        if (_attackerHashPower < 1.0 && honestBasePower() == 0.0)
            throw new IllegalStateException("Configured attackerHashPower=" + _attackerHashPower + " but honest nodes have zero base resource power");

        double attackerScale = (_attackerHashPower * totalBasePower()) / attackerBasePower();
        double honestScale = ((1.0 - _attackerHashPower) * totalBasePower()) / honestBasePower();
        return isAttacker ? basePower * attackerScale : basePower * honestScale;
    }

    @Override
    public double calculateGlobalResourcePower() {
        return totalBasePower();
    }
}
