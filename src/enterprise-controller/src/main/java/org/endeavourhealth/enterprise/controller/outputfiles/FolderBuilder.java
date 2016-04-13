package org.endeavourhealth.enterprise.controller.outputfiles;

import org.endeavourhealth.enterprise.controller.configuration.models.OutputFilesType;
import org.endeavourhealth.enterprise.controller.jobinventory.JobInventory;
import org.endeavourhealth.enterprise.controller.jobinventory.JobReportInfo;
import org.endeavourhealth.enterprise.controller.jobinventory.JobReportItemInfo;
import org.endeavourhealth.enterprise.core.FileHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

class FolderBuilder {
    private final JobInventory jobInventory;
    private final List<Folder> folders;
    private final OutputFilesType configuration;
    private final UUID jobUuid;
    private final Instant startDateTime;
    private Path temporaryJobFolder;

    public FolderBuilder(
            JobInventory jobInventory,
            List<Folder> folders,
            OutputFilesType configuration,
            UUID jobUuid,
            Instant startDateTime) {

        this.jobInventory = jobInventory;
        this.folders = folders;
        this.configuration = configuration;
        this.jobUuid = jobUuid;
        this.startDateTime = startDateTime;
    }

    public void build() throws Exception {

        for (JobReportInfo jobReportInfo : jobInventory.getJobReportInfoList()) {

            if (!hasChildListReport(jobReportInfo.getChildren()))
                continue;

            Folder folder = new Folder(jobReportInfo);
            folders.add(folder);
            prepareFolders(folder, jobReportInfo.getChildren());
        }

        calculatePreferredFolderNames();
        createFolders();
    }

    private void prepareFolders(Folder parentFolder, List<JobReportItemInfo> items) {

        if (items == null)
            return;

        for (JobReportItemInfo item: items) {

            if (!hasChildListReport(item.getChildren()))
                continue;

            Folder folder = new Folder(item);
            parentFolder.getChildren().add(folder);

            prepareFolders(folder, item.getChildren());
        }
    }

    private boolean hasChildListReport(List<JobReportItemInfo> items) {

        if (items == null)
            return false;

        for (JobReportItemInfo item: items) {
            if (item.getListReportInfo() != null)
                return true;

            boolean result = hasChildListReport(item.getChildren());

            if (result)
                return true;
        }

        return false;
    }


    private void createFolders() throws Exception {

        if (FileHelper.pathNotExists(configuration.getStreamingFolder()))
            throw new Exception("Streaming folder does not exist: " + configuration.getStreamingFolder());

        Path completeStreamingFolder = Paths.get(configuration.getStreamingFolder(), jobUuid.toString());
        FileHelper.createFolder(completeStreamingFolder);

        if (FileHelper.pathNotExists(configuration.getWorkingFolder()))
            throw new Exception("Working folder does not exist: " + configuration.getWorkingFolder());

        Path completeWorkingFolder = Paths.get(configuration.getWorkingFolder(), jobUuid.toString());
        FileHelper.createFolder(completeWorkingFolder);

        if (FileHelper.pathNotExists(configuration.getTargetFolder()))
            throw new Exception("Target folder does not exist: " + configuration.getTargetFolder());

        String jobFolderName = NameHandler.calculateJobName(startDateTime);
        Path branchPath = Paths.get(jobFolderName);
        Path workingFolder = Paths.get(configuration.getWorkingFolder());
        temporaryJobFolder = workingFolder.resolve(branchPath);

        FileHelper.createFolder(temporaryJobFolder);

        addBranchPaths(workingFolder, branchPath, folders);
        setFileInfo(folders);
    }

    private void setFileInfo(List<Folder> folders) throws Exception {
        for (Folder folder: folders) {

            if (folder.getJobReportInfo() != null) {
                setFileInfo(folder, folder.getJobReportInfo().getChildren());
            } else if (folder.getJobReportItemInfo() != null) {
                setFileInfo(folder, folder.getJobReportItemInfo().getChildren());
            }

            setFileInfo(folder.getChildren());
        }
    }

    private void setFileInfo(Folder folder, List<JobReportItemInfo> items) throws Exception {
        if (items == null)
            return;

        for (JobReportItemInfo item: items) {
            if (item.getListReportInfo() != null)
                setFileInfo(folder, item);
        }
    }

    private void setFileInfo(Folder folder, JobReportItemInfo item) throws Exception {
        if (item.getListReportInfo() == null)
            throw new Exception("JobReportItemInfo does not contain list report information");

        item.getListReportInfo().setFolderBranch(folder.getFolderPathBranch());
        item.getListReportInfo().setRootName(jobInventory.getItemName(item.getLibraryItemUuid()));
    }

    private void addBranchPaths(Path rootFolder, Path parentBranch, List<Folder> folders) throws IOException {

        for (Folder folder: folders) {
            Path path = parentBranch.resolve(folder.getPreferredFolderName());
            folder.setFolderPathBranch(path);

            Path fullPath = rootFolder.resolve(path);
            FileHelper.createFolder(fullPath);

            addBranchPaths(rootFolder, path, folder.getChildren());
        }
    }

    private void calculatePreferredFolderNames() throws Exception {

        calculatePreferredFolderNames(folders);
    }

    private void calculatePreferredFolderNames(List<Folder> folders) throws Exception {

        for (Folder folder: folders) {
            folder.setPreferredFolderName(jobInventory);
            calculatePreferredFolderNames(folder.getChildren());
        }
    }

    public Path getTemporaryJobFolder() {
        return temporaryJobFolder;
    }
}
