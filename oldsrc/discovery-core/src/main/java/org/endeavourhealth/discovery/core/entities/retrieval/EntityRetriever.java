package org.endeavourhealth.discovery.core.entities.retrieval;

import net.sourceforge.jtds.jdbc.JtdsResultSet;
import org.endeavourhealth.discovery.core.entities.model.DataContainer;
import org.endeavourhealth.discovery.core.database.DatabaseHelper;
import org.endeavourhealth.discovery.core.entities.model.DataEntity;
import org.endeavourhealth.discovery.core.entities.model.DataField;
import org.endeavourhealth.discovery.core.entitymap.EntityMap;
import org.joda.time.LocalDate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class EntityRetriever {
    private EntityMap entityMap;

    public EntityRetriever(EntityMap entityMap) {
        this.entityMap = entityMap;
    }

    public DataContainer getEntityContainer(int patientId) {

        String sql = "EXEC dbo.GetObservations ?";
        DataField guid = new DataField();
        DataField effectiveDate = new DataField();
        DataField legacyCode = new DataField();
        DataField value = new DataField();

        try (
                Connection con = new DatabaseHelper().getConnection(null);
                PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setEscapeProcessing(true);
            ps.setInt(1, patientId);

            try (JtdsResultSet rs = (JtdsResultSet)ps.executeQuery()) {
                while (rs.next()) {
                    //net.sourceforge.jtds.jdbc.UniqueIdentifier
                    guid.add(UUID.fromString(rs.getString(1)));  //Junk!

                    effectiveDate.add(rs.getDate(2) == null ? null : new LocalDate(rs.getDate(2)));
                    legacyCode.add(rs.getString(3));
                    value.add(rs.getBigDecimal(4));
                }
            }

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        DataEntity entity = new DataEntity();
        entity.getFields().add(guid);
        entity.getFields().add(effectiveDate);
        entity.getFields().add(legacyCode);
        entity.getFields().add(value);

        DataContainer container = new DataContainer();
        container.getEntities().add(entity);

        return container;
    }
}
