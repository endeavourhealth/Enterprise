package org.endeavourhealth.enterprise.controller.outputfiles;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.controller.configuration.models.OutputFilesType;
import org.endeavourhealth.enterprise.controller.jobinventory.JobInventory;
import org.endeavourhealth.enterprise.controller.jobinventory.JobReportInfo;
import org.endeavourhealth.enterprise.controller.jobinventory.JobReportItemInfo;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class OutputFileApi {

    private final OutputFilesType configuration;
    private final JobInventory jobInventory;
    private final List<JobReportItemInfo> listReports = new ArrayList<>();
    //private final NameHandler nameHandler;

    public OutputFileApi(
            OutputFilesType configuration,
            JobInventory jobInventory,
            Instant startDateTime) throws Exception {

        this.configuration = configuration;
        this.jobInventory = jobInventory;
        //this.nameHandler = new NameHandler(configuration.getRootFolder(), startDateTime);
    }

    public void prepareFiles() throws Exception {

        //nameHandler.buildJobFolder();

        Path path = Paths.get("H:\\Deletable\\", "Temp");
        File file = new File(path.toString());

        if (!file.mkdir())
            throw new Exception("Could not make path: " + path.toString());

        Path path2 = Paths.get("H:\\Deletable\\", "Temp", "Test.csv");
        Files.createFile(path2);

        try(  PrintWriter out = new PrintWriter( path2.toString() )  ){

            String header = "First,Second,Third";
            out.println(header);
        }
//
//        Files.createDirectories(path.getParent());
//
//        try {
//            Files.createFile(path);
//        } catch (FileAlreadyExistsException e) {
//            System.err.println("already exists: " + e.getMessage());
//        }
//
//        PrintWriter out = new PrintWriter("filename.txt");

//        Stack<String> folderStack = new Stack<>();
//
//        for (JobReportInfo jobReportInfo : jobInventory.getJobReportInfoList()) {
//
//            folderStack.push(jobReportInfo.getReportName());
//
//            prepareFolders(jobReportInfo.getChildren(), folderStack);
//
//            folderStack.pop();
//        }
//
//        if (noFiles())
//            return;
//
//        nameHandler.buildJobFolder();

    }

    private boolean noFiles() {
        return listReports.isEmpty();
    }

    private void prepareFolders(List<JobReportItemInfo> items, Stack<String> folderStack) {

//        if (CollectionUtils.isEmpty(items))
//            return;
//
//        for (JobReportItemInfo jobReportItemInfo: items) {
//
//            if (jobReportItemInfo.getListReportInfo() != null) {
//                jobReportItemInfo.getListReportInfo().setFolderStack(folderStack);
//                listReports.add(jobReportItemInfo);
//            }
//
//            if (hasListReportDescendent(jobReportItemInfo.getChildren())) {
//
//                String itemName = jobInventory.getLibraryItemName(jobReportItemInfo.getLibraryItemUuid());
//                folderStack.push(itemName);
//                prepareFolders(jobReportItemInfo.getChildren(), folderStack);
//                folderStack.pop();
//            }
//        }
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
        if (noFiles())
            return;

        //nameHandler.renameTemporaryFolder();
    }
}
