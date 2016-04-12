package org.endeavourhealth.enterprise.controller.jobinventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ListReportInfo {
    private List<String> folders;

    public void setFolderStack(Stack<String> folderStack) {
        //noinspection unchecked

        List<String> folders = new ArrayList<>();
        folders.addAll(folderStack);  //This will not alter the stack.

    }
}
