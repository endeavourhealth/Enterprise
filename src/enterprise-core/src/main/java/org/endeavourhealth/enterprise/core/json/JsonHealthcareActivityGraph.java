package org.endeavourhealth.enterprise.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonHealthcareActivityGraph {
    public String breakdown;
    public List<String> gender;
    public List<String> ethnicity;
    public List<String> postcode;
    public List<String> lsoa;
    public List<String> msoa;
    public List<String> orgs;
    public List<String> agex10;
    public List<String> ccgs;
    private String timePeriodNo = null;
    private String timePeriod = null;

    public String getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(String breakdown) {
        this.breakdown = breakdown;
    }

    public List<String> getGender() {
        return gender;
    }

    public void setGender(List<String> gender) {
        this.gender = gender;
    }

    public List<String> getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(List<String> ethnicity) {
        this.ethnicity = ethnicity;
    }

    public List<String> getPostcode() {
        return postcode;
    }

    public void setPostcode(List<String> postcode) {
        this.postcode = postcode;
    }

    public List<String> getLsoa() {
        return lsoa;
    }

    public void setLsoa(List<String> lsoa) {
        this.lsoa = lsoa;
    }

    public List<String> getMsoa() {
        return msoa;
    }

    public void setMsoa(List<String> msoa) {
        this.msoa = msoa;
    }

    public List<String> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<String> orgs) {
        this.orgs = orgs;
    }

    public List<String> getAgex10() {
        return agex10;
    }

    public void setAgex10(List<String> agex10) {
        this.agex10 = agex10;
    }

    public List<String> getCcgs() {
        return ccgs;
    }

    public void setCcgs(List<String> ccgs) {
        this.ccgs = ccgs;
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
}
