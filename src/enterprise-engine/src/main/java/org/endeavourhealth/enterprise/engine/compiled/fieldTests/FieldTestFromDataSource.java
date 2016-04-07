package org.endeavourhealth.enterprise.engine.compiled.fieldTests;

import org.endeavourhealth.enterprise.engine.compiled.ICompiledDataSource;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataEntity;

public class FieldTestFromDataSource {
    private int fieldIndex;
    private ICompiledFieldTest filter;

    public FieldTestFromDataSource(ICompiledFieldTest filter, int fieldIndex) {
        this.filter = filter;
        this.fieldIndex = fieldIndex;
    }

    public boolean test(DataEntity entity, int rowIndex) {
        return filter.test(entity.getFields().get(fieldIndex).get(rowIndex));
    }

    public boolean test(ICompiledDataSource dataSource, int rowIndex) {
        return filter.test(dataSource.getValue(rowIndex, fieldIndex));
    }
}
