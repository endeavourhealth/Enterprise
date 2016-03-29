package org.endeavourhealth.enterprise.processornode.datasource;

import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;

import java.io.IOException;

public interface IDataSourceFromEngine {
    DataContainer getNextDataContainer() throws InterruptedException;
    void dataContainerProcessed(DataContainer item) throws IOException;
}
