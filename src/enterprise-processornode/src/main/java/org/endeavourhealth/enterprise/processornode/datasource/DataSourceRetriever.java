package org.endeavourhealth.enterprise.processornode.datasource;

import org.endeavourhealth.enterprise.enginecore.carerecord.CareRecordDal;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;

import java.util.Map;
import java.util.Set;

public class DataSourceRetriever implements Runnable {

    private DataSourceRetrieverFinishedCallback callback;
    private CareRecordDal careRecordDal;
    private DataSourceQueue buffer;
    private Set<Long> dataItemsToProcess;
    private long from;
    private long to;

    public interface DataSourceRetrieverFinishedCallback {
        void finishedRetrievingData();
    }

    public DataSourceRetriever(
            DataSourceRetrieverFinishedCallback callback,
            CareRecordDal careRecordDal,
            DataSourceQueue buffer,
            Set<Long> dataItemsToProcess) {

        this.callback = callback;
        this.careRecordDal = careRecordDal;
        this.buffer = buffer;
        this.dataItemsToProcess = dataItemsToProcess;
    }

    public void setParameters(long from, long to) {

        this.from = from;
        this.to = to;
    }

    @Override
    public void run() {

        try {
            Map<Long, DataContainer> dataContainerMap = careRecordDal.getRecords(from, to);

            for (long i = from; i <= to; i++) {
                if (dataContainerMap.containsKey(i)) {
                    buffer.add(dataContainerMap.get(i));
                } else {
                    dataItemsToProcess.remove(i);
                }
            }

            callback.finishedRetrievingData();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
