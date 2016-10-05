package org.endeavourhealth.enterprise.controller.outputfiles;

import org.endeavourhealth.enterprise.controller.configuration.models.Configuration;
import org.endeavourhealth.enterprise.controller.configuration.models.OutputFilesType;

import java.nio.file.Path;

public class OutputFileContext {

    private final Configuration configuration;
    private Path jobStreamingFolder;
    private Path jobWorkingFolder;
    private Path jobTargetFolder;

    public OutputFileContext(Configuration configuration) {

        this.configuration = configuration;
    }


    public Configuration getConfiguration() {
        return configuration;
    }

    public OutputFilesType getOutputFileConfiguration() {
        return configuration.getOutputFiles();
    }

    public void setJobStreamingFolder(Path jobStreamingFolder) {
        this.jobStreamingFolder = jobStreamingFolder;
    }

    public Path getJobStreamingFolder() {
        return jobStreamingFolder;
    }

    public void setJobWorkingFolder(Path jobWorkingFolder) {
        this.jobWorkingFolder = jobWorkingFolder;
    }

    public Path getJobWorkingFolder() {
        return jobWorkingFolder;
    }

    public void setJobTargetFolder(Path jobTargetFolder) {
        this.jobTargetFolder = jobTargetFolder;
    }

    public Path getJobTargetFolder() {
        return jobTargetFolder;
    }
}
