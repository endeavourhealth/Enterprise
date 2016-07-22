package org.endeavour.enterprise.json;

import org.endeavourhealth.enterprise.core.ProcessorState;
import org.endeavourhealth.enterprise.core.database.models.JobEntity;

import java.util.Date;

public final class JsonJob {
    private Date date = null;
    private String status = null;

    public JsonJob() {}
    public JsonJob(JobEntity job) {
        this.date = new Date(job.getStartdatetime().getTime());
        this.status = String.valueOf(ProcessorState.get(job.getStatusid()));
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
