package org.endeavourhealth.enterprise.core.entity;

/**
 * Created by Drew on 29/02/2016.
 */
public enum DependencyType {
    IsChildOf(0),
    IsContainedWithin(1),
    Uses(2);

    private int value;

    DependencyType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DependencyType get(int value) {
        for (DependencyType e : DependencyType.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null; // not found
    }
}
