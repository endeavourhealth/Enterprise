package org.endeavour.enterprise.framework;

import org.endeavourhealth.enterprise.core.entity.database.DatabaseManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Drew on 19/03/2016.
 */
public final class Startup implements ServletContextListener {

    public void contextInitialized(ServletContextEvent contextEvent) {

        //tell our database manager to set up logging to db
        DatabaseManager.db().registerLogbackDbAppender();
    }

    public void contextDestroyed(ServletContextEvent contextEvent) {

    }

}
