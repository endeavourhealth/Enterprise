DROP TABLE IF EXISTS enterprise_admin.ActiveItem;
DROP TABLE IF EXISTS enterprise_admin.Audit;
DROP TABLE IF EXISTS enterprise_admin.DependencyType;
DROP TABLE IF EXISTS enterprise_admin.Item;
DROP TABLE IF EXISTS enterprise_admin.ItemDependency;
DROP TABLE IF EXISTS enterprise_admin.ItemType;


CREATE TABLE ActiveItem (
	ActiveItemUuid char(36) NOT NULL,
	OrganisationUuid char(36) NOT NULL,
	ItemUuid char(36) NOT NULL,
	AuditUuid char(36) NOT NULL,
	ItemTypeId smallint NOT NULL,
	IsDeleted TINYINT(1) NOT NULL,
	PRIMARY KEY (ActiveItemUuid)
);
CREATE TABLE Audit (
	AuditUuid char(36) NOT NULL,
	EndUserUuid char(36) NOT NULL,
	TimeStamp timestamp NOT NULL,
	OrganisationUuid char(36) NOT NULL,
	PRIMARY KEY (AuditUuid)
);
CREATE TABLE DependencyType (
	DependencyTypeId smallint NOT NULL,
	Description varchar(100) NOT NULL,
	PRIMARY KEY (DependencyTypeId)
);
CREATE TABLE Item (
	ItemUuid char(36) NOT NULL,
	AuditUuid char(36) NOT NULL,
	XmlContent text NOT NULL,
	Title varchar(255) NOT NULL,
	Description text NOT NULL,
	IsDeleted TINYINT(1) NOT NULL,
	PRIMARY KEY (AuditUuid,ItemUuid)
);
CREATE TABLE ItemDependency (
	ItemUuid char(36) NOT NULL,
	AuditUuid char(36) NOT NULL,
	DependentItemUuid char(36) NOT NULL,
	DependencyTypeId smallint NOT NULL,
	PRIMARY KEY (AuditUuid,DependentItemUuid,ItemUuid)
);
CREATE TABLE ItemType (
	ItemTypeId smallint NOT NULL,
	Description varchar(100) NOT NULL,
	PRIMARY KEY (ItemTypeId)
);

INSERT INTO ItemType(ItemTypeId, Description) VALUES (5, 'CodeSet');
INSERT INTO ItemType(ItemTypeId, Description) VALUES (4, 'DataSource');
INSERT INTO ItemType(ItemTypeId, Description) VALUES (7, 'LibraryFolder');
INSERT INTO ItemType(ItemTypeId, Description) VALUES (6, 'ListOutput');
INSERT INTO ItemType(ItemTypeId, Description) VALUES (2, 'Query');
INSERT INTO ItemType(ItemTypeId, Description) VALUES (1, 'Report');
INSERT INTO ItemType(ItemTypeId, Description) VALUES (0, 'ReportFolder');
INSERT INTO ItemType(ItemTypeId, Description) VALUES (3, 'Test');

INSERT INTO DependencyType(DependencyTypeId, Description) VALUES (0, 'IsChildOf');
INSERT INTO DependencyType(DependencyTypeId, Description) VALUES (1, 'IsContainedWithin');
INSERT INTO DependencyType(DependencyTypeId, Description) VALUES (2, 'Uses');

