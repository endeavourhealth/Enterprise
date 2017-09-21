package org.endeavourhealth.enterprise.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonPrevInc {
    private Integer organisationGroup = null;
    private String population = null;
    private String codeSet = null;
    private String timePeriodNo = null;
    private String timePeriod = null;
    private String title = null;
    private String diseaseCategory = null;
    private String postCodePrefix = null;
    private List<JsonLsoa> lsoaCode = null;
    private List<JsonMsoa> msoaCode = null;
    private String sex = null;
    private List<String> ethnicity = null;
    private String orgType = null;
    private String ageFrom = null;
    private String ageTo = null;
    private String dateType = null;

    public Integer getOrganisationGroup() {
        return organisationGroup;
    }

    public void setOrganisationGroup(Integer organisationGroup) {
        this.organisationGroup = organisationGroup;
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

    public String getDiseaseCategory() {
        return diseaseCategory;
    }

    public void setDiseaseCategory(String diseaseCategory) {
        this.diseaseCategory = diseaseCategory;
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

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
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

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }
}
