package org.endeavourhealth.enterprise.enginecore.entitymap;

public class EntityMapException extends Exception {
    public EntityMapException(String message) {
        super(message);
    }
    public EntityMapException(String message, Throwable cause) { super(message, cause); }
    public EntityMapException(Throwable cause) { super(cause); }
}
