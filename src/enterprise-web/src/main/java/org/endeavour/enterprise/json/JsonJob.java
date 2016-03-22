package org.endeavour.enterprise.json;

import org.endeavourhealth.enterprise.core.database.execution.DbJob;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

public final class JsonJob implements Serializable {
    private Date date = null;
    private String status = null;

    public JsonJob() {}
    public JsonJob(DbJob job) {
        this.date = new Date(job.getStartDateTime().toEpochMilli());
        this.status = job.getStatusId().toString();
    }

    /**
     * gets/sets
     */
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
