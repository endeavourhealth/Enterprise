package org.endeavourhealth.enterprise.core.entity.database;

/**
 * Created by Drew on 11/03/2016.
 */
public enum TableSaveMode {
    INSERT(1),
    UPDATE(2),
    DELETE(3);

    private int value;

    TableSaveMode(int value) {
        this.value = value;
    }
}
