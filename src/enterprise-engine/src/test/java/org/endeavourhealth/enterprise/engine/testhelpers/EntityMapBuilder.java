package org.endeavourhealth.enterprise.engine.testhelpers;

import org.endeavourhealth.enterprise.core.entitymap.models.Entity;
import org.endeavourhealth.enterprise.core.entitymap.models.EntityMap;
import org.endeavourhealth.enterprise.core.entitymap.models.Field;
import org.endeavourhealth.enterprise.core.entitymap.models.LogicalDataType;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;

import java.util.ArrayList;
import java.util.List;

public class EntityMapBuilder {

    private List<Entity> entityList = new ArrayList<>();

    public EntityMapBuilder addEntity(LogicalDataType... fieldTypes) {

        Entity entity = new Entity();
        String entityName = "ENTITY_" + entityList.size();
        entity.setLogicalName(entityName);

        for (LogicalDataType logicalDataType: fieldTypes) {
            Field field = new Field();
            String fieldName = "FIELD_" + entity.getField().size();
            field.setLogicalDataType(logicalDataType);

            field.setLogicalName(fieldName);

            entity.getField().add(field);
        }

        entityList.add(entity);
        return this;
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public EntityMapWrapper.EntityMap build() {

        EntityMap entityMap = new EntityMap();

        for (Entity entity: entityList) {
            entityMap.getEntity().add(entity);
        }

        return new EntityMapWrapper.EntityMap(entityMap);
    }

    public Field buildField() throws Exception {
        if (entityList.size() != 1)
            throw new Exception("EntityList must contain a single entity");

        if (entityList.get(0).getField().size() != 1)
            throw new Exception("Entity must contain a single field");

        return entityList.get(0).getField().get(0);
    }
}
