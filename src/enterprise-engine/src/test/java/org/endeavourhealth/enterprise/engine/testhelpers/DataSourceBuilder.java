package org.endeavourhealth.enterprise.engine.testhelpers;

import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;


public class DataSourceBuilder {

    private final EntityMapWrapper.EntityMap entityMap;
    private DataSource dataSource = new DataSource();
    private EntityMapWrapper.Entity entity;

    public DataSourceBuilder(EntityMapWrapper.EntityMap entityMap) {
        this.entityMap = entityMap;
    }

    public DataSourceBuilder setEntity(int entityId) {
        entity = entityMap.getEntities().get(entityId);
        String name = entity.getSource().getLogicalName();
        dataSource.setEntity(name);
        return this;
    }

    public DataSourceBuilder addFilter(int fieldId, ValueFrom valueFrom) {
        FieldTest fieldTest = addFieldTest(fieldId);
        fieldTest.setValueFrom(valueFrom);
        return this;
    }

    public DataSourceBuilder setRestriction(int fieldId, OrderDirection direction, int count) {
        String fieldName = entity.getField(fieldId).getLogicalName();

        Restriction restriction = new Restriction();
        restriction.setCount(count);
        restriction.setFieldName(fieldName);
        restriction.setOrderDirection(direction);

        dataSource.setRestriction(restriction);
        return this;
    }

    private FieldTest addFieldTest(int fieldId) {
        String fieldName = entity.getField(fieldId).getLogicalName();
        FieldTest fieldTest = new FieldTest();
        fieldTest.setField(fieldName);
        dataSource.getFilter().add(fieldTest);
        return fieldTest;
    }

    public DataSource build() {
        return dataSource;
    }
}
