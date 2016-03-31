package org.endeavourhealth.enterprise.enginecore;

import java.util.UUID;

public class InvalidQueryDocumentException extends Exception {

    public InvalidQueryDocumentException(UUID itemUuid, String message) { super(createMessage(itemUuid, message)); }
    public InvalidQueryDocumentException(UUID itemUuid, String message, Throwable cause) { super(createMessage(itemUuid, message), cause); }
    public InvalidQueryDocumentException(UUID itemUuid, Throwable cause) { super(createMessage(itemUuid, null), cause); }

    private static String createMessage(UUID itemUuid, String message) {
        if (message == null)
            return "Item UUID: " + itemUuid;
        else
            return "Item UUID: " + itemUuid + ".  " + message;
    }
}