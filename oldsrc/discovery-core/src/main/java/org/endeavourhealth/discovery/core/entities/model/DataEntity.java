package org.endeavourhealth.discovery.core.entities.model;

import java.util.ArrayList;
import java.util.List;

public class DataEntity {
    private List<DataField> fields = new ArrayList<>();

    public int getSize() {
        return fields.get(0).size();
    }

    public List<DataField> getFields() {
        return fields;
    }
}
