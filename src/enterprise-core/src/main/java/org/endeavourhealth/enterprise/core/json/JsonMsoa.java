package org.endeavourhealth.enterprise.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonMsoa {

    private String msoaCode = null;
    private String msoaName = null;

    public JsonMsoa() {
    }

    /**
     * gets/sets
     */
    public String getMsoaCode() {
        return msoaCode;
    }

    public void setMsoaCode(String msoaCode) {
        this.msoaCode = msoaCode;
    }

    public String getMsoaName() {
        return msoaName;
    }

    public void setMsoaName(String msoaName) {
        this.msoaName = msoaName;
    }


}
