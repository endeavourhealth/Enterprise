package org.endeavourhealth.enterprise.controller.outputfiles;

import java.io.File;

class FileHelper {
    public static void makeDirectory(File directory) throws Exception {

        if (!directory.isDirectory())
            throw new Exception("Not a directory: " + directory.getAbsolutePath());

        if (!directory.mkdir()) {
            throw new Exception("Could not create folder: " + directory.getAbsolutePath());
        }
    }

    public static void checkDirectoryExists(File directory) throws Exception {

        if (!directory.isDirectory())
            throw new Exception("Not a directory: " + directory.getAbsolutePath());

        if (!directory.exists())
            throw new Exception("Directory does not exist: " + directory.getAbsolutePath());
    }

    public static void renameDirectory(File originalDirectory, String newName) throws Exception {

        File newDir = new File(originalDirectory.getParent() + "/" + newName);

        if (!originalDirectory.renameTo(newDir))
            throw new Exception("Could not rename directory from: " + originalDirectory.getAbsolutePath() + " to " + newDir.getAbsolutePath());
    }
}
