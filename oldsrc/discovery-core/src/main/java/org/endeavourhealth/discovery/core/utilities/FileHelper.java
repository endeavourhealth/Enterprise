package org.endeavourhealth.discovery.core.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {

    public static String readFullFile(String fullFileName) throws IOException {
        Path path = Paths.get(fullFileName);
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded);
    }
}
