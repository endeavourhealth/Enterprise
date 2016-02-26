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

go