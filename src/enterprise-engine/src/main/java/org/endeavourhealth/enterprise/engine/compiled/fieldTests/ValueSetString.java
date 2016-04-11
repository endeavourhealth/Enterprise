package org.endeavourhealth.enterprise.engine.compiled.fieldTests;

import java.util.Set;

public class ValueSetString implements ICompiledFieldTest{
    private final Set<String> allowedPhysicalValues;

    public ValueSetString(Set<String> allowedPhysicalValues) {
        this.allowedPhysicalValues = allowedPhysicalValues;
    }

    @Override
    public boolean test(Object value) {
        if (value == null)
            return false;
        else
            return allowedPhysicalValues.contains((String)value);
    }
}
