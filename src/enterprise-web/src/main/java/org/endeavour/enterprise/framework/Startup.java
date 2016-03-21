package org.endeavour.enterprise.framework;

import org.endeavourhealth.enterprise.core.database.DatabaseManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public final class Startup implements ServletContextListener {

    public void contextInitialized(ServletContextEvent contextEvent) {

        //tell our database manager to set up logging to db
        DatabaseManager.db().registerLogbackDbAppender();

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
