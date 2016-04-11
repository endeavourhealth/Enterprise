package org.endeavourhealth.enterprise.core;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHelper {

    public static String readTextFile(Path file) throws Exception {

        try {
            if (!Files.exists(file))
                throw new Exception("Could not find file: " + file.getFileName());

            byte[] encoded = Files.readAllBytes(file);
            return new String(encoded, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new Exception("Error reading file: " + file.getFileName(), e);
        }
    }
}
