package org.endeavourhealth.enterprise.engine.testhelpers;

import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;

public class FieldTestBuilder {

    private final EntityMapWrapper.EntityMap entityMap;
    private FieldTest fieldTest = new FieldTest();

    public FieldTestBuilder(EntityMapWrapper.EntityMap entityMap) {

        this.entityMap = entityMap;
    }

    public FieldTestBuilder setField(int entityId, int fieldId) {
        String fieldName = entityMap.getEntities().get(entityId).getField(fieldId).getLogicalName();
        fieldTest.setField(fieldName);

        return this;
    }

    public FieldTestBuilder setFieldTest(ValueFrom value) {
        fieldTest.setValueFrom(value);
        return this;
    }

    public FieldTestBuilder setFieldTest(ValueTo value) {
        fieldTest.setValueTo(value);
        return this;
    }

    public FieldTestBuilder setFieldTest(ValueRange value) {
        fieldTest.setValueRange(value);
        return this;
    }

    public FieldTestBuilder negate() {
        fieldTest.setNegate(true);
        return this;
    }

    public FieldTest build() {
        return fieldTest;
    }
}
