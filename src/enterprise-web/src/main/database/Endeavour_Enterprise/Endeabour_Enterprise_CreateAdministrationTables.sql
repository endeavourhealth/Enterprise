USE [Endeavour_Enterprise]
GO

/****** Object:  Table [Definition].[ItemType]    Script Date: 01/03/2016 10:28:47 ******/
DROP TABLE [Definition].[ItemType]
GO

/****** Object:  Table [Definition].[Item]    Script Date: 01/03/2016 10:28:47 ******/
DROP TABLE [Definition].[Item]
GO

/****** Object:  Table [Definition].[DependencyType]    Script Date: 01/03/2016 10:28:47 ******/
DROP TABLE [Definition].[DependencyType]
GO

/****** Object:  Table [Definition].[ActiveItemDependency]    Script Date: 01/03/2016 10:28:47 ******/
DROP TABLE [Definition].[ActiveItemDependency]
GO

/****** Object:  Table [Definition].[ActiveItem]    Script Date: 01/03/2016 10:28:47 ******/
DROP TABLE [Definition].[ActiveItem]
GO

/****** Object:  Table [Administration].[OrganisationEndUserLink]    Script Date: 01/03/2016 10:28:47 ******/
DROP TABLE [Administration].[OrganisationEndUserLink]
GO

/****** Object:  Table [Administration].[Organisation]    Script Date: 01/03/2016 10:28:47 ******/
DROP TABLE [Administration].[Organisation]
GO

/****** Object:  Table [Administration].[FolderItemLink]    Script Date: 01/03/2016 10:28:47 ******/
DROP TABLE [Administration].[FolderItemLink]
GO

/****** Object:  Table [Administration].[Folder]    Script Date: 01/03/2016 10:28:47 ******/
DROP TABLE [Administration].[Folder]
GO

/****** Object:  Table [Administration].[EndUserPwd]    Script Date: 01/03/2016 10:28:47 ******/
DROP TABLE [Administration].[EndUserPwd]
GO

/****** Object:  Table [Administration].[EndUserEmailInvite]    Script Date: 01/03/2016 10:28:47 ******/
DROP TABLE [Administration].[EndUserEmailInvite]
GO

/****** Object:  Table [Administration].[EndUser]    Script Date: 01/03/2016 10:28:47 ******/
DROP TABLE [Administration].[EndUser]
GO


USE [Endeavour_Enterprise]
GO

