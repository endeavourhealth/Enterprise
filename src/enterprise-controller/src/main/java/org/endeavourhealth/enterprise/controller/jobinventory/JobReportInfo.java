package org.endeavourhealth.enterprise.controller.jobinventory;


import org.endeavourhealth.enterprise.core.database.models.RequestEntity;
import org.endeavourhealth.enterprise.core.requestParameters.RequestParametersSerializer;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class JobReportInfo {
    private final UUID jobReportUuid = UUID.randomUUID();
    private final String reportName;
    private final List<JobReportItemInfo> children = new ArrayList<>();
    private final RequestEntity request;
    private Set<String> organisations;
    private RequestParameters requestParameters;

    public JobReportInfo(RequestEntity request, String reportName) {
        this.request = request;
        this.reportName = reportName;
    }

    public UUID getReportUuid() {
        return request.getReportuuid();
    }

    public UUID getJobReportUuid() {
        return jobReportUuid;
    }

    public List<JobReportItemInfo> getChildren() {
        return children;
    }

    public RequestEntity getRequest() {
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

    public String getReportName() {
        return reportName;
    }
}
