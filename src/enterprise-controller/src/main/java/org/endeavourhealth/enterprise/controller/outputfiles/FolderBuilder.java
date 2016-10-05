package org.endeavourhealth.enterprise.controller.outputfiles;

import org.endeavourhealth.enterprise.controller.configuration.models.Configuration;
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
    private final OutputFileContext context;
    private final UUID jobUuid;
    private final Instant startDateTime;

    public FolderBuilder(
            JobInventory jobInventory,
            List<Folder> folders,
            OutputFileContext context,
            UUID jobUuid,
            Instant startDateTime) {

        this.jobInventory = jobInventory;
        this.folders = folders;
        this.context = context;
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

        Path streamingFolder = getAndValidatePath(context.getOutputFileConfiguration().getStreamingFolder());
        context.setJobStreamingFolder(streamingFolder.resolve(jobUuid.toString()));
        FileHelper.createFolder(context.getJobStreamingFolder());

        Path workingFolder = getAndValidatePath(context.getOutputFileConfiguration().getWorkingFolder());
        context.setJobWorkingFolder(workingFolder.resolve(jobUuid.toString()));
        FileHelper.createFolder(context.getJobWorkingFolder());

        Path targetFolder = getAndValidatePath(context.getOutputFileConfiguration().getTargetFolder());
        String jobFolderName = NameHandler.calculateJobName(startDateTime);
        context.setJobTargetFolder(targetFolder.resolve(jobFolderName));
        FileHelper.createFolder(context.getJobTargetFolder());

        Path branchPath = Paths.get(jobFolderName);
        addBranchPaths(branchPath, folders);
        setFileInfo(folders);
    }

    private Path getAndValidatePath(String path) throws Exception {
        Path pathObject = Paths.get(path);

        if (FileHelper.pathNotExists(pathObject))
            throw new Exception("Folder does not exist: " + pathObject.toString());

        return pathObject;
    }

    private void addBranchPaths(Path parentBranch, List<Folder> folders) throws IOException {

        for (Folder folder: folders) {
            Path path = parentBranch.resolve(folder.getPreferredFolderName());
            folder.setFolderPathBranch(path);

            Path fullPath = context.getJobWorkingFolder().resolve(path);
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
            if (item.getListReportInfo() != null) {
                item.getListReportInfo().setFolderBranch(folder.getFolderPathBranch());
                item.getListReportInfo().setRootName(jobInventory.getItemName(item.getLibraryItemUuid()));
            }
        }
    }
}
