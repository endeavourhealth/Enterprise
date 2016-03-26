package org.endeavourhealth.enterprise.core.database;

import org.endeavourhealth.enterprise.core.DefinitionItemType;
import org.endeavourhealth.enterprise.core.DependencyType;
import org.endeavourhealth.enterprise.core.ExecutionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

public final class TableAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(TableAdapter.class);

    private Class cls = null;
    private String cachedTableName = null;
    private String cachedSchemaNameWithPrefix = null;
    private String[] cachedColumns = null;
    private String[] cachedPrimaryKeyColumns = null;
    private List<Field> cachedColumnFields = null;
    private List<Field> cachedPrimaryKeyFields = null;
    private HashMap<Field, Method> cachedGetMethods = null;
    private HashMap<Field, Method> cachedSetMethods = null;

    public TableAdapter(Class cls) {
        this.cls = cls;
        //all caching is done lazily, as the class hasn't finished loading yet and can't be accessed

    }

    public void appendSchemaAndTableName(StringBuilder sb) {
        sb.append(getSchemaName());
        sb.append(getTableName());
    }

    public void writeForDb(DbAbstractTable entity, ArrayList<Object> builder) throws Exception {
        if (!entity.getClass().equals(cls)) {
            throw new RuntimeException("Expected " + cls + " but got " + entity.getClass());
        }

        List<Field> fields = getColumnFields();
        HashMap<Field, Method> hmMethods = getGetMethods();

        for (Field field: fields) {
            Method method = hmMethods.get(field);
            Object obj = method.invoke(entity);
            builder.add(obj);
        }
    }

    public void readFromDb(DbAbstractTable entity, ResultReader reader) throws Exception {

        List<Field> fields = getColumnFields();
        HashMap<Field, Method> hmMethods = getSetMethods();

        for (Field field: fields) {
            Method method = hmMethods.get(field);
            Class type = field.getType();
            if (type == UUID.class) {
                method.invoke(entity, reader.readUuid());
            } else if (type == String.class) {
                method.invoke(entity, reader.readString());
            } else if (type == Instant.class) {
                method.invoke(entity, reader.readDateTime());
            } else if (type == Integer.class) { //we use Integer object fields
                method.invoke(entity, reader.readInt());
            } else if (type == Boolean.TYPE) { //we use primitive boolean fields
                method.invoke(entity, reader.readBoolean());
            } else if (type == DefinitionItemType.class) {
                method.invoke(entity, DefinitionItemType.get(reader.readInt()));
            } else if (type == DependencyType.class) {
                method.invoke(entity, DependencyType.get(reader.readInt()));
            } else if (type == ExecutionStatus.class) {
                method.invoke(entity, ExecutionStatus.get(reader.readInt()));
            } else {
                throw new RuntimeException("Unsupported field class for database persistance " + type);
            }
        }
    }

    public List<Object> getPrimaryKeys(DbAbstractTable entity) throws Exception {
        if (!entity.getClass().equals(cls)) {
            throw new RuntimeException("Expected " + cls + " but got " + entity.getClass());
        }

        List<Object> ret = new ArrayList<>();

        List<Field> primaryKeyFields = getPrimaryKeyFields();
        for (Field field: primaryKeyFields) {
            ret.add(field.get(entity));
        }

        return ret;
    }

    public boolean hasPrimaryKeysSet(DbAbstractTable entity) throws Exception {
        if (!entity.getClass().equals(cls)) {
            throw new RuntimeException("Expected " + cls + " but got " + entity.getClass());
        }

        List<Field> primaryKeyFields = getPrimaryKeyFields();
        HashMap<Field, Method> hmMethods = getGetMethods();

        for (Field field: primaryKeyFields) {
            Method method = hmMethods.get(field);
            if (method.invoke(entity) == null) {
                return false;
            }
        }

        return true;
    }

    public void assignPrimaryKeys(DbAbstractTable entity) throws Exception {
        if (!entity.getClass().equals(cls)) {
            throw new RuntimeException("Expected " + cls + " but got " + entity.getClass());
        }

        List<Field> primaryKeyFields = getPrimaryKeyFields();
        HashMap<Field, Method> hmGetMethods = getGetMethods();
        HashMap<Field, Method> hmSetMethods = getSetMethods();

        for (Field field: primaryKeyFields) {
            Method getMethod = hmGetMethods.get(field);
            if (getMethod.invoke(entity) != null) {
                continue;
            }

            if (field.getType() == UUID.class) {
                Method setMethod = hmSetMethods.get(field);
                setMethod.invoke(entity, UUID.randomUUID());
            } else {
                throw new RuntimeException("Only UUID primary key columns are supported");
            }
        }
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

        if (cachedColumns == null) {
            cachedColumns = getFieldNamesWithAnnotation(null);
        }
        return cachedColumns;
    }

    public String[] getPrimaryKeyColumns() {
        if (cachedPrimaryKeyColumns == null) {
            cachedPrimaryKeyColumns = getFieldNamesWithAnnotation(PrimaryKeyColumn.class);
        }
        return cachedPrimaryKeyColumns;
    }

    public List<Field> getColumnFields()
    {
        if (cachedColumnFields == null) {
            List<Field> v = new ArrayList<>();
            Field[] fields = cls.getDeclaredFields();
            for (Field field: fields) {
                if (field.isAnnotationPresent(DatabaseColumn.class)) {
                    v.add(field);
                }
            }
            cachedColumnFields = v;
        }
        return cachedColumnFields;
    }

    public List<Field> getPrimaryKeyFields() {
        if (cachedPrimaryKeyFields == null) {
            List<Field> v = new ArrayList<>();
            List<Field> columnFields = getColumnFields();
            for (Field field: columnFields) {
                if (field.isAnnotationPresent(PrimaryKeyColumn.class)) {
                    v.add(field);
                }
            }
            cachedPrimaryKeyFields = v;
        }
        return cachedPrimaryKeyFields;
    }

    public HashMap<Field, Method> getGetMethods() {
        if (cachedGetMethods == null) {
            cachedGetMethods = getMethodsForColumnsAndPrefix(true);
        }
        return cachedGetMethods;
    }

    public HashMap<Field, Method> getSetMethods() {
        if (cachedSetMethods == null) {
            cachedSetMethods = getMethodsForColumnsAndPrefix(false);
        }
        return cachedSetMethods;
    }


    /**
     * creates a new instance of our database class
     */
    public DbAbstractTable newEntity() throws Exception {
        return (DbAbstractTable) getCls().newInstance();
    }

    private String[] getFieldNamesWithAnnotation(Class annotationCls) {
        List<String> v = new ArrayList<>();

        List<Field> fields = getColumnFields();
        for (Field field: fields) {
            if (annotationCls == null
                    || field.isAnnotationPresent(annotationCls)) {

                String name = uppercaseFieldName(field);
                v.add(name);
            }
        }

        return v.toArray(new String[0]);
    }

    private HashMap<Field, Method> getMethodsForColumnsAndPrefix(boolean getMethods) {

        HashMap<Field, Method> hm = new HashMap<>();

        List<Field> fields = getColumnFields();
        for (Field field: fields) {
            try {
                Method m = getAccessorMethodForField(getMethods, field);
                hm.put(field, m);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        return hm;
    }
    private Method getAccessorMethodForField(boolean getMethod, Field field) throws NoSuchMethodException {
        Class type = field.getType();

        //boolean get/set accessors use a different pattern to all others
        if (type == Boolean.TYPE) {
            String fieldName = field.getName();
            if (getMethod) {
                if (fieldName.startsWith("is")) {
                    //if the field starts with "is", then the GET method is the same as the fieldName
                    return cls.getMethod(fieldName);
                } else {
                    //if the field name doesn't start with "is", then the GET method will have IS prefixed
                    return cls.getMethod("get" + uppercaseFieldName(field));
                }
            } else {
                if (fieldName.startsWith("is")) {
                    //if the field starts with "is", then the SET method drops the "is"
                    fieldName = fieldName.substring(2);
                    return cls.getMethod("set" + fieldName, type);
                } else {
                    //if the field name doesn't start with "is", then the SET method will have IS prefixed
                    return cls.getMethod("set" + uppercaseFieldName(field), type);
                }
            }
        } else {
            if (getMethod) {
                return cls.getMethod("get" + uppercaseFieldName(field));
            } else {
                return cls.getMethod("set" + uppercaseFieldName(field), type);
            }
        }
    }

    private static String uppercaseFieldName(Field field) {
        String name = field.getName();
        char[] chars = name.toCharArray();
        char first = chars[0];
        first = Character.toUpperCase(first);
        chars[0] = first;
        return new String(chars);
    }
}