package org.endeavourhealth.enterprise.core.database;

import com.fasterxml.jackson.databind.JsonNode;
import org.endeavourhealth.common.config.ConfigManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public enum PersistenceManager {
    INSTANCE;
	private static final Logger LOG = LoggerFactory.getLogger(PersistenceManager.class);

    private EntityManagerFactory emEnterpriseAdmin;
    private EntityManagerFactory emEnterpriseData;
	private EntityManagerFactory emDataSharingManagerData;

    PersistenceManager() {

		Map<String, Object> override = getHibernateOverridesFromConfig("admin_database");

		emEnterpriseAdmin = Persistence.createEntityManagerFactory("enterprise_admin", override);

		override = getHibernateOverridesFromConfig("patient_database");
		emEnterpriseData = Persistence.createEntityManagerFactory("enterprise_data", override);

		override = getHibernateOverridesFromConfig("data_sharing_manager");
		emDataSharingManagerData = Persistence.createEntityManagerFactory("data_sharing_manager", override);
	}

	public EntityManager getEmEnterpriseAdmin() {
        return emEnterpriseAdmin.createEntityManager();
    }

    public EntityManager getEmEnterpriseData() {
        return emEnterpriseData.createEntityManager();
    }

	public EntityManager getEmDataSharingManagerData() {
		return emDataSharingManagerData.createEntityManager();
	}

    public void close() {
        emEnterpriseAdmin.close();
        emEnterpriseData.close();
		emDataSharingManagerData.close();

    }

	private Map<String, Object> getHibernateOverridesFromConfig(String configId) {
		Map<String, Object> override = new HashMap<>();

		try {
			JsonNode config = ConfigManager.getConfigurationAsJson(configId);

			if (config.has("driverClass"))
				override.put("hibernate.connection.driver_class", config.get("driverClass").asText());

			if (config.has("enterprise_url"))
				override.put("hibernate.connection.url", config.get("enterprise_url").asText());

			if (config.has("enterprise_username"))
				override.put("hibernate.connection.username", config.get("enterprise_username").asText());

			if (config.has("enterprise_password"))
				override.put("hibernate.connection.password", config.get("enterprise_password").asText());

		} catch (Exception e) {
			LOG.warn("Error loading config ["+configId+"]", e);
		}
		return override;
	}
}
