package org.endeavourhealth.enterprise.controller.jobinventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JobReportItemInfo {
    private final UUID jobReportItemUuid = UUID.randomUUID();
    private final List<JobReportItemInfo> jobReportItemInfoList = new ArrayList<>();
    private final UUID libraryItemUuid;
    private ListReportInfo listReportInfo;

    public JobReportItemInfo(UUID libraryItemUuid) {
        this.libraryItemUuid = libraryItemUuid;
    }

    public List<JobReportItemInfo> getChildren() {
        return jobReportItemInfoList;
    }

    public UUID getJobReportItemUuid() {
        return jobReportItemUuid;
    }

    public UUID getLibraryItemUuid() {
        return libraryItemUuid;
    }

    public ListReportInfo getListReportInfo() {
        return listReportInfo;
    }

    public void setListReportInfo(ListReportInfo listReportInfo) {
        this.listReportInfo = listReportInfo;
    }
}
