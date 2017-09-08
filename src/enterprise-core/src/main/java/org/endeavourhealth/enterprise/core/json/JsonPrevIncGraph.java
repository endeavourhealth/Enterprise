package org.endeavourhealth.enterprise.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonPrevIncGraph {
    public String breakdown;
    public List<String> gender;
    public List<String> ethnicity;
    public List<String> postcode;
    public List<String> lsoa;
    public List<String> msoa;
    public List<String> agex10;
}
