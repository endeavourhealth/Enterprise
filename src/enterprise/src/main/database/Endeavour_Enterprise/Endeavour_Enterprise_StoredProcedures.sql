use Endeavour_Enterprise

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

create procedure Definition.GetRootFolders
  @OrganisationUuid uniqueidentifier,
  @ModuleId integer
as
begin
  SELECT
    ta.ItemUuid,
    ta.Title,
    CASE WHEN exists (SELECT NULL FROM [Definition].ActiveItemDependency AS dc WHERE dc.DependsOnItemUuid = a.ItemUuid AND dc.DependencyTypeId = 0)
          THEN 1
          ELSE 0
    END AS HasChildren
  FROM Definition.ActiveItems AS a
  LEFT JOIN Definition.ActiveItemDependency AS d
    ON d.ItemUuid = a.ItemUuid
    AND d.DependencyTypeId = 0
  WHERE a.OwnerOrganisationUuid = @OrganisationUuid
  AND a.ModuleId = @ModuleId
  AND a.ItemTypeId = 0
  AND d.ItemUuid IS NULL
  ORDER BY a.Title;
end

go

create procedure Definition.GetChildFolders
  @ItemUuid uniqueidentifier
as
begin
  SELECT
    a.ItemUuid,
    a.Title,
    CASE WHEN exists (SELECT NULL FROM [Definition].ActiveItemDependency AS dc WHERE dc.DependsOnItemUuid = a.ItemUuid AND dc.DependencyTypeId = 0)
      THEN 1
      ELSE 0
    END AS HasChildren
  FROM Definition.ActiveItems AS a
  INNER JOIN Definition.ActiveItemDependency AS d
    ON d.ItemUuid = a.ItemUuid
    AND d.DependsOnItemUuid = @ItemUuid
    AND d.DependencyTypeId = 0
  WHERE a.ItemTypeId = 0
--  AND a.ModuleId = 0\n" +
--  AND a.OwnerOrganisationUuid = ?\n" +
  ORDER BY a.Title;
end

go

create procedure Definition.GetFolderContents
  @FolderId uniqueidentifier
as
begin
	select
		d.ItemUuid,
		i.Title,
		i.ItemTypeId
	from Definition.ActiveItemDependency d
	join Definition.ActiveItems i on i.ItemUuid = d.ItemUuid
	where d.DependsOnItemUuid = @FolderId
	and d.DependencyTypeId != 0
end

go
