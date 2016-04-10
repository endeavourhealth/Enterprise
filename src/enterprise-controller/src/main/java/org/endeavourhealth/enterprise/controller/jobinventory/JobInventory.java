package org.endeavourhealth.enterprise.controller.jobinventory;

import org.endeavourhealth.enterprise.core.database.execution.DbRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class JobInventory {

    private JobContentRetriever jobContentRetriever;
    private final List<JobReportInfo> jobReportInfoList = new ArrayList<>();

    public void initialise(List<DbRequest> dbRequests) throws Exception {

        jobContentRetriever = new JobContentRetriever(dbRequests);

        for (DbRequest request: dbRequests) {

            JobReportInfo jobReportInfo = RequestProcessor.createJobReportInfo(request, this);
            jobReportInfoList.add(jobReportInfo);
        }
    }

    public List<JobReportInfo> getJobReportInfoList() {
        return jobReportInfoList;
    }

    public Set<UUID> getAllItemsUsed() {
        return jobContentRetriever.getAllItemsUsed();
    }

    public UUID getItemsAuditUuid(UUID itemUuid) {
        return jobContentRetriever.getAuditUuid(itemUuid);
    }
}
