package org.endeavourhealth.enterprise.engine.compiled.listreports.outputfields;

import org.apache.commons.collections4.CollectionUtils;
import org.endeavourhealth.enterprise.core.entitymap.models.DataValueType;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiled.listreports.IListReportOutputField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataValuesOutputField implements IListReportOutputField {

    private final Map<Object, String> map = new HashMap<>();

    public DataValuesOutputField(List<DataValueType> dataValueTypeList) throws UnableToCompileExpection {

        if (CollectionUtils.isEmpty(dataValueTypeList))
            throw new UnableToCompileExpection("No datavalues in entitymap");

        for (DataValueType dataValueType: dataValueTypeList) {
            map.put(dataValueType.getPhysicalValue(), dataValueType.getDisplayName());
        }
    }

    @Override
    public String getResult(Object value) {

        if (map.containsKey(value))
            return map.get(value);
        else
            return value.toString();
    }
}
