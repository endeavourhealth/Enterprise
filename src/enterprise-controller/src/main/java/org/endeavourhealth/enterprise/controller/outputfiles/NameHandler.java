package org.endeavourhealth.enterprise.controller.outputfiles;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
        return DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                .withZone(ZoneOffset.UTC)
                .format(startDateTime);
    }

    public void buildJobFolder() throws Exception {
        //String folderName = "Temporary_" + formatStartDateTimeForName();
        String folderName = "Temporary";
        String fullFolderName = rootDirectory + "/" + folderName;
        temporaryFolder = new File(fullFolderName);
        FileHelper.makeDirectory(temporaryFolder);
    }

    public Path getTestFile() {
        String folderName = "Temporary";
        return Paths.get(rootDirectory, folderName, "Test.csv");
    }

    public void renameTemporaryFolder() throws Exception {
        String newFolderName = "Job_" + formatStartDateTimeForName();
        FileHelper.renameDirectory(temporaryFolder, newFolderName);
    }
}
