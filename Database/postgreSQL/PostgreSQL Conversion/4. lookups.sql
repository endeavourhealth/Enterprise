CREATE TABLE "Lookups".SourceOrganisation (
	OdsCode varchar(255) NOT NULL,
	Name varchar(255) NOT NULL,
	IsReferencedByData boolean NOT NULL,
	PRIMARY KEY (OdsCode)
);
CREATE TABLE "Lookups".SourceOrganisationSet (
	SourceOrganisationSetUuid UUID NOT NULL,
	OrganisationUuid UUID NOT NULL,
	Name varchar(255) NOT NULL,
	OdsCodes text NOT NULL,
	PRIMARY KEY (SourceOrganisationSetUuid)
);
INSERT INTO "Lookups".SourceOrganisation(OdsCode, Name, IsReferencedByData) VALUES ('T00001', 'Test Surgery a', false);
INSERT INTO "Lookups".SourceOrganisation(OdsCode, Name, IsReferencedByData) VALUES ('T00003', 'Test Surgery 1', true);
INSERT INTO "Lookups".SourceOrganisation(OdsCode, Name, IsReferencedByData) VALUES ('T00015', 'Test Surgery b', false);
INSERT INTO "Lookups".SourceOrganisationSet(SourceOrganisationSetUuid, OrganisationUuid, Name, OdsCodes) VALUES ('D8886965-2F13-4A31-AF18-6AC3DD4C51F5', 'B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6', 'Andys org set', 'T00003');
INSERT INTO "Lookups".SourceOrganisationSet(SourceOrganisationSetUuid, OrganisationUuid, Name, OdsCodes) VALUES ('03009606-3919-4505-8E5B-9AB7B9017267', 'B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6', 'Rich''s Org Set', 'T00003');
INSERT INTO "Lookups".SourceOrganisationSet(SourceOrganisationSetUuid, OrganisationUuid, Name, OdsCodes) VALUES ('582D1F46-7949-44AD-9857-C760279BA391', 'B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6', 'Drew''s Org Set 2', 'T00001|T00003|T00015');
ALTER TABLE "Lookups".SourceOrganisationSet
	ADD FOREIGN KEY (OrganisationUuid) 
	REFERENCES "Administration".Organisation (OrganisationUuid);


