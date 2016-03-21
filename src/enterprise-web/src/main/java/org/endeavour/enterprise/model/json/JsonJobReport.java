package org.endeavour.enterprise.model.json;

import java.io.Serializable;
import java.time.Instant;

public final class JsonJobReport implements Serializable {
    private String name = null;
    private Instant date = null;

    public JsonJobReport() {}
    public JsonJobReport(String name, Instant date) {
        this.name = name;
        this.date = date;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
