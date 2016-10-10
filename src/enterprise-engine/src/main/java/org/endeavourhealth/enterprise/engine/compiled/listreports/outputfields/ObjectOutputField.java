package org.endeavourhealth.enterprise.engine.compiled.listreports.outputfields;

import org.endeavourhealth.enterprise.engine.compiled.listreports.IListReportOutputField;

public class ObjectOutputField implements IListReportOutputField {
    @Override
    public String getResult(Object value) {
        return value.toString();
    }
}
