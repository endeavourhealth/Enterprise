package org.endeavour.enterprise.framework;

import org.endeavour.enterprise.email.EmailProvider;
import org.endeavour.enterprise.framework.config.ConfigSerializer;
import org.endeavour.enterprise.framework.config.models.*;
import org.endeavour.enterprise.framework.security.SecurityConfig;
import org.endeavour.enterprise.utility.MessagingQueueProvider;
import org.endeavourhealth.enterprise.core.database.PersistenceManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

public final class Startup implements ServletContextListener {

    public void contextInitialized(ServletContextEvent contextEvent) {

        Config config = ConfigSerializer.getConfig();

        String url = config.getDatabase().getUrl();
        String username = config.getDatabase().getUsername();
        String password = config.getDatabase().getPassword();

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

        //messaging queue
        MessagingQueue queueSettings = config.getMessagingQueue();
        if (queueSettings != null) {
            url = queueSettings.getUrl();
            username = queueSettings.getUsername();
            password = queueSettings.getPassword();
            String queueName = queueSettings.getQueueName();
            MessagingQueueProvider.getInstance().setConnectionProperties(url, username, password, queueName);
        }

    }

    public void contextDestroyed(ServletContextEvent contextEvent) {
        PersistenceManager.INSTANCE.close();
    }


}
