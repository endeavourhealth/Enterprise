package org.endeavourhealth.enterprise.controller.jobinventory;

import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.endeavourhealth.enterprise.core.database.execution.DbRequest;

import java.util.*;

public class JobInventory {

    private JobContentRetriever jobContentRetriever;
    private final List<JobReportInfo> jobReportInfoList = new ArrayList<>();
    private final HashMap<UUID, String> libraryItemUuidToNameMap = new HashMap<>();

    public void initialise(List<DbRequest> dbRequests) throws Exception {

        jobContentRetriever = new JobContentRetriever(dbRequests);

        for (DbRequest request: dbRequests) {

            JobReportInfo jobReportInfo = RequestProcessor.createJobReportInfo(request, jobContentRetriever);
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

    public String getItemName(UUID libraryItemUuid) throws Exception {
        if (libraryItemUuidToNameMap.containsKey(libraryItemUuid))
            return libraryItemUuidToNameMap.get(libraryItemUuid);

        DbItem item = DbItem.retrieveForUuidAndAudit(libraryItemUuid, getItemsAuditUuid(libraryItemUuid));
        return item.getTitle();
    }
}
