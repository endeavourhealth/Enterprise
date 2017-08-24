package org.endeavourhealth.enterprise.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonPrevInc {
    private List<JsonOrganisation> organisation;
    private String population;
    private String codeSet;
    private String timePeriodNo;
    private String timePeriod;
    private String title;

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

    public String getCodeSet() {
        return codeSet;
    }

    public void setCodeSet(String codeSet) {
        this.codeSet = codeSet;
    }

    public String getTimePeriodNo() {
        return timePeriodNo;
    }

    public void setTimePeriodNo(String timePeriodNo) {
        this.timePeriodNo = timePeriodNo;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
