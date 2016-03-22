package org.endeavour.enterprise.framework;

import org.endeavourhealth.enterprise.core.database.DatabaseManager;
import org.endeavourhealth.enterprise.core.database.SqlServerConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public final class Startup implements ServletContextListener {

    public void contextInitialized(ServletContextEvent contextEvent) {

        //set up our DB - this will be moved to a config file soon
        DatabaseManager.getInstance().setConnectionProperties(SqlServerConfig.URL, SqlServerConfig.USERNAME, SqlServerConfig.PASSWORD);

        //tell our database manager to set up logging to db
        DatabaseManager.getInstance().registerLogbackDbAppender();

        try {
            //Examples.findingPendingRequestsAndCreateJob();
            //Examples.findNonCompletedJobsAndContents();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent contextEvent) {

    }

}
