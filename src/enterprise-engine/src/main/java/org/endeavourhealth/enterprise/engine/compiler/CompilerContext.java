package org.endeavourhealth.enterprise.engine.compiler;

import org.endeavourhealth.enterprise.engine.compiled.CompiledLibrary;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;

import java.util.UUID;

public class CompilerContext {
    private final EntityMapWrapper.EntityMap entityMapWrapper;
    private final CompiledLibrary compiledLibrary;

    public CompilerContext(EntityMapWrapper.EntityMap entityMapWrapper, CompiledLibrary compiledLibrary) {
        this.entityMapWrapper = entityMapWrapper;
        this.compiledLibrary = compiledLibrary;
    }

    public EntityMapWrapper.EntityMap getEntityMapWrapper() {
        return entityMapWrapper;
    }

    public CompiledLibrary getCompiledLibrary() {
        return compiledLibrary;
    }
}
