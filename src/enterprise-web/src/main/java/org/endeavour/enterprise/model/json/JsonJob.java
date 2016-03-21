package org.endeavour.enterprise.model.json;

import org.endeavourhealth.enterprise.core.database.execution.DbJob;

import java.io.Serializable;
import java.time.Instant;

public final class JsonJob implements Serializable {
    private Instant date = null;
    private String status = null;

    public JsonJob() {}
    public JsonJob(DbJob job) {
        this.date = job.getStartDateTime();
        this.status = job.getStatusId().toString();
    }

    /**
     * gets/sets
     */
    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
