package org.endeavourhealth.enterprise.enginecore.carerecord;

import net.sourceforge.jtds.jdbc.JtdsResultSet;
import org.endeavourhealth.enterprise.core.database.execution.DbJob;
import org.endeavourhealth.enterprise.enginecore.database.DatabaseConnectionDetails;
import org.endeavourhealth.enterprise.enginecore.database.DatabaseHelper;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainerPool;
import org.endeavourhealth.enterprise.enginecore.entities.model.DataEntity;
import org.endeavourhealth.enterprise.enginecore.entitymap.EntityMapWrapper;
import org.endeavourhealth.enterprise.core.entitymap.models.Field;

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

        String sql = "exec EndeavourEnterprise.GetStatistics;";

        try (
                Connection con = DatabaseHelper.getConnection(connectionDetails);
                Statement statement = con.createStatement();
        ) {
            try (JtdsResultSet rs = (JtdsResultSet)statement.executeQuery(sql)) {
                rs.next();

                SourceStatistics stats = new SourceStatistics(
                    rs.getInt(1),
                    rs.getInt(2),
                    rs.getInt(3)
                );

                return stats;
            }
        }
    }

    public Map<Long, DataContainer> getRecords(
            long minimumId,
            long maximumId) throws Exception {

        String sql = "exec EndeavourEnterprise.GetRecords ?, ?";
        int resultSetIndex = 0;
        Map<Long, DataContainer> dataContainerDictionary = new HashMap<>();

        try (
                Connection con = DatabaseHelper.getConnection(careRecordConnectionDetails);
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setEscapeProcessing(true);
            ps.setLong(1, minimumId);
            ps.setLong(2, maximumId);

            ps.execute();
            boolean hasResults = ps.getMoreResults();

            while (hasResults) {
                resultSetIndex++;

                try (JtdsResultSet rs = (JtdsResultSet) ps.getResultSet()) {
                    processResultSet(rs, resultSetIndex, dataContainerDictionary);
                }

                hasResults = ps.getMoreResults();
            }

            ps.close();
        }

        return dataContainerDictionary;
    }

    private void processResultSet(
            JtdsResultSet rs,
            int resultSetIndex,
            Map<Long, DataContainer> dataContainerDictionary) throws Exception {

        int entityIndex = entityMapWrapper.getEntityIndexByResultSetIndex(resultSetIndex);
        EntityMapWrapper.Entity entity = entityMapWrapper.getEntity(entityIndex);
        int populationFieldIndex = entity.getSource().getPopulationFieldIndex();
        List<Field> entityMapFields = entity.getSource().getField();
        int entityMapFieldCount = entityMapFields.size();

        DataContainer dataContainer = null;

        while (rs.next()) {
            Long populationId = rs.getLong(populationFieldIndex);

            if (dataContainer == null || dataContainer.getId() != populationId) {

                if (dataContainerDictionary.containsKey(populationId))
                    dataContainer = dataContainerDictionary.get(populationId);
                else {
                    dataContainer = dataContainerPool.acquire();
                    dataContainer.setId(populationId);
                    dataContainerDictionary.put(populationId, dataContainer);
                }
            }

            DataEntity dataEntity = dataContainer.getDataEntities().get(entityIndex);

            for (int i = 0; i < entityMapFieldCount; i++) {
                dataEntity.getFields().get(i).add(rs.getObject(entityMapFields.get(i).getIndex()));
            }
        }
    }
}
