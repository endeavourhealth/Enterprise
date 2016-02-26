USE [Endeavour_Enterprise]
GO

/****** Object:  Table [Administration].[EndUserPwd]    Script Date: 22/02/2016 11:59:12 ******/
DROP TABLE [Administration].[EndUserPwd]
GO

/****** Object:  Table [Administration].[DbEndUserEmailInvite]    Script Date: 22/02/2016 11:59:12 ******/
DROP TABLE [Administration].[EndUserEmailInvite]
GO

/****** Object:  Table [Administration].[EndUser]    Script Date: 22/02/2016 11:59:12 ******/
DROP TABLE [Administration].[EndUser]
GO

/****** Object:  Table [Administration].[OrganisationEndUserLink]    Script Date: 22/02/2016 11:59:12 ******/
DROP TABLE [Administration].[OrganisationEndUserLink]
GO

/****** Object:  Table [Administration].[Organisation]    Script Date: 22/02/2016 11:59:12 ******/
DROP TABLE [Administration].[Organisation]
GO

/****** Object:  Table [Administration].[FolderItemLink]    Script Date: 22/02/2016 11:59:12 ******/
DROP TABLE [Administration].[FolderItemLink]
GO

/****** Object:  Table [Administration].[Folder]    Script Date: 22/02/2016 11:59:12 ******/
DROP TABLE [Administration].[Folder]
GO

/****** Object:  Table [Administration].[Folder]    Script Date: 22/02/2016 11:59:12 ******/
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
 CONSTRAINT [PK_Folder] PRIMARY KEY CLUSTERED 
(
	[FolderUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/****** Object:  Table [Administration].[FolderItemLink]    Script Date: 22/02/2016 11:59:12 ******/
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

/****** Object:  Table [Administration].[Organisation]    Script Date: 22/02/2016 11:59:12 ******/
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

/****** Object:  Table [Administration].[OrganisationEndUserLink]    Script Date: 22/02/2016 11:59:12 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [Administration].[OrganisationEndUserLink](
	[OrganisationEndUserLinkUuid] [uniqueidentifier] NOT NULL,
	[OrganisationUuid] [uniqueidentifier] NOT NULL,
	[EndUserUuid] [uniqueidentifier] NOT NULL,
	[Persmissions] [int] NOT NULL,
	[DtExpired] [datetime] NOT NULL,
 CONSTRAINT [PK_OrganisationPersonLink] PRIMARY KEY CLUSTERED 
(
	[OrganisationEndUserLinkUuid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

/****** Object:  Table [Administration].[EndUser]    Script Date: 22/02/2016 11:59:12 ******/
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

/****** Object:  Table [Administration].[DbEndUserEmailInvite]    Script Date: 22/02/2016 11:59:12 ******/
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

/****** Object:  Table [Administration].[EndUserPwd]    Script Date: 22/02/2016 11:59:12 ******/
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

