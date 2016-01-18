package org.endeavourhealth.discovery.client.services;

import org.endeavourhealth.discovery.core.entities.model.DataContainer;
import org.endeavourhealth.discovery.core.entities.retrieval.EntityRetriever;
import org.endeavourhealth.discovery.core.entities.userFriendly.UserFriendlyConverter;
import org.endeavourhealth.discovery.core.entities.userFriendly.UserFriendlyEntityContainer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/patient")
public class PatientWebService {

    @GET
    @Path("{patientId}/careRecord")
    @Produces(MediaType.APPLICATION_JSON)
    public UserFriendlyEntityContainer getCareRecord(@PathParam("patientId") int patientId){

        EntityRetriever retriever = new EntityRetriever(null);
        DataContainer rawData = retriever.getEntityContainer(patientId);

        UserFriendlyConverter converter = new UserFriendlyConverter();
        UserFriendlyEntityContainer container = converter.convert(rawData, null);

        return container;

        //return "Hello Patient:" + patientId;

    }
}