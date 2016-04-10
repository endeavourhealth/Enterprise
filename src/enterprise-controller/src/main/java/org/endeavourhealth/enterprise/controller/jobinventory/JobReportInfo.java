package org.endeavourhealth.enterprise.controller.jobinventory;

import org.endeavourhealth.enterprise.core.database.execution.DbRequest;
import org.endeavourhealth.enterprise.core.requestParameters.RequestParametersSerializer;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class JobReportInfo {
    private final UUID jobReportUuid = UUID.randomUUID();
    private final List<JobReportItemInfo> children = new ArrayList<>();
    private final DbRequest request;
    private Set<String> organisations;
    private RequestParameters requestParameters;

    public JobReportInfo(DbRequest request) {
        this.request = request;
    }

    public UUID getReportUuid() {
        return request.getReportUuid();
    }

    public UUID getJobReportUuid() {
        return jobReportUuid;
    }

    public List<JobReportItemInfo> getChildren() {
        return children;
    }

    public DbRequest getRequest() {
        return request;
    }

    public Set<String> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(Set<String> organisations) {
        this.organisations = organisations;
    }

    public void setRequestParameters(RequestParameters requestParameters) {
        this.requestParameters = requestParameters;
    }

    public String getParametersAsString() {
        return RequestParametersSerializer.writeToXml(requestParameters);
    }
}
