package org.endeavourhealth.enterprise.engine.execution.listreports;

import java.util.UUID;

public class FileReference {
    private final UUID jobReportItemUuid;
    private final int listReportGroupId;
    private final FileContentBuilder fileContentBuilder = new FileContentBuilder();

    public FileReference(UUID jobReportItemUuid, int listReportGroupId) {
        this.jobReportItemUuid = jobReportItemUuid;
        this.listReportGroupId = listReportGroupId;
    }

    public UUID getJobReportItemUuid() {
        return jobReportItemUuid;
    }

    public int getListReportGroupId() {
        return listReportGroupId;
    }

    public FileContentBuilder getFileContentBuilder() {
        return fileContentBuilder;
    }
}
