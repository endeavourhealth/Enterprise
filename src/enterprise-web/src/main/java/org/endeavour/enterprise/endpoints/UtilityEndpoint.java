package org.endeavour.enterprise.endpoints;

import org.endeavourhealth.enterprise.core.database.ReportManager;
import org.endeavourhealth.enterprise.core.database.UtilityManager;
import org.endeavourhealth.enterprise.core.database.models.data.ReportResultEntity;
import org.endeavourhealth.enterprise.core.json.JsonPrevInc;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.UUID;

@Path("/utility")
public final class UtilityEndpoint extends AbstractItemEndpoint {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/diabetes")
    public Response reportResult(@Context SecurityContext sc, JsonPrevInc options) throws Exception {
        System.out.println("in endpoint");
        super.setLogbackMarkers(sc);

        boolean success = new UtilityManager().runDiabetesReport(options);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(success)
                .build();
    }
}
