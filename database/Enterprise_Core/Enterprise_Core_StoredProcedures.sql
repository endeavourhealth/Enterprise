use Enterprise_Core;

go

create procedure Administration.GetOrganisation
	@OrganisationId integer
as
begin

	select 
		o.OrganisationUuid,
		o.Name
	from Administration.Organisation o
	
end

go

create procedure Administration.GetPasswordHash
	@EmailAddress varchar(200)
as
begin

	declare @PasswordHash varchar(500)

	select 
		@PasswordHash = up.PasswordHash
	from Administration.[User] u
	inner join Administration.UserPassword up on u.UserUuid = up.UserUuid
	where u.EmailAddress = @EmailAddress

	select
		@PasswordHash as PasswordHash

end

go

--go

--create procedure [Definition].GetRootFolders
--	@OrganisationUuid uniqueidentifier,
--	@ModuleId tinyint
--as
--begin

--	set transaction isolation level read committed;

--	select
--		i.ItemUuid,
--		i.AuditId,
--		i.Title,
--		i.Description,
--		i.Content,
--		case when exists (select null from [Definition].ActiveItemDependency as dc where dc.DependsOnItemUuid = a.ItemUuid and dc.IsChild = 1)
--            then 1
--            else 0
--		end as HasChildren
--	from Definition.ActiveItems as a
--	left join Definition.ActiveItemDependency as d on d.ItemUuid = a.ItemUuid
--		and d.IsChild = 0
--	inner join Definition.Items
--	where a.OwnerOrganisationUuid = @OrganisationUuid
--	and a.ModuleId = @ModuleId
--	and a.ItemTypeId = 0
--	and d.ItemUuid is null
	
--end;
