package org.endeavourhealth.enterprise.controller.outputfiles;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.controller.configuration.models.OutputFilesType;
import org.endeavourhealth.enterprise.controller.jobinventory.JobInventory;
import org.endeavourhealth.enterprise.controller.jobinventory.JobReportInfo;
import org.endeavourhealth.enterprise.controller.jobinventory.JobReportItemInfo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OutputFileApi {

    private final OutputFilesType configuration;
    private final JobInventory jobInventory;
    private final List<JobReportItemInfo> listReports = new ArrayList<>();
    private final NameHandler nameHandler;

    public OutputFileApi(
            OutputFilesType configuration,
            JobInventory jobInventory,
            Instant startDateTime) throws Exception {

        this.configuration = configuration;
        this.jobInventory = jobInventory;
        this.nameHandler = new NameHandler(configuration.getRootFolder(), startDateTime);
    }

    public void prepareFiles() throws Exception {

        for (JobReportInfo jobReportInfo : jobInventory.getJobReportInfoList()) {
            List<JobReportItemInfo> localListReports = getListReports(jobReportInfo.getChildren());

            if (CollectionUtils.isEmpty(localListReports))
                continue;;

            listReports.addAll(localListReports);

            String folderName = jobReportInfo.getJobReportUuid().toString();

            for (JobReportItemInfo localListReport : localListReports) {
                localListReport.getListReportInfo().setFolder(folderName);
            }
        }

        if (noFiles())
            return;

        nameHandler.buildJobFolder();
//
//        for (JobReportItemInfo listReport : listReports) {
//
//        }
    }

    private boolean noFiles() {
        return listReports.isEmpty();
    }

    private List<JobReportItemInfo> getListReports(List<JobReportItemInfo> items) {
        List<JobReportItemInfo> list = new ArrayList<>();
        addListReports(items, list);
        return list;
    }

    private void addListReports(List<JobReportItemInfo> items, List<JobReportItemInfo> list) {

        if (CollectionUtils.isEmpty(items))
            return;

        for (JobReportItemInfo item: items) {
            if (item.getListReportInfo() != null)
                list.add(item);

            addListReports(item.getChildren(), list);
        }
    }

    public void complete() throws Exception {
        nameHandler.renameTemporaryFolder();
    }
}
