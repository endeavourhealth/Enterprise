package org.endeavourhealth.enterprise.core.database.models.data;

import org.endeavourhealth.enterprise.core.querydocument.models.RuleAction;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class QueryResult {
    private Long organisationId;
    private Integer ruleId;
    private List<Long> patients = new ArrayList<>();
    private List<ObservationEntity> observations = new ArrayList<>();
    private RuleAction onPass;
    private RuleAction onFail;

    public long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public RuleAction getOnPass() {
        return onPass;
    }

    public void setOnPass(RuleAction value) {
        this.onPass = value;
    }

    public RuleAction getOnFail() {
        return onFail;
    }

    public void setOnFail(RuleAction value) {
        this.onFail = value;
    }

    public List<Long> getPatients() { return patients; }

    public void setPatients(List<Long> patients) { this.patients = patients; }

    public List<ObservationEntity> getObservations() { return observations; }

    public void setObservations(List<ObservationEntity> observations) { this.observations = observations; }

}
