package org.endeavour.enterprise.endpoints;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.endeavour.enterprise.fhir.FhirStu3;
import org.endeavourhealth.common.fhir.schema.MedicationAuthorisationType;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.coreui.endpoints.AbstractEndpoint;
import org.endeavourhealth.enterprise.core.database.models.*;

import org.endeavourhealth.enterprise.core.json.JsonFolderContent;

import org.hl7.fhir.dstu3.model.Bundle;

import org.hl7.fhir.dstu3.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.Console;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Path("/dashboard")
public final class DashboardEndpoint extends AbstractEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(DashboardEndpoint.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getRecentDocuments")
    public Response getRecentDocuments(@Context SecurityContext sc, @QueryParam("count") int count) throws Exception {
        super.setLogbackMarkers(sc);

        String userUuid = SecurityUtils.getCurrentUserId(sc).toString();

        String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";

        LOG.trace("getRecentDocuments {}", count);

        List<JsonFolderContent> ret = new ArrayList<>();

        ActiveItemEntity aI = new ActiveItemEntity();

        List<ActiveItemEntity> activeItems = aI.retrieveActiveItemRecentItems(userUuid, orgUuid, count);
        for (ActiveItemEntity activeItem: activeItems) {
            ItemEntity item = ItemEntity.retrieveForActiveItem(activeItem);
            AuditEntity audit = AuditEntity.retrieveForUuid(item.getAuditUuid());

            JsonFolderContent content = new JsonFolderContent(activeItem, item, audit, null);
            ret.add(content);
        }

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getStructuredRecord")
    public Response getStructuredRecord(@Context SecurityContext sc, @QueryParam("params") String params) throws Exception {
        super.setLogbackMarkers(sc);

        String resource = null;

        try {
            String userUuid = SecurityUtils.getCurrentUserId(sc).toString();

            // Create a context for DSTU3
            FhirContext ctx = FhirContext.forDstu3();

            Patient patient = FhirStu3.getPatient("");

            Bundle bundle = new Bundle();
            bundle.setId("1");
            bundle.addEntry().setFullUrl("http://localhost:8080/fhir/STU3/Patient/9314739d-6ab6-4caa-a820-15931023efcd").setResource(patient);


            resource = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);

            System.out.println(resource);

            clearLogbackMarkers();
        }
        catch (Exception e) {
            System.out.println(e);
            LOG.error("getStructuredRecord error: ", e);
        }

        return Response
                .ok()
                .entity(resource)
                .build();

    }








}
