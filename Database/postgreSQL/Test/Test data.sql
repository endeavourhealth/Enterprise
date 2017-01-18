use Endeavour_Enterprise;

begin transaction;

	declare @OrganisationUuid uniqueidentifier = '04CF1D8D-B6E6-4E20-9A74-D6F197A9FE78';
	declare @UserUuid1 uniqueidentifier = 'A8F2A206-4384-4E92-AF55-86D590BA07DA'
	declare @UserUuid2 uniqueidentifier = '4E6A847E-F0D4-4B6F-B797-6D94C4571460'
	declare @FirstItem uniqueidentifier = '841D92D1-E80C-4512-A8C2-BBDE37FD80E1'
	

	insert into Administration.Organisation 
	(
		OrganisationUuid,
		Name
	)
	values 
	(
		@OrganisationUuid, 
		'Alpha Surgery'
	)

	insert into Administration.[User]
		(UserUuid, EmailAddress, Title, GivenName, FamilyName)
	values
		(@UserUuid1, 'david.stables@endeavourhealth.org', 'Dr', 'David', 'Stables')

	insert into Administration.UserPassword
		(UserUuid, PasswordHash, PasswordCreated)
	values
		(@UserUuid1, '1000:00edacb7bbffa28264a8fc9a02d7d9982f89b5de7337aed5:7b4c79b3c97260908d429bd3296011abe953d708291632d7', getdate())
	
	insert into Administration.[User]
		(UserUuid, EmailAddress, Title, GivenName, FamilyName)
	values
		(@UserUuid2, 'kb', 'Dr', 'Kambiz', 'Boomla')

	insert into Administration.UserPassword
		(UserUuid, PasswordHash, PasswordCreated)
	values
		(@UserUuid2, '1000:0a0dc7f9b97198dfac6dfb2335ab6485689179c5697b7972:c62bd866a3dffbe5684b910fca154ef1acc40e3ad4fe2c39', getdate())


	insert into Administration.UserAtOrganisation values (@UserUuid, @OrganisationUuid);

	--insert into [Definition].[Audit] (UserUuid, [DateTime]) values (@UserUuid, getdate());
	--declare @AuditId int = @@identity;


	--declare @ItemsToAdd table
	--(
	--	ItemUuid uniqueidentifier not null,
	--	Title varchar(100) not null,
	--	ModuleId tinyint not null,
	--	ItemTypeId tinyint not null,
	--	WithinDependency uniqueidentifier null,
	--	ChildDependency uniqueidentifier null
	--);

	--insert into @ItemsToAdd
	--values
	--	('01BE7640-802F-416B-8FD4-CC58A133F40E', 'Clinical', 0, 0, null, null),
	--	('C69970DA-CF6C-406B-BEEC-31762FC9A0A4', 'My JsonFolder', 0, 0, null, null),
	--	('D279B1ED-E854-4438-BEF2-C9F70A0B7C7D', 'Child 1', 0, 0, null, 'C69970DA-CF6C-406B-BEEC-31762FC9A0A4'),
	--	('62B07090-F17A-43C5-BD5A-4B808508A874', 'Child 2', 0, 0, null, 'C69970DA-CF6C-406B-BEEC-31762FC9A0A4'),
	--	('B6DE05EE-B7ED-4058-B4F6-93154A48028B', 'Sub Child', 0, 0, null, '62B07090-F17A-43C5-BD5A-4B808508A874'),
	--	('973473AD-9A9C-4E70-ACE6-E3D831BF192A', 'Sub Child 2', 0, 0, null, '62B07090-F17A-43C5-BD5A-4B808508A874'),

	--	('D7219FF4-339F-4A54-9DDD-818A0E00ACE9', 'Diabetics', 0, 2, '01BE7640-802F-416B-8FD4-CC58A133F40E', null),
	--	('ECB4497A-16A2-44C3-8B51-15CFC4BEA9F5', 'Asthmatics', 0, 2, '01BE7640-802F-416B-8FD4-CC58A133F40E', null),
	--	('098BE27B-1DD3-432F-9EDD-1049DAD4F7AC', 'Sub Asthmatics', 0, 2, '01BE7640-802F-416B-8FD4-CC58A133F40E', 'ECB4497A-16A2-44C3-8B51-15CFC4BEA9F5'),

	--	('5F7665F1-7970-4091-8BFB-6B281489565D', 'Diabetic Indicators', 0, 5, '01BE7640-802F-416B-8FD4-CC58A133F40E', null)
	--;

	--insert into [Definition].Items
	--select a.ItemUuid, @AuditId, null, 0, @OrganisationUuid, a.ModuleId, a.ItemTypeId, a.Title, null
	--from @ItemsToAdd as a;

	--insert into Definition.ActiveItems
	--select a.ItemUuid, @AuditId, @OrganisationUuid, a.ModuleId, a.ItemTypeId, a.Title
	--from @ItemsToAdd as a;

	--insert into Definition.ActiveItemDependency
	--select a.ItemUuid, a.ChildDependency, 0
	--from @ItemsToAdd as a
	--where a.ChildDependency is not null;


	--insert into Definition.ActiveItemDependency
	--select a.ItemUuid, a.WithinDependency, 1
	--from @ItemsToAdd as a
	--where a.WithinDependency is not null;


	--insert into [Definition].Items (ItemUuid, AuditId, Content, IsDeleted, OwnerOrganisationUuid, ItemTypeId, Title, ModuleId)
	--	values (@firstItem, @AuditId, null, 0, @OrganisationUuid, 1, 'Test report', 0);

	--insert into [Definition].ActiveItems (ItemUuid, CurrentAuditId, OwnerOrganisationUuid, ItemTypeId, Title, ModuleId)
	--	values (@firstItem, @AuditId, @OrganisationUuid, 1, 'Test report', 0);

	--insert into Execution.Request (ReportItemUuid, [DateTime], UserUuid, [Parameters])
	--	values (@firstItem, getdate(), @userId, '');

commit transaction;
