package org.endeavourhealth.enterprise.engine.compiler;

import org.endeavourhealth.enterprise.core.entitymap.models.Field;
import org.endeavourhealth.enterprise.core.entitymap.models.LogicalDataType;
import org.endeavourhealth.enterprise.core.querydocument.models.FieldOutput;
import org.endeavourhealth.enterprise.engine.UnableToCompileExpection;
import org.endeavourhealth.enterprise.engine.compiled.listreports.IListReportOutputField;
import org.endeavourhealth.enterprise.engine.compiled.listreports.outputfields.DataValuesOutputField;
import org.endeavourhealth.enterprise.engine.compiled.listreports.outputfields.DateOutputField;
import org.endeavourhealth.enterprise.engine.compiled.listreports.outputfields.ObjectOutputField;
import org.endeavourhealth.enterprise.engine.compiled.listreports.outputfields.StringOutputField;

public class ListReportFieldCompiler {
    public IListReportOutputField compileField(FieldOutput source, Field field) throws UnableToCompileExpection {

        switch (field.getLogicalDataType()) {
            case DATE:
                return new DateOutputField();
            case ORGANISATION_ODS:
            case STRING:
            case FLOAT:
            case CODE:
            case INTEGER:
            case UUID:
                return new ObjectOutputField();
            case DATA_VALUES:
                return new DataValuesOutputField(field.getDataValues());
            default:
                throw new UnableToCompileExpection("Unsupported output field type: " + field.getLogicalDataType());
        }
    }
}
