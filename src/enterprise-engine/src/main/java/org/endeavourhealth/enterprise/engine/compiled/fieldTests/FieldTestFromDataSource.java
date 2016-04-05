package org.endeavourhealth.enterprise.engine.compiled.fieldTests;

import org.endeavourhealth.enterprise.enginecore.entities.model.DataEntity;

public class FieldTestFromDataSource {
    private int fieldIndex;
    private ICompiledFieldTest filter;

    public FieldTestFromDataSource(ICompiledFieldTest filter, int fieldIndex) {
        this.filter = filter;
        this.fieldIndex = fieldIndex;
    }

    public boolean test(DataEntity entity, int valueIndex) {
        return filter.test(entity.getFields().get(fieldIndex).get(valueIndex));
    }
}
