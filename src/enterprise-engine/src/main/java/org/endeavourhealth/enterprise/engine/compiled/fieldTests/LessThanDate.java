package org.endeavourhealth.enterprise.engine.compiled.fieldTests;

import java.time.LocalDate;

public class LessThanDate implements ICompiledFieldTest {

    private final LocalDate comparisonValue;

    public LessThanDate(LocalDate comparisonValue) {
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
            return comparisonValue.isAfter((LocalDate)value);
    }
}
