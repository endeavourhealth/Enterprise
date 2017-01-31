package org.endeavourhealth.enterprise.enginecore.carerecord;

public class SourceStatistics {
    private final long recordCount;
    private final int minimumId;
    private final int maximumId;

    public SourceStatistics(long recordCount, int minimumId, int maximumId) {
        this.recordCount = recordCount;
        this.minimumId = minimumId;
        this.maximumId = maximumId;
    }

    public final long getRecordCount() {
        return recordCount;
    }

    public final int getMinimumId() {
        return minimumId;
    }

    public final int getMaximumId() {
        return maximumId;
    }
}
