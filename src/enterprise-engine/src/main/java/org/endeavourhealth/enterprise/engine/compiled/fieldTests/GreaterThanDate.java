package org.endeavourhealth.enterprise.engine.compiled.fieldTests;

import java.time.LocalDate;

public class GreaterThanDate implements ICompiledFieldTest {

    private final LocalDate comparisonValue;

    public GreaterThanDate(LocalDate comparisonValue) {
        this.comparisonValue = comparisonValue;
    }

    public LocalDate getComparisonValue() {
        return comparisonValue;
    }

    @Override
    public boolean test(Object value) {
        if (value == null)
            return false;
        else
            return comparisonValue.isBefore((LocalDate)value);
    }
}
