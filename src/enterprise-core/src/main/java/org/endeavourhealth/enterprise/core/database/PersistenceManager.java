package org.endeavourhealth.enterprise.core.database;

import com.fasterxml.jackson.databind.JsonNode;
import org.endeavourhealth.common.config.ConfigManager;

import org.endeavourhealth.coreui.framework.ContextShutdownHook;
import org.endeavourhealth.coreui.framework.StartupConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class PersistenceManager implements ContextShutdownHook {
    public static final PersistenceManager INSTANCE = new PersistenceManager();
	private static final Logger LOG = LoggerFactory.getLogger(PersistenceManager.class);

    private EntityManagerFactory emEnterpriseAdmin;
    private EntityManagerFactory emEnterpriseData;
	private EntityManagerFactory emDataSharingManagerData;
	//private EntityManagerFactory emPublisherCommon;
	private EntityManagerFactory emRf2;
	//private EntityManagerFactory emEnterpriseDemographic;

    private PersistenceManager() {
        StartupConfig.registerShutdownHook("Enterprise Persistence Manager", this);

		try {
			System.out.println("Fetching hibernate overrides (admin)...");
			Map<String, Object> override = getHibernateOverridesFromConfig("admin_database");
			System.out.println("Connecting to database (admin)...");
			emEnterpriseAdmin = Persistence.createEntityManagerFactory("enterprise_admin", override);

			System.out.println("Fetching hibernate overrides (patient)...");
			override = getHibernateOverridesFromConfig("patient_database");
			System.out.println("Connecting to database (patient)...");
			emEnterpriseData = Persistence.createEntityManagerFactory("enterprise_data", override);

			System.out.println("Fetching hibernate overrides (DSM)...");
			override = getHibernateOverridesFromConfig("data_sharing_manager");
			System.out.println("Connecting to database (DSM)...");
			emDataSharingManagerData = Persistence.createEntityManagerFactory("data_sharing_manager", override);

			/*System.out.println("Fetching hibernate overrides (publisher)...");
			override = getHibernateOverridesFromConfig("publisher_common");
			System.out.println("Connecting to database (publisher)...");
			emPublisherCommon = Persistence.createEntityManagerFactory("publisher_common", override);*/

			System.out.println("Fetching hibernate overrides (rf2)...");
			override = getHibernateOverridesFromConfig("rf2");
			System.out.println("Connecting to database (rf2)...");
			emRf2 = Persistence.createEntityManagerFactory("rf2", override);

			/*System.out.println("Fetching hibernate overrides (demographic)...");
			override = getHibernateOverridesFromConfig("demographic");
			System.out.println("Connecting to database (demographic)...");
			emEnterpriseDemographic = Persistence.createEntityManagerFactory("enterprise_demographic", override);*/

			System.out.println("Persistence manager initialized");
		} catch (Throwable t) {
			String s = t.toString();
			System.out.println(s);
			throw t;
		}
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

	/*public EntityManager getEmPublisherCommonData() {
		return emPublisherCommon.createEntityManager();
	}*/

	public EntityManager getEmRf2() {
		return emRf2.createEntityManager();
	}

	/*public EntityManager getEmEnterpriseDemographic() {
		return emEnterpriseDemographic.createEntityManager();
	}*/

    public void close() {
        emEnterpriseAdmin.close();
        emEnterpriseData.close();
		emDataSharingManagerData.close();
		//emPublisherCommon.close();
		emRf2.close();
		//emEnterpriseDemographic.close();

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
		    System.out.println("Error loading config ["+configId+"]");
		    System.out.println(e.getMessage());
		    e.printStackTrace();
			LOG.warn("Error loading config ["+configId+"]", e);
		}
		return override;
	}

    @Override
    public void contextShutdown() {
        this.close();
    }
}
