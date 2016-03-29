package org.endeavourhealth.enterprise.processornode;

import org.endeavourhealth.enterprise.enginecore.carerecord.CareRecordDal;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

class DataSourceRetriever {

    private final static Logger logger = LoggerFactory.getLogger(DataSourceRetriever.class);

    private long maximumId;
    private long nextStartingId;
    private final long dataItemBufferSize;
    private final CareRecordDal careRecordDal;

    public DataSourceRetriever(long dataItemBufferSize, CareRecordDal careRecordDal) {
        this.dataItemBufferSize = dataItemBufferSize;
        this.careRecordDal = careRecordDal;
    }

    public void setBatch(long minimumId, long maximumId) {
        this.nextStartingId = minimumId;
        this.maximumId = maximumId;
    }

    public Collection<DataContainer> getRecords() throws Exception {

        while (true) {

            if (isBatchComplete())
                return null;

            long from = nextStartingId;
            long to = from + dataItemBufferSize - 1;

            if (to > maximumId)
                to = maximumId;

            nextStartingId = to + 1;

            logger.trace("Getting records " + from + " to " + to);
            Map<Long, DataContainer> dataContainerMap = careRecordDal.getRecords(from, to);

            if (dataContainerMap.isEmpty()) {
                continue;
            } else {
                return dataContainerMap.values();
            }
        }
    }

    public boolean isBatchComplete() {
        return nextStartingId >= maximumId;
    }
}
