package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ReportResultOrganisation", schema = "enterprise_data_pseudonymised", catalog = "")
public class ReportResultOrganisationEntity {
	@Id
	private ReportResultOrganisationEntityKey id;

	public ReportResultOrganisationEntityKey getId() {
		return id;
	}

	public ReportResultOrganisationEntity setId(ReportResultOrganisationEntityKey id) {
		this.id = id;
		return this;
	}
}

