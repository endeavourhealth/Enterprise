package org.endeavourhealth.enterprise.engine.compiled.fieldTests;

import java.math.BigDecimal;

public class LessThanDecimal implements ICompiledFieldTest {

    private final BigDecimal comparisonValue;
    private final boolean lessThanOrEqualTo;

    public LessThanDecimal(BigDecimal comparisonValue, boolean lessThanOrEqualTo) {
        this.comparisonValue = comparisonValue;
        this.lessThanOrEqualTo = lessThanOrEqualTo;
    }

    public BigDecimal getComparisonValue() {
        return comparisonValue;
    }

    @Override
    public boolean test(Object value) {
        if (value == null) {
            return false;
        } else if (lessThanOrEqualTo) {
            int comparison = ((BigDecimal) value).compareTo(comparisonValue);
            return comparison < 0 || comparison == 0;
        } else {
            return ((BigDecimal) value).compareTo(comparisonValue) < 0;
        }
    }
}