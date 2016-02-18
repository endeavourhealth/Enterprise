package org.endeavour.enterprise.entity.database;

import net.sourceforge.jtds.jdbc.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Drew on 18/02/2016.
 */
public final class InsertBuilder {

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private StringBuilder sb = new StringBuilder();

    public InsertBuilder()
    {}
    public InsertBuilder(String s)
    {
        sb.append(s);
    }

    public void add(String s)
    {
        addCommaIfRequired();

        s = s.replaceAll("'", "''");
        sb.append("'" + s + "'");
    }

    public void add(int i)
    {
        addCommaIfRequired();
        sb.append(i);
    }

    public void add(UUID uuid)
    {
        addCommaIfRequired();
        sb.append("'" + uuid.toString() + "'");
    }

    public void add(boolean b)
    {
        addCommaIfRequired();
        if (b) {
            sb.append(1);
        }
        else
        {
            sb.append(0);
        }
    }

    public void add(Date dt)
    {
        addCommaIfRequired();

        sb.append(dateFormatter.format(dt));
    }

    private void addCommaIfRequired()
    {
        if (sb.length() > 0)
        {
            sb.append(", ");
        }
    }


    public String toString()
    {
        return sb.toString();
    }
}
