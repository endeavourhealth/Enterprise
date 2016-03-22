package org.endeavour.enterprise.framework;

import org.endeavour.enterprise.framework.config.ConfigSerializer;
import org.endeavour.enterprise.framework.config.models.Config;
import org.endeavourhealth.enterprise.core.database.DatabaseManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public final class Startup implements ServletContextListener {

    public void contextInitialized(ServletContextEvent contextEvent) {

        //set up our DB - this will be moved to a config file soon
        Config config = ConfigSerializer.getConfig();
        String url = config.getDatabase().getUrl();
        String username = config.getDatabase().getUsername();
        String password = config.getDatabase().getPassword();
        DatabaseManager.getInstance().setConnectionProperties(url, username, password);

        //tell our database manager to set up logging to db
        DatabaseManager.getInstance().registerLogbackDbAppender();
    }

    public void contextDestroyed(ServletContextEvent contextEvent) {

    }

}
