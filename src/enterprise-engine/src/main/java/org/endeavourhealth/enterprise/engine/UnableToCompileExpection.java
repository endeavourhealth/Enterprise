package org.endeavourhealth.enterprise.engine;

import java.util.UUID;

public class UnableToCompileExpection extends Exception {

    public UnableToCompileExpection(UUID libraryItemUuid, String message) { super(createMessage(libraryItemUuid, message)); }
    public UnableToCompileExpection(UUID libraryItemUuid, String message, Throwable cause) { super(createMessage(libraryItemUuid, message), cause); }
    public UnableToCompileExpection(UUID libraryItemUuid, Throwable cause) { super(createMessage(libraryItemUuid, null), cause); }

    private static String createMessage(UUID libraryItemUuid, String message) {
        if (message == null)
            return "Library Item UUID: " + libraryItemUuid;
        else
            return "Library Item UUID: " + libraryItemUuid + ".  " + message;
    }
}