CREATE TABLE "Administration".EndUser (
	EndUserUuid UUID NOT NULL,
	Title varchar(255) NOT NULL,
	Forename varchar(255) NOT NULL,
	Surname varchar(255) NOT NULL,
	Email varchar(255) NOT NULL,
	IsSuperUser boolean NOT NULL,
	PRIMARY KEY (EndUserUuid)
);
CREATE TABLE "Administration".EndUserEmailInvite (
	EndUserEmailInviteUuid UUID NOT NULL,
	EndUserUuid UUID NOT NULL,
	UniqueToken varchar(255) NOT NULL,
	DtCompleted timestamp,
	PRIMARY KEY (EndUserEmailInviteUuid)
);
CREATE TABLE "Administration".EndUserPwd (
	EndUserPwdUuid UUID NOT NULL,
	EndUserUuid UUID NOT NULL,
	PwdHash varchar(500) NOT NULL,
	DtExpired timestamp,
	FailedAttempts int NOT NULL,
	IsOneTimeUse boolean NOT NULL,
	PRIMARY KEY (EndUserPwdUuid)
);
CREATE TABLE "Administration".Organisation (
	OrganisationUuid UUID NOT NULL,
	Name varchar(255) NOT NULL,
	NationalId varchar(255) NOT NULL,
	PRIMARY KEY (OrganisationUuid)
);
CREATE TABLE "Administration".OrganisationEndUserLink (
	OrganisationEndUserLinkUuid UUID NOT NULL,
	OrganisationUuid UUID NOT NULL,
	EndUserUuid UUID NOT NULL,
	IsAdmin boolean NOT NULL,
	DtExpired timestamp,
	PRIMARY KEY (OrganisationEndUserLinkUuid)
);
INSERT INTO "Administration".EndUser(EndUserUuid, Title, Forename, Surname, Email, IsSuperUser) VALUES ('C8E02710-C380-454C-8776-45E4DD180825', 'Mr', 'Admin', 'User', 'admin@email', false);
INSERT INTO "Administration".EndUser(EndUserUuid, Title, Forename, Surname, Email, IsSuperUser) VALUES ('B5D86DA5-5E57-422E-B2C5-7E9C6F3DEA32', 'Mr', 'Regular', 'User', 'regular@email', false);
INSERT INTO "Administration".EndUser(EndUserUuid, Title, Forename, Surname, Email, IsSuperUser) VALUES ('27A9798A-CDC4-442F-A543-7FBF8C3FC5F1', 'Mr', 'Super', 'User', 'super@email', true);
INSERT INTO "Administration".EndUser(EndUserUuid, Title, Forename, Surname, Email, IsSuperUser) VALUES ('97902338-A71D-4609-9D66-80AE7F8A74E1', '', 'New', 'SuperUser', 'super2@email', true);
INSERT INTO "Administration".EndUser(EndUserUuid, Title, Forename, Surname, Email, IsSuperUser) VALUES ('91B0A67F-024B-46AD-ACEB-BDB63D2783A2', 'Mr', 'Drew', 'Littler', 'drewlittler@hotmail.com', false);
INSERT INTO "Administration".EndUser(EndUserUuid, Title, Forename, Surname, Email, IsSuperUser) VALUES ('EC09DB1A-A670-494C-8BFC-FA09BDE74804', 'Dr', 'William', 'BeDeleted', 'deletetest@email', false);
INSERT INTO "Administration".EndUserEmailInvite(EndUserEmailInviteUuid, EndUserUuid, UniqueToken, DtCompleted) VALUES ('E84172BB-6A30-4039-BB71-2FAC752F200D', '91B0A67F-024B-46AD-ACEB-BDB63D2783A2', 'OThhMjQwNDQtMzhkYi00YTk5LTk1YmQtNDI1NjU4YmYyYWE1', null);
INSERT INTO "Administration".EndUserEmailInvite(EndUserEmailInviteUuid, EndUserUuid, UniqueToken, DtCompleted) VALUES ('536991FB-36C3-4738-9193-827798F4A0D9', 'B5D86DA5-5E57-422E-B2C5-7E9C6F3DEA32', 'ZDdkMDc2ZDEtY2ZmNy00NDEwLWEyMjAtNDk2NjI1MTgyMWY5', '2016-04-05 17:41:06.1530000');
INSERT INTO "Administration".EndUserEmailInvite(EndUserEmailInviteUuid, EndUserUuid, UniqueToken, DtCompleted) VALUES ('FB9A1D1A-49EF-413D-96E7-8C5F44D6DA7B', 'B5D86DA5-5E57-422E-B2C5-7E9C6F3DEA32', 'NDdkM2JjYTItZDk0Ni00Mjk0LThkMDEtZDY4OWViNjRkMmE0', null);
INSERT INTO "Administration".EndUserEmailInvite(EndUserEmailInviteUuid, EndUserUuid, UniqueToken, DtCompleted) VALUES ('51424F6A-C0A6-4099-B6AC-9A8D7D9474AC', 'B5D86DA5-5E57-422E-B2C5-7E9C6F3DEA32', 'NWE2NjQ5NDUtNDEzMC00NjBmLWEyYTAtNjZhMWM3MTBmZThl', '2016-04-05 17:41:39.8900000');
INSERT INTO "Administration".EndUserEmailInvite(EndUserEmailInviteUuid, EndUserUuid, UniqueToken, DtCompleted) VALUES ('B86C50F5-810E-4BDD-A716-B1C5AD8F22C3', '91B0A67F-024B-46AD-ACEB-BDB63D2783A2', 'MmQ0ZjM5YTQtNGFhYS00NTA1LWJkNjUtNjIyMWQ0ZjFjYWNl', '2016-04-06 10:42:41.5530000');
INSERT INTO "Administration".EndUserEmailInvite(EndUserEmailInviteUuid, EndUserUuid, UniqueToken, DtCompleted) VALUES ('A2D9EEC6-472C-44A0-9712-B8CA08F68FCF', 'B5D86DA5-5E57-422E-B2C5-7E9C6F3DEA32', 'YTM4ZGYzZTgtOTFkYS00ZjI4LTk2ZjAtMmU1Zjk1Nzk3OTRl', '2016-04-05 17:58:23.6030000');
INSERT INTO "Administration".EndUserEmailInvite(EndUserEmailInviteUuid, EndUserUuid, UniqueToken, DtCompleted) VALUES ('BCFF35D0-17F0-44B6-86F7-E2E62AE17181', '97902338-A71D-4609-9D66-80AE7F8A74E1', '92100002-55b6-400f-998d-d6cae583c2f9', null);
INSERT INTO "Administration".EndUserEmailInvite(EndUserEmailInviteUuid, EndUserUuid, UniqueToken, DtCompleted) VALUES ('57C85217-594A-41FB-8FD7-F11F8375CFAE', 'EC09DB1A-A670-494C-8BFC-FA09BDE74804', '0155f5b6-f625-4e9b-aeec-43c416f2e596', null);
INSERT INTO "Administration".EndUserPwd(EndUserPwdUuid, EndUserUuid, PwdHash, DtExpired, FailedAttempts, IsOneTimeUse) VALUES ('A858A09C-2A50-41BF-867C-21D5497ADB5E', 'C8E02710-C380-454C-8776-45E4DD180825', '1000:728fea95e3c928a0d02881d855aa515360e75d53ce4fc48c:09b8b1951fe7bbf881e7c3cf2444f48c5797ad3b073830a4', '2016-03-26 16:17:33.0040000', 0, false);
INSERT INTO "Administration".EndUserPwd(EndUserPwdUuid, EndUserUuid, PwdHash, DtExpired, FailedAttempts, IsOneTimeUse) VALUES ('EAE80128-5CC7-4D03-9DF4-68D26EC8DE7A', 'C8E02710-C380-454C-8776-45E4DD180825', '1000:ca64926b4976b94a58b37cec1abdd681871e8e3077b790d2:b16ceb05be8edc77e8ccede4b0b12ff0f6a2cce3d0821b68', null, 0, false);
INSERT INTO "Administration".EndUserPwd(EndUserPwdUuid, EndUserUuid, PwdHash, DtExpired, FailedAttempts, IsOneTimeUse) VALUES ('36C1A5AD-B33C-45FB-9609-B5665662D471', 'C8E02710-C380-454C-8776-45E4DD180825', '1000:1d049ba2ce1cbc28d76fed47e9699b21885252496d58b58b:c4d1df46d3ba5867349bec73fafba63468803ba5fe0d8edd', '2016-03-26 16:17:16.3080000', 0, false);
INSERT INTO "Administration".EndUserPwd(EndUserPwdUuid, EndUserUuid, PwdHash, DtExpired, FailedAttempts, IsOneTimeUse) VALUES ('A9B3F8BE-ADF8-4923-832C-BD0AB7E484DB', 'B5D86DA5-5E57-422E-B2C5-7E9C6F3DEA32', '1000:1d049ba2ce1cbc28d76fed47e9699b21885252496d58b58b:c4d1df46d3ba5867349bec73fafba63468803ba5fe0d8edd', null, 0, false);
INSERT INTO "Administration".EndUserPwd(EndUserPwdUuid, EndUserUuid, PwdHash, DtExpired, FailedAttempts, IsOneTimeUse) VALUES ('23A03045-53AF-4C65-87AF-F5FF6D15BDA8', '27A9798A-CDC4-442F-A543-7FBF8C3FC5F1', '1000:1d049ba2ce1cbc28d76fed47e9699b21885252496d58b58b:c4d1df46d3ba5867349bec73fafba63468803ba5fe0d8edd', null, 0, false);
INSERT INTO "Administration".Organisation(OrganisationUuid, Name, NationalId) VALUES ('B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6', 'Test Organisation', '12345');
INSERT INTO "Administration".OrganisationEndUserLink(OrganisationEndUserLinkUuid, OrganisationUuid, EndUserUuid, IsAdmin, DtExpired) VALUES ('188EAC6C-7864-4C7A-8DE8-0E65A7491B26', 'B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6', 'EC09DB1A-A670-494C-8BFC-FA09BDE74804', false, '2016-03-26 16:18:52.7050000');
INSERT INTO "Administration".OrganisationEndUserLink(OrganisationEndUserLinkUuid, OrganisationUuid, EndUserUuid, IsAdmin, DtExpired) VALUES ('6A82F368-DEC5-42F1-8C10-20AF183FC3FB', 'B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6', 'B5D86DA5-5E57-422E-B2C5-7E9C6F3DEA32', false, null);
INSERT INTO "Administration".OrganisationEndUserLink(OrganisationEndUserLinkUuid, OrganisationUuid, EndUserUuid, IsAdmin, DtExpired) VALUES ('5778C9CE-7096-424C-948E-20D537C19EA8', 'B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6', 'EC09DB1A-A670-494C-8BFC-FA09BDE74804', false, '2016-03-26 16:30:33.6960000');
INSERT INTO "Administration".OrganisationEndUserLink(OrganisationEndUserLinkUuid, OrganisationUuid, EndUserUuid, IsAdmin, DtExpired) VALUES ('008D8585-F17E-4F22-9064-37814C2D1D60', 'B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6', 'EC09DB1A-A670-494C-8BFC-FA09BDE74804', false, '2016-03-26 16:42:04.3790000');
INSERT INTO "Administration".OrganisationEndUserLink(OrganisationEndUserLinkUuid, OrganisationUuid, EndUserUuid, IsAdmin, DtExpired) VALUES ('FD503919-979A-417A-A039-582218234FAC', 'B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6', 'C8E02710-C380-454C-8776-45E4DD180825', true, null);
INSERT INTO "Administration".OrganisationEndUserLink(OrganisationEndUserLinkUuid, OrganisationUuid, EndUserUuid, IsAdmin, DtExpired) VALUES ('C77EA029-EDD6-4CA8-9108-7AE0EBA91045', 'B6FF900D-8FCD-43D8-AF37-5DB3A87A6EF6', '91B0A67F-024B-46AD-ACEB-BDB63D2783A2', false, null);
ALTER TABLE "Administration".EndUserEmailInvite
	ADD FOREIGN KEY (EndUserUuid) 
	REFERENCES "Administration".EndUser (EndUserUuid);


ALTER TABLE "Administration".EndUserPwd
	ADD FOREIGN KEY (EndUserUuid) 
	REFERENCES "Administration".EndUser (EndUserUuid);


ALTER TABLE "Administration".OrganisationEndUserLink
	ADD FOREIGN KEY (EndUserUuid) 
	REFERENCES "Administration".EndUser (EndUserUuid);

ALTER TABLE "Administration".OrganisationEndUserLink
	ADD FOREIGN KEY (OrganisationUuid) 
	REFERENCES "Administration".Organisation (OrganisationUuid);


