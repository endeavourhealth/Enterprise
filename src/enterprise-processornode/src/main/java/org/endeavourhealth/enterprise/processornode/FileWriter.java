package org.endeavourhealth.enterprise.processornode;

import org.endeavourhealth.enterprise.engine.Processor;
import org.endeavourhealth.enterprise.engine.execution.listreports.FileReference;
import org.endeavourhealth.enterprise.engine.execution.listreports.InMemoryFiles;
import org.endeavourhealth.enterprise.enginecore.communication.ProcessorNodesStartMessage;

import java.util.List;

class FileWriter {

    private final ProcessorNodesStartMessage.StartMessagePayload startMessage;

    public FileWriter(ProcessorNodesStartMessage.StartMessagePayload startMessage) {

        this.startMessage = startMessage;
    }

    public void flushFilesToDisk(List<Processor> allProcessors) {

        for (Processor processor: allProcessors) {
            InMemoryFiles inMemoryFiles = processor.getInMemoryFiles();

            for (FileReference fileReference: inMemoryFiles.getAllFileReferences()) {
                flushFileToDisk(fileReference);
            }
        }
    }

    private void flushFileToDisk(FileReference fileReference) {

        String filename = fileReference.getJobReportItemUuid().toString() + "_" + fileReference.getListReportGroupId();

        String content = fileReference.getFileContentBuilder().toString();


    }
}
