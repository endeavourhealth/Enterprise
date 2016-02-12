package org.endeavour.enterprise.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.UUID;

@XmlRootElement
public class RootFolderRequest implements Serializable
{
	private UUID organisationUuid;
	private Integer moduleId;

	public UUID getOrganisationUuid() {
		return organisationUuid;
	}

	public void setOrganisationUuid(UUID organisationUuid) {
		this.organisationUuid = organisationUuid;
	}

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}
}
