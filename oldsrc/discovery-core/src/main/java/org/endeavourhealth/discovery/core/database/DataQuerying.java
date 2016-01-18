package org.endeavourhealth.discovery.core.database;

import net.sourceforge.jtds.jdbc.JtdsResultSet;
import org.endeavourhealth.discovery.core.entitymap.EntityMapException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

class DataQuerying {

    private static final String queryTableSql = "select\n"+
            "  c.[name] as ColumnName,\n"+
            "  t.[name] as TypeName,\n"+
            "  c.is_nullable as IsNullable,\n"+
            "  c.collation_name as Collation,\n"+
            "  (\n"+
            "\tselect cast(1 as bit)\n"+
            "\tfrom sys.index_columns as ic\n"+
            "\tinner join sys.indexes as i on i.index_id = ic.index_id\n"+
            "\t\tand i.[object_id] = ic.[object_id]\n"+
            "\twhere ic.[object_id] = c.[object_id]\n"+
            "\t\tand ic.column_id = c.column_id\n"+
            "\t\tand i.is_primary_key = 1\n"+
            "  ) as IsPrimaryKey\n"+
            "from sys.columns as c\n"+
            "inner join sys.objects as o on c.[object_id] = o.[object_id]\n"+
            "inner join sys.schemas as s on s.[schema_id] = o.[schema_id]\n"+
            "inner join sys.types as t on c.system_type_id = t.system_type_id\n"+
            "where s.name = ?\n"+
            "and o.name = ?\n"+
            "and o.[type] in ('U', 'V', 'TF')\n"+
            "and t.system_type_id = t.user_type_id\n"+
            "order by c.column_id\n";

    public static TableInformation queryTable(DatabaseConnectionDetails connectionDetails, String schemaName, String tableName) throws SQLException, ClassNotFoundException, EntityMapException {

        TableInformation result = new TableInformation(schemaName, tableName);

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                PreparedStatement ps = con.prepareStatement(queryTableSql);
        ) {
            ps.setEscapeProcessing(true);
            ps.setString(1, schemaName);
            ps.setString(2, tableName);

            try (JtdsResultSet rs = (JtdsResultSet)ps.executeQuery()) {
                while (rs.next()) {

                    TableInformation.ColumnInformation columnInformation = new TableInformation.ColumnInformation(
                            rs.getString(1), 
                            rs.getBoolean(3)
                    );

                    columnInformation.setDataType(mapType(rs.getString(2)));

                    result.getColumns().add(columnInformation);

                    if (rs.getBoolean(5)) {
                        if (result.getPrimaryKey() != null) {
                            throw new EntityMapException(schemaName + "." + tableName + " contains composite primary key");
                        } else {
                            result.setPrimaryKey(columnInformation);
                        }
                    }
                }
            }
        }

        if (result.getPrimaryKey() == null)
            throw new EntityMapException(schemaName + "." + tableName + " does not contain a primary key");

        return result;
    }

    private static Class<?> mapType(String databaseType) {
        switch (databaseType.toLowerCase()) {
            case "int":
                return int.class;
            case "date":
                return org.joda.time.LocalDate.class;
            case "datetime":
            case "datetime2":
                return org.joda.time.DateTime.class;
            case "time":
                return org.joda.time.LocalTime.class;
            case "tinyint":
                return byte.class;
            case "uniqueidentifier":
                return UUID.class;
            case "varchar":
            case "char":
            case "nvarchar":
            case "nchar":
                return String.class;
            case "bit":
                return boolean.class;

            default:
                throw new IllegalArgumentException("Database type not supported: " + databaseType);
        }
    }
}
