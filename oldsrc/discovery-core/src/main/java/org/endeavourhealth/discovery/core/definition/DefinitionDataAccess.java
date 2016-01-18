package org.endeavourhealth.discovery.core.definition;

import net.sourceforge.jtds.jdbc.JtdsResultSet;
import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.discovery.core.database.DatabaseConnectionDetails;
import org.endeavourhealth.discovery.core.database.DatabaseHelper;
import org.endeavourhealth.discovery.core.definition.models.Dependency;
import org.endeavourhealth.discovery.core.definition.models.Item;
import org.endeavourhealth.discovery.core.definition.models.ItemType;
import org.endeavourhealth.discovery.core.definition.models.ModuleType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class DefinitionDataAccess {

    public static Integer findCurrentAuditId(DatabaseConnectionDetails connectionDetails) throws SQLException, ClassNotFoundException {
        String sql = "select max(AuditId) from [Definition].[Audit];";

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                Statement ps = con.createStatement();
        ) {
            try (JtdsResultSet rs = (JtdsResultSet)ps.executeQuery(sql)) {
                rs.next();

                int auditId = rs.getInt(1);

                if (rs.wasNull())
                    return null;
                else
                    return auditId;
            }
        }
    }

    public static List<Item> getItemWithDependencies(DatabaseConnectionDetails connectionDetails, UUID startingItemUuid) throws SQLException, ClassNotFoundException {
        List<Item> items = getItemsAndDependants(connectionDetails, startingItemUuid);
        Map<UUID, List<Dependency>> dependencies = getActiveItemDependencies(connectionDetails, startingItemUuid);

        for (Item item : items) {
            List<Dependency> dependants = dependencies.get(item.getItemUuid());

            if (CollectionUtils.isNotEmpty(dependants))
                item.setDependencies(dependants);
        }

        return items;
    }

    private static List<Item> getItemsAndDependants(DatabaseConnectionDetails connectionDetails, UUID startingItemUuid) throws SQLException, ClassNotFoundException {
        String sql = "with dependencyTree as (\n" +
                "\n" +
                "select d.ItemUuid, d.DependsOnItemUuid\n" +
                "from [Definition].ActiveItemDependency as d\n" +
                "where d.ItemUuid = ?\n" +
                "\n" +
                "union all\n" +
                "\n" +
                "select d.ItemUuid, d.DependsOnItemUuid\n" +
                "from [Definition].ActiveItemDependency as d\n" +
                "inner join dependencyTree as i on i.DependsOnItemUuid = d.ItemUuid\n" +
                ")\n" +
                "select i.ItemUuid, i.AuditId, i.Content, i.IsDeleted, i.OwnerOrganisationUuid, i.ItemTypeId, i.Title, i.[Description]\n" +
                "from dependencyTree as d\n" +
                "inner join [Definition].ActiveItems as a\n" +
                "on a.ItemUuid = d.ItemUuid\n" +
                "inner join [Definition].Items as i\n" +
                "on i.ItemUuid = a.ItemUuid and i.AuditId = a.CurrentAuditId;";

        List<Item> results = new ArrayList<>();

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setEscapeProcessing(true);
            ps.setString(1, startingItemUuid.toString());

            try (JtdsResultSet rs = (JtdsResultSet) ps.executeQuery(sql)) {
                while (rs.next()) {

                    Item item = new Item(
                            UUID.fromString(rs.getString(1)),
                            rs.getInt(2),
                            rs.getString(3),
                            rs.getBoolean(4),
                            UUID.fromString(rs.getString(5)),
                            ItemType.get(rs.getByte(6)),
                            rs.getString(7),
                            rs.getString(8)
                    );

                    results.add(item);
                }
            }
        }

        return results;
    }

    private static Map<UUID, List<Dependency>> getActiveItemDependencies(DatabaseConnectionDetails connectionDetails, UUID startingItemUuid) throws SQLException, ClassNotFoundException {
        String sql =
                "with dependencyTree as (\n" +
                "\n" +
                "select d.ItemUuid, d.DependsOnItemUuid, d.DrivesUI\n" +
                "from [Definition].ActiveItemDependency as d\n" +
                "where d.ItemUuid = ?\n" +
                "\n" +
                "union all\n" +
                "\n" +
                "select d.ItemUuid, d.DependsOnItemUuid, d.DrivesUI\n" +
                "from [Definition].ActiveItemDependency as d\n" +
                "inner join dependencyTree as i on i.DependsOnItemUuid = d.ItemUuid\n" +
                ")\n" +
                "select d.ItemUuid, d.DependsOnItemUuid, d.DrivesUI\n" +
                "from dependencyTree as d;\n";

        Map<UUID, List<Dependency>> results = new HashMap<>();

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setEscapeProcessing(true);
            ps.setString(1, startingItemUuid.toString());

            try (JtdsResultSet rs = (JtdsResultSet) ps.executeQuery(sql)) {
                while (rs.next()) {

                    UUID itemId = UUID.fromString(rs.getString(1));

                    if (!results.containsKey(itemId))
                        results.put(itemId, new ArrayList<>());

                    Dependency dependency = new Dependency(
                            UUID.fromString(rs.getString(2)),
                            rs.getBoolean(3)
                    );

                    results.get(itemId).add(dependency);
                }
            }
        }

        return results;
    }
}
