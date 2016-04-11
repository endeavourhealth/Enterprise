package org.endeavourhealth.enterprise.engine.compiled.fieldTests;

import java.util.Set;

public class CodeFieldTest implements ICompiledFieldTest {
    private Set<Long> allowedValues;

    public CodeFieldTest(Set<Long> allowedValues) {
        this.allowedValues = allowedValues;
    }

    @Override
    public boolean test(Object value) {
        if (value == null)
            return false;
        else
            return allowedValues.contains((Long)value);
    }
}
