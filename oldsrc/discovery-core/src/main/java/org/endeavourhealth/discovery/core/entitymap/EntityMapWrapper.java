package org.endeavourhealth.discovery.core.entitymap;

import java.util.ArrayList;
import java.util.List;

public class EntityMapWrapper {

    public static class EntityMapException extends Exception {
        public EntityMapException(String message) { super(message); }
        public EntityMapException(String message, Throwable cause) { super(message, cause); }
        public EntityMapException(Throwable cause) { super(cause); }
    }

    public static class EntityMap {

        private org.endeavourhealth.discovery.core.entitymap.EntityMap entityMap;
        private List<Entity> entities = new ArrayList<>();

        public EntityMap(org.endeavourhealth.discovery.core.entitymap.EntityMap source) {
            this.entityMap = source;

            for (org.endeavourhealth.discovery.core.entitymap.Entity s: source.getEntity()) {
                Entity target = new Entity(s);
                entities.add(target);
            }
        }

        public int getEntityIndex(String logicalName) throws EntityMapException {

            for (int i = 0; i < entityMap.getEntity().size(); i++) {
                if (entityMap.getEntity().get(i).getLogicalName().equals(logicalName))
                    return i;
            }

            throw new EntityMapException("EntityID not found: " + logicalName);
        }

        public Entity getEntity(int entityIndex) {
            return entities.get(entityIndex);
        }
    }

    public static class Entity {
        private org.endeavourhealth.discovery.core.entitymap.Entity source;

        public Entity(org.endeavourhealth.discovery.core.entitymap.Entity source) {
            this.source = source;
        }

        public int getFieldIndex(String logicalName) throws EntityMapException {

            for (int i = 0; i < source.getField().size(); i++) {
                if (source.getField().get(i).getLogicalName().equals(logicalName))
                    return i;
            }

            throw new EntityMapException(String.format("Field '%s' not found on table '%s'", logicalName, source.getLogicalName()));
        }

        public Field getField(int fieldIndex) {
            return source.getField().get(fieldIndex);
        }
    }
}
