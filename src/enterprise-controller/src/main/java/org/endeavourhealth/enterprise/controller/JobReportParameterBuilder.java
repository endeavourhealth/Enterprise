package org.endeavourhealth.enterprise.controller;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.endeavourhealth.enterprise.controller.jobinventory.JobReportInfo;
import org.endeavourhealth.enterprise.core.database.models.*;
import org.endeavourhealth.enterprise.core.requestParameters.RequestParametersSerializer;
import org.endeavourhealth.enterprise.core.requestParameters.models.RequestParameters;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class JobReportParameterBuilder {

    private final XMLGregorianCalendar defaultBaselineDate;

    public JobReportParameterBuilder() throws DatatypeConfigurationException {
        this.defaultBaselineDate = getDefaultBaselineDate();
    }

    public void buildParameters(List<JobReportInfo> jobReportInfoList) throws Exception{

        for (JobReportInfo jobReportInfo : jobReportInfoList) {
            buildParameters(jobReportInfo);
        }
    }

    private void buildParameters(JobReportInfo jobReportInfo) throws Exception {

        RequestParameters requestParameters = RequestParametersSerializer.readFromXml(jobReportInfo.getRequest().getParameters());

        if (requestParameters.getBaselineDate() == null)
            requestParameters.setBaselineDate(defaultBaselineDate);

        if (CollectionUtils.isEmpty(requestParameters.getOrganisation()))
            populateWithAllOrganisations(requestParameters.getOrganisation());

        jobReportInfo.setRequestParameters(requestParameters);

        Set<String> organisations = new HashSet<>();
        organisations.addAll(requestParameters.getOrganisation());
        jobReportInfo.setOrganisations(organisations);
    }

    private void populateWithAllOrganisations(List<String> requestParametersOrganisation) throws Exception {

        List<SourceorganisationEntity> all = SourceorganisationEntity.retrieveAll(false);

        if (CollectionUtils.isEmpty(all))
            throw new Exception("No active organisations");

        Set<String> targetSet = new HashSet<>();

        for (SourceorganisationEntity source: all) {

            if (StringUtils.isEmpty(source.getOdscode()))
                throw new Exception("Empty ODS code");

            targetSet.add(source.getOdscode());
        }

        requestParametersOrganisation.addAll(targetSet);
    }


    public XMLGregorianCalendar getDefaultBaselineDate() throws DatatypeConfigurationException {
        Instant now = Instant.now();

        GregorianCalendar cal1 = new GregorianCalendar();
        cal1.setTimeInMillis(now.toEpochMilli());

        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal1);
    }


}
