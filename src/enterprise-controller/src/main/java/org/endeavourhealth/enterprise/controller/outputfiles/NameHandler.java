package org.endeavourhealth.enterprise.controller.outputfiles;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class NameHandler {

    private final String rootDirectory;
    private final Instant startDateTime;
    private File temporaryFolder;

    public NameHandler(String rootDirectory, Instant startDateTime) throws Exception {

        this.rootDirectory = rootDirectory;
        this.startDateTime = startDateTime;

        File file = new File(rootDirectory);
        FileHelper.checkDirectoryExists(file);
    }

    private String formatStartDateTimeForName() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        LocalDateTime localDateTime = LocalDateTime.from(startDateTime);
        return localDateTime.format(formatter);
    }

    public void buildJobFolder() throws Exception {
        String folderName = "Temporary_" + formatStartDateTimeForName();
        String fullFolderName = rootDirectory + "/" + folderName;
        temporaryFolder = new File(fullFolderName);
        FileHelper.makeDirectory(temporaryFolder);
    }

    public void renameTemporaryFolder() throws Exception {
        String newFolderName = "Job_" + formatStartDateTimeForName();
        FileHelper.renameDirectory(temporaryFolder, newFolderName);
    }
}
