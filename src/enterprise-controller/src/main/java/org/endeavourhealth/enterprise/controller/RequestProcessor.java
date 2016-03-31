package org.endeavourhealth.enterprise.controller;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.endeavourhealth.enterprise.core.database.execution.DbJobReportItem;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.Report;
import org.endeavourhealth.enterprise.core.querydocument.models.ReportItem;
import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException;

import java.util.*;

class RequestProcessor {
    private final List<DbJobReportItem> dbJobReportItems = new ArrayList<>();
    private final UUID jobReportUuid;
    private final JobContentRetriever jobContentRetriever;

    public RequestProcessor(UUID jobReportUuid, UUID reportUuid, JobContentRetriever jobContentRetriever) throws Exception {

        this.jobReportUuid = jobReportUuid;
        this.jobContentRetriever = jobContentRetriever;

        DbItem dbItem = DbItem.retrieveForUuidAndAudit(reportUuid, jobContentRetriever.getAuditUuid(reportUuid));

        String itemXml = dbItem.getXmlContent();
        Report report = QueryDocumentSerializer.readReportFromXml(itemXml);

        if (CollectionUtils.isEmpty(report.getReportItem()))
            throw new InvalidQueryDocumentException(reportUuid, "No ReportItems");

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
        jobReportItem.setAuditUuid(jobContentRetriever.getAuditUuid(reportItemUuid));

        if (parent != null)
            jobReportItem.setParentJobReportItemUuid(parent.getJobReportItemUuid());

        return jobReportItem;
    }

    private UUID getLibraryItemUuidFromReportItem(ReportItem item) {
        String stringUuid;

        if (StringUtils.isNotEmpty(item.getListReportLibraryItemUuid()))
            stringUuid = item.getListReportLibraryItemUuid();
        else
            stringUuid = item.getQueryLibraryItemUuid();

        return UUID.fromString(stringUuid);
    }

    public List<DbJobReportItem> getDbJobReportItems() {
        return dbJobReportItems;
    }
}
