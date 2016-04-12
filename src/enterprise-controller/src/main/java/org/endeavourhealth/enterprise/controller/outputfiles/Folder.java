package org.endeavourhealth.enterprise.controller.outputfiles;

import org.endeavourhealth.enterprise.controller.jobinventory.JobInventory;
import org.endeavourhealth.enterprise.controller.jobinventory.JobReportInfo;
import org.endeavourhealth.enterprise.controller.jobinventory.JobReportItemInfo;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Folder {

    private final List<Folder> children = new ArrayList<>();
    private final JobReportInfo jobReportInfo;
    private final JobReportItemInfo jobReportItemInfo;
    private String preferredFolderName;
    private Path folderPathBranch;

    public Folder(JobReportInfo jobReportInfo) {

        this.jobReportInfo = jobReportInfo;
        this.jobReportItemInfo = null;
    }

    public Folder(JobReportItemInfo jobReportItemInfo) {

        this.jobReportItemInfo = jobReportItemInfo;
        this.jobReportInfo = null;
    }

    public List<Folder> getChildren() {
        return children;
    }

    public void setPreferredFolderName(JobInventory jobInventory) throws Exception {

        if (jobReportInfo != null)
            this.preferredFolderName = NameHandler.calculatePreferredName(jobReportInfo);
        else
            this.preferredFolderName = NameHandler.calculatePreferredName(jobReportItemInfo, jobInventory);
    }

    public String getPreferredFolderName() {
        return preferredFolderName;
    }

    public void setFolderPathBranch(Path folderPathBranch) {
        this.folderPathBranch = folderPathBranch;
    }

    public JobReportInfo getJobReportInfo() {
        return jobReportInfo;
    }

    public JobReportItemInfo getJobReportItemInfo() {
        return jobReportItemInfo;
    }

    public Path getFolderPathBranch() {
        return folderPathBranch;
    }
}
