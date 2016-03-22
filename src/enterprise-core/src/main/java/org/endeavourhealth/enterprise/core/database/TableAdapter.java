package org.endeavourhealth.enterprise.core.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TableAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(TableAdapter.class);

    private Class cls = null;
    private String[] columns = null;
    private String[] primaryKeyColumns = null;
    private String cachedTableName = null;
    private String cachedSchemaNameWithPrefix = null;

    public TableAdapter(Class cls, String columns, String primaryKeyColumns) {
        this.cls = cls;
        this.columns = columns.split(",");
        this.primaryKeyColumns = primaryKeyColumns.split(",");
        //cannot derive table and schema name here, as the class hasn't finished loading yet

    }

    public void appendSchemaAndTableName(StringBuilder sb) {
        sb.append(getSchemaName());
        sb.append(getTableName());
    }

    /**
     * gets only
     */
    public Class getCls() {
        return cls;
    }

    public String getSchemaName() {
        if (cachedSchemaNameWithPrefix == null) {
            String packageName = cls.getPackage().getName();
            String[] packages = packageName.split("\\.");
            String last = packages[packages.length - 1];
            if (last.equals("database")) {
                this.cachedSchemaNameWithPrefix = "";
            } else {
                this.cachedSchemaNameWithPrefix = last + ".";
            }
        }
        return cachedSchemaNameWithPrefix;
    }

    public String getTableName() {
        if (cachedTableName == null) {
            String s = cls.getSimpleName();
            if (s.startsWith("Db")) {
                s = s.substring(2);
            }
            cachedTableName = s;
        }
        return cachedTableName;
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