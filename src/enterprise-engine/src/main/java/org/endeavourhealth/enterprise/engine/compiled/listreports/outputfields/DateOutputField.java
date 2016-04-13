package org.endeavourhealth.enterprise.engine.compiled.listreports.outputfields;

import org.endeavourhealth.enterprise.engine.compiled.listreports.IListReportOutputField;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateOutputField implements IListReportOutputField {

    @Override
    public String getResult(Object value) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneOffset.UTC)
                .format((LocalDate)value);
    }
}
