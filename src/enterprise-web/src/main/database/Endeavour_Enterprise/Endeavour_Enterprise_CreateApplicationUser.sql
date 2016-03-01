use master

-- Note 1 - On Azure you will need to seperate this script into two
--          and execute against each seperate database as Azure
--          doesn't allow the use statement to change database.

-- Note 2 - Ensure you change the password below.
--

create login Endeavour_Enterprise_ApplicationUser
with password = 'TheQuickBrownFox1234%^&*';

go

use Endeavour_Enterprise

create user Endeavour_Enterprise_ApplicationUser for login Endeavour_Enterprise_ApplicationUser
grant connect to Endeavour_Enterprise_ApplicationUser
grant execute to Endeavour_Enterprise_ApplicationUser

--DL 01/03/2016 - user needs to be able to read and write SQL
ALTER ROLE [db_datareader] ADD MEMBER [Endeavour_Enterprise_ApplicationUser]
ALTER ROLE [db_datawriter] ADD MEMBER [Endeavour_Enterprise_ApplicationUser]


go