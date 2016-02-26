use Endeavour_Enterprise

--generate the standard SPs
exec GenerateAllStandardStoredProcedures

--create basic user and org
INSERT INTO Administration.Organisation
VALUES ('E3EB66E3-589A-40CB-9A7E-1426D7B88F98', 'test organisation', '123')

INSERT INTO Administration.EndUser
VALUES ('28381777-B253-4E2A-B9C0-DB800E099618', '', 'Firstname', 'Surname', 'email@email', 1)

INSERT INTO Administration.OrganisationEndUserLink
VALUES ('05C029A0-E12F-4C9E-A21A-FD83839FF0A8', 'E3EB66E3-589A-40CB-9A7E-1426D7B88F98', '28381777-B253-4E2A-B9C0-DB800E099618', 2, '31 Dec 9999')

INSERT INTO Administration.EndUserPwd
VALUES ('545E4710-17B4-4C54-92F2-622070916670', '28381777-B253-4E2A-B9C0-DB800E099618', '1000:1d049ba2ce1cbc28d76fed47e9699b21885252496d58b58b:c4d1df46d3ba5867349bec73fafba63468803ba5fe0d8edd', '31 Dec 9999')

