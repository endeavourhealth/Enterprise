use Discovery_Core;

declare @orgId uniqueidentifier = '04CF1D8D-B6E6-4E20-9A74-D6F197A9FE78';
declare @userId uniqueidentifier = '007A3176-98EF-41F6-AEAB-16B34369ECC2'
declare @firstItem uniqueidentifier = '841D92D1-E80C-4512-A8C2-BBDE37FD80E1'

begin transaction;

insert into Administration.Organisation values (@orgId, 'Test org');

insert into Administration.[User] values (@userId);

insert into Administration.UserAtOrganisation values (@userId, @orgId);

insert into [Definition].[Audit] (UserUuid, [DateTime]) values (@userId, getdate());
declare @AuditId int = @@identity;


declare @ItemsToAdd table
(
	ItemUuid uniqueidentifier not null,
	Title varchar(100) not null,
	ModuleId tinyint not null,
	ItemTypeId tinyint not null,
	WithinDependency uniqueidentifier null,
	ChildDependency uniqueidentifier null
);

insert into @ItemsToAdd
values
	('01BE7640-802F-416B-8FD4-CC58A133F40E', 'Clinical', 0, 0, null, null),
	('C69970DA-CF6C-406B-BEEC-31762FC9A0A4', 'My Folder', 0, 0, null, null),
	('D279B1ED-E854-4438-BEF2-C9F70A0B7C7D', 'Child 1', 0, 0, null, 'C69970DA-CF6C-406B-BEEC-31762FC9A0A4'),
	('62B07090-F17A-43C5-BD5A-4B808508A874', 'Child 2', 0, 0, null, 'C69970DA-CF6C-406B-BEEC-31762FC9A0A4'),
	('B6DE05EE-B7ED-4058-B4F6-93154A48028B', 'Sub Child', 0, 0, null, '62B07090-F17A-43C5-BD5A-4B808508A874'),
	('973473AD-9A9C-4E70-ACE6-E3D831BF192A', 'Sub Child 2', 0, 0, null, '973473AD-9A9C-4E70-ACE6-E3D831BF192A'),

	('D7219FF4-339F-4A54-9DDD-818A0E00ACE9', 'Diabetics', 0, 2, '01BE7640-802F-416B-8FD4-CC58A133F40E', null),
	('ECB4497A-16A2-44C3-8B51-15CFC4BEA9F5', 'Asthmatics', 0, 2, '01BE7640-802F-416B-8FD4-CC58A133F40E', null),
	('098BE27B-1DD3-432F-9EDD-1049DAD4F7AC', 'Sub Asthmatics', 0, 2, '01BE7640-802F-416B-8FD4-CC58A133F40E', 'ECB4497A-16A2-44C3-8B51-15CFC4BEA9F5'),

	('5F7665F1-7970-4091-8BFB-6B281489565D', 'Diabetic Indicators', 0, 5, '01BE7640-802F-416B-8FD4-CC58A133F40E', null)
;

insert into [Definition].Items
select a.ItemUuid, @AuditId, null, 0, @orgId, a.ModuleId, a.ItemTypeId, a.Title, null
from @ItemsToAdd as a;

insert into Definition.ActiveItems
select a.ItemUuid, @AuditId, @orgId, a.ModuleId, a.ItemTypeId, a.Title
from @ItemsToAdd as a;

insert into Definition.ActiveItemDependency
select a.ItemUuid, a.ChildDependency, 0
from @ItemsToAdd as a
where a.ChildDependency is not null;


insert into Definition.ActiveItemDependency
select a.ItemUuid, a.WithinDependency, 1
from @ItemsToAdd as a
where a.WithinDependency is not null;


--insert into [Definition].Items (ItemGuid, AuditId, Content, IsDeleted, OwnerOrganisationGuid, ItemTypeId, Title)
--	values (@firstItem, @AuditId, null, 0, @orgId, 1, 'Test report');

--insert into [Definition].ActiveItems (ItemGuid, CurrentAuditId, OwnerOrganisationGuid, ItemTypeId, Title)
--	values (@firstItem, @AuditId, @orgId, 1, 'Test report');

--insert into Execution.Request (ReportItemUuid, [DateTime], UserUuid, [Parameters])
--	values (@firstItem, getdate(), @userId, '');

/*
select newid()
*/



commit transaction;