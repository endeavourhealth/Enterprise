-- Table: organisation

-- DROP TABLE organisation;

CREATE TABLE public.organisation
(
  id uuid NOT NULL,
  ods_code character varying(50),
  name character varying(255) NOT NULL,
  type_code character varying(50),
  type_desc character varying(255),
  postcode character varying(10),
  parent_organisation_id uuid,
  CONSTRAINT pk_organisation_id PRIMARY KEY (id),
  CONSTRAINT fk_organisation_parent_organisation_id FOREIGN KEY (parent_organisation_id)
      REFERENCES public.organisation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.organisation
  OWNER TO postgres;

-- Index: fki_organisation_parent_organisation_id

-- DROP INDEX fki_organisation_parent_organisation_id;

CREATE INDEX fki_organisation_parent_organisation_id
  ON organisation
  USING btree
  (parent_organisation_id);

-- Index: organisation_id

-- DROP INDEX organisation_id;

CREATE UNIQUE INDEX organisation_id
  ON organisation
  USING btree
  (id);

-- Table: practitioner

-- DROP TABLE practitioner;

CREATE TABLE practitioner
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  name character varying(1024) NOT NULL,
  role_code character varying(50),
  role_desc character varying(255),
  CONSTRAINT pk_practitioner_id PRIMARY KEY (id),
  CONSTRAINT fk_practitioner_organisation_id FOREIGN KEY (organisation_id)
      REFERENCES organisation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE practitioner
  OWNER TO postgres;

-- Index: practitioner_id

-- DROP INDEX practitioner_id;

CREATE UNIQUE INDEX practitioner_id
  ON practitioner
  USING btree
  (id);

-- Table: schedule

-- DROP TABLE schedule;

CREATE TABLE schedule
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  practitioner_id uuid,
  date date NOT NULL,
  type character varying(255),
  location character varying(255),
  CONSTRAINT pk_schedule_id PRIMARY KEY (id),
  CONSTRAINT fk_schedule_organisation_id FOREIGN KEY (organisation_id)
      REFERENCES organisation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE schedule
  OWNER TO postgres;

-- Index: schedule_id

-- DROP INDEX schedule_id;

CREATE UNIQUE INDEX schedule_id
  ON schedule
  USING btree
  (id);


-- Table: patient

-- DROP TABLE patient;

CREATE TABLE patient
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  date_of_birth date NOT NULL,
  year_of_death integer,
  gender character(20) NOT NULL,
  date_registered date NOT NULL,
  date_registered_end date,
  usual_gp_name character varying(255),
  registration_type_code character varying(50) NOT NULL,
  registration_type_desc character varying(255) NOT NULL,
  pseudo_id character varying(255) NOT NULL,
  CONSTRAINT pk_patient_id_organisation_id PRIMARY KEY (id, organisation_id),
  CONSTRAINT fk_patient_organisation_id FOREIGN KEY (organisation_id)
      REFERENCES organisation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE patient
  OWNER TO postgres;

-- Index: patient_id

-- DROP INDEX patient_id;

CREATE UNIQUE INDEX patient_id
  ON patient
  USING btree
  (id);
ALTER TABLE patient CLUSTER ON patient_id;


-- Table: appointment

-- DROP TABLE appointment;

CREATE TABLE appointment
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  practitioner_id uuid NOT NULL,
  schedule_id uuid NOT NULL,
  date date NOT NULL,
  planned_duration integer NOT NULL,
  actual_duration integer,
  status character varying(50) NOT NULL,
  patient_wait integer,
  patient_delay integer,
  sent_in date,
  "left" date,
  CONSTRAINT pk_appointment_id PRIMARY KEY (id),
  CONSTRAINT fk_appointment_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_appointment_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_appointment_schedule_id FOREIGN KEY (schedule_id)
      REFERENCES schedule (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE appointment
  OWNER TO postgres;

-- Index: appointment_id

-- DROP INDEX appointment_id;

CREATE UNIQUE INDEX appointment_id
  ON appointment
  USING btree
  (id);

-- Index: appointment_patient_id

-- DROP INDEX appointment_patient_id;

CREATE INDEX appointment_patient_id
  ON appointment
  USING btree
  (patient_id);
ALTER TABLE appointment CLUSTER ON appointment_patient_id;


-- Table: encounter

-- DROP TABLE encounter;

CREATE TABLE encounter
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  practitioner_id uuid NOT NULL,
  appointment_id uuid,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  reason_snomed_concept_id bigint,
  CONSTRAINT pk_encounter_id PRIMARY KEY (id),
  CONSTRAINT fk_encounter_appointment_id FOREIGN KEY (appointment_id)
      REFERENCES appointment (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE encounter
  OWNER TO postgres;

-- Index: encounter_id

-- DROP INDEX encounter_id;

CREATE UNIQUE INDEX encounter_id
  ON encounter
  USING btree
  (id);

-- Index: encounter_patient_id

-- DROP INDEX encounter_patient_id;

CREATE INDEX encounter_patient_id
  ON encounter
  USING btree
  (patient_id);
ALTER TABLE encounter CLUSTER ON encounter_patient_id;

-- Index: fki_encounter_appointment_id

-- DROP INDEX fki_encounter_appointment_id;

CREATE INDEX fki_encounter_appointment_id
  ON encounter
  USING btree
  (appointment_id);

-- Index: fki_encounter_patient_id_organisation_id

-- DROP INDEX fki_encounter_patient_id_organisation_id;

CREATE INDEX fki_encounter_patient_id_organisation_id
  ON encounter
  USING btree
  (patient_id, organisation_id);

-- Table: allergy_intolerance

-- DROP TABLE allergy_intolerance;

CREATE TABLE allergy_intolerance
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint,
  CONSTRAINT pk_allergy_intolerance_id PRIMARY KEY (id),
  CONSTRAINT fk_allergy_intolerance_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_allergy_intolerance_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_allergy_intolerance_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE allergy_intolerance
  OWNER TO postgres;

-- Index: allergy_intolerance_id

-- DROP INDEX allergy_intolerance_id;

CREATE UNIQUE INDEX allergy_intolerance_id
  ON allergy_intolerance
  USING btree
  (id);

-- Index: allergy_intolerance_patient_id

-- DROP INDEX allergy_intolerance_patient_id;

CREATE INDEX allergy_intolerance_patient_id
  ON allergy_intolerance
  USING btree
  (patient_id);
ALTER TABLE allergy_intolerance CLUSTER ON allergy_intolerance_patient_id;


-- Table: condition

-- DROP TABLE condition;

CREATE TABLE condition
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint,
  is_review boolean NOT NULL,
  CONSTRAINT pk_condition_id PRIMARY KEY (id),
  CONSTRAINT fk_condition_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_condition_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_condition_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE condition
  OWNER TO postgres;

-- Index: condition_id

-- DROP INDEX condition_id;

CREATE UNIQUE INDEX condition_id
  ON condition
  USING btree
  (id);

-- Index: condition_patient_id

-- DROP INDEX condition_patient_id;

CREATE INDEX condition_patient_id
  ON condition
  USING btree
  (patient_id);
ALTER TABLE condition CLUSTER ON condition_patient_id;

-- Table: diagnostic_order

-- DROP TABLE diagnostic_order;

CREATE TABLE diagnostic_order
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint,
  CONSTRAINT pk_diagnostic_order_id PRIMARY KEY (id),
  CONSTRAINT fk_diagnostic_order_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_diagnostic_order_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_diagnostic_order_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE diagnostic_order
  OWNER TO postgres;

-- Index: diagnostic_order_id

-- DROP INDEX diagnostic_order_id;

CREATE UNIQUE INDEX diagnostic_order_id
  ON diagnostic_order
  USING btree
  (id);

-- Index: diagnostic_order_patient_id

-- DROP INDEX diagnostic_order_patient_id;

CREATE INDEX diagnostic_order_patient_id
  ON diagnostic_order
  USING btree
  (patient_id);
ALTER TABLE diagnostic_order CLUSTER ON diagnostic_order_patient_id;


-- Table: family_member_history

-- DROP TABLE family_member_history;

CREATE TABLE family_member_history
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint,
  CONSTRAINT pk_family_member_history_id PRIMARY KEY (id),
  CONSTRAINT fk_family_member_history_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_family_member_history_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_family_member_history_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE family_member_history
  OWNER TO postgres;

-- Index: family_member_history_id

-- DROP INDEX family_member_history_id;

CREATE UNIQUE INDEX family_member_history_id
  ON family_member_history
  USING btree
  (id);

-- Index: family_member_history_patient_id

-- DROP INDEX family_member_history_patient_id;

CREATE INDEX family_member_history_patient_id
  ON family_member_history
  USING btree
  (patient_id);
ALTER TABLE family_member_history CLUSTER ON family_member_history_patient_id;

-- Table: immunisation

-- DROP TABLE immunisation;

CREATE TABLE immunisation
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint,
  CONSTRAINT pk_immunisation_id PRIMARY KEY (id),
  CONSTRAINT fk_immunisation_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_immunisation_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_immunisation_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE immunisation
  OWNER TO postgres;

-- Index: immunisation_id

-- DROP INDEX immunisation_id;

CREATE UNIQUE INDEX immunisation_id
  ON immunisation
  USING btree
  (id);

-- Index: immunisation_patient_id

-- DROP INDEX immunisation_patient_id;

CREATE INDEX immunisation_patient_id
  ON immunisation
  USING btree
  (patient_id);
ALTER TABLE immunisation CLUSTER ON immunisation_patient_id;


-- Table: medication_statement

-- DROP TABLE medication_statement;

CREATE TABLE medication_statement
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  dmd_id bigint,
  status character varying(50) NOT NULL,
  cancellation_date date,
  dose character varying(255) NOT NULL,
  quantity_value real,
  quantity_unit character varying(255),
  authorisation_type character varying(50) NOT NULL,
  CONSTRAINT pk_medication_statement_id PRIMARY KEY (id),
  CONSTRAINT fk_medication_statement_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_statement_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_statement_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE medication_statement
  OWNER TO postgres;

-- Index: medication_statement_id

-- DROP INDEX medication_statement_id;

CREATE UNIQUE INDEX medication_statement_id
  ON medication_statement
  USING btree
  (id);

-- Index: medication_statement_patient_id

-- DROP INDEX medication_statement_patient_id;

CREATE INDEX medication_statement_patient_id
  ON medication_statement
  USING btree
  (patient_id);
ALTER TABLE medication_statement CLUSTER ON medication_statement_patient_id;

-- Table: medication_order

-- DROP TABLE medication_order;

CREATE TABLE medication_order
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  dmd_id bigint,
  dose character varying(255) NOT NULL,
  quantity_value real,
  quantity_unit character varying(255),
  duration_days integer NOT NULL,
  estimated_cost real,
  medication_statement_id uuid NOT NULL,
  CONSTRAINT pk_medication_order_id PRIMARY KEY (id),
  CONSTRAINT fk_medication_order_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_order_medication_statement_id FOREIGN KEY (medication_statement_id)
      REFERENCES medication_statement (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_order_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_order_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE medication_order
  OWNER TO postgres;

-- Index: medication_order_id

-- DROP INDEX medication_order_id;

CREATE UNIQUE INDEX medication_order_id
  ON medication_order
  USING btree
  (id);

-- Index: medication_order_patient_id

-- DROP INDEX medication_order_patient_id;

CREATE INDEX medication_order_patient_id
  ON medication_order
  USING btree
  (patient_id);
ALTER TABLE medication_order CLUSTER ON medication_order_patient_id;

-- Table: observation

-- DROP TABLE observation;

CREATE TABLE observation
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint,
  value real NOT NULL,
  units character varying(50) NOT NULL,
  CONSTRAINT pk_observation_id PRIMARY KEY (id),
  CONSTRAINT fk_observation_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_observation_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_observation_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE observation
  OWNER TO postgres;

-- Index: observation_id

-- DROP INDEX observation_id;

CREATE UNIQUE INDEX observation_id
  ON observation
  USING btree
  (id);

-- Index: observation_patient_id

-- DROP INDEX observation_patient_id;

CREATE INDEX observation_patient_id
  ON observation
  USING btree
  (patient_id);
ALTER TABLE observation CLUSTER ON observation_patient_id;


-- Table: procedure

-- DROP TABLE procedure;

CREATE TABLE procedure
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint,
  CONSTRAINT pk_procedure_id PRIMARY KEY (id),
  CONSTRAINT fk_procedure_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE procedure
  OWNER TO postgres;

-- Index: procedure_id

-- DROP INDEX procedure_id;

CREATE UNIQUE INDEX procedure_id
  ON procedure
  USING btree
  (id);

-- Index: procedure_patient_id

-- DROP INDEX procedure_patient_id;

CREATE INDEX procedure_patient_id
  ON procedure
  USING btree
  (patient_id);
ALTER TABLE procedure CLUSTER ON procedure_patient_id;

-- Table: procedure_request

-- DROP TABLE procedure_request;

CREATE TABLE procedure_request
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint,
  procedure_status character varying(50),
  CONSTRAINT pk_procedure_request_id PRIMARY KEY (id),
  CONSTRAINT fk_procedure_request_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_request_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_request_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE procedure_request
  OWNER TO postgres;

-- Index: procedure_request_id

-- DROP INDEX procedure_request_id;

CREATE UNIQUE INDEX procedure_request_id
  ON procedure_request
  USING btree
  (id);

-- Index: procedure_request_patient_id

-- DROP INDEX procedure_request_patient_id;

CREATE INDEX procedure_request_patient_id
  ON procedure_request
  USING btree
  (patient_id);
ALTER TABLE procedure_request CLUSTER ON procedure_request_patient_id;

-- Table: referral_request

-- DROP TABLE referral_request;

CREATE TABLE referral_request
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint,
  recipient_organisation_name character varying(255),
  recipient_organisation_ods_code character varying(50),
  priority character varying(50),
  service_requested character varying(255),
  mode character varying(50),
  CONSTRAINT pk_referral_request_id PRIMARY KEY (id),
  CONSTRAINT fk_referral_request_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE referral_request
  OWNER TO postgres;

-- Index: referral_request_id

-- DROP INDEX referral_request_id;

CREATE UNIQUE INDEX referral_request_id
  ON referral_request
  USING btree
  (id);

-- Index: referral_request_patient_id

-- DROP INDEX referral_request_patient_id;

CREATE INDEX referral_request_patient_id
  ON referral_request
  USING btree
  (patient_id);
ALTER TABLE referral_request CLUSTER ON referral_request_patient_id;


