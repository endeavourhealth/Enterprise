package org.endeavour.enterprise.framework.database;

import org.endeavour.enterprise.framework.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class DatabaseConnection
{
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConnection.class); //2016-02-24 DL - logging

    private static Date endOfTime = null;

    public static Connection get(String databaseName) throws ClassNotFoundException, SQLException
    {
        // databaseName not used at present

        Class.forName(net.sourceforge.jtds.jdbc.Driver.class.getCanonicalName());
        return DriverManager.getConnection(Configuration.DB_CONNECTION_STRING);
    }


    /**
     * 2016-02-25 DL - haven't got anywhere good to put these, but leaving with other DB stuff
     */
    public static Date getEndOfTime()
    {
        if (endOfTime == null)
        {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            try {
                endOfTime = formatter.parse("31/12/9999");
            }
            catch (ParseException pe)
            {
                LOG.error("Failed to create end of time date", pe);
            }
        }
        return endOfTime;
    }
}