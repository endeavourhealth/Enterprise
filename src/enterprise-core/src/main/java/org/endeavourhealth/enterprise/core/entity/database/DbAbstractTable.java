package org.endeavourhealth.enterprise.core.entity.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Drew on 17/02/2016.
 * Base class for all DB entities, containing common methods for saving and retrieving. All actual
 * persistence is done in the TableAdapter class
 */
public abstract class DbAbstractTable {
    private UUID primaryUuid = null;
    private TableSaveMode saveMode = null;

    public abstract TableAdapter getAdapter();

    public abstract void writeForDb(ArrayList<Object> builder);

    public abstract void readFromDb(ResultReader reader) throws SQLException;


    public void writeToDb() throws Exception {

        DatabaseManager.db().writeEntity(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DbAbstractTable) {
            DbAbstractTable other = (DbAbstractTable) o;
            if (getPrimaryUuid() != null
                    && other.getPrimaryUuid() != null
                    && getPrimaryUuid().equals(other.getPrimaryUuid())) {
                return true;
            }
        }
        return super.equals(o);
    }


    /**
     * get/sets method only
     */
    public UUID getPrimaryUuid() {
        return primaryUuid;
    }

    public void setPrimaryUuid(UUID primaryUuid) {
        this.primaryUuid = primaryUuid;
    }

    public TableSaveMode getSaveMode() {

        //if we have no primary UUID, then generate one and go into insert mode
        if (primaryUuid == null) {
            primaryUuid = UUID.randomUUID();
            saveMode = TableSaveMode.INSERT;
        }
        //if we have a UUID, but no explicity set save mode, then assume an update
        else if (saveMode == null) {
            saveMode = TableSaveMode.UPDATE;
        }

        return saveMode;
    }

    public void setSaveMode(TableSaveMode saveMode) {
        this.saveMode = saveMode;
    }
}

