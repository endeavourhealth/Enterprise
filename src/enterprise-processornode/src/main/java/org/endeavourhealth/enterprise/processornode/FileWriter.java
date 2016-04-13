package org.endeavourhealth.enterprise.processornode;

import com.jcraft.jsch.SftpException;
import org.endeavourhealth.enterprise.core.FileHelper;
import org.endeavourhealth.enterprise.core.FtpWrapper;
import org.endeavourhealth.enterprise.engine.Processor;
import org.endeavourhealth.enterprise.engine.execution.listreports.FileReference;
import org.endeavourhealth.enterprise.engine.execution.listreports.InMemoryFiles;
import org.endeavourhealth.enterprise.enginecore.communication.ProcessorNodesStartMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class FileWriter {

    private final ProcessorNodesStartMessage.StartMessagePayload startMessage;
    private final FtpWrapper.FtpConnectionDetails ftpConnectionDetails;
    private final static Logger logger = LoggerFactory.getLogger(FileWriter.class);

    public FileWriter(
            ProcessorNodesStartMessage.StartMessagePayload startMessage) {

        this.startMessage = startMessage;

        if (this.startMessage.getFtpConnectionDetails() == null)
            ftpConnectionDetails = null;
        else {
            ftpConnectionDetails = new FtpWrapper.FtpConnectionDetails(
                    startMessage.getFtpConnectionDetails().getHostname(),
                    startMessage.getFtpConnectionDetails().getUsername(),
                    startMessage.getFtpConnectionDetails().getPassword());
        }
    }

    public void flushFilesToDisk(List<Processor> allProcessors, long batchId) throws Exception {

        if (!isThereAnyDataToSave(allProcessors)) {
            logger.debug("No file data to write");
            return;
        }

        Path root = createTargetFolder(batchId);

        if (ftpConnectionDetails == null)
            flushFilesToLocalDisk(allProcessors, root);
        else
            flushFilesToRemoteDisk(allProcessors, root);
    }

    private void flushFilesToLocalDisk(List<Processor> allProcessors, Path root) throws Exception {

        for (Processor processor : allProcessors) {
            InMemoryFiles inMemoryFiles = processor.getInMemoryFiles();

            for (FileReference fileReference : inMemoryFiles.getAllFileReferences()) {

                if (fileReference.hasContent()) {
                    flushFileToLocalDisk(fileReference, root);
                    fileReference.clearContent();
                }
            }
        }
    }

    private void flushFileToLocalDisk(FileReference fileReference, Path root) throws IOException {
        Path path = createFilePath(root, fileReference);
        String content = fileReference.getFileContentBuilder().getContent();

        FileHelper.writeLargeFileToDisk(path, content);
    }

    private Path createFilePath(Path root, FileReference fileReference) {
        String filename = fileReference.getJobReportItemUuid().toString() + "_" + fileReference.getListReportGroupId();
        return root.resolve(filename);
    }

    private void flushFilesToRemoteDisk(List<Processor> allProcessors, Path root) throws Exception {
        try (FtpWrapper ftpWrapper = new FtpWrapper(ftpConnectionDetails)) {

            for (Processor processor : allProcessors) {
                InMemoryFiles inMemoryFiles = processor.getInMemoryFiles();

                for (FileReference fileReference : inMemoryFiles.getAllFileReferences()) {

                    if (fileReference.hasContent()) {
                        flushFileToRemoteDisk(fileReference, root, ftpWrapper);
                        fileReference.clearContent();
                    }
                }
            }
        }
    }

    private void flushFileToRemoteDisk(FileReference fileReference, Path root, FtpWrapper ftpWrapper) throws SftpException {
        Path path = createFilePath(root, fileReference);
        String content = fileReference.getFileContentBuilder().getContent();

        ftpWrapper.writeFileToDisk(path, content);
    }

    private boolean isThereAnyDataToSave(List<Processor> allProcessors) {
        for (Processor processor: allProcessors) {
            InMemoryFiles inMemoryFiles = processor.getInMemoryFiles();

            for (FileReference fileReference: inMemoryFiles.getAllFileReferences()) {
                if (fileReference.hasContent())
                    return true;
            }
        }

        return false;
    }

    private Path createTargetFolder(long batchId) throws Exception {

        Path path = Paths.get(startMessage.getStreamingFolder(), "Batch_" + batchId);

        if (ftpConnectionDetails == null)
            FileHelper.createFolder(path);
        else
            FtpWrapper.createFolder(ftpConnectionDetails, path);

        return path;
    }
}
