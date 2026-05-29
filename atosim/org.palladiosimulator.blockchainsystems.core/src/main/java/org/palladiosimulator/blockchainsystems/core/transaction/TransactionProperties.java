package org.palladiosimulator.blockchainsystems.core.transaction;

public class TransactionProperties {

    private final int _size;
    private final double _fee;
    private final double _amount;

    public TransactionProperties(int size, double fee, double amount) {
        _size = size;
        _fee = fee;
        _amount = amount;
    }

    public int getSize() { return _size; }
    public double getFee() { return _fee; }
    public double getAmount() { return _amount; }
}
