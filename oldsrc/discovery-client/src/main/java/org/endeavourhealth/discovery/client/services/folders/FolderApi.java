package org.endeavourhealth.discovery.client.services.folders;

import net.sourceforge.jtds.jdbc.JtdsResultSet;
import org.endeavourhealth.discovery.core.database.DatabaseConnectionDetails;
import org.endeavourhealth.discovery.core.database.DatabaseHelper;
import org.endeavourhealth.discovery.core.definition.models.ItemType;
import org.endeavourhealth.discovery.core.definition.models.ModuleType;
import org.joda.time.LocalDateTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FolderApi {

    public List<Folder> getRootFolders(
            UUID organisationUuid,
            ModuleType moduleType,
            DatabaseConnectionDetails connectionDetails) throws Exception {

        String sql = "\n" +
                "SELECT\n" +
                "\ta.ItemUuid,\n" +
                "\ta.CurrentAuditId,\n" +
                "\ta.Title,\n" +
                "\tCASE WHEN exists (SELECT NULL FROM [Definition].ActiveItemDependency AS dc WHERE dc.DependsOnItemUuid = a.ItemUuid AND dc.DependencyTypeId = 0)\n" +
                "        THEN 1\n" +
                "        ELSE 0\n" +
                "\tEND AS HasChildren\n" +
                "FROM Definition.ActiveItems AS a\n" +
                "LEFT JOIN Definition.ActiveItemDependency AS d\n" +
                "\tON d.ItemUuid = a.ItemUuid\n" +
                "\tAND d.DependencyTypeId = 0\n" +
                "WHERE a.OwnerOrganisationUuid = ?\n" +
                "AND a.ModuleId = ?\n" +
                "AND a.ItemTypeId = 0\n" +
                "AND d.ItemUuid IS NULL\n" +
                "ORDER BY a.Title;";

        List<Folder> folders = new ArrayList<>();

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setEscapeProcessing(true);
            ps.setString(1, organisationUuid.toString());
            ps.setInt(2, moduleType.getValue());

            receiveFolders(folders, ps);
        }

        return folders;
    }

    private void receiveFolders(List<Folder> folders, PreparedStatement ps) throws SQLException {
        try (JtdsResultSet rs = (JtdsResultSet)ps.executeQuery()) {
            while (rs.next()) {
                Folder folder = new Folder();

                folder.setFolderGuid(UUID.fromString(rs.getString(1)));
                folder.setAuditId(rs.getInt(2));
                folder.setName(rs.getString(3));
                folder.setHasChildren(rs.getBoolean(4));

                folders.add(folder);
            }
        }
    }

    public List<Folder> getChildFolders(
            UUID organisationUuid,
            UUID folderUuid,
            DatabaseConnectionDetails connectionDetails) throws Exception {

        String sql = "SELECT\n" +
                "\ta.ItemUuid,\n" +
                "\ta.CurrentAuditId,\n" +
                "\ta.Title,\n" +
                "\tCASE WHEN exists (SELECT NULL FROM [Definition].ActiveItemDependency AS dc WHERE dc.DependsOnItemUuid = a.ItemUuid AND dc.DependencyTypeId = 0)\n" +
                "        THEN 1\n" +
                "        ELSE 0\n" +
                "\tEND AS HasChildren\n" +
                "FROM Definition.ActiveItems AS a\n" +
                "INNER JOIN Definition.ActiveItemDependency AS d\n" +
                "\tON d.ItemUuid = a.ItemUuid\n" +
                "\tAND d.DependsOnItemUuid = ?\n" +
                "\tAND d.DependencyTypeId = 0\n" +
                "WHERE a.OwnerOrganisationUuid = ?\n" +
                "AND a.ModuleId = 0\n" +
                "AND a.ItemTypeId = 0\n" +
                "ORDER BY a.Title;";

        List<Folder> folders = new ArrayList<>();

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setEscapeProcessing(true);
            ps.setString(1, folderUuid.toString());
            ps.setString(2, organisationUuid.toString());

            receiveFolders(folders, ps);
        }

        return folders;
    }

    public List<FolderItem> getContent(UUID organisationUuid, UUID folderUuid, DatabaseConnectionDetails coreConnectionDetails) throws Exception {

        String sql = "select\n" +
                "\ta.ItemUuid,\n" +
                "\ta.Title,\n" +
                "\ta.ItemTypeId,\n" +
                "\taud.DateTime,\n" +
                "\t(select dc.DependsOnItemUuid from [Definition].ActiveItemDependency as dc where dc.ItemUuid = a.ItemUuid and dc.DependencyTypeId = 0) as ParentUuid\n" +
                "from Definition.ActiveItems as a\n" +
                "inner join Definition.ActiveItemDependency as d\n" +
                "\ton d.ItemUuid = a.ItemUuid\n" +
                "\tand d.DependsOnItemUuid = ?\n" +
                "\tand d.DependencyTypeId = 1\n" +
                "inner join Definition.[Audit] as aud\n" +
                "\ton aud.AuditId = a.CurrentAuditId\n" +
                "where a.OwnerOrganisationUuid = ?\n" +
                "and a.ModuleId = 0\n" +
                "and a.ItemTypeId != 0\n" +
                "order by a.Title;";

        List<FolderItem> items = new ArrayList<>();

        try (
                Connection con = DatabaseHelper.getConnection(coreConnectionDetails);
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setEscapeProcessing(true);
            ps.setString(1, folderUuid.toString());
            ps.setString(2, organisationUuid.toString());

            try (JtdsResultSet rs = (JtdsResultSet)ps.executeQuery()) {
                while (rs.next()) {
                    FolderItem item = new FolderItem();

                    item.setItemUuid(UUID.fromString(rs.getString(1)));
                    item.setTitle(rs.getString(2));
                    item.setItemType(ItemType.get(rs.getInt(3)));
                    item.setAuditDateTime(new LocalDateTime(rs.getTimestamp(4)));

                    String parentUuidString = rs.getString(5);

                    if (!rs.wasNull())
                        item.setParentUuid(UUID.fromString(parentUuidString));

                    items.add(item);
                }
            }
        }

        return items;
    }
}
