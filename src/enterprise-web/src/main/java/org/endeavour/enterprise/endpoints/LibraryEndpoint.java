package org.endeavour.enterprise.endpoints;

import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;

import org.endeavourhealth.enterprise.core.database.ResultsManager;
import org.endeavourhealth.enterprise.core.database.models.ActiveItemEntity;
import org.endeavourhealth.enterprise.core.database.models.ItemDependencyEntity;
import org.endeavourhealth.enterprise.core.database.models.ItemEntity;
import org.endeavourhealth.enterprise.core.database.models.data.*;
import org.endeavourhealth.enterprise.core.json.*;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/library")
public final class LibraryEndpoint extends AbstractItemEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(LibraryEndpoint.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getLibraryItem")
    public Response getLibraryItem(@Context SecurityContext sc, @QueryParam("uuid") String uuidStr) throws Exception {
        super.setLogbackMarkers(sc);

        String libraryItemUuid = uuidStr;

        LOG.trace("GettingLibraryItem for UUID {}", libraryItemUuid);

        ItemEntity item = ItemEntity.retrieveLatestForUUid(libraryItemUuid);
        String xml = item.getXmlContent();

        LibraryItem ret = QueryDocumentSerializer.readLibraryItemFromXml(xml);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveLibraryItem")
    public Response saveLibraryItem(@Context SecurityContext sc, LibraryItem libraryItem) throws Exception {
        super.setLogbackMarkers(sc);

        String userUuid = SecurityUtils.getCurrentUserId(sc).toString();
        String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";

        String libraryItemUuid = parseUuidFromStr(libraryItem.getUuid());
        String name = libraryItem.getName();
        String description = libraryItem.getDescription();
        String folderUuid = parseUuidFromStr(libraryItem.getFolderUuid());

        Query query = libraryItem.getQuery();
        CodeSet codeSet = libraryItem.getCodeSet();

        LOG.trace(String.format("SavingLibraryItem UUID %s, Name %s FolderUuid %s", libraryItemUuid, name, folderUuid));

        QueryDocument doc = new QueryDocument();
        doc.getLibraryItem().add(libraryItem);

        //work out the item type (query, test etc.) from the content passed up
        Short type = null;
        if (query != null) {
            type = (short)DefinitionItemType.Query.getValue();
        } else if (codeSet != null) {
            type = (short)DefinitionItemType.CodeSet.getValue();

        } else {
            //if we've been passed no proper content, we might just be wanting to rename an existing item,
            //so work out the type from what's on the DB already
            if (libraryItemUuid == null) {
                throw new BadRequestException("Can't save LibraryItem without some content (e.g. query, test etc.)");
            }

            ActiveItemEntity activeItem = ActiveItemEntity.retrieveForItemUuid(libraryItemUuid);
            type = activeItem.getItemTypeId();
            doc = null; //clear this, because we don't want to overwrite what's on the DB with an empty query doc
        }

        boolean inserting = libraryItemUuid == null;
        if (inserting) {
            libraryItemUuid = UUID.randomUUID().toString();
            libraryItem.setUuid(libraryItemUuid.toString());
        }

        super.saveItem(inserting, libraryItemUuid, orgUuid, userUuid, type.intValue(), name, description, doc, folderUuid);

        //return the UUID of the libraryItem
        LibraryItem ret = new LibraryItem();
        ret.setUuid(libraryItemUuid.toString());

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/deleteLibraryItem")
    public Response deleteLibraryItem(@Context SecurityContext sc, LibraryItem libraryItem) throws Exception {
        super.setLogbackMarkers(sc);

        String libraryItemUuid = parseUuidFromStr(libraryItem.getUuid());
        String userUuid = SecurityUtils.getCurrentUserId(sc).toString();;
        String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";
        
        LOG.trace("DeletingLibraryItem UUID {}", libraryItemUuid);

        JsonDeleteResponse ret = deleteItem(libraryItemUuid, orgUuid, userUuid);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getContentNamesForReportLibraryItem")
    public Response getContentNamesForReportLibraryItem(@Context SecurityContext sc, @QueryParam("uuid") String uuidStr) throws Exception {
        super.setLogbackMarkers(sc);

        String itemUuid = uuidStr;

        LOG.trace("getContentNamesforReportLibraryItem for UUID {}", itemUuid);

        JsonFolderContentsList ret = new JsonFolderContentsList();

        ActiveItemEntity activeItem = ActiveItemEntity.retrieveForItemUuid(itemUuid);
        List<ItemDependencyEntity> dependentItems = ItemDependencyEntity.retrieveForActiveItemType(activeItem, (short)DependencyType.Uses.getValue());

        for (ItemDependencyEntity dependentItem: dependentItems) {
            String dependentItemUuid = dependentItem.getDependentItemUuid();
            ItemEntity item = ItemEntity.retrieveLatestForUUid(dependentItemUuid);

            JsonFolderContent content = new JsonFolderContent(item, null, null);
            ret.addContent(content);
        }

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/moveLibraryItems")
    public Response moveLibraryItems(@Context SecurityContext sc, JsonMoveItems parameters) throws Exception {
        super.setLogbackMarkers(sc);

        String userUuid = SecurityUtils.getCurrentUserId(sc).toString();;
        String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";

        LOG.trace("moveLibraryItems");

        super.moveItems(userUuid, orgUuid, parameters);

        clearLogbackMarkers();

        return Response
                .ok()
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getConceptChildren")
    public Response getConceptChildren(@Context SecurityContext sc, @QueryParam("id") String id) throws Exception {
        super.setLogbackMarkers(sc);

        List<Object[]> concepts = ConceptEntity.findConceptChildren(id);

        List<JsonCode> ret = new ArrayList<>();

        String prevDefinition = "";

        for (Object[] conceptEntity: concepts) {
            String conceptId = conceptEntity[0].toString();
            String definition = conceptEntity[1].toString();
            String parentType = conceptEntity[2]==null?"":conceptEntity[2].toString();
            String parentTypeId = conceptEntity[3]==null?"":conceptEntity[3].toString();
            String baseType = conceptEntity[4]==null?"":conceptEntity[4].toString();
            String baseTypeId = conceptEntity[5]==null?"":conceptEntity[5].toString();
            String dataTypeId = conceptEntity[6].toString();
            String conceptTypeId = conceptEntity[7].toString();
            String present = conceptEntity[8].toString();
            String units = conceptEntity[9]==null?"":conceptEntity[9].toString();


            if (definition.equals(prevDefinition))
                continue;

            prevDefinition = definition;

            if (conceptTypeId.equals("1")|| // don't show resource or unit types
                    conceptTypeId.equals("3")) {
                continue;
            }

            JsonCode code = new JsonCode();
            code.setId(conceptId);
            code.setLabel(definition);
            code.setDataType(dataTypeId);
            code.setParentType(parentType);
            code.setBaseType(baseType);
            code.setPresent(present);
            code.setUnits(units);

            ret.add(code);
        }

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getConceptParents")
    public Response getConceptParents(@Context SecurityContext sc, @QueryParam("id") String id) throws Exception {
        super.setLogbackMarkers(sc);

        List<Object[]> concepts = ConceptEntity.findConceptParents(id);

        List<JsonCode> ret = new ArrayList<>();

        String prevDefinition = "";

        for (Object[] conceptEntity: concepts) {
            String conceptId = conceptEntity[0].toString();
            String definition = conceptEntity[1].toString();
            String parentType = conceptEntity[2]==null?"":conceptEntity[2].toString();
            String parentTypeId = conceptEntity[3]==null?"":conceptEntity[3].toString();
            String baseType = conceptEntity[4]==null?"":conceptEntity[4].toString();
            String baseTypeId = conceptEntity[5]==null?"":conceptEntity[5].toString();
            String dataTypeId = conceptEntity[6].toString();
            String conceptTypeId = conceptEntity[7].toString();
            String parentConceptTypeId = conceptEntity[8].toString();
            String present = conceptEntity[9].toString();
            String units = conceptEntity[10]==null?"":conceptEntity[10].toString();


            if (parentType.equals(prevDefinition))
                continue;

            prevDefinition = parentType;

            if (conceptTypeId.equals("1")|| // don't show resource or unit types
                    conceptTypeId.equals("3")||
                    parentConceptTypeId.equals("1")|| // don't show resource or unit types
                    parentConceptTypeId.equals("3")) {
                continue;
            }

            JsonCode code = new JsonCode();
            code.setId(parentTypeId);
            code.setLabel(parentType);
            code.setDataType(dataTypeId);
            code.setParentType(parentType);
            code.setBaseType(baseType);
            code.setPresent(present);
            code.setUnits(units);


            ret.add(code);
        }

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(ret)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getConcepts")
    public Response getConcepts(@Context SecurityContext sc, @QueryParam("term") String term) throws Exception {
        super.setLogbackMarkers(sc);

        List<Object[]> concepts = ConceptEntity.findConcept(term);

        List<JsonCode> results = new ArrayList<>();

        String prevDefinition = "";

        for (Object[] conceptEntity: concepts) {
            String conceptId = conceptEntity[0].toString();
            String definition = conceptEntity[1].toString();
            String parentType = conceptEntity[2]==null?"":conceptEntity[2].toString();
            String parentTypeId = conceptEntity[3]==null?"":conceptEntity[3].toString();
            String baseType = conceptEntity[4]==null?"":conceptEntity[4].toString();
            String baseTypeId = conceptEntity[5]==null?"":conceptEntity[5].toString();
            String dataTypeId = conceptEntity[6].toString();
            String conceptTypeId = conceptEntity[7].toString();
            String present = conceptEntity[8].toString();
            String units = conceptEntity[9]==null?"":conceptEntity[9].toString();

            if (definition.equals(prevDefinition))
                continue;

            prevDefinition = definition;

            if (conceptTypeId.equals("1")|| // don't show resource or unit types
                    conceptTypeId.equals("3")) {
                continue;
            }

            JsonCode code = new JsonCode();
            code.setId(conceptId);
            code.setLabel(definition);
            code.setDataType(dataTypeId);
            code.setParentType(parentType);
            code.setBaseType(baseType);
            code.setPresent(present);
            code.setUnits(units);

            results.add(code);
        }

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getOrganisations")
    public Response getOrganisations(@Context SecurityContext sc) throws Exception {
        super.setLogbackMarkers(sc);

        List<Object[]> organisations = OrganizationEntity.getOrganisations();

        List<JsonOrganisation> results = new ArrayList<>();

        for (Object[] orgEntity: organisations) {
            String id = orgEntity[0].toString();
            String name = orgEntity[1].toString();

            JsonOrganisation org = new JsonOrganisation();
            org.setId(id);
            org.setName(name);

            results.add(org);
        }

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getReportResults")
    public Response getReportResults(@Context SecurityContext sc, @QueryParam("queryItemUuid") String queryItemUuid, @QueryParam("runDate") String runDate) throws Exception {
        super.setLogbackMarkers(sc);

        List<ReportResultEntity[]> results = ReportResultEntity.getReportResults(queryItemUuid, runDate);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getAllReportResults")
    public Response getAllReportResults(@Context SecurityContext sc, @QueryParam("queryItemUuid") String queryItemUuid, @QueryParam("runDate") String runDate) throws Exception {
        super.setLogbackMarkers(sc);

        List<ReportResultEntity[]> results = ReportResultEntity.getAllReportResults(queryItemUuid);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getReportPatients")
    public Response getReportPatients(@Context SecurityContext sc, @QueryParam("queryItemUuid") String queryItemUuid, @QueryParam("runDate") String runDate, @QueryParam("organisationId") Short organisationId) throws Exception {
        super.setLogbackMarkers(sc);

        List<ReportPatientsEntity[]> results = ReportPatientsEntity.getReportPatients(queryItemUuid, runDate, organisationId);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getReportPatientsEHR")
    public Response getReportPatientsEHR(@Context SecurityContext sc, @QueryParam("queryItemUuid") String queryItemUuid, @QueryParam("runDate") String runDate, @QueryParam("organisationId") Short organisationId) throws Exception {
        super.setLogbackMarkers(sc);

        List<Object[]> results = ReportPatientsEntity.getReportPatientsEHR(queryItemUuid, runDate, organisationId);

        Object[] header = new Object[] { "patient_id", "patient_pseudo_id", "patient_sex", "patient_age_years", "patient_age_months", "patient_age_weeks", "patient_date_of_death", "patient_postcode_prefix", "patient_household_id", "patient_lsoa_code", "patient_msoa_code", "patient_townsend_score", "observation_clinical_effective_date", "observation_snomed_concept_id", "observation_original_code", "observation_original_term", "observation_value", "observation_units", "observation_is_problem", "organisation_name", "organisation_ods_code", "organisation_type", "practitioner_name", "practitioner_role_code", "practitioner_role_description" };

        results.add(0, header);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(results)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/runReport")
    public Response runReport(@Context SecurityContext sc, JsonReportRun report) throws Exception {
        super.setLogbackMarkers(sc);

        String userUuid = SecurityUtils.getCurrentUserId(sc).toString();
        String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";

        ResultsManager resultsManager = new ResultsManager();

        ItemEntity item = ItemEntity.retrieveLatestForUUid(report.getQueryItemUuid());
        String xml = item.getXmlContent();
        LibraryItem libraryItem = QueryDocumentSerializer.readLibraryItemFromXml(xml);

        resultsManager.runReport(libraryItem, report, userUuid);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(null)
                .build();
    }

}
