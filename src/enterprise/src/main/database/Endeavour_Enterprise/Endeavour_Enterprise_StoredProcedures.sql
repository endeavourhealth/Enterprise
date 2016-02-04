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