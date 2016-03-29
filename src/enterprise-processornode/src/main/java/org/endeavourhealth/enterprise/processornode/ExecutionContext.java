package org.endeavourhealth.enterprise.processornode;

import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainerPool;

class ExecutionContext {

    private final EngineProcessorPool engineProcessorPool;
    private final DataContainerPool dataContainerPool;

    public ExecutionContext(EngineProcessorPool engineProcessorPool, DataContainerPool dataContainerPool) {

        this.engineProcessorPool = engineProcessorPool;
        this.dataContainerPool = dataContainerPool;
    }

    public EngineProcessorPool getEngineProcessorPool() {
        return engineProcessorPool;
    }

    public DataContainerPool getDataContainerPool() {
        return dataContainerPool;
    }
}
