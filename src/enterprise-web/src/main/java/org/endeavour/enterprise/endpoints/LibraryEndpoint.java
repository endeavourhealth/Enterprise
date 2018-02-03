package org.endeavour.enterprise.endpoints;

import org.apache.commons.lang3.StringUtils;
import org.endeavourhealth.common.security.SecurityUtils;
import org.endeavourhealth.core.terminology.Snomed;
import org.endeavourhealth.core.terminology.SnomedCode;
import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;

import org.endeavourhealth.enterprise.core.database.models.*;
import org.endeavourhealth.enterprise.core.database.models.data.*;
import org.endeavourhealth.enterprise.core.json.*;
import org.endeavourhealth.enterprise.core.querydocument.QueryDocumentSerializer;
import org.endeavourhealth.enterprise.core.querydocument.models.*;
import org.endeavourhealth.core.terminology.TerminologyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;

@Path("/library")
public final class LibraryEndpoint extends AbstractItemEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(LibraryEndpoint.class);


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/getFolderContents")
	public Response getFolderContents(@Context SecurityContext sc, @QueryParam("folderUuid") String uuidStr) throws Exception {
		super.setLogbackMarkers(sc);

		String folderUuid = uuidStr;
		String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";

		LOG.trace("GettingFolderContents for folder {}", folderUuid);

		JsonFolderContentsList ret = new JsonFolderContentsList();

		List<ActiveItemEntity> childActiveItems = ActiveItemEntity.retrieveDependentItems(orgUuid, folderUuid, (short)DependencyType.IsContainedWithin.getValue());

		HashMap<String, AuditEntity> hmAuditsByAuditUuid = new HashMap<>();
		List<AuditEntity> audits = AuditEntity.retrieveForActiveItems(childActiveItems);
		for (AuditEntity audit: audits) {
			hmAuditsByAuditUuid.put(audit.getAuditUuid(), audit);
		}

		HashMap<String, ItemEntity> hmItemsByItemUuid = new HashMap<>();
		List<ItemEntity> items = ItemEntity.retrieveForActiveItems(childActiveItems);
		for (ItemEntity item: items) {
			hmItemsByItemUuid.put(item.getItemUuid(), item);
		}

		HashMap<String, CohortResultEntity> hmReportsByItemUuid = new HashMap<>();
		List<CohortResultEntity> reports = ItemEntity.retrieveForReports(childActiveItems);
		for (CohortResultEntity report: reports) {
			hmReportsByItemUuid.put(report.getQueryItemUuid(), report);
		}

		for (int i = 0; i < childActiveItems.size(); i++) {

			ActiveItemEntity activeItem = childActiveItems.get(i);
			ItemEntity item = hmItemsByItemUuid.get(activeItem.getItemUuid());
			Short itemType = activeItem.getItemTypeId();
			AuditEntity audit = hmAuditsByAuditUuid.get(item.getAuditUuid());
			CohortResultEntity cohort = hmReportsByItemUuid.get(activeItem.getItemUuid());

			JsonFolderContent c = new JsonFolderContent(activeItem, item, audit, cohort);
			ret.addContent(c);

			if (itemType == DefinitionItemType.Query.getValue()) {

			} else if (itemType == DefinitionItemType.Test.getValue()) {

			} else if (itemType == DefinitionItemType.DataSource.getValue()) {

			} else if (itemType == DefinitionItemType.CodeSet.getValue()) {

			} else if (itemType == DefinitionItemType.Report.getValue()) {
				LibraryItem libraryItem = QueryDocumentSerializer.readLibraryItemFromXml(item.getXmlContent());
				Report report = libraryItem.getReport();
				if (report != null && report.getLastRunDate() != null)
					c.setLastRun(new Date(report.getLastRunDate()));
			} else {
				//throw new RuntimeException("Unexpected content " + item + " in folder");
			}
		}

		if (ret.getContents() != null) {
			Collections.sort(ret.getContents());
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
        Report report = libraryItem.getReport();

        LOG.trace(String.format("SavingLibraryItem UUID %s, Name %s FolderUuid %s", libraryItemUuid, name, folderUuid));

        QueryDocument doc = new QueryDocument();
        doc.getLibraryItem().add(libraryItem);

        //work out the item type (query, test etc.) from the content passed up
        Short type = null;
        if (query != null) {
            type = (short)DefinitionItemType.Query.getValue();
        } else if (codeSet != null) {
					type = (short) DefinitionItemType.CodeSet.getValue();
				} else if (report != null) {
					type = (short) DefinitionItemType.Report.getValue();
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
		@Path("/getLibraryItemNames")
		public Response getLibraryItemNames(@Context SecurityContext sc, @QueryParam("itemUuids") List<String> itemUuids) {
			super.setLogbackMarkers(sc);

			LOG.trace("getLibraryItemNames", itemUuids);
			Map<String, String> names = new HashMap<>();

			for (String itemUuid : itemUuids) {
				try {
					ItemEntity item = ItemEntity.retrieveLatestForUUid(itemUuid);
					names.put(itemUuid, item.getTitle());
				} catch (Exception e) {
					names.put(itemUuid, "Error!");
					LOG.error("Error loading name for library item " + itemUuid);
				}
			}

			return Response
					.ok()
					.entity(names)
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
            String parentConceptTypeId = conceptEntity[8]==null?"":conceptEntity[8].toString();
            String present = conceptEntity[9]==null?"":conceptEntity[9].toString();
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
    @Path("/getCodeSets")
    public Response getCodeSets(@Context SecurityContext sc) throws Exception {
        super.setLogbackMarkers(sc);

        String userUuid = SecurityUtils.getCurrentUserId(sc).toString();

        String orgUuid = "B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6";

        List<JsonFolderContent> ret = new ArrayList<>();

        ActiveItemEntity aI = new ActiveItemEntity();

        List<ActiveItemEntity> activeItems = aI.retrieveActiveItemCodeSets(userUuid, orgUuid);
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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getConceptsFromRead")
    public Response getConceptsFromRead(@Context SecurityContext sc,
                                        @QueryParam("inclusions") String inclusions,
                                        @QueryParam("exclusions") String exclusions) throws Exception {
        super.setLogbackMarkers(sc);

        return processReadLists(inclusions, exclusions);


    }

    private static Response processReadLists(String inclusions, String exclusions) throws Exception {

	    List<String> inclusionCodes = convertStringToList(inclusions);
        List<String> exclusionCodes = new ArrayList<>();

	    if (exclusions != null && !exclusions.isEmpty()) {
            exclusionCodes = convertStringToList(exclusions);
        }

	    List<String> allCodes = findChildCodes(inclusionCodes);
	    List<String> excludedCodes = new ArrayList<>();

	    if (exclusionCodes.size() > 0) {
	        excludedCodes = findChildCodes(exclusionCodes);
        }

        removeExcludedCodes(allCodes, excludedCodes);

	    List<SnomedCode> snomedCodes = getSnomedFromReadList(allCodes);

	    List<JsonCode> codes = generateResultSet(snomedCodes);

        clearLogbackMarkers();

        return Response
                .ok()
                .entity(codes)
                .build();
    }

    private static List<String> convertStringToList(String codeString) throws Exception {
	    codeString = codeString.replaceAll(" ", "");
	    String[] codes = codeString.split(",");

	    return Arrays.asList(codes);
    }

    private static void removeExcludedCodes(List<String> includedCodes, List<String> excludedCodes) throws Exception {
	    includedCodes.removeAll(excludedCodes);
    }

    private static List<String> findChildCodes(List<String> codes) throws Exception {

	    List<Long> parents = new ArrayList<>();
	    List<String> childCodes = new ArrayList<>();

	    for(String code : codes) {
	        if (code.endsWith("%")) {
	            String formattedCode = code.replace("%","");
                formattedCode = padCode(formattedCode);
	            childCodes.add(formattedCode);
                parents.add(EmisCsvCodeMapEntity.findCodeIdFromReadCode(formattedCode));
                while (parents.size() > 0) {
                    parents = getChildren(parents, childCodes);
                    System.out.println(childCodes);
                }
	        } else {
	            childCodes.add(code);
            }
        }

        return childCodes;
    }

    private static String padCode(String code) throws Exception {
        return StringUtils.rightPad(code, 5, ".");
    }

    private static List<Long> getChildren(List<Long> parents, List<String> children) throws Exception {
	    List<EmisCsvCodeMapEntity> codeMaps = EmisCsvCodeMapEntity.findChildCodes(parents);

	    parents.clear();
	    for (EmisCsvCodeMapEntity code : codeMaps) {
	        children.add(code.getReadCode());
	        parents.add(code.getCodeId());
        }

        return parents;
    }

    private static List<SnomedCode> getSnomedFromReadList(List<String> readCodes) throws Exception {

	    removeSynonyms(readCodes);
	    List<SnomedCode> snomedCodes = new ArrayList<>();
	    for (String code : readCodes) {
            System.out.println("getting the snomed for the following code : " + code);
            try {
                snomedCodes.add(TerminologyService.translateRead2ToSnomed(code));
            } catch (Exception e) {
                System.out.println("unable to find snomed for code : " + code);
            }
        }

        return snomedCodes;
    }

    private static void removeSynonyms(List<String> readCodes) throws Exception {

	    for (Iterator<String> iterator = readCodes.iterator(); iterator.hasNext();) {
	        String code = iterator.next();
	        if (code.contains("-")) {
	            iterator.remove();
            }
        }
    }

    private static List<JsonCode> generateResultSet(List<SnomedCode> snomedCodes) throws Exception {

	    List<JsonCode> codes = new ArrayList<>();
        for (SnomedCode snomed : snomedCodes) {
            JsonCode code =  new JsonCode();
            code.setId(snomed.getConceptCode());
            code.setLabel(snomed.getTerm());
            code.setDataType("11");
            code.setParentType("");
            code.setBaseType("Observation");
            code.setPresent("1");
            code.setUnits("");
            codes.add(code);
        }

        return codes;
    }

}
