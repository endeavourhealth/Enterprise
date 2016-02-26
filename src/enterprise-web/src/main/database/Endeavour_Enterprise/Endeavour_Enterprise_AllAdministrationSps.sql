USE [Endeavour_Enterprise]
GO

/****** Object:  StoredProcedure [dbo].[GenerateStandardStoredProcedures]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [dbo].[GenerateStandardStoredProcedures]
GO

/****** Object:  StoredProcedure [dbo].[GenerateAllStandardStoredProcedures]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [dbo].[GenerateAllStandardStoredProcedures]
GO

/****** Object:  StoredProcedure [Administration].[OrganisationEndUserLink_SelectForOrganisationNotExpired]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[OrganisationEndUserLink_SelectForOrganisationNotExpired]
GO

/****** Object:  StoredProcedure [Administration].[OrganisationEndUserLink_SelectForEndUserNotExpired]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[OrganisationEndUserLink_SelectForEndUserNotExpired]
GO

/****** Object:  StoredProcedure [Administration].[Organisation_SelectForAll]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[Organisation_SelectForAll]
GO

/****** Object:  StoredProcedure [Administration].[FolderItemLink_SelectForFolder]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[FolderItemLink_SelectForFolder]
GO

/****** Object:  StoredProcedure [Administration].[EndUserPwd_SelectForEndUserNotExpired]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[EndUserPwd_SelectForEndUserNotExpired]
GO

/****** Object:  StoredProcedure [Administration].[EndUserEmailInvite_SelectForTokenNotCompleted]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[EndUserEmailInvite_SelectForTokenNotCompleted]
GO

/****** Object:  StoredProcedure [Administration].[EndUserEmailInvite_SelectForEndUserNotCompleted]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[EndUserEmailInvite_SelectForEndUserNotCompleted]
GO

/****** Object:  StoredProcedure [Administration].[EndUser_SelectForEmail]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[EndUser_SelectForEmail]
GO

/****** Object:  StoredProcedure [Administration].[_OrganisationEndUserLink_Update]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_OrganisationEndUserLink_Update]
GO

/****** Object:  StoredProcedure [Administration].[_OrganisationEndUserLink_SelectForUuid]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_OrganisationEndUserLink_SelectForUuid]
GO

/****** Object:  StoredProcedure [Administration].[_OrganisationEndUserLink_Insert]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_OrganisationEndUserLink_Insert]
GO

/****** Object:  StoredProcedure [Administration].[_OrganisationEndUserLink_Delete]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_OrganisationEndUserLink_Delete]
GO

/****** Object:  StoredProcedure [Administration].[_Organisation_Update]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_Organisation_Update]
GO

/****** Object:  StoredProcedure [Administration].[_Organisation_SelectForUuid]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_Organisation_SelectForUuid]
GO

/****** Object:  StoredProcedure [Administration].[_Organisation_Insert]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_Organisation_Insert]
GO

/****** Object:  StoredProcedure [Administration].[_Organisation_Delete]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_Organisation_Delete]
GO

/****** Object:  StoredProcedure [Administration].[_FolderItemLink_Update]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_FolderItemLink_Update]
GO

/****** Object:  StoredProcedure [Administration].[_FolderItemLink_SelectForUuid]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_FolderItemLink_SelectForUuid]
GO

/****** Object:  StoredProcedure [Administration].[_FolderItemLink_Insert]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_FolderItemLink_Insert]
GO

/****** Object:  StoredProcedure [Administration].[_FolderItemLink_Delete]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_FolderItemLink_Delete]
GO

/****** Object:  StoredProcedure [Administration].[_Folder_Update]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_Folder_Update]
GO

/****** Object:  StoredProcedure [Administration].[_Folder_SelectForUuid]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_Folder_SelectForUuid]
GO

/****** Object:  StoredProcedure [Administration].[_Folder_Insert]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_Folder_Insert]
GO

/****** Object:  StoredProcedure [Administration].[_Folder_Delete]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_Folder_Delete]
GO

/****** Object:  StoredProcedure [Administration].[_EndUserPwd_Update]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_EndUserPwd_Update]
GO

/****** Object:  StoredProcedure [Administration].[_EndUserPwd_SelectForUuid]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_EndUserPwd_SelectForUuid]
GO

/****** Object:  StoredProcedure [Administration].[_EndUserPwd_Insert]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_EndUserPwd_Insert]
GO

/****** Object:  StoredProcedure [Administration].[_EndUserPwd_Delete]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_EndUserPwd_Delete]
GO

/****** Object:  StoredProcedure [Administration].[_EndUserEmailInvite_Update]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_EndUserEmailInvite_Update]
GO

/****** Object:  StoredProcedure [Administration].[_EndUserEmailInvite_SelectForUuid]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_EndUserEmailInvite_SelectForUuid]
GO

/****** Object:  StoredProcedure [Administration].[_EndUserEmailInvite_Insert]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_EndUserEmailInvite_Insert]
GO

/****** Object:  StoredProcedure [Administration].[_EndUserEmailInvite_Delete]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_EndUserEmailInvite_Delete]
GO

/****** Object:  StoredProcedure [Administration].[_EndUser_Update]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_EndUser_Update]
GO

/****** Object:  StoredProcedure [Administration].[_EndUser_SelectForUuid]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_EndUser_SelectForUuid]
GO

/****** Object:  StoredProcedure [Administration].[_EndUser_Insert]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_EndUser_Insert]
GO

/****** Object:  StoredProcedure [Administration].[_EndUser_Delete]    Script Date: 23/02/2016 14:35:40 ******/
DROP PROCEDURE [Administration].[_EndUser_Delete]
GO




