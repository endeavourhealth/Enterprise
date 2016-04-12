package org.endeavourhealth.enterprise.controller.jobinventory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ListReportInfo {
    private Path folderBranch;
    private String rootName;

    public void setFolderBranch(Path folderBranch) {
        this.folderBranch = folderBranch;
    }

    public String getRootName() {
        return rootName;
    }

    public void setRootName(String rootName) {
        this.rootName = rootName;
    }
}
