package org.endeavourhealth.enterprise.core.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TableAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(TableAdapter.class);

    private Class cls = null;
    private String tableName = null;
    private String schema = null;
    private String[] columns = null;
    private String[] primaryKeyColumns = null;


    public TableAdapter(Class cls, String tableName, String schema, String columns, String primaryKeyColumns) {
        this.cls = cls;
        this.tableName = tableName;
        this.schema = schema;
        this.columns = columns.split(",");
        this.primaryKeyColumns = primaryKeyColumns.split(",");
    }

    /**
     * gets only
     */
    public Class getCls() {
        return cls;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSchema() {
        return schema;
    }

    public String[] getColumns() {
        return columns;
    }

    public String[] getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }

    /**
     * creates a new instance of our database class
     */
    public DbAbstractTable newEntity() throws Exception {
        return (DbAbstractTable) getCls().newInstance();
    }

}
