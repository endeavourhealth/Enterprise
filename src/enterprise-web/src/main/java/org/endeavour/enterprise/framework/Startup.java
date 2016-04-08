package org.endeavour.enterprise.framework;

import org.endeavour.enterprise.email.EmailProvider;
import org.endeavour.enterprise.framework.config.ConfigSerializer;
import org.endeavour.enterprise.framework.config.models.Config;
import org.endeavour.enterprise.framework.config.models.Email;
import org.endeavour.enterprise.framework.config.models.Template;
import org.endeavour.enterprise.framework.config.models.WebServer;
import org.endeavour.enterprise.framework.security.SecurityConfig;
import org.endeavourhealth.enterprise.core.database.DatabaseManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

public final class Startup implements ServletContextListener {

    public void contextInitialized(ServletContextEvent contextEvent) {

        Config config = ConfigSerializer.getConfig();

        //set up our DB
        String url = config.getDatabase().getUrl();
        String username = config.getDatabase().getUsername();
        String password = config.getDatabase().getPassword();
        DatabaseManager.getInstance().setConnectionProperties(url, username, password);

        //tell our database manager to set up logging to db
        DatabaseManager.getInstance().registerLogbackDbAppender();

        //domain for our cookies
        WebServer ws = config.getWebServer();
        String cookieDomain = ws.getCookieDomain();
        SecurityConfig.AUTH_COOKIE_VALID_DOMAIN = cookieDomain;

        //set up our email provision
        Email emailSettings = config.getEmail();
        if (emailSettings != null) {
            url = emailSettings.getUrl();
            username = emailSettings.getUsername();
            password = emailSettings.getPassword();
            List<Template> templates = emailSettings.getTemplate();
            EmailProvider.getInstance().setConnectionProperties(url, username, password, templates);
        }

    }

    public void contextDestroyed(ServletContextEvent contextEvent) {
        DatabaseManager.getInstance().deregisterLogbackDbAppender();
    }

}
