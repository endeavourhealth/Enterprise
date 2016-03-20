package org.endeavourhealth.enterprise.core.entity;

/**
 * Created by Drew on 19/03/2016.
 */
public enum ExecutionStatus {
    Executing(0),
    Succeeded(1),
    Failed(2);

    private int value;

    ExecutionStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ExecutionStatus get(int value) {
        for (ExecutionStatus e : ExecutionStatus.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null;// not found
    }
}
