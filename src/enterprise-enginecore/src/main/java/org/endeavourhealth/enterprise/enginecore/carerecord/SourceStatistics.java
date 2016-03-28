package org.endeavourhealth.enterprise.enginecore.carerecord;

public class SourceStatistics {
    private final int recordCount;
    private final long minimumId;
    private final long maximumId;

    public SourceStatistics(int recordCount, long minimumId, long maximumId) {
        this.recordCount = recordCount;
        this.minimumId = minimumId;
        this.maximumId = maximumId;
    }

    public final int getRecordCount() {
        return recordCount;
    }

    public final long getMinimumId() {
        return minimumId;
    }

    public final long getMaximumId() {
        return maximumId;
    }
}
