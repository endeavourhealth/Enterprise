package org.endeavourhealth.enterprise.engine.compiled.fieldTests;

public class Code implements ICompiledFieldTest {
    private String comparisonValue;

    public Code(String comparisonValue) {
        this.comparisonValue = comparisonValue;
    }

    @Override
    public boolean test(Object value) {
        if (value == null)
            return false;
        else
            return comparisonValue.equals(value);
    }
}
