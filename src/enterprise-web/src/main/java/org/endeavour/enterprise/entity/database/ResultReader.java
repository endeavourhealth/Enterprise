package org.endeavour.enterprise.entity.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Drew on 18/02/2016.
 */
public final class ResultReader {

    private static final int FIRST_COL = 1; //keep forgetting this, as it's not zero, so made a constant

    private ResultSet rs = null;
    private int currentCol = FIRST_COL;

    public ResultReader(ResultSet rs)
    {
        this.rs = rs;
    }

    public String readString() throws SQLException
    {
        return rs.getString(currentCol++);
    }

    public int readInt() throws SQLException
    {
        return rs.getInt(currentCol++);
    }

    public UUID readUuid() throws SQLException
    {
        String uuidString = rs.getString(currentCol++);

        //UUID may be null
        return uuidString == null ? null : UUID.fromString(uuidString);
    }

    public boolean readBoolean() throws SQLException
    {
        return rs.getBoolean(currentCol++);
    }

    public Date readDateTime() throws SQLException
    {
        return rs.getDate(currentCol++);
    }

    public boolean nextResult() throws SQLException
    {
        currentCol = FIRST_COL;
        return rs.next();
    }

}
