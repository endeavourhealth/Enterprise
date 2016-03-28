package org.endeavourhealth.enterprise.enginecore.entities.userFriendly;

//import org.endeavourhealth.enterprise.enginecore.entities.model.DataContainer;
//import org.endeavourhealth.enterprise.enginecore.entities.model.DataEntity;
//import org.endeavourhealth.enterprise.core.entitymap.models.EntityMap;
//import org.joda.time.LocalDate;
//import org.joda.time.format.DateTimeFormatter;
//import org.joda.time.format.ISODateTimeFormat;

public class UserFriendlyConverter {
//
//    public UserFriendlyEntityContainer convert(DataContainer source, EntityMap entityMap) {
//
//        UserFriendlyEntity entity = new UserFriendlyEntity();
//        entity.setName("Events");
//        entity.getFields().add(new UserFriendlyField("GUID"));
//        entity.getFields().add(new UserFriendlyField("Clinical Date"));
//        entity.getFields().add(new UserFriendlyField("Code"));
//        entity.getFields().add(new UserFriendlyField("Value"));
//
//        DateTimeFormatter isoDateFormat = ISODateTimeFormat.date();
//
//        DataEntity sourceEntity = source.getDataEntities().get(0);
//
//        for (int i = 0; i < sourceEntity.getSize(); i++) {
//            UserFriendlyRow row = new UserFriendlyRow();
//
//            row.add(sourceEntity.getFields().get(0).get(i));
//
//            if (sourceEntity.getFields().get(1).get(i) == null)
//                row.add(null);
//            else
//                row.add(isoDateFormat.print((LocalDate)sourceEntity.getFields().get(1).get(i)));
//
//            row.add(sourceEntity.getFields().get(2).get(i));
//            row.add(sourceEntity.getFields().get(3).get(i));
//
//            entity.getRows().add(row);
//        }
//
//        UserFriendlyEntityContainer container = new UserFriendlyEntityContainer();
//        container.getEntities().add(entity);
//        return container;
//    }
}

