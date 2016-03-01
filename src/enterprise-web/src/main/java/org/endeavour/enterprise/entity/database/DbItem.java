package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.model.DatabaseName;
import org.endeavour.enterprise.model.DefinitionItemType;
import org.endeavour.enterprise.model.DependencyType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Drew on 25/02/2016.
 */
public final class DbItem extends DbAbstractTable
{
    //register as a DB entity
    private static final TableAdapter adapter = new TableAdapter(DbItem.class, "Item", "Definition", DatabaseName.ENDEAVOUR_ENTERPRISE,
            "ItemUuid,Version,XmlContent,Title,Description,EndUserUuid,TimeStamp,IsDeleted", "ItemUuid,Version");

    private int version = -1;
    private String xmlContent = null; //xml
    private String title = null;
    private String description = null;
    private UUID endUserUuid = null;
    private Date timeStamp = null;
    private boolean isDeleted = false;
    //private DefinitionItemType itemType = null; //2016-02-29 DL - this can never change, so is on the ActiveItem entity

    public DbItem()
    {}
    public static DbItem factoryNew(UUID endUserUuid, String title)
    {
        DbItem ret = new DbItem();
        ret.setVersion(1); //doesn't really matter, but start somewhere positive
        ret.setTitle(title);
        ret.setTimeStamp(new Date());
        ret.setEndUserUuid(endUserUuid);
        return ret;
    }

    /*public static DbItem retrieveForUuidVersion(UUID uuid, int version) throws Exception
    {
        return (DbItem)adapter.retrieveSingleEntity("Definition.Item_SelectForUuidVersion", uuid, version);
    }*/
    public static DbItem retrieveForUuidVersion(UUID uuid, int version) throws Exception
    {
        //2016-02-29 DL - changed how we connect to db
        return (DbItem)DatabaseManager.db().retrieveForPrimaryKeys(adapter, uuid, new Integer(version));
        //return (DbItem)adapter.retrieveSingleEntity("Definition._Item_SelectForUuid", uuid);
    }
    public static DbItem retrieveForUuidLatestVersion(UUID organisationUuid, UUID uuid) throws Exception
    {
        return DatabaseManager.db().retrieveForUuidLatestVersion(organisationUuid, uuid);
    }
    public static List<DbItem> retrieveDependentItems(UUID organisationUuid, UUID itemUuid, DependencyType dependencyType) throws Exception
    {
        return DatabaseManager.db().retrieveDependentItems(organisationUuid, itemUuid, dependencyType);
    }
    public static List<DbItem> retrieveNonDependentItems(UUID organisationUuid, DependencyType dependencyType, DefinitionItemType itemType) throws Exception
    {
        return DatabaseManager.db().retrieveNonDependentItems(organisationUuid, dependencyType, itemType);
    }

    @Override
    public TableAdapter getAdapter()
    {
        return adapter;
    }

    @Override
    public void writeForDb(ArrayList<Object> builder)
    {
        builder.add(getPrimaryUuid());
        builder.add(version);
        builder.add(xmlContent);
        builder.add(title);
        builder.add(description);
        builder.add(endUserUuid);
        builder.add(timeStamp);
        builder.add(isDeleted);
        //builder.add(itemType.getValue());
    }

    @Override
    public void readFromDb(ResultReader reader) throws SQLException
    {
        setPrimaryUuid(reader.readUuid());
        version = reader.readInt();
        xmlContent = reader.readString();
        title = reader.readString();
        description = reader.readString();
        endUserUuid = reader.readUuid();
        timeStamp = reader.readDateTime();
        isDeleted = reader.readBoolean();
        //itemType = DefinitionItemType.get(reader.readInt());
    }

    /**
     * gets/sets
     */
    public int getVersion() {
        return version;
    }

    public void setVersion(int auditId) {
        this.version = auditId;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String content) {
        this.xmlContent = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getEndUserUuid() {
        return endUserUuid;
    }

    public void setEndUserUuid(UUID endUserUuid) {
        this.endUserUuid = endUserUuid;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    /*public DefinitionItemType getItemType() {
        return itemType;
    }

    public void setItemType(DefinitionItemType itemType) {
        this.itemType = itemType;
    }*/
}
