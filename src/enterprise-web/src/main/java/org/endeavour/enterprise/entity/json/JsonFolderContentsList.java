package org.endeavour.enterprise.entity.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Drew on 23/02/2016.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonFolderContentsList implements Serializable {
    private List<JsonFolderContent> contents = null;
/*
    private List<JsonQuery> queries = null;
    private List<JsonListOutput> listOutputs = null;
    private List<JsonReport> reports = null;
*/

    public JsonFolderContentsList() {
    }


    /*public void addQuery(JsonQuery query)
    {
        if (contents == null)
        {
            contents = new List<JsonFolderContent>();
        }
        queries.add(query);
    }
    public void addListOutput(JsonListOutput listOutput)
    {
        if (listOutputs == null)
        {
            listOutputs = new ArrayList<JsonListOutput>();
        }
        listOutputs.add(listOutput);
    }
    public void addReport(JsonReport report)
    {
        if (reports == null)
        {
            reports = new ArrayList<JsonReport>();
        }
        reports.add(report);
    }*/
    public void addContent(JsonFolderContent content) {
        if (contents == null) {
            contents = new ArrayList<JsonFolderContent>();
        }
        contents.add(content);
    }


    /**
     * gets/sets
     */
    public List<JsonFolderContent> getContents() {
        return contents;
    }

    public void setContents(List<JsonFolderContent> contents) {
        this.contents = contents;
    }

    /*public List<JsonQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<JsonQuery> queries) {
        this.queries = queries;
    }

    public List<JsonListOutput> getListOutputs() {
        return listOutputs;
    }

    public void setListOutputs(List<JsonListOutput> listOutputs) {
        this.listOutputs = listOutputs;
    }

    public List<JsonReport> getReports() {
        return reports;
    }

    public void setReports(List<JsonReport> reports) {
        this.reports = reports;
    }*/
}
