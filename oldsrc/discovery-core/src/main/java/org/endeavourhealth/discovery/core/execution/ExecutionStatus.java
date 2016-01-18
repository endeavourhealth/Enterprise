package org.endeavourhealth.discovery.core.execution;

public enum ExecutionStatus {
    EXECUTING(0),
    SUCCEEDED(1),
    FAILED(2);

    private final int value;

    ExecutionStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
