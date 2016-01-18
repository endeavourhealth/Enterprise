package org.endeavourhealth.discovery.core.entities.userFriendly;

import java.util.ArrayList;
import java.util.List;

public class UserFriendlyEntity {
    private String name;
    private List<UserFriendlyField> fields = new ArrayList<>();
    private List<UserFriendlyRow> rows = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserFriendlyField> getFields() {
        return fields;
    }

    public void setFields(List<UserFriendlyField> fields) {
        this.fields = fields;
    }

    public List<UserFriendlyRow> getRows() {
        return rows;
    }

    public void setRows(List<UserFriendlyRow> rows) {
        this.rows = rows;
    }
}
