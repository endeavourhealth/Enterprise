package org.endeavourhealth.enterprise.engine.compiled.fieldTests;

import java.math.BigDecimal;

public class GreaterThanDecimal implements ICompiledFieldTest {

    private final BigDecimal comparisonValue;
    private final boolean greaterThanOrEqualTo;

    public GreaterThanDecimal(BigDecimal comparisonValue, boolean greaterThanOrEqualTo) {
        this.comparisonValue = comparisonValue;
        this.greaterThanOrEqualTo = greaterThanOrEqualTo;
    }

    public BigDecimal getComparisonValue() {
        return comparisonValue;
    }

    @Override
    public boolean test(Object value) {
        if (value == null) {
            return false;
        } else if (greaterThanOrEqualTo) {
            int comparison = ((BigDecimal) value).compareTo(comparisonValue);
            return comparison > 0 || comparison == 0;
        } else {
            return ((BigDecimal) value).compareTo(comparisonValue) > 0;
        }
    }
}