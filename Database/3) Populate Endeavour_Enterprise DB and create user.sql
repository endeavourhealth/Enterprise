USE Endeavour_Enterprise

INSERT INTO Definition.DependencyType VALUES (0, 'IsChildOf')
INSERT INTO Definition.DependencyType VALUES (1, 'IsContainedWithin')
INSERT INTO Definition.DependencyType VALUES (2, 'Uses')

INSERT INTO Definition.ItemType VALUES (0, 'ReportFolder')
INSERT INTO Definition.ItemType VALUES (1, 'Report')
INSERT INTO Definition.ItemType VALUES (2, 'Query')
INSERT INTO Definition.ItemType VALUES (3, 'Test')
INSERT INTO Definition.ItemType VALUES (4, 'DataSource')
INSERT INTO Definition.ItemType VALUES (5, 'CodeSet')
INSERT INTO Definition.ItemType VALUES (6, 'ListOutput')
INSERT INTO Definition.ItemType VALUES (7, 'LibraryFolder')

INSERT INTO Execution.Status VALUES (0, 'Executing')
INSERT INTO Execution.Status VALUES (1, 'Succeeded')
INSERT INTO Execution.Status VALUES (2, 'Failed')
INSERT INTO Execution.Status VALUES (3, 'NoJobRequests')

DECLARE @OrgId uniqueIdentifier = NEWID();
DECLARE @RegularUser uniqueIdentifier = NEWID();
DECLARE @AdminUser uniqueIdentifier = NEWID();
DECLARE @SuperUser uniqueIdentifier = NEWID();

INSERT INTO Administration.Organisation VALUES (@OrgId, 'Test Organisation', '12345')

INSERT INTO Administration.EndUser VALUES (@RegularUser, 'Mr', 'Regular', 'User', 'regular@email', 0)
INSERT INTO Administration.EndUser VALUES (@AdminUser, 'Mr', 'Admin', 'User', 'admin@email', 0)
INSERT INTO Administration.EndUser VALUES (@SuperUser, 'Mr', 'Super', 'User', 'super@email', 1)

INSERT INTO Administration.OrganisationEndUserLink VALUES (NEWID(), @OrgId, @RegularUser, 0, null)
INSERT INTO Administration.OrganisationEndUserLink VALUES (NEWID(), @OrgId, @AdminUser, 1, null)
--no org/user link required for super user

INSERT INTO Administration.EndUserPwd VALUES (NEWID(), @RegularUser, '1000:1d049ba2ce1cbc28d76fed47e9699b21885252496d58b58b:c4d1df46d3ba5867349bec73fafba63468803ba5fe0d8edd', null, 0, 0)
INSERT INTO Administration.EndUserPwd VALUES (NEWID(), @AdminUser, '1000:1d049ba2ce1cbc28d76fed47e9699b21885252496d58b58b:c4d1df46d3ba5867349bec73fafba63468803ba5fe0d8edd', null, 0, 0)
INSERT INTO Administration.EndUserPwd VALUES (NEWID(), @SuperUser, '1000:1d049ba2ce1cbc28d76fed47e9699b21885252496d58b58b:c4d1df46d3ba5867349bec73fafba63468803ba5fe0d8edd', null, 0, 0)

--the above creates three users all with password "test"
--regular@email    non-admin user
--admin@email      admin user
--super@email      super user





