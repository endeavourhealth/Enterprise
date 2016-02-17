package org.endeavour.enterprise.entity.database;

import org.endeavour.enterprise.framework.database.DatabaseConnection;
import org.endeavour.enterprise.framework.database.StoredProcedure;
import org.endeavour.enterprise.model.DatabaseName;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
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

    /**
     * get method only
     */
    public UUID getPrimaryUuid() {
        return primaryUuid;
    }


    /**
     * convenience method to insert/update an entity on the DB
     */
    public void saveToDb() throws Throwable
    {
        //if we're saving a brand NEW entity, then we won't have a primary uuid, so generate it
        //and use the INSERT stored procedure
        if (primaryUuid == null)
        {
            primaryUuid = UUID.randomUUID();
            getAdapter().saveToDb(true, this);
        }
        //if we already have a UUID then we're updating an existing entity,
        //so use the UPDATE stored procedure
        else
        {
            getAdapter().saveToDb(false, this);
        }
    }

    /**
     * convenience method to delete an entity from the DB
     */
    public void deleteFromDb() throws Throwable
    {
        //if no primary UUID has been assigned, then it's never been saved, so just return out
        if (primaryUuid == null)
        {
            return;
        }

        getAdapter().deleteFromDb(this);
    }



}
