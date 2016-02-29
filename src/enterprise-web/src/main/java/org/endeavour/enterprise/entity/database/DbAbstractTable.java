package org.endeavour.enterprise.entity.database;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Drew on 17/02/2016.
 * Base class for all DB entities, containing common methods for saving and retrieving. All actual
 * persistence is done in the TableAdapter class
 */
public abstract class DbAbstractTable
{
    private UUID primaryUuid = null;

    public abstract TableAdapter getAdapter();
    public abstract void writeForDb(InsertBuilder builder);
    public abstract void readFromDb(ResultReader reader) throws SQLException;

    /**
     * get/sets method only
     */
    public UUID getPrimaryUuid() {
        return primaryUuid;
    }

    public void setPrimaryUuid(UUID primaryUuid) {
        this.primaryUuid = primaryUuid;
    }

    /**
     * convenience method to insert/update an entity on the DB
     */
    public void saveToDb() throws Exception
    {
        //if we're saving a brand NEW entity, then we won't have a primary uuid, so generate it
        //and use the INSERT stored procedure
        if (primaryUuid == null)
        {
            primaryUuid = UUID.randomUUID();
            saveToDbInsert();
        }
        //if we already have a UUID then we're updating an existing entity,
        //so use the UPDATE stored procedure
        else
        {
            saveToDbUpdate();
        }
    }
    public void saveToDbInsert() throws Exception
    {
        getAdapter().saveToDb(true, this);
    }
    public void saveToDbUpdate() throws Exception
    {
        getAdapter().saveToDb(false, this);
    }


    /**
     * convenience method to delete an entity from the DB
     */
    public void deleteFromDb() throws Exception
    {
        //if no primary UUID has been assigned, then it's never been saved, so just return out
        if (primaryUuid == null)
        {
            return;
        }

        getAdapter().deleteFromDb(this);
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof DbAbstractTable)
        {
            DbAbstractTable other = (DbAbstractTable)o;
            if (getPrimaryUuid() != null
                    && other.getPrimaryUuid() != null
                    && getPrimaryUuid().equals(other.getPrimaryUuid()))
            {
                return true;
            }
        }
        return super.equals(o);
    }


}

