-- Table: clinical.allergy_intolerance

-- DROP TABLE clinical.allergy_intolerance;

CREATE TABLE clinical.allergy_intolerance
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint NOT NULL,
  CONSTRAINT pk_allergy_intolerance_id PRIMARY KEY (id),
  CONSTRAINT fk_allergy_intolerance_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES clinical.encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_allergy_intolerance_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES clinical.patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_allergy_intolerance_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES admin.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE clinical.allergy_intolerance
  OWNER TO postgres;

-- Index: clinical.allergy_intolerance_id

-- DROP INDEX clinical.allergy_intolerance_id;

CREATE UNIQUE INDEX allergy_intolerance_id
  ON clinical.allergy_intolerance
  USING btree
  (id);

-- Index: clinical.allergy_intolerance_patient_id

-- DROP INDEX clinical.allergy_intolerance_patient_id;

CREATE INDEX allergy_intolerance_patient_id
  ON clinical.allergy_intolerance
  USING btree
  (patient_id);
ALTER TABLE clinical.allergy_intolerance CLUSTER ON allergy_intolerance_patient_id;

-- Table: clinical.appointment

-- DROP TABLE clinical.appointment;

CREATE TABLE clinical.appointment
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
      REFERENCES clinical.patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_appointment_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES admin.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_appointment_schedule_id FOREIGN KEY (schedule_id)
      REFERENCES admin.schedule (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE clinical.appointment
  OWNER TO postgres;

-- Index: clinical.appointment_id

-- DROP INDEX clinical.appointment_id;

CREATE UNIQUE INDEX appointment_id
  ON clinical.appointment
  USING btree
  (id);

-- Index: clinical.appointment_patient_id

-- DROP INDEX clinical.appointment_patient_id;

CREATE INDEX appointment_patient_id
  ON clinical.appointment
  USING btree
  (patient_id);
ALTER TABLE clinical.appointment CLUSTER ON appointment_patient_id;

-- Table: clinical.condition

-- DROP TABLE clinical.condition;

CREATE TABLE clinical.condition
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint NOT NULL,
  is_review bit(1) NOT NULL,
  CONSTRAINT pk_condition_id PRIMARY KEY (id),
  CONSTRAINT fk_condition_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES clinical.encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_condition_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES clinical.patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_condition_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES admin.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE clinical.condition
  OWNER TO postgres;

-- Index: clinical.condition_id

-- DROP INDEX clinical.condition_id;

CREATE UNIQUE INDEX condition_id
  ON clinical.condition
  USING btree
  (id);

-- Index: clinical.condition_patient_id

-- DROP INDEX clinical.condition_patient_id;

CREATE INDEX condition_patient_id
  ON clinical.condition
  USING btree
  (patient_id);
ALTER TABLE clinical.condition CLUSTER ON condition_patient_id;

-- Table: clinical.diagnostic_order

-- DROP TABLE clinical.diagnostic_order;

CREATE TABLE clinical.diagnostic_order
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint NOT NULL,
  CONSTRAINT pk_diagnostic_order_id PRIMARY KEY (id),
  CONSTRAINT fk_diagnostic_order_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES clinical.encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_diagnostic_order_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES clinical.patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_diagnostic_order_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES admin.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE clinical.diagnostic_order
  OWNER TO postgres;

-- Index: clinical.diagnostic_order_id

-- DROP INDEX clinical.diagnostic_order_id;

CREATE UNIQUE INDEX diagnostic_order_id
  ON clinical.diagnostic_order
  USING btree
  (id);

-- Index: clinical.diagnostic_order_patient_id

-- DROP INDEX clinical.diagnostic_order_patient_id;

CREATE INDEX diagnostic_order_patient_id
  ON clinical.diagnostic_order
  USING btree
  (patient_id);
ALTER TABLE clinical.diagnostic_order CLUSTER ON diagnostic_order_patient_id;

-- Table: clinical.encounter

-- DROP TABLE clinical.encounter;

CREATE TABLE clinical.encounter
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
      REFERENCES clinical.appointment (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_encounter_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES clinical.patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES admin.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE clinical.encounter
  OWNER TO postgres;

-- Index: clinical.encounter_id

-- DROP INDEX clinical.encounter_id;

CREATE UNIQUE INDEX encounter_id
  ON clinical.encounter
  USING btree
  (id);

-- Index: clinical.encounter_patient_id

-- DROP INDEX clinical.encounter_patient_id;

CREATE INDEX encounter_patient_id
  ON clinical.encounter
  USING btree
  (patient_id);
ALTER TABLE clinical.encounter CLUSTER ON encounter_patient_id;

-- Index: clinical.fki_encounter_appointment_id

-- DROP INDEX clinical.fki_encounter_appointment_id;

CREATE INDEX fki_encounter_appointment_id
  ON clinical.encounter
  USING btree
  (appointment_id);

-- Index: clinical.fki_encounter_patient_id_organisation_id

-- DROP INDEX clinical.fki_encounter_patient_id_organisation_id;

CREATE INDEX fki_encounter_patient_id_organisation_id
  ON clinical.encounter
  USING btree
  (patient_id, organisation_id);

-- Table: clinical.family_member_history

-- DROP TABLE clinical.family_member_history;

CREATE TABLE clinical.family_member_history
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint NOT NULL,
  CONSTRAINT pk_family_member_history_id PRIMARY KEY (id),
  CONSTRAINT fk_family_member_history_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES clinical.encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_family_member_history_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES clinical.patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_family_member_history_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES admin.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE clinical.family_member_history
  OWNER TO postgres;

-- Index: clinical.family_member_history_id

-- DROP INDEX clinical.family_member_history_id;

CREATE UNIQUE INDEX family_member_history_id
  ON clinical.family_member_history
  USING btree
  (id);

-- Index: clinical.family_member_history_patient_id

-- DROP INDEX clinical.family_member_history_patient_id;

CREATE INDEX family_member_history_patient_id
  ON clinical.family_member_history
  USING btree
  (patient_id);
ALTER TABLE clinical.family_member_history CLUSTER ON family_member_history_patient_id;

-- Table: clinical.immunisation

-- DROP TABLE clinical.immunisation;

CREATE TABLE clinical.immunisation
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint NOT NULL,
  CONSTRAINT pk_immunisation_id PRIMARY KEY (id),
  CONSTRAINT fk_immunisation_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES clinical.encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_immunisation_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES clinical.patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_immunisation_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES admin.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE clinical.immunisation
  OWNER TO postgres;

-- Index: clinical.immunisation_id

-- DROP INDEX clinical.immunisation_id;

CREATE UNIQUE INDEX immunisation_id
  ON clinical.immunisation
  USING btree
  (id);

-- Index: clinical.immunisation_patient_id

-- DROP INDEX clinical.immunisation_patient_id;

CREATE INDEX immunisation_patient_id
  ON clinical.immunisation
  USING btree
  (patient_id);
ALTER TABLE clinical.immunisation CLUSTER ON immunisation_patient_id;

-- Table: clinical.medication_order

-- DROP TABLE clinical.medication_order;

CREATE TABLE clinical.medication_order
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  dmd_id bigint NOT NULL,
  dose character varying(255) NOT NULL,
  quantity_value real,
  quantity_unit character varying(255),
  duration_days integer NOT NULL,
  estimated_cost real,
  medication_statement_id uuid NOT NULL,
  CONSTRAINT pk_medication_order_id PRIMARY KEY (id),
  CONSTRAINT fk_medication_order_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES clinical.encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_order_medication_statement_id FOREIGN KEY (medication_statement_id)
      REFERENCES clinical.medication_statement (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_order_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES clinical.patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_order_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES admin.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE clinical.medication_order
  OWNER TO postgres;

-- Index: clinical.medication_order_id

-- DROP INDEX clinical.medication_order_id;

CREATE UNIQUE INDEX medication_order_id
  ON clinical.medication_order
  USING btree
  (id);

-- Index: clinical.medication_order_patient_id

-- DROP INDEX clinical.medication_order_patient_id;

CREATE INDEX medication_order_patient_id
  ON clinical.medication_order
  USING btree
  (patient_id);
ALTER TABLE clinical.medication_order CLUSTER ON medication_order_patient_id;

-- Table: clinical.medication_statement

-- DROP TABLE clinical.medication_statement;

CREATE TABLE clinical.medication_statement
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  dmd_id bigint NOT NULL,
  status character varying(50) NOT NULL,
  cancellation_date date,
  dose character varying(255) NOT NULL,
  quantity_value real,
  quantity_unit character varying(255),
  authorisation_type character varying(50) NOT NULL,
  CONSTRAINT pk_medication_statement_id PRIMARY KEY (id),
  CONSTRAINT fk_medication_statement_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES clinical.encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_statement_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES clinical.patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_medication_statement_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES admin.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE clinical.medication_statement
  OWNER TO postgres;

-- Index: clinical.medication_statement_id

-- DROP INDEX clinical.medication_statement_id;

CREATE UNIQUE INDEX medication_statement_id
  ON clinical.medication_statement
  USING btree
  (id);

-- Index: clinical.medication_statement_patient_id

-- DROP INDEX clinical.medication_statement_patient_id;

CREATE INDEX medication_statement_patient_id
  ON clinical.medication_statement
  USING btree
  (patient_id);
ALTER TABLE clinical.medication_statement CLUSTER ON medication_statement_patient_id;

-- Table: clinical.observation

-- DROP TABLE clinical.observation;

CREATE TABLE clinical.observation
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint NOT NULL,
  value real NOT NULL,
  units character varying(50) NOT NULL,
  CONSTRAINT pk_observation_id PRIMARY KEY (id),
  CONSTRAINT fk_observation_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES clinical.encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_observation_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES clinical.patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_observation_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES admin.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE clinical.observation
  OWNER TO postgres;

-- Index: clinical.observation_id

-- DROP INDEX clinical.observation_id;

CREATE UNIQUE INDEX observation_id
  ON clinical.observation
  USING btree
  (id);

-- Index: clinical.observation_patient_id

-- DROP INDEX clinical.observation_patient_id;

CREATE INDEX observation_patient_id
  ON clinical.observation
  USING btree
  (patient_id);
ALTER TABLE clinical.observation CLUSTER ON observation_patient_id;

-- Table: clinical.patient

-- DROP TABLE clinical.patient;

CREATE TABLE clinical.patient
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  date_of_birth date NOT NULL,
  year_of_death integer,
  gender character(1) NOT NULL,
  date_registered date NOT NULL,
  date_registered_end date,
  usual_gp_name character varying(255),
  registration_type_code character varying(50) NOT NULL,
  registration_type_desc character varying(255) NOT NULL,
  pseudo_id character varying(255) NOT NULL,
  CONSTRAINT pk_patient_id_organisation_id PRIMARY KEY (id, organisation_id),
  CONSTRAINT fk_patient_organisation_id FOREIGN KEY (organisation_id)
      REFERENCES admin.organisation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE clinical.patient
  OWNER TO postgres;

-- Index: clinical.patient_id

-- DROP INDEX clinical.patient_id;

CREATE UNIQUE INDEX patient_id
  ON clinical.patient
  USING btree
  (id);
ALTER TABLE clinical.patient CLUSTER ON patient_id;

-- Table: clinical.procedure

-- DROP TABLE clinical.procedure;

CREATE TABLE clinical.procedure
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint NOT NULL,
  CONSTRAINT pk_procedure_id PRIMARY KEY (id),
  CONSTRAINT fk_procedure_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES clinical.encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES clinical.patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES admin.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE clinical.procedure
  OWNER TO postgres;

-- Index: clinical.procedure_id

-- DROP INDEX clinical.procedure_id;

CREATE UNIQUE INDEX procedure_id
  ON clinical.procedure
  USING btree
  (id);

-- Index: clinical.procedure_patient_id

-- DROP INDEX clinical.procedure_patient_id;

CREATE INDEX procedure_patient_id
  ON clinical.procedure
  USING btree
  (patient_id);
ALTER TABLE clinical.procedure CLUSTER ON procedure_patient_id;

-- Table: clinical.procedure_request

-- DROP TABLE clinical.procedure_request;

CREATE TABLE clinical.procedure_request
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
      REFERENCES clinical.encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_request_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES clinical.patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_procedure_request_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES admin.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE clinical.procedure_request
  OWNER TO postgres;

-- Index: clinical.procedure_request_id

-- DROP INDEX clinical.procedure_request_id;

CREATE UNIQUE INDEX procedure_request_id
  ON clinical.procedure_request
  USING btree
  (id);

-- Index: clinical.procedure_request_patient_id

-- DROP INDEX clinical.procedure_request_patient_id;

CREATE INDEX procedure_request_patient_id
  ON clinical.procedure_request
  USING btree
  (patient_id);
ALTER TABLE clinical.procedure_request CLUSTER ON procedure_request_patient_id;

-- Table: clinical.referral_request

-- DROP TABLE clinical.referral_request;

CREATE TABLE clinical.referral_request
(
  id uuid NOT NULL,
  organisation_id uuid NOT NULL,
  patient_id uuid NOT NULL,
  encounter_id uuid,
  practitioner_id uuid NOT NULL,
  date date NOT NULL,
  date_precision character varying(50) NOT NULL,
  snomed_concept_id bigint NOT NULL,
  recipient_organisation_id uuid,
  urgency character varying(50),
  service_requested character varying(255),
  type character varying(50),
  CONSTRAINT pk_referral_request_id PRIMARY KEY (id),
  CONSTRAINT fk_referral_request_encounter_id FOREIGN KEY (encounter_id)
      REFERENCES clinical.encounter (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_patient_id_organisation_id FOREIGN KEY (patient_id, organisation_id)
      REFERENCES clinical.patient (id, organisation_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_practitioner_id FOREIGN KEY (practitioner_id)
      REFERENCES admin.practitioner (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_referral_request_recipient_organisation_id FOREIGN KEY (recipient_organisation_id)
      REFERENCES admin.organisation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE clinical.referral_request
  OWNER TO postgres;

-- Index: clinical.referral_request_id

-- DROP INDEX clinical.referral_request_id;

CREATE UNIQUE INDEX referral_request_id
  ON clinical.referral_request
  USING btree
  (id);

-- Index: clinical.referral_request_patient_id

-- DROP INDEX clinical.referral_request_patient_id;

CREATE INDEX referral_request_patient_id
  ON clinical.referral_request
  USING btree
  (patient_id);
ALTER TABLE clinical.referral_request CLUSTER ON referral_request_patient_id;

