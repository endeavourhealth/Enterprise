package org.endeavourhealth.enterprise.enginecore.carerecord;

import org.endeavourhealth.enterprise.core.database.PersistenceManager;
import org.endeavourhealth.enterprise.core.database.models.ActiveitemEntity;
import org.endeavourhealth.enterprise.core.database.models.AuditEntity;
import org.endeavourhealth.enterprise.core.database.models.ItemEntity;
import org.endeavourhealth.enterprise.core.entitymap.models.LogicalDataType;
import org.endeavourhealth.enterprise.enginecore.database.DatabaseConnectionDetails;
import org.endeavourhealth.enterprise.enginecore.database.DatabaseHelper;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainerPool;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataField;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;
import org.endeavourhealth.enterprise.core.entitymap.models.Field;

import javax.persistence.EntityManager;
import javax.xml.stream.events.EndElement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CareRecordDal {

    private final DatabaseConnectionDetails careRecordConnectionDetails;
    private final DataContainerPool dataContainerPool;
    private final EntityMapWrapper.EntityMap entityMapWrapper;

    public CareRecordDal(
            DatabaseConnectionDetails careRecordConnectionDetails,
            DataContainerPool dataContainerPool,
            EntityMapWrapper.EntityMap entityMapWrapper) {

        this.careRecordConnectionDetails = careRecordConnectionDetails;
        this.dataContainerPool = dataContainerPool;
        this.entityMapWrapper = entityMapWrapper;
    }

    public static SourceStatistics calculateTableStatistics(
            DatabaseConnectionDetails connectionDetails) throws SQLException, ClassNotFoundException {

        String where = "select count(*), min(id), max(id) from PatientEntity";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        Object[] stats = (Object[])entityManager.createQuery(where)
                .getSingleResult();

        SourceStatistics stats2 = new SourceStatistics((Long)stats[0],(int)stats[1],(int)stats[2]);

        entityManager.close();

        return stats2;

    }

    public Map<Integer, DataContainer> getRecords(
            long minimumId,
            long maximumId) throws Exception {

        String where = "select * from PatientEntity where id >= :MinimumId and id <= :MaximumId;";

        EntityManager entityManager = PersistenceManager.INSTANCE.getEntityManager();

        /*List<PatientEntity> patients = entityManager.createQuery(where, PatientEntity.class)
                .setParameter("MinimumId", minimumId)
                .setParameter("MaximumId", maximumId)
                .getResultList();*/

        int resultSetIndex = 0;
        Map<Integer, DataContainer> dataContainerDictionary = new HashMap<>();

        /*for (PatientEntity patient: patients) {
            processPatientResultSet(patient, dataContainerDictionary);
        }*/

        entityManager.close();

        return dataContainerDictionary;
    }

    /*private void processPatientResultSet(
            PatientEntity patient,
            Map<Integer, DataContainer> dataContainerDictionary) throws Exception {

        int entityIndex = entityMapWrapper.getEntityIndexByResultSetIndex(1);
        EntityMapWrapper.Entity entity = entityMapWrapper.getEntity(entityIndex);
        int populationFieldIndex = entity.getSource().getPopulationFieldIndex();
        Integer organisationOdsFieldIndex = null;

        if (entity.getSource().getOrganisationOdsFieldIndex() != null)
            organisationOdsFieldIndex = entity.getSource().getOrganisationOdsFieldIndex();

        List<Field> entityMapFields = entity.getSource().getField();
        int entityMapFieldCount = entityMapFields.size();

        DataContainer dataContainer = null;

        Integer populationId = patient.getId();

        if (dataContainer == null || dataContainer.getId() != populationId) {

            if (dataContainerDictionary.containsKey(populationId))
                dataContainer = dataContainerDictionary.get(populationId);
            else {
                dataContainer = dataContainerPool.acquire();
                dataContainer.setId(populationId);

                if (organisationOdsFieldIndex != null)
                    dataContainer.setOrganisationId(patient.getOrganizationId());

                dataContainerDictionary.put(populationId, dataContainer);
            }
        }

        List<DataField> fields = dataContainer.getDataEntities().get(entityIndex).getFields();

        for (int i = 0; i < entityMapFieldCount; i++) {

            Field field = entityMapFields.get(i);

            if (field.getLogicalName() == "PSEUDO_ID")
                fields.get(i).add(patient.getPseudoId());
            else if (field.getLogicalName() == "ORGANISATION_ID")
                fields.get(i).add(patient.getOrganizationId());
            else if (field.getLogicalName() == "YEAR_OF_BIRTH")
                fields.get(i).add(patient.getYearOfBirth());
            else if (field.getLogicalName() == "GENDER")
                fields.get(i).add(patient.getPatientGenderId());
            else if (field.getLogicalName() == "REGISTRATION_DATE")
                fields.get(i).add(patient.getDateRegistered());
            else if (field.getLogicalName() == "REGISTRATION_END_DATE")
                fields.get(i).add(patient.getDateRegisteredEnd());
            else if (field.getLogicalName() == "YEAR_OF_DEATH")
                fields.get(i).add(patient.getYearOfDeath());
        }
    }*/
}
