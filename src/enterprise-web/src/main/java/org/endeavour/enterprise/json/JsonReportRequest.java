package org.endeavour.enterprise.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.endeavourhealth.enterprise.core.ExecutionStatus;
import org.endeavourhealth.enterprise.core.ProcessorState;
import org.endeavourhealth.enterprise.core.database.models.EnduserEntity;
import org.endeavourhealth.enterprise.core.database.models.JobEntity;
import org.endeavourhealth.enterprise.core.database.models.RequestEntity;
import org.endeavourhealth.enterprise.core.requestParameters.RequestParametersSerializer;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;

import java.util.Date;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonReportRequest {

    private UUID uuid = null;
    private Date date = null;
    private String status = null;
    private UUID endUserUuid = null;
    private String endUserName = null;
    private RequestParameters parameters = null;

    public JsonReportRequest() { }

    public JsonReportRequest(RequestEntity request, JobEntity job, EnduserEntity user, String parameterXml) throws Exception {

        String status = "Scheduled";
        if (job != null) {
            status = String.valueOf(ExecutionStatus.get(job.getStatusid()));
        }
        String userName = user.getForename() + " " + user.getSurname();

        this.uuid = request.getRequestuuid();
        this.date = new Date(request.getTimestamp().getTime());
        this.status = status;
        this.endUserUuid = request.getEnduseruuid();
        this.endUserName = userName;
        this.parameters = RequestParametersSerializer.readFromXml(parameterXml);
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

    public String getEndUserName() {
        return endUserName;
    }

    public void setEndUserName(String endUserName) {
        this.endUserName = endUserName;
    }

    public UUID getEndUserUuid() {
        return endUserUuid;
    }

    public void setEndUserUuid(UUID endUserUuid) {
        this.endUserUuid = endUserUuid;
    }

    public RequestParameters getParameters() {
        return parameters;
    }

    public void setParameters(RequestParameters parameters) {
        this.parameters = parameters;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
