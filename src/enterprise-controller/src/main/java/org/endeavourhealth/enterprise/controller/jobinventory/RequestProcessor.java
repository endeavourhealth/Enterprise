package org.endeavourhealth.enterprise.controller.jobinventory;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.endeavourhealth.enterprise.core.database.definition.DbItem;
import org.endeavourhealth.enterprise.core.database.execution.DbRequest;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.Report;
import org.endeavourhealth.enterprise.core.querydocument.models.ReportItem;
import org.endeavourhealth.enterprise.enginecore.InvalidQueryDocumentException;

import java.util.*;

class RequestProcessor {

    public static JobReportInfo createJobReportInfo(DbRequest request, JobContentRetriever jobContentRetriever) throws Exception {

        try {

            DbItem dbItem = DbItem.retrieveForUuidAndAudit(request.getReportUuid(), jobContentRetriever.getAuditUuid(request.getReportUuid()));

            String itemXml = dbItem.getXmlContent();
            Report report = QueryDocumentSerializer.readReportFromXml(itemXml);

            if (CollectionUtils.isEmpty(report.getReportItem()))
                throw new InvalidQueryDocumentException(request.getReportUuid(), "No ReportItems");

            JobReportInfo jobReportInfo = new JobReportInfo(request);

            List<JobReportItemInfo> children = createJobReportItemInfoList(report.getReportItem(), jobContentRetriever);
            jobReportInfo.getChildren().addAll(children);

            return jobReportInfo;

        } catch (Exception e) {
            throw new Exception("Error processing report: " + request.getReportUuid());
        }
    }

    private static List<JobReportItemInfo> createJobReportItemInfoList(
            List<ReportItem> reportItems,
            JobContentRetriever jobContentRetriever) throws Exception {

        List<JobReportItemInfo> result = new ArrayList<>();

        if (CollectionUtils.isEmpty(reportItems))
            return result;

        for (ReportItem item: reportItems) {
            UUID libraryItemUuid = getLibraryItemUuidFromReportItem(item);
            JobReportItemInfo target = new JobReportItemInfo(libraryItemUuid);

            if (jobContentRetriever.isListReport(libraryItemUuid))
                target.setListReportInfo(new ListReportInfo());

            result.add(target);

            List<JobReportItemInfo> children = createJobReportItemInfoList(item.getReportItem(), jobContentRetriever);
            target.getChildren().addAll(children);
        }

        return result;
    }

    private static UUID getLibraryItemUuidFromReportItem(ReportItem item) {
        String stringUuid;

        if (StringUtils.isNotEmpty(item.getListReportLibraryItemUuid()))
            stringUuid = item.getListReportLibraryItemUuid();
        else
            stringUuid = item.getQueryLibraryItemUuid();

        return UUID.fromString(stringUuid);
    }
}
