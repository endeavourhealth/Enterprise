package org.endeavour.enterprise.model.json;

import org.endeavourhealth.enterprise.core.entity.ExecutionStatus;
import org.endeavourhealth.enterprise.core.entity.database.DbJob;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Drew on 19/03/2016.
 */
public final class JsonJob implements Serializable {
    private Date date = null;
    private String status = null;

    public JsonJob() {}
    public JsonJob(DbJob job) {
        this.date = job.getStartDateTime();
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
