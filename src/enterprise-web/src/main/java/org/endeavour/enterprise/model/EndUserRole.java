package org.endeavour.enterprise.model;

public enum EndUserRole {
    USER(1),
    ADMIN(2);

    private int value;

    EndUserRole(int value) {
        this.value = value;
    }

    public int get() {
        return value;
    }

    public static EndUserRole get(int value) {
        for (EndUserRole e : EndUserRole.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null;// not found
    }
}
