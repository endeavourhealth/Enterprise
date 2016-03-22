package org.endeavour.enterprise.model.json;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

public final class JsonJobReport implements Serializable {
    private String name = null;
    private Date date = null;

    public JsonJobReport() {}
    public JsonJobReport(String name, Date date) {
        this.name = name;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
