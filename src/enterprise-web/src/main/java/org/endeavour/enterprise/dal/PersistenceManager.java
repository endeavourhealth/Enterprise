package org.endeavour.enterprise.dal;

import com.fasterxml.jackson.databind.JsonNode;
import org.endeavourhealth.common.config.ConfigManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;


public class PersistenceManager {
    private static EntityManagerFactory entityManagerFactory;


    public static EntityManager getEntityManager() throws Exception {

        if (entityManagerFactory == null
                || !entityManagerFactory.isOpen()) {
            createEntityManager();
        }

        return entityManagerFactory.createEntityManager();
    }

    private static synchronized void createEntityManager() throws Exception {

        if (entityManagerFactory != null
                && entityManagerFactory.isOpen()) {
            return;
        }

        JsonNode json = ConfigManager.getConfigurationAsJson("database", "data-generator");
        String url = json.get("url").asText();
        String user = json.get("username").asText();
        String pass = json.get("password").asText();

        Map<String, Object> properties = new HashMap<>();
        //properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.put("hibernate.hikari.dataSource.url", url);
        properties.put("hibernate.hikari.dataSource.user", user);
        properties.put("hibernate.hikari.dataSource.password", pass);
        properties.put("javax.persistence.provider", "org.hibernate.jpa.HibernatePersistenceProvider");
        properties.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        entityManagerFactory = Persistence.createEntityManagerFactory("data-generator", properties);
    }
}
