package org.endeavour.enterprise.framework.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StoredProcedure
{
    private Connection connection;
    private String storedProcedureName;
    private Map<String, Object> parameters = new HashMap<>();

    public StoredProcedure(Connection connection, String storedProcedureName)
    {
        this.connection = connection;
        this.storedProcedureName = storedProcedureName;
    }

    public void setParameter(String parameterName, String value)
    {
        this.parameters.put(parameterName, value);
    }

    public void setParameter(String parameterName, int value)
    {
        this.parameters.put(parameterName, value);
    }

    public void execute() throws SQLException
    {
        String parametersDeclaration = String.join(", ", Collections.nCopies(parameters.size(), "?"));

        CallableStatement ps = connection.prepareCall("call " + this.storedProcedureName + " (" + parametersDeclaration + ")");

        for (String parameterName : parameters.keySet())
            ps.setObject(parameterName, parameters.get(parameterName));

        ps.execute();
    }
}
