package org.endeavourhealth.enterprise.core.database;

import com.fasterxml.jackson.databind.JsonNode;
import org.endeavourhealth.common.config.ConfigManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public enum PersistenceManager {
    INSTANCE;

    private EntityManagerFactory emEnterpriseAdmin;
    private EntityManagerFactory emEnterpriseData;

    private PersistenceManager() {

			Map<String, Object> override = getHibernateOverridesFromConfig("admin_database");
			//emEnterpriseAdmin = Persistence.createEntityManagerFactory("enterprise_admin", override);

			override = getHibernateOverridesFromConfig("patient_database");
			//emEnterpriseData = Persistence.createEntityManagerFactory("enterprise_data", override);
		}

	public EntityManager getEmEnterpriseAdmin() {
        return emEnterpriseAdmin.createEntityManager();
    }

    public EntityManager getEmEnterpriseData() {
        return emEnterpriseData.createEntityManager();
    }

    public void close() {
        //emEnterpriseAdmin.close();
        //emEnterpriseData.close();

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

		}
		return override;
	}
}
