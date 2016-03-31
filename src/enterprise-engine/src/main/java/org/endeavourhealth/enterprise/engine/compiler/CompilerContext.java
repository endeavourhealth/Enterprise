package org.endeavourhealth.enterprise.engine.compiler;

import org.endeavourhealth.enterprise.enginecore.Library;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;

public class CompilerContext {
    private final EntityMapWrapper.EntityMap entityMapWrapper;
    private final Library requiredLibraryItems;

    public CompilerContext(EntityMapWrapper.EntityMap entityMapWrapper, Library requiredLibraryItems) {

        this.entityMapWrapper = entityMapWrapper;
        this.requiredLibraryItems = requiredLibraryItems;
    }

    public EntityMapWrapper.EntityMap getEntityMapWrapper() {
        return entityMapWrapper;
    }

    public Library getRequiredLibraryItems() {
        return requiredLibraryItems;
    }
}
