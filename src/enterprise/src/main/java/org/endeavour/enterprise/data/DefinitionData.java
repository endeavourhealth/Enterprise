package org.endeavour.enterprise.data;

import org.endeavour.enterprise.framework.database.DatabaseConnection;
import org.endeavour.enterprise.framework.database.StoredProcedure;
import org.endeavour.enterprise.model.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefinitionData {

	public List<Folder> getRootFolders(UUID organisationUuid, Module module) throws Exception {
		try (Connection conn = DatabaseConnection.get(DatabaseName.ENDEAVOUR_ENTERPRISE)) {
			try (StoredProcedure storedProcedure = new StoredProcedure(conn, "Definition.GetRootFolders")) {
				storedProcedure.setParameter("@OrganisationUuid", organisationUuid.toString());
				storedProcedure.setParameter("@ModuleId", module.getValue());

				ResultSet resultSet = storedProcedure.executeQuery();

				ArrayList<Folder> result = new ArrayList<>();

				while (resultSet.next()) {
					Folder folder = new Folder();
					folder.setItemUuid(UUID.fromString(resultSet.getString("ItemUuid")));
					folder.setTitle(resultSet.getString("Title"));
					folder.setHasChildren(resultSet.getBoolean("HasChildren"));

					result.add(folder);
				}

				return result;
			}
		}
	}

	public List<Folder> getChildFolders(UUID itemUuid) throws Exception {
		try (Connection conn = DatabaseConnection.get(DatabaseName.ENDEAVOUR_ENTERPRISE)) {
			try (StoredProcedure storedProcedure = new StoredProcedure(conn, "Definition.GetChildFolders")) {
				storedProcedure.setParameter("@ItemUuid", itemUuid.toString());

				ResultSet resultSet = storedProcedure.executeQuery();

				ArrayList<Folder> result = new ArrayList<>();

				while (resultSet.next()) {
					Folder folder = new Folder();
					folder.setItemUuid(UUID.fromString(resultSet.getString("ItemUuid")));
					folder.setTitle(resultSet.getString("Title"));
					folder.setHasChildren(resultSet.getBoolean("HasChildren"));

					result.add(folder);
				}

				return result;
			}
		}
	}

	public List<DefinitionItemSummary> getFolderContents(UUID folderId) throws Exception {
		try (Connection conn = DatabaseConnection.get(DatabaseName.ENDEAVOUR_ENTERPRISE)) {
			try (StoredProcedure storedProcedure = new StoredProcedure(conn, "Definition.GetFolderContents")) {

				storedProcedure.setParameter("@FolderUuid", folderId.toString());

				ResultSet resultSet = storedProcedure.executeQuery();

				ArrayList<DefinitionItemSummary> result = new ArrayList<>();

				while (resultSet.next()) {
					DefinitionItemSummary definitionItemSummary = new DefinitionItemSummary();
					definitionItemSummary.setItemUuid(UUID.fromString(resultSet.getString("ItemUuid")));
					definitionItemSummary.setTitle(resultSet.getString("Title"));
					definitionItemSummary.setType(DefinitionItemType.valueOf(resultSet.getInt("ItemTypeId")));

					result.add(definitionItemSummary);
				}

				return result;
			}
		}
	}
}
