package org.endeavourhealth.enterprise.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonReportRun {

    private List<JsonOrganisation> organisation = new ArrayList<>();
    private String population = null;
    private String baselineDate = null;
    private String reportItemUuid = null;

    public JsonReportRun() {
    }

    /**
     * gets/sets
     */
    public List<JsonOrganisation> getOrganisation() {
        return organisation;
    }

    public void setOrganisation(List<JsonOrganisation> organisation) {
        this.organisation = organisation;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getBaselineDate() {
        return baselineDate;
    }

    public void setBaselineDate(String baselineDate) {
        this.baselineDate = baselineDate;
    }

    public String getReportItemUuid() {
        return reportItemUuid;
    }

    public void setReportItemUuid(String queryItemUuid) {
        this.reportItemUuid = queryItemUuid;
    }


}
