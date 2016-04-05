package org.endeavourhealth.enterprise.engine.compiled.fieldTests;

public class RangeDate implements ICompiledFieldTest {

    private final GreaterThanDate from;
    private final LessThanDate to;

    public RangeDate(GreaterThanDate from, LessThanDate to) {
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