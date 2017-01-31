package org.endeavourhealth.enterprise.controller.outputfiles;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.controller.configuration.models.OutputFilesType;
import org.endeavourhealth.enterprise.controller.jobinventory.JobInventory;
import org.endeavourhealth.enterprise.controller.jobinventory.JobReportInfo;
import org.endeavourhealth.enterprise.controller.jobinventory.JobReportItemInfo;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OutputFileApi {

    private final OutputFilesType configuration;
    private final OutputFileContext context;
    private final JobInventory jobInventory;
    private final UUID jobUuid;
    private final Instant startDateTime;
    private List<JobReportItemInfo> listReports;
    private final List<Folder> folders = new ArrayList<>();

    private Path temporaryJobFolder;

    public OutputFileApi(
            OutputFilesType configuration,
            JobInventory jobInventory,
            UUID jobUuid,
            Instant startDateTime,
            OutputFileContext context) throws Exception {

        this.configuration = configuration;
        this.jobInventory = jobInventory;
        this.jobUuid = jobUuid;
        this.startDateTime = startDateTime;
        this.context = context;
    }

    public void prepareFiles() throws Exception {

        listReports = getListReports();

        if (noFiles())
            return;

        populateFolders();

        if (folders.isEmpty())
            throw new Exception("There are list reports but the folders field is empty");


    }


    private void populateFolders() throws Exception {

        FolderBuilder folderBuilder = new FolderBuilder(jobInventory, folders, context, jobUuid, startDateTime);
        folderBuilder.build();

        //temporaryJobFolder = folderBuilder.getTemporaryJobFolder(); TODO
    }

    private boolean noFiles() {
        return listReports.isEmpty();
    }

    private List<JobReportItemInfo> getListReports() {
        List<JobReportItemInfo> target = new ArrayList<>();

        for (JobReportInfo item: jobInventory.getJobReportInfoList()) {
            getListReports(item.getChildren(), target);
        }

        return target;
    }

    private void getListReports(List<JobReportItemInfo> items, List<JobReportItemInfo> target) {

        if (CollectionUtils.isEmpty(items))
            return;

        for (JobReportItemInfo item: items) {
            if (item.getListReportInfo() != null)
                target.add(item);

            getListReports(item.getChildren(), target);
        }
    }

    public void complete() throws Exception {
        if (noFiles())
            return;

        //nameHandler.renameTemporaryFolder();
    }
}
