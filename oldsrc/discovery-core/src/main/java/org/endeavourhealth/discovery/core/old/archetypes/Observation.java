package org.endeavourhealth.discovery.core.old.archetypes;

import org.joda.time.LocalDate;

public class Observation {

    private LocalDate effectiveDate;
    private long snomedCTConceptId;

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public long getSnomedCTConceptId() {
        return snomedCTConceptId;
    }

    public void setSnomedCTConceptId(long snomedCTConceptId) {
        this.snomedCTConceptId = snomedCTConceptId;
    }
}
