package org.endeavourhealth.enterprise.controller;

import org.apache.commons.lang3.StringUtils;
import org.endeavourhealth.enterprise.core.database.definition.DbActiveItem;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReportItem;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.Report;
import org.endeavourhealth.enterprise.core.querydocument.models.ReportItem;

import java.util.*;

class RequestProcessor {
    private final List<DbJobReportItem> dbJobReportItems = new ArrayList<>();
    private final Map<UUID, UUID> libraryItemToAudit = new HashMap<>();
    private final UUID jobReportUuid;

    public RequestProcessor(UUID jobReportUuid, UUID reportUuid, UUID auditUuid) throws Exception {

        this.jobReportUuid = jobReportUuid;
        DbItem dbItem = DbItem.retrieveForUuidAndAudit(reportUuid, auditUuid);
        String itemXml = dbItem.getXmlContent();
        Report report = QueryDocumentSerializer.readReportFromXml(itemXml);

        HashSet<UUID> uniqueListOfLibraryItems = getUniqueListOfLibraryItems(report);
        populateLibraryItemToAudit(uniqueListOfLibraryItems);

        processReportItems(report.getReportItem(), null);
    }

    private void processReportItems(List<ReportItem> reportItems, DbJobReportItem parent) throws Exception {

        if (reportItems == null)
            return;

        for (ReportItem item: reportItems) {
            DbJobReportItem dbReportItem = createDbJobReportItem(item, parent);
            dbJobReportItems.add(dbReportItem);
        }
    }

    private DbJobReportItem createDbJobReportItem(ReportItem item, DbJobReportItem parent) throws Exception {
        DbJobReportItem jobReportItem = new DbJobReportItem();
        jobReportItem.assignPrimaryUUid();

        UUID reportItemUuid = getLibraryItemUuidFromReportItem(item);

        jobReportItem.setJobReportUuid(jobReportUuid);
        jobReportItem.setItemUuid(reportItemUuid);
        jobReportItem.setAuditUuid(libraryItemToAudit.get(reportItemUuid));

        if (parent != null)
            jobReportItem.setParentJobReportItemUuid(parent.getJobReportItemUuid());

        return jobReportItem;
    }

    private void populateLibraryItemToAudit(HashSet<UUID> uniqueListOfLibraryItems) throws Exception {

        for (UUID uuid: uniqueListOfLibraryItems) {
            DbActiveItem activeItem = DbActiveItem.retrieveForItemUuid(uuid);
            libraryItemToAudit.put(uuid, activeItem.getAuditUuid());
        }
    }

    private HashSet<UUID> getUniqueListOfLibraryItems(Report report) {
        HashSet<UUID> set = new HashSet<>();

        addUniqueListOfLibraryItems(report.getReportItem(), set);

        return set;
    }

    private void addUniqueListOfLibraryItems(List<ReportItem> reportItems, HashSet<UUID> set) {

        if (reportItems == null)
            return;

        for (ReportItem item: reportItems) {
            UUID uuid = getLibraryItemUuidFromReportItem(item);

            if (!set.contains(uuid))
                set.add(uuid);

            addUniqueListOfLibraryItems(item.getReportItem(), set);
        }
    }

    private UUID getLibraryItemUuidFromReportItem(ReportItem item) {
        String stringGuid;

        if (StringUtils.isNotEmpty(item.getListReportLibraryItemUuid()))
            stringGuid = item.getListReportLibraryItemUuid();
        else
            stringGuid = item.getQueryLibraryItemUuid();

        return UUID.fromString(stringGuid);
    }

    public List<DbJobReportItem> getDbJobReportItems() {
        return dbJobReportItems;
    }
}
