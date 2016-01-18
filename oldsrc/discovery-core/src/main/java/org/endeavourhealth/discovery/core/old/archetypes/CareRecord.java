package org.endeavourhealth.discovery.core.old.archetypes;

import java.util.List;

public class CareRecord {

    private List<Observation> observations;

    public List<Observation> getObservations() {
        return observations;
    }

    public void setObservations(List<Observation> observations) {
        this.observations = observations;
    }
}
