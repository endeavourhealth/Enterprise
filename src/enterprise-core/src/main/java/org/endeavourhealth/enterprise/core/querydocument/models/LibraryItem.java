
package org.endeavourhealth.enterprise.core.querydocument.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "libraryItem", propOrder = {
    "uuid",
    "name",
    "description",
    "folderUuid",
    "query",
    "codeSet"
})
public class LibraryItem {

    @XmlElement(required = true)
    protected String uuid;
    @XmlElement(required = true)
    protected String name;
    protected String description;
    @XmlElement(required = true)
    protected String folderUuid;
    protected Query query;
    protected CodeSet codeSet;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String value) {
        this.uuid = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getFolderUuid() {
        return folderUuid;
    }

    public void setFolderUuid(String value) {
        this.folderUuid = value;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query value) {
        this.query = value;
    }

    public CodeSet getCodeSet() {
        return codeSet;
    }

    public void setCodeSet(CodeSet value) {
        this.codeSet = value;
    }



}
