use master

if exists
(
	select * from master.sys.sql_logins
	where name = 'Enterprise'
)
begin
	drop login Enterprise
end

-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! --
-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! --
-- !!!! ENSURE YOU CHANGE THE PASSWORD !!!! --
-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! --
-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! --

create login [Enterprise] 
with password = 'XXXXXXXX',
default_database = [Enterprise_Core],
default_language = [us_english],
check_expiration = off,
check_policy = off;

go

use Enterprise_Core;

	if exists 
	(
		select * 
		from sys.database_principals 
		where name = N'Enterprise'
	)
	begin
		drop user [Enterprise]
	end

	create user [Enterprise] for login [Enterprise]
	grant connect to [Enterprise]

use Enterprise_Logging;

	if exists 
	(
		select * 
		from sys.database_principals 
		where name = N'Enterprise'
	)
	begin
		drop user [Enterprise]
	end

	create user [Enterprise] for login [Enterprise]
	grant connect to [Enterprise]

use Enterprise_Coding;

	if exists 
	(
		select * 
		from sys.database_principals 
		where name = N'Enterprise'
	)
	begin
		drop user [Enterprise]
	end

	create user [Enterprise] for login [Enterprise]
	grant connect to [Enterprise]

use SampleCareRecord;

	if exists 
	(
		select * 
		from sys.database_principals 
		where name = N'Enterprise'
	)
	begin
		drop user [Enterprise]
	end

	create user [Enterprise] for login [Enterprise]
	grant connect to [Enterprise]



