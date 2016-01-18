package org.endeavourhealth.discovery.client.services;

import org.endeavourhealth.discovery.client.services.configuration.ConfigurationAPI;
import org.endeavourhealth.discovery.client.services.core.Constants;
import org.endeavourhealth.discovery.client.services.folders.Folder;
import org.endeavourhealth.discovery.client.services.folders.FolderApi;
import org.endeavourhealth.discovery.client.services.folders.FolderItem;
import org.endeavourhealth.discovery.core.definition.models.ModuleType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

@Path("/folders")
public class FolderWebService {

    @GET
    @Path("/root/{moduleTypeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Folder> rootFolders(@PathParam("moduleTypeId") int moduleTypeId) throws Exception {

        ModuleType moduleType = ModuleType.get(moduleTypeId);

        FolderApi api = new FolderApi();
        List<Folder> folders = api.getRootFolders(Constants.OrganisationUuid, moduleType, ConfigurationAPI.getCoreConnectionDetails());

        return folders;
    }

    @GET
    @Path("/childFolders/{folderUuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Folder> childFolders(@PathParam("folderUuid") UUID folderUuid) throws Exception {
        FolderApi api = new FolderApi();
        List<Folder> folders = api.getChildFolders(Constants.OrganisationUuid, folderUuid, ConfigurationAPI.getCoreConnectionDetails());

        return folders;
    }

    @GET
    @Path("/content/{folderUuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FolderItem> content(@PathParam("folderUuid") UUID folderUuid) throws Exception {

        FolderApi api = new FolderApi();
        List<FolderItem> items = api.getContent(Constants.OrganisationUuid, folderUuid, ConfigurationAPI.getCoreConnectionDetails());

        return items;
    }
}
