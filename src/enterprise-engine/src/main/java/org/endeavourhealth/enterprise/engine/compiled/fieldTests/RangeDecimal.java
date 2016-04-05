package org.endeavourhealth.enterprise.engine.compiled.fieldTests;

public class RangeDecimal implements ICompiledFieldTest {

    private final GreaterThanDecimal from;
    private final LessThanDecimal to;

    public RangeDecimal(GreaterThanDecimal from, LessThanDecimal to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean test(Object value) {
        if (value == null)
            return false;
        else
            return from.test(value) && to.test(value);
    }
}