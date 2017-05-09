package org.endeavourhealth.enterprise.core.database.models.data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "ReportResultQuery", schema = "enterprise_data_pseudonymised", catalog = "")
public class ReportResultQueryEntity {

	@Id
	private ReportResultQueryEntityKey id;

	public ReportResultQueryEntityKey getId() {
		return id;
	}

	public ReportResultQueryEntity setId(ReportResultQueryEntityKey id) {
		this.id = id;
		return this;
	}
}

