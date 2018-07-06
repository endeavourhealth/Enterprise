package org.endeavourhealth.enterprise.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonTerm {

    private String originalTerm = null;
    private String snomedTerm = null;
    private String snomedConceptId = null;
    private String originalCode = null;
    private String recordType = null;

    public JsonTerm() {
    }

    /**
     * gets/sets
     */
    public String getOriginalTerm() {
        return originalTerm;
    }

    public void setOriginalTerm(String originalTerm) {
        this.originalTerm = originalTerm;
    }

    public String getSnomedTerm() {
        return snomedTerm;
    }

    public void setSnomedTerm(String snomedTerm) {
        this.snomedTerm = snomedTerm;
    }

    public String getSnomedConceptId() {
        return snomedConceptId;
    }

    public void setSnomedConceptId(String snomedConceptId) {
        this.snomedConceptId = snomedConceptId;
    }

    public String getOriginalCode() {
        return originalCode;
    }

    public void setOriginalCode(String originalCode) {
        this.originalCode = originalCode;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }



}