USE [Endeavour_Enterprise]
GO

/****** Object:  StoredProcedure [Administration].[_EndUser_Delete]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_EndUser_Delete]
@EndUserUuid uniqueidentifier
 AS BEGIN
DELETE FROM [Administration].[EndUser] WHERE EndUserUuid = @EndUserUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_EndUser_Insert]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_EndUser_Insert]
@EndUserUuid uniqueidentifier, @Title varchar(256), @Forename varchar(256), @Surname varchar(256), @Email varchar(256), @IsSuperUser bit
 AS BEGIN
INSERT INTO [Administration].[EndUser] VALUES (@EndUserUuid, @Title, @Forename, @Surname, @Email, @IsSuperUser)
END
GO

/****** Object:  StoredProcedure [Administration].[_EndUser_SelectForUuid]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_EndUser_SelectForUuid]
@EndUserUuid uniqueidentifier
 AS BEGIN
SELECT EndUserUuid, Title, Forename, Surname, Email, IsSuperUser FROM [Administration].[EndUser] WHERE EndUserUuid = @EndUserUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_EndUser_Update]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_EndUser_Update]
@EndUserUuid uniqueidentifier, @Title varchar(256), @Forename varchar(256), @Surname varchar(256), @Email varchar(256), @IsSuperUser bit
 AS BEGIN
UPDATE [Administration].[EndUser] SET EndUserUuid = @EndUserUuid, Title = @Title, Forename = @Forename, Surname = @Surname, Email = @Email, IsSuperUser = @IsSuperUser WHERE EndUserUuid = @EndUserUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_EndUserEmailInvite_Delete]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_EndUserEmailInvite_Delete]
@EndUserEmailInviteUuid uniqueidentifier
 AS BEGIN
DELETE FROM [Administration].[EndUserEmailInvite] WHERE EndUserEmailInviteUuid = @EndUserEmailInviteUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_EndUserEmailInvite_Insert]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_EndUserEmailInvite_Insert]
@EndUserEmailInviteUuid uniqueidentifier, @EndUserUuid uniqueidentifier, @UniqueToken varchar(256), @DtCompleted datetime
 AS BEGIN
INSERT INTO [Administration].[EndUserEmailInvite] VALUES (@EndUserEmailInviteUuid, @EndUserUuid, @UniqueToken, @DtCompleted)
END
GO

/****** Object:  StoredProcedure [Administration].[_EndUserEmailInvite_SelectForUuid]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_EndUserEmailInvite_SelectForUuid]
@EndUserEmailInviteUuid uniqueidentifier
 AS BEGIN
SELECT EndUserEmailInviteUuid, EndUserUuid, UniqueToken, DtCompleted FROM [Administration].[EndUserEmailInvite] WHERE EndUserEmailInviteUuid = @EndUserEmailInviteUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_EndUserEmailInvite_Update]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_EndUserEmailInvite_Update]
@EndUserEmailInviteUuid uniqueidentifier, @EndUserUuid uniqueidentifier, @UniqueToken varchar(256), @DtCompleted datetime
 AS BEGIN
UPDATE [Administration].[EndUserEmailInvite] SET EndUserEmailInviteUuid = @EndUserEmailInviteUuid, EndUserUuid = @EndUserUuid, UniqueToken = @UniqueToken, DtCompleted = @DtCompleted WHERE EndUserEmailInviteUuid = @EndUserEmailInviteUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_EndUserPwd_Delete]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_EndUserPwd_Delete]
@EndUserPwdUuid uniqueidentifier
 AS BEGIN
DELETE FROM [Administration].[EndUserPwd] WHERE EndUserPwdUuid = @EndUserPwdUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_EndUserPwd_Insert]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_EndUserPwd_Insert]
@EndUserPwdUuid uniqueidentifier, @EndUserUuid uniqueidentifier, @PwdHash varchar(501), @DtExpired datetime
 AS BEGIN
INSERT INTO [Administration].[EndUserPwd] VALUES (@EndUserPwdUuid, @EndUserUuid, @PwdHash, @DtExpired)
END
GO

/****** Object:  StoredProcedure [Administration].[_EndUserPwd_SelectForUuid]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_EndUserPwd_SelectForUuid]
@EndUserPwdUuid uniqueidentifier
 AS BEGIN
SELECT EndUserPwdUuid, EndUserUuid, PwdHash, DtExpired FROM [Administration].[EndUserPwd] WHERE EndUserPwdUuid = @EndUserPwdUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_EndUserPwd_Update]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_EndUserPwd_Update]
@EndUserPwdUuid uniqueidentifier, @EndUserUuid uniqueidentifier, @PwdHash varchar(501), @DtExpired datetime
 AS BEGIN
UPDATE [Administration].[EndUserPwd] SET EndUserPwdUuid = @EndUserPwdUuid, EndUserUuid = @EndUserUuid, PwdHash = @PwdHash, DtExpired = @DtExpired WHERE EndUserPwdUuid = @EndUserPwdUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_Folder_Delete]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_Folder_Delete]
@FolderUuid uniqueidentifier
 AS BEGIN
DELETE FROM [Administration].[Folder] WHERE FolderUuid = @FolderUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_Folder_Insert]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_Folder_Insert]
@FolderUuid uniqueidentifier, @OrganisationUuid uniqueidentifier, @ParentFolderUuid uniqueidentifier, @Title varchar(256), @FolderType int
 AS BEGIN
INSERT INTO [Administration].[Folder] VALUES (@FolderUuid, @OrganisationUuid, @ParentFolderUuid, @Title, @FolderType)
END
GO

/****** Object:  StoredProcedure [Administration].[_Folder_SelectForUuid]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_Folder_SelectForUuid]
@FolderUuid uniqueidentifier
 AS BEGIN
SELECT f.FolderUuid, f.OrganisationUuid, f.ParentFolderUuid, f.Title, f.FolderType,
CASE WHEN exists (SELECT NULL FROM [Administration].[Folder] AS cf WHERE cf.ParentFolderUuid = f.FolderUuid)
      THEN 1
      ELSE 0
    END AS HasChildren
FROM [Administration].[Folder] f WHERE f.FolderUuid = @FolderUuid
END

/****** Object:  StoredProcedure [Administration].[_Folder_Update]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_Folder_Update]
@FolderUuid uniqueidentifier, @OrganisationUuid uniqueidentifier, @ParentFolderUuid uniqueidentifier, @Title varchar(256), @FolderType int
 AS BEGIN
UPDATE [Administration].[Folder] SET FolderUuid = @FolderUuid, OrganisationUuid = @OrganisationUuid, ParentFolderUuid = @ParentFolderUuid, Title = @Title, FolderType = @FolderType WHERE FolderUuid = @FolderUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_FolderItemLink_Delete]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_FolderItemLink_Delete]
@FolderItemLinkUuid uniqueidentifier
 AS BEGIN
DELETE FROM [Administration].[FolderItemLink] WHERE FolderItemLinkUuid = @FolderItemLinkUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_FolderItemLink_Insert]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_FolderItemLink_Insert]
@FolderItemLinkUuid uniqueidentifier, @FolderUuid uniqueidentifier, @ItemUuid uniqueidentifier
 AS BEGIN
INSERT INTO [Administration].[FolderItemLink] VALUES (@FolderItemLinkUuid, @FolderUuid, @ItemUuid)
END
GO

/****** Object:  StoredProcedure [Administration].[_FolderItemLink_SelectForUuid]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_FolderItemLink_SelectForUuid]
@FolderItemLinkUuid uniqueidentifier
 AS BEGIN
SELECT FolderItemLinkUuid, FolderUuid, ItemUuid FROM [Administration].[FolderItemLink] WHERE FolderItemLinkUuid = @FolderItemLinkUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_FolderItemLink_Update]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_FolderItemLink_Update]
@FolderItemLinkUuid uniqueidentifier, @FolderUuid uniqueidentifier, @ItemUuid uniqueidentifier
 AS BEGIN
UPDATE [Administration].[FolderItemLink] SET FolderItemLinkUuid = @FolderItemLinkUuid, FolderUuid = @FolderUuid, ItemUuid = @ItemUuid WHERE FolderItemLinkUuid = @FolderItemLinkUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_Organisation_Delete]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_Organisation_Delete]
@OrganisationUuid uniqueidentifier
 AS BEGIN
DELETE FROM [Administration].[Organisation] WHERE OrganisationUuid = @OrganisationUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_Organisation_Insert]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_Organisation_Insert]
@OrganisationUuid uniqueidentifier, @Name varchar(256), @NationalId varchar(256)
 AS BEGIN
INSERT INTO [Administration].[Organisation] VALUES (@OrganisationUuid, @Name, @NationalId)
END
GO

/****** Object:  StoredProcedure [Administration].[_Organisation_SelectForUuid]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_Organisation_SelectForUuid]
@OrganisationUuid uniqueidentifier
 AS BEGIN
SELECT OrganisationUuid, Name, NationalId FROM [Administration].[Organisation] WHERE OrganisationUuid = @OrganisationUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_Organisation_Update]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_Organisation_Update]
@OrganisationUuid uniqueidentifier, @Name varchar(256), @NationalId varchar(256)
 AS BEGIN
UPDATE [Administration].[Organisation] SET OrganisationUuid = @OrganisationUuid, Name = @Name, NationalId = @NationalId WHERE OrganisationUuid = @OrganisationUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_OrganisationEndUserLink_Delete]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_OrganisationEndUserLink_Delete]
@OrganisationEndUserLinkUuid uniqueidentifier
 AS BEGIN
DELETE FROM [Administration].[OrganisationEndUserLink] WHERE OrganisationEndUserLinkUuid = @OrganisationEndUserLinkUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_OrganisationEndUserLink_Insert]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_OrganisationEndUserLink_Insert]
@OrganisationEndUserLinkUuid uniqueidentifier, @OrganisationUuid uniqueidentifier, @EndUserUuid uniqueidentifier, @Persmissions int, @DtExpired datetime
 AS BEGIN
INSERT INTO [Administration].[OrganisationEndUserLink] VALUES (@OrganisationEndUserLinkUuid, @OrganisationUuid, @EndUserUuid, @Persmissions, @DtExpired)
END
GO

/****** Object:  StoredProcedure [Administration].[_OrganisationEndUserLink_SelectForUuid]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_OrganisationEndUserLink_SelectForUuid]
@OrganisationEndUserLinkUuid uniqueidentifier
 AS BEGIN
SELECT OrganisationEndUserLinkUuid, OrganisationUuid, EndUserUuid, Persmissions, DtExpired FROM [Administration].[OrganisationEndUserLink] WHERE OrganisationEndUserLinkUuid = @OrganisationEndUserLinkUuid
END
GO

/****** Object:  StoredProcedure [Administration].[_OrganisationEndUserLink_Update]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [Administration].[_OrganisationEndUserLink_Update]
@OrganisationEndUserLinkUuid uniqueidentifier, @OrganisationUuid uniqueidentifier, @EndUserUuid uniqueidentifier, @Persmissions int, @DtExpired datetime
 AS BEGIN
UPDATE [Administration].[OrganisationEndUserLink] SET OrganisationEndUserLinkUuid = @OrganisationEndUserLinkUuid, OrganisationUuid = @OrganisationUuid, EndUserUuid = @EndUserUuid, Persmissions = @Persmissions, DtExpired = @DtExpired WHERE OrganisationEndUserLinkUuid = @OrganisationEndUserLinkUuid
END
GO

/****** Object:  StoredProcedure [Administration].[EndUser_SelectForEmail]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE PROCEDURE [Administration].[EndUser_SelectForEmail]
@Email varchar(256)
 AS BEGIN
SELECT EndUserUuid, Title, Forename, Surname, Email, IsSuperUser FROM [Administration].[EndUser] WHERE Email = @Email
END


GO

/****** Object:  StoredProcedure [Administration].[EndUserEmailInvite_SelectForEndUserNotCompleted]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE PROCEDURE [Administration].[EndUserEmailInvite_SelectForEndUserNotCompleted]
@EndUserUuid uniqueIdentifier
 AS BEGIN
	SELECT EndUserEmailInviteUuid, EndUserUuid, UniqueToken, DtCompleted
	FROM [Administration].[EndUserEmailInvite]
	WHERE
		EndUserUuid = @EndUserUuid
END


GO

/****** Object:  StoredProcedure [Administration].[EndUserEmailInvite_SelectForTokenNotCompleted]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [Administration].[EndUserEmailInvite_SelectForTokenNotCompleted]
@UniqueToken varchar(256)
 AS BEGIN
	SELECT EndUserEmailInviteUuid, EndUserUuid, UniqueToken, DtCompleted
	FROM [Administration].[EndUserEmailInvite]
	WHERE
		UniqueToken = @UniqueToken
END

GO

/****** Object:  StoredProcedure [Administration].[EndUserPwd_SelectForEndUserNotExpired]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE PROCEDURE [Administration].[EndUserPwd_SelectForEndUserNotExpired]
@EndUserUuid uniqueidentifier
 AS BEGIN
	SELECT EndUserPwdUuid, EndUserUuid, PwdHash, DtExpired
	FROM [Administration].[EndUserPwd]
	WHERE
		EndUserUuid = @EndUserUuid
		AND DtExpired > GETDATE()
END


GO

/****** Object:  StoredProcedure [Administration].[FolderItemLink_SelectForFolder]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [Administration].[FolderItemLink_SelectForFolder]
@FolderUuid uniqueidentifier
 AS BEGIN
	SELECT FolderItemLinkUuid, FolderUuid, ItemUuid
	FROM [Administration].[FolderItemLink]
	WHERE
		FolderUuid = @FolderUuid
END

GO

/****** Object:  StoredProcedure [Administration].[Organisation_SelectForAll]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [Administration].[Organisation_SelectForAll]
 AS BEGIN
	SELECT OrganisationUuid, Name, NationalId
	FROM [Administration].[Organisation]
END

GO

/****** Object:  StoredProcedure [Administration].[OrganisationEndUserLink_SelectForEndUserNotExpired]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE PROCEDURE [Administration].[OrganisationEndUserLink_SelectForEndUserNotExpired]
@EndUserUuid uniqueidentifier
 AS BEGIN
	SELECT OrganisationEndUserLinkUuid, OrganisationUuid, EndUserUuid, Persmissions, DtExpired
	FROM [Administration].[OrganisationEndUserLink]
	WHERE
		EndUserUuid = @EndUserUuid
		AND DtExpired > GETDATE()
END


GO

/****** Object:  StoredProcedure [Administration].[OrganisationEndUserLink_SelectForOrganisationNotExpired]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO




CREATE PROCEDURE [Administration].[OrganisationEndUserLink_SelectForOrganisationNotExpired]
@OrganisationUuid uniqueidentifier
 AS BEGIN
	SELECT OrganisationEndUserLinkUuid, OrganisationUuid, EndUserUuid, Persmissions, DtExpired
	FROM [Administration].[OrganisationEndUserLink]
	WHERE
		OrganisationUuid = @OrganisationUuid
		AND DtExpired > GETDATE()
END



GO

/****** Object:  StoredProcedure [dbo].[GenerateAllStandardStoredProcedures]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[GenerateAllStandardStoredProcedures]
AS
BEGIN

	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	EXEC GenerateStandardStoredProcedures 'Administration.Folder'
	EXEC GenerateStandardStoredProcedures 'Administration.FolderItemLink'
	EXEC GenerateStandardStoredProcedures 'Administration.Organisation'
	EXEC GenerateStandardStoredProcedures 'Administration.OrganisationEndUserLink'
	EXEC GenerateStandardStoredProcedures 'Administration.EndUser'
	EXEC GenerateStandardStoredProcedures 'Administration.EndUserPwd'
	EXEC GenerateStandardStoredProcedures 'Administration.EndUserEmailInvite'
END


GO

/****** Object:  StoredProcedure [dbo].[GenerateStandardStoredProcedures]    Script Date: 23/02/2016 14:35:46 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



-- =============================================
-- Author:		DL
-- Create date: 2016-02-17
-- Description:	Stored Procedure to generate the standard insert/update/retrieve/delete SPs for a given table
-- =============================================
CREATE PROCEDURE [dbo].[GenerateStandardStoredProcedures]
	@tableAndSchema varchar(MAX) 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;


	--split the parameter into schema and table name
	DECLARE @schemaName varchar(255)
	DECLARE @tableName varchar(255)

	--see if a schema name was given. If not assume it's dbo
	DECLARE @dotIndex int = CHARINDEX('.', @tableAndSchema)
	IF (@dotIndex = -1)
	BEGIN
		SET @schemaName = 'dbo'
		SET @tableName = @tableAndSchema
	END
	ELSE
	BEGIN
		SET @schemaName = SUBSTRING(@tableAndSchema, 0, @dotIndex)
		SET @tableName = SUBSTRING(@tableAndSchema, @dotIndex+1, LEN(@tableAndSchema)-@dotIndex)
	END

	PRINT 'Schema = ' + @schemaName
	PRINT 'Table = ' + @tableName

	--work out the schema ID so we don't have to keep joining back to sys.schemas
	DECLARE @schemaId int = (SELECT schema_Id FROM Sys.schemas WHERE name = @schemaName)
	If (@schemaId IS NULL)
	BEGIN
		PRINT 'No schema found for name ' + @schemaName
		RETURN
	END

	PRINT 'SchemaId = ' + CONVERT(Varchar, @schemaId)

	--to create the four SPs, we need the column names formatted in four different ways
	DECLARE @spParametersPrimaryKeyOnly nvarchar(Max) = ''
	DECLARE @spParametersAllColumns nvarchar(Max) = ''
	DECLARE @spWherePrimaryKeyMatches nvarchar(Max) = ''
	DECLARE @spSelectAllColumns nvarchar(Max) = ''
	DECLARE @spInsertAllColumns nvarchar(Max) = ''
	DECLARE @spUpdateAllColumns nvarchar(Max) = ''

	--SQL to get all the column names for the table
	DECLARE columnCursor CURSOR
	FOR
		SELECT c.Name, c.column_id, y.name, c.max_length
		FROM
			Sys.Columns c, 
			Sys.Tables t,
			Sys.Types y
		WHERE
			t.Name = @tableName
			AND t.schema_id = @schemaId
			AND c.object_id = t.object_id
			AND c.system_type_id = y.system_type_id
		ORDER BY 
			c.column_id

	DECLARE @ColumnName nvarchar(255)
	DECLARE @ColumnId nvarchar(255)
	DECLARE @ColumnType nvarchar(255)
	DECLARE @ColumnLen nvarchar(255)

	--run the SQL with a cursor, so we can iterate through each row in the result set
	OPEN columnCursor

	FETCH NEXT FROM columnCursor
	INTO @ColumnName, @ColumnId, @ColumnType, @ColumnLen

	WHILE @@FETCH_STATUS = 0
	BEGIN

		print '' + @ColumnName + '  ' + @ColumnId + '  ' + @ColumnType

		IF (CONVERT(int, @ColumnId) = 1)
		BEGIN
			SET @spParametersPrimaryKeyOnly += '@' + @ColumnName + ' ' + @ColumnType
			SET @spParametersAllColumns += '@' + @ColumnName + ' ' + @ColumnType
			SET @spWherePrimaryKeyMatches += @ColumnName + ' = @' + @ColumnName
		
			SET @spSelectAllColumns += @ColumnName
			SET @spInsertAllColumns += '@' + @ColumnName
			SET @spUpdateAllColumns += @ColumnName + ' = @' + @ColumnName
		END
		ELSE
		BEGIN
			--SET @spParametersPrimaryKeyOnly --only uses first column name
			SET @spParametersAllColumns += ', @' + @ColumnName + ' ' + @ColumnType
			--SET @spWherePrimaryKeyMatches 
			SET @spSelectAllColumns += ', ' + @ColumnName
			SET @spInsertAllColumns += ', @' + @ColumnName
			SET @spUpdateAllColumns += ', ' + @ColumnName + ' = @' + @ColumnName
		END

		--if it's a varchar column we need to specify the column length in the SP parameters
		if (@ColumnType = 'varchar' OR @ColumnType = 'nvarchar'
			OR @ColumnType = 'char' OR @ColumnType = 'nchar')
		BEGIN
			--set the parameter length to be ONE MORE than the table actually supports. This
			--means that if we try to insert a String that's too long, it'll raise an error
			--rather than silently truncating it
			DECLARE @ColumnLenPlusOne varchar(255) = (CONVERT(int, @ColumnLen)+1) 
			SET @spParametersAllColumns += '(' + @columnLenPlusOne + ')'
		END

		--select the next row from the result set
		FETCH NEXT FROM columnCursor
		INTO @ColumnName, @ColumnId, @ColumnType, @ColumnLen
	END


	CLOSE columnCursor;
	DEALLOCATE columnCursor;


	--work out the names for the SPs we're going to create
	DECLARE @spInsert nvarchar(255) = '_' + @TableName + '_Insert'
	DECLARE @spUpdate nvarchar(255) = '_' + @TableName + '_Update'
	DECLARE @spDelete nvarchar(255) = '_' + @TableName + '_Delete'
	DECLARE @spSelect nvarchar(255) = '_' + @TableName + '_SelectForUuid'

	DECLARE @NewLine char(2) = CHAR(13)+CHAR(10)

	--start defining the four SPs
	DECLARE @spInsertSql nvarchar(Max) = 'CREATE PROCEDURE [' + @schemaName + '].[' + @spInsert + ']' + @NewLine
									   + @spParametersAllColumns + @NewLine
									   + ' AS BEGIN' + @NewLine
									   + 'INSERT INTO [' + @schemaName + '].[' + @TableName + '] VALUES ('
									   + @spInsertAllColumns
									   + ')' + @NewLine
									   + 'END'

	DECLARE @spUpdateSql nvarchar(Max) = 'CREATE PROCEDURE [' + @schemaName + '].[' + @spUpdate + ']' + @NewLine
									   + @spParametersAllColumns + @NewLine
									   + ' AS BEGIN' + @NewLine
									   + 'UPDATE [' + @schemaName + '].[' + @TableName + '] SET '
									   + @spUpdateAllColumns
									   + ' WHERE '
									   + @spWherePrimaryKeyMatches
									   + @NewLine
									   + 'END'

	DECLARE @spDeleteSql nvarchar(Max) = 'CREATE PROCEDURE [' + @schemaName + '].[' + @spDelete + ']' + @NewLine
									   + @spParametersPrimaryKeyOnly + @NewLine
									   + ' AS BEGIN' + @NewLine
									   + 'DELETE FROM [' + @schemaName + '].[' + @TableName + '] WHERE '
									   + @spWherePrimaryKeyMatches
									   + @NewLine
									   + 'END'

	DECLARE @spSelectSql nvarchar(Max) = 'CREATE PROCEDURE [' + @schemaName + '].[' + @spSelect + ']' + @NewLine
									   + @spParametersPrimaryKeyOnly + @NewLine
									   + ' AS BEGIN' + @NewLine
									   + 'SELECT '
									   + @spSelectAllColumns
									   + ' FROM [' + @schemaName + '].[' + @TableName + '] WHERE '
									   + @spWherePrimaryKeyMatches
									   + @NewLine
									   + 'END'

	--drop all the existing SPs
	DECLARE @sql nvarchar(MAX)

	SET @sql = N'IF EXISTS (SELECT 1 FROM Sys.Procedures '
			 + 'WHERE schema_id = ' + CONVERT(varchar, @schemaId) + ' AND name = ''' + @SpInsert + ''') '
			 + 'DROP PROCEDURE [' + @schemaName + '].[' + @SpInsert + ']'
	PRINT @sql
	EXEC sp_executesql @sql

	SET @sql = N'IF EXISTS (SELECT 1 FROM Sys.Procedures '
			 + 'WHERE schema_id = ' + CONVERT(varchar, @schemaId) + ' AND name = ''' + @SpUpdate + ''') '
			 + 'DROP PROCEDURE [' + @schemaName + '].[' + @SpUpdate + ']'
	PRINT @sql
	EXEC sp_executesql @sql

	SET @sql = N'IF EXISTS (SELECT 1 FROM Sys.Procedures '
			 + 'WHERE schema_id = ' + CONVERT(varchar, @schemaId) + ' AND name = ''' + @SpDelete + ''') '
			 + 'DROP PROCEDURE [' + @schemaName + '].[' + @SpDelete + ']'
	PRINT @sql
	EXEC sp_executesql @sql

	SET @sql = N'IF EXISTS (SELECT 1 FROM Sys.Procedures '
			 + 'WHERE schema_id = ' + CONVERT(varchar, @schemaId) + ' AND name = ''' + @SpSelect + ''') '
			 + 'DROP PROCEDURE [' + @schemaName + '].[' + @SpSelect + ']'
	PRINT @sql
	EXEC sp_executesql @sql

	--now create the new SPs
	PRINT @spInsertSql
	EXEC sp_executesql @spInsertSql

	PRINT @spUpdateSql
	EXEC sp_executesql @spUpdateSql

	PRINT @spDeleteSql
	EXEC sp_executesql @spDeleteSql

	PRINT @spSelectSql
	EXEC sp_executesql @spSelectSql


END



GO