/****** Object:  Table [Administration].[EndUser]    Script Date: 01/03/2016 10:28:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [Administration].[EndUser](
	[EndUserUuid] [uniqueidentifier] NOT NULL,
	[Title] [varchar](255) NOT NULL,
	[Forename] [varchar](255) NOT NULL,
	[Surname] [varchar](255) NOT NULL,
	[Email] [varchar](255) NOT NULL,
	[IsSuperUser] [bit] NOT NULL,
 CONSTRAINT [PK_Person] PRIMARY KEY CLUSTERED
(
	[EndUserUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/****** Object:  Table [Administration].[EndUserEmailInvite]    Script Date: 01/03/2016 10:28:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [Administration].[EndUserEmailInvite](
	[EndUserEmailInviteUuid] [uniqueidentifier] NOT NULL,
	[EndUserUuid] [uniqueidentifier] NOT NULL,
	[UniqueToken] [varchar](255) NOT NULL,
	[DtCompleted] [datetime] NOT NULL,
 CONSTRAINT [PK_PersonEmailInvite] PRIMARY KEY CLUSTERED
(
	[EndUserEmailInviteUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/****** Object:  Table [Administration].[EndUserPwd]    Script Date: 01/03/2016 10:28:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [Administration].[EndUserPwd](
	[EndUserPwdUuid] [uniqueidentifier] NOT NULL,
	[EndUserUuid] [uniqueidentifier] NOT NULL,
	[PwdHash] [varchar](500) NOT NULL,
	[DtExpired] [datetime] NOT NULL,
 CONSTRAINT [PK_PersonPwd] PRIMARY KEY CLUSTERED
(
	[EndUserPwdUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/****** Object:  Table [Administration].[Folder]    Script Date: 01/03/2016 10:28:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [Administration].[Folder](
	[FolderUuid] [uniqueidentifier] NOT NULL,
	[OrganisationUuid] [uniqueidentifier] NOT NULL,
	[ParentFolderUuid] [uniqueidentifier] NULL,
	[Title] [varchar](255) NOT NULL,
	[FolderType] [int] NOT NULL,
 CONSTRAINT [PK_Folder] PRIMARY KEY CLUSTERED
(
	[FolderUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/****** Object:  Table [Administration].[FolderItemLink]    Script Date: 01/03/2016 10:28:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [Administration].[FolderItemLink](
	[FolderItemLinkUuid] [uniqueidentifier] NOT NULL,
	[FolderUuid] [uniqueidentifier] NOT NULL,
	[ItemUuid] [uniqueidentifier] NOT NULL,
 CONSTRAINT [PK_FolderItemLink] PRIMARY KEY CLUSTERED
(
	[FolderItemLinkUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

/****** Object:  Table [Administration].[Organisation]    Script Date: 01/03/2016 10:28:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [Administration].[Organisation](
	[OrganisationUuid] [uniqueidentifier] NOT NULL,
	[Name] [varchar](255) NOT NULL,
	[NationalId] [varchar](255) NOT NULL,
 CONSTRAINT [PK_Organisation] PRIMARY KEY CLUSTERED
(
	[OrganisationUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/****** Object:  Table [Administration].[OrganisationEndUserLink]    Script Date: 01/03/2016 10:28:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [Administration].[OrganisationEndUserLink](
	[OrganisationEndUserLinkUuid] [uniqueidentifier] NOT NULL,
	[OrganisationUuid] [uniqueidentifier] NOT NULL,
	[EndUserUuid] [uniqueidentifier] NOT NULL,
	[Permissions] [int] NOT NULL,
	[DtExpired] [datetime] NOT NULL,
 CONSTRAINT [PK_OrganisationPersonLink] PRIMARY KEY CLUSTERED
(
	[OrganisationEndUserLinkUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

/****** Object:  Table [Definition].[ActiveItem]    Script Date: 01/03/2016 10:28:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [Definition].[ActiveItem](
	[ActiveItemUuid] [uniqueidentifier] NOT NULL,
	[OrganisationUuid] [uniqueidentifier] NOT NULL,
	[ItemUuid] [uniqueidentifier] NOT NULL,
	[Version] [int] NOT NULL,
	[ItemType] [int] NOT NULL,
 CONSTRAINT [PK_ActiveItem] PRIMARY KEY CLUSTERED
(
	[ActiveItemUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

/****** Object:  Table [Definition].[ActiveItemDependency]    Script Date: 01/03/2016 10:28:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [Definition].[ActiveItemDependency](
	[ActiveItemDependencyUuid] [uniqueidentifier] NOT NULL,
	[ItemUuid] [uniqueidentifier] NOT NULL,
	[DependentItemUuid] [uniqueidentifier] NOT NULL,
	[DependencyType] [int] NOT NULL,
 CONSTRAINT [PK_ActiveItemDependency] PRIMARY KEY CLUSTERED
(
	[ActiveItemDependencyUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

/****** Object:  Table [Definition].[DependencyType]    Script Date: 01/03/2016 10:28:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [Definition].[DependencyType](
	[DependencyTypeId] [tinyint] NOT NULL,
	[Description] [varchar](100) NOT NULL,
 CONSTRAINT [PK_Definition_DependencyType] PRIMARY KEY CLUSTERED
(
	[DependencyTypeId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_Definition_DependencyType_Description] UNIQUE NONCLUSTERED
(
	[Description] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/****** Object:  Table [Definition].[Item]    Script Date: 01/03/2016 10:28:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [Definition].[Item](
	[ItemUuid] [uniqueidentifier] NOT NULL,
	[Version] [int] NOT NULL,
	[XmlContent] [varchar](max) NOT NULL,
	[Title] [varchar](255) NOT NULL,
	[Description] [varchar](max) NOT NULL,
	[EndUserUuid] [uniqueidentifier] NOT NULL,
	[TimeStamp] [datetime] NOT NULL,
	[IsDeleted] [bit] NOT NULL,
 CONSTRAINT [PK_Item] PRIMARY KEY CLUSTERED
(
	[ItemUuid] ASC,
	[Version] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/****** Object:  Table [Definition].[ItemType]    Script Date: 01/03/2016 10:28:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [Definition].[ItemType](
	[ItemTypeId] [tinyint] NOT NULL,
	[Description] [varchar](100) NOT NULL,
 CONSTRAINT [PK_Definition_ItemType] PRIMARY KEY CLUSTERED
(
	[ItemTypeId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_Definition_ItemType_Description] UNIQUE NONCLUSTERED
(
	[Description] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO


--populate the item types
insert into [Definition].ItemType
	(ItemTypeId, [Description])
values
	--(0, 'Folder'),
	(0, 'ReportFolder'), --DL 2016/02/29 - folder containing report
	(1, 'Report'),
	(2, 'Query'),
	(3, 'Test'),
	(4, 'Datasource'),
	(5, 'CodeSet'),
	(6, 'ListOutput'),
	(7, 'LibraryFolder') --DL 2016/02/29 - folder containing library items

--populate the dependency types
insert into [Definition].DependencyType
	(DependencyTypeId, [Description])
values
	(0, 'IsChildOf'),
	(1, 'IsContainedWithin'),
	(2, 'Uses');

--create an initial org
DECLARE @orgId UNIQUEIDENTIFIER  = NEWID();
INSERT INTO Administration.Organisation VALUES (@orgId, 'Test Organisation', 123)

--create a super user
DECLARE @userId UNIQUEIDENTIFIER = NEWID();
INSERT INTO Administration.EndUser VALUES (@userId, '', 'Super', 'User', 'super@test.com', 1)
INSERT INTO Administration.EndUserPwd VALUES (NEWID(), @userId, '1000:5f142f710a12f16af5def945dc8250d19d595fcdba46de66:acce1affccca2d1aa2479e455985e625bec02c9fc5525aea', '31 Dec 9999')

--create a regular admin user
SET @userId = NEWID();
INSERT INTO Administration.EndUser VALUES (@userId, '', 'Admin', 'User', 'admin@test.com', 0)
INSERT INTO Administration.EndUserPwd VALUES (NEWID(), @userId, '1000:5f142f710a12f16af5def945dc8250d19d595fcdba46de66:acce1affccca2d1aa2479e455985e625bec02c9fc5525aea', '31 Dec 9999')
INSERT INTO Administration.OrganisationEndUserLink VALUES (NEWID(), @orgId, @userId, 2, '31 Dec 9999')

--create a regular non-admin user
SET @userId = NEWID();
INSERT INTO Administration.EndUser VALUES (@userId, '', 'User', 'User', 'user@test.com', 0)
INSERT INTO Administration.EndUserPwd VALUES (NEWID(), @userId, '1000:5f142f710a12f16af5def945dc8250d19d595fcdba46de66:acce1affccca2d1aa2479e455985e625bec02c9fc5525aea', '31 Dec 9999')
INSERT INTO Administration.OrganisationEndUserLink VALUES (NEWID(), @orgId, @userId, 1, '31 Dec 9999')
