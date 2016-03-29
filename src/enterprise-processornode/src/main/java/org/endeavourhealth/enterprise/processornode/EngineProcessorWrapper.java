package org.endeavourhealth.enterprise.processornode;

import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;
import org.endeavourhealth.enterprise.engine.Processor;
import org.endeavourhealth.enterprise.processornode.datasource.IDataSourceFromEngine;

import java.io.IOException;

class EngineProcessorWrapper implements Runnable {

    private Processor engineProcessor;
    private IDataSourceFromEngine dataSource;

    public EngineProcessorWrapper(Processor engineProcessor, IDataSourceFromEngine dataSource) {

        this.engineProcessor = engineProcessor;
        this.dataSource = dataSource;
    }

    @Override
    public void run() {

        try {
            DataContainer dataContainer = dataSource.getNextDataContainer();

            while (dataContainer != null) {
                engineProcessor.process(dataContainer);
                dataSource.dataContainerProcessed(dataContainer);
                dataContainer = dataSource.getNextDataContainer();
            }

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
