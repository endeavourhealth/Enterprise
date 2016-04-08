package org.endeavourhealth.enterprise.processornode;

import org.endeavourhealth.enterprise.engine.Processor;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RunnableItem implements Runnable{

    private final static Logger logger = LoggerFactory.getLogger(RunnableItem.class);

    private final DataContainer dataContainer;
    private final ExecutionContext executionContext;

    public RunnableItem(
            DataContainer dataContainer,
            ExecutionContext executionContext) {

        this.dataContainer = dataContainer;
        this.executionContext = executionContext;
    }

    @Override
    public void run() {

        try {

            logger.trace("Processing ID: " + dataContainer.getId());

            Processor processor = executionContext.getEngineProcessorPool().acquire();
            processor.process(dataContainer);
            executionContext.getEngineProcessorPool().recycle(processor);
            executionContext.getDataContainerPool().recycle(dataContainer);

        } catch (Exception e) {
            throw new RuntimeException("Error processing patient: " + dataContainer.getId(), e);
        }
    }
}
