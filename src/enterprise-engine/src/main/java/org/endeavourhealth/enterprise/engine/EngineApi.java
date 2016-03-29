package org.endeavourhealth.enterprise.engine;

import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;

public class EngineApi {

    public EngineApi(EntityMapWrapper.EntityMap entityMap) {

    }

    public Processor createProcessor() {
        return new Processor();
    }
}
