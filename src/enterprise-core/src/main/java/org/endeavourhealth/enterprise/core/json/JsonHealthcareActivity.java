package org.endeavourhealth.enterprise.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonHealthcareActivity {
    private Integer organisationGroup = null;
    private String title = null;
    private String postCodePrefix = null;
    private List<JsonLsoa> lsoaCode = null;
    private List<JsonMsoa> msoaCode = null;
    private String sex = null;
    private List<String> ethnicity = null;
    private String ageFrom = null;
    private String ageTo = null;
    private String timePeriodNo = null;
    private String timePeriod = null;
    private Integer ServiceGroupId = null;
    private String dateType = null;
    private String population = null;
    private String orgType = null;
    private List<String> encounterType = null;

    public Integer getOrganisationGroup() {
        return organisationGroup;
    }

    public void setOrganisationGroup(Integer organisationGroup) {
        this.organisationGroup = organisationGroup;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostCodePrefix() {
        return postCodePrefix;
    }

    public void setPostCodePrefix(String postCodePrefix) {
        this.postCodePrefix = postCodePrefix;
    }

    public List<JsonLsoa> getLsoaCode() {
        return lsoaCode;
    }

    public void setLsoaCode(List<JsonLsoa> lsoaCode) {
        this.lsoaCode = lsoaCode;
    }

    public List<JsonMsoa> getMsoaCode() {
        return msoaCode;
    }

    public void setMsoaCode(List<JsonMsoa> msoaCode) {
        this.msoaCode = msoaCode;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public List<String> getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(List<String> ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getAgeFrom() {
        return ageFrom;
    }

    public void setAgeFrom(String ageFrom) {
        this.ageFrom = ageFrom;
    }

    public String getAgeTo() {
        return ageTo;
    }

    public void setAgeTo(String ageTo) {
        this.ageTo = ageTo;
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

    public Integer getServiceGroupId() {
        return ServiceGroupId;
    }

    public void setServiceGroupId(Integer serviceGroupId) {
        ServiceGroupId = serviceGroupId;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public List<String> getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(List<String> encounterType) {
        this.encounterType = encounterType;
    }
}
