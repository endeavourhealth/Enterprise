package org.endeavourhealth.enterprise.engine;

public class UnableToCompileExpection extends Exception {

    public UnableToCompileExpection(String message) { super(message); }
    public UnableToCompileExpection(String message, Throwable cause) { super(message, cause); }
    public UnableToCompileExpection(Throwable cause) { super(cause); }
}