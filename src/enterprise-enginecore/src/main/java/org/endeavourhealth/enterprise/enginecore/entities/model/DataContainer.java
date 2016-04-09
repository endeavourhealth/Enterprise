package org.endeavourhealth.enterprise.enginecore.entities.model;

import java.util.ArrayList;
import java.util.List;

/*
One patient's record will be stored in a datacontainer
 */
public class DataContainer {
    private List<DataEntity> entities = new ArrayList<>();
    private long id;
    private String organisationOds;

    public List<DataEntity> getDataEntities() {
        return entities;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrganisationOds() {
        return organisationOds;
    }

    public void setOrganisationOds(String organisationOds) {
        this.organisationOds = organisationOds;
    }
}
