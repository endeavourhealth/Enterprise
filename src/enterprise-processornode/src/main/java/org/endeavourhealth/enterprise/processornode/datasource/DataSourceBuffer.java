package org.endeavourhealth.enterprise.processornode.datasource;

import org.endeavourhealth.enterprise.enginecore.carerecord.CareRecordDal;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;
import org.endeavourhealth.enterprise.processornode.ShortLivedThreadWrapper;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class DataSourceBuffer implements DataSourceRetriever.DataSourceRetrieverFinishedCallback {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(DataSourceBuffer.class);

    private final DataSourceQueue dataSourceBufferQueue = new DataSourceQueue();
    private final int dataItemBufferSize;
    private final int dataItemBufferTriggerSize;

    private final ShortLivedThreadWrapper<DataSourceRetriever> retrieverWrapper;

    private long maximumId;
    private long nextStartingId;
    private boolean sourceExhausted = true;
    private boolean shutdown;

    public DataSourceBuffer(
            CareRecordDal careRecordDal,
            int dataItemBufferSize,
            int dataItemBufferTriggerSize,
            Set<Long> dataItemsToProcess) {

        this.dataItemBufferSize = dataItemBufferSize;
        this.dataItemBufferTriggerSize = dataItemBufferTriggerSize;

        DataSourceRetriever retriever = new DataSourceRetriever(this, careRecordDal, dataSourceBufferQueue, dataItemsToProcess);
        retrieverWrapper = new ShortLivedThreadWrapper<>(retriever);
    }

    public DataContainer poll() throws InterruptedException {

        if (shutdown)
            return null;

        if (isLow())
            requestDataSourceBufferPopulated();

        DataContainer patientContainer = dataSourceBufferQueue.poll();

        while (patientContainer == null) {

            if (shutdown)
                break;

            if (sourceExhausted)
                break;

            requestDataSourceBufferPopulated();
            Thread.sleep(1000);
            patientContainer = dataSourceBufferQueue.poll();
        }

        return patientContainer;
    }

    private String ids;

    private void requestDataSourceBufferPopulated() {

        if (shutdown)
            return;

        if (sourceExhausted)
            return;

        if (!retrieverWrapper.tryLock())
            return;

        logger.trace("Data retriever started");

        long from = nextStartingId;
        long to = from + dataItemBufferSize - 1;

        if (to > maximumId)
            to = maximumId;

        nextStartingId = to + 1;

        if (nextStartingId >= maximumId)
            sourceExhausted = true;

        retrieverWrapper.getItem().setParameters(from, to);
        retrieverWrapper.start();
    }

    private boolean isLow() {
        return dataSourceBufferQueue.size() < dataItemBufferTriggerSize;
    }

    @Override
    public void finishedRetrievingData() {
        logger.trace("Data retriever stopped");
        retrieverWrapper.unlock();
    }

    public void setParameters(long minimumId, long maximumId) {
        this.maximumId = maximumId;
        nextStartingId = minimumId;
        sourceExhausted = false;
    }

    public void shutdown() throws InterruptedException {
        shutdown = true;
        retrieverWrapper.shutdown();
    }
}
