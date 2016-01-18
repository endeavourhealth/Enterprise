package org.endeavourhealth.discovery.client.services;

import org.endeavourhealth.discovery.client.services.configuration.ConfigurationAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ClientServletContextListener implements ServletContextListener {

    private final static Logger logger = LoggerFactory.getLogger(ClientServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //Any app startup code goes here

        String fullName = servletContextEvent.getServletContext().getRealPath("/WEB-INF/client.config");

        try {
            ConfigurationAPI.initialise(fullName);
        } catch (Exception e) {
            logger.error("Could not load configuration", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
