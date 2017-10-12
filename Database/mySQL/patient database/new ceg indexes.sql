CREATE INDEX ix_encounter_compound2 ON enterprise_data_pseudonymised.encounter
(
	snomed_concept_id ASC,
	clinical_effective_date ASC,
	patient_id ASC
);

CREATE INDEX ix_encounter_compound ON enterprise_data_pseudonymised.encounter
(
	patient_id ASC,
	clinical_effective_date ASC,
	snomed_concept_id ASC
);

CREATE INDEX ix_encounter_patient_id ON enterprise_data_pseudonymised.encounter
(
	patient_id ASC
);

CREATE INDEX ix_encounter_snomed_concept_id ON enterprise_data_pseudonymised.encounter
(
	snomed_concept_id ASC
);

CREATE INDEX ix_episode_of_care_patient_id ON enterprise_data_pseudonymised.episode_of_care
(
	patient_id ASC
);

CREATE INDEX ix_episode_of_care_compound ON enterprise_data_pseudonymised.episode_of_care
(
	registration_type_id ASC,
	patient_id ASC,
	date_registered ASC,
	date_registered_end ASC
);

CREATE INDEX ix_episode_of_care_date_registered ON enterprise_data_pseudonymised.episode_of_care
(
	date_registered ASC
);

CREATE INDEX ix_episode_of_care_date_registered_end ON enterprise_data_pseudonymised.episode_of_care
(
	date_registered_end ASC
);

CREATE INDEX ix_episode_of_care_registration_type_id ON enterprise_data_pseudonymised.episode_of_care
(
	registration_type_id ASC
);

CREATE INDEX ix_observation_patient_id ON enterprise_data_pseudonymised.observation
(
	patient_id ASC
);

CREATE INDEX ix_observation_compound ON enterprise_data_pseudonymised.observation
(
	snomed_concept_id ASC,
	patient_id ASC,
	clinical_effective_date ASC
);

CREATE INDEX ix_observation_clinical_effective_date ON enterprise_data_pseudonymised.observation
(
	clinical_effective_date DESC
);

CREATE INDEX ix_observation_snomed_concept_id ON enterprise_data_pseudonymised.observation
(
	snomed_concept_id ASC
);

CREATE INDEX ix_organization_id ON enterprise_data_pseudonymised.organization
(
	id ASC
);

CREATE INDEX ix_organization_id_parent_organization_id ON enterprise_data_pseudonymised.organization
(
	id ASC,
	parent_organization_id ASC
);

CREATE INDEX ix_patient_id ON enterprise_data_pseudonymised.patient
(
	id ASC
);

CREATE INDEX ix_patient_id_organization_id ON enterprise_data_pseudonymised.patient
(
	id ASC,
	organization_id ASC
);

CREATE INDEX ix_patient_compound ON enterprise_data_pseudonymised.patient
(
	organization_id ASC,
	date_of_death ASC,
	id ASC
);

CREATE INDEX ix_patient_date_of_death ON enterprise_data_pseudonymised.patient
(
	date_of_death ASC
);

CREATE INDEX ix_patient_organization_id ON enterprise_data_pseudonymised.patient
(
	organization_id ASC
);


