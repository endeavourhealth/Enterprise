package org.endeavourhealth.discovery.core.database;

import java.util.ArrayList;
import java.util.List;

public class TableInformation {

    private List<ColumnInformation> columns = new ArrayList<>();
    private ColumnInformation primaryKey;
    private final String schemaName;
    private final String tableName;

    public TableInformation(String schemaName, String tableName) {
        this.schemaName = schemaName;
        this.tableName = tableName;
    }

    public List<ColumnInformation> getColumns() {
        return columns;
    }

    public ColumnInformation getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(ColumnInformation primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public static class ColumnInformation {
        private final String columnName;
        private final boolean isNullable;
        private Class<?> dataType;

        public ColumnInformation(String columnName, boolean isNullable) {
            this.columnName = columnName;
            this.isNullable = isNullable;
        }

        public String getColumnName() {
            return columnName;
        }

        public boolean isNullable() {
            return isNullable;
        }

        public Class<?> getDataType() {
            return dataType;
        }

        public void setDataType(Class<?> dataType) {
            this.dataType = dataType;
        }
    }
}
