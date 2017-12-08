package org.endeavourhealth.enterprise.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonCohortRun {

    private List<JsonOrganisation> organisation = new ArrayList<>();
    private String population = null;
    private String baselineDate = null;
    private String queryItemUuid = null;
    private String baselineCohortId = null;

    public JsonCohortRun() {
    }

    /**
     * gets/sets
     */
    public List<JsonOrganisation> getOrganisation() {
        return organisation;
    }

    public JsonCohortRun setOrganisation(List<JsonOrganisation> organisation) {
        this.organisation = organisation;
        return this;
    }

    public String getPopulation() {
        return population;
    }

    public JsonCohortRun setPopulation(String population) {
        this.population = population;
        return this;
    }

    public String getBaselineDate() {
        return baselineDate;
    }

    public JsonCohortRun setBaselineDate(String baselineDate) {
        this.baselineDate = baselineDate;
        return this;
    }

    public String getQueryItemUuid() {
        return queryItemUuid;
    }

    public JsonCohortRun setQueryItemUuid(String queryItemUuid) {
        this.queryItemUuid = queryItemUuid;
        return this;
    }

    public String getBaselineCohortId() {
        return baselineCohortId;
    }

    public JsonCohortRun setBaselineCohortId(String baselineCohortId) {
        this.baselineCohortId = baselineCohortId;
        return this;
    }


}
