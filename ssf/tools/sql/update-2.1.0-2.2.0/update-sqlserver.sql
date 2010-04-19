use sitescape;
alter table SS_LdapConnectionConfig add ldapGuidAttribute varchar(255) null;
alter table SS_Principals add ldapGuid varchar(128) null;
create index ldapGuid_principal on SS_Principals (ldapGuid);

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[dbo].[FK_SSQRTZ_JOB_LISTENERS_SSQRTZ_JOB_DETAILS]') AND OBJECTPROPERTY(id, N'ISFOREIGNKEY') = 1)
ALTER TABLE [dbo].[SSQRTZ_JOB_LISTENERS] DROP CONSTRAINT FK_SSQRTZ_JOB_LISTENERS_SSQRTZ_JOB_DETAILS;

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[dbo].[FK_SSQRTZ_TRIGGERS_SSQRTZ_JOB_DETAILS]') AND OBJECTPROPERTY(id, N'ISFOREIGNKEY') = 1)
ALTER TABLE [dbo].[SSQRTZ_TRIGGERS] DROP CONSTRAINT FK_SSQRTZ_TRIGGERS_SSQRTZ_JOB_DETAILS;

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[dbo].[FK_SSQRTZ_CRON_TRIGGERS_SSQRTZ_TRIGGERS]') AND OBJECTPROPERTY(id, N'ISFOREIGNKEY') = 1)
ALTER TABLE [dbo].[SSQRTZ_CRON_TRIGGERS] DROP CONSTRAINT FK_SSQRTZ_CRON_TRIGGERS_SSQRTZ_TRIGGERS;

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[dbo].[FK_SSQRTZ_SIMPLE_TRIGGERS_SSQRTZ_TRIGGERS]') AND OBJECTPROPERTY(id, N'ISFOREIGNKEY') = 1)
ALTER TABLE [dbo].[SSQRTZ_SIMPLE_TRIGGERS] DROP CONSTRAINT FK_SSQRTZ_SIMPLE_TRIGGERS_SSQRTZ_TRIGGERS;

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[dbo].[FK_SSQRTZ_TRIGGER_LISTENERS_SSQRTZ_TRIGGERS]') AND OBJECTPROPERTY(id, N'ISFOREIGNKEY') = 1)
ALTER TABLE [dbo].[SSQRTZ_TRIGGER_LISTENERS] DROP CONSTRAINT FK_SSQRTZ_TRIGGER_LISTENERS_SSQRTZ_TRIGGERS;

IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[dbo].[FK_SSQRTZ_BLOB_TRIGGERS_SSQRTZ_TRIGGERS]') AND OBJECTPROPERTY(id, N'ISFOREIGNKEY') = 1)
ALTER TABLE [dbo].[SSQRTZ_BLOB_TRIGGERS] DROP CONSTRAINT FK_SSQRTZ_BLOB_TRIGGERS_SSQRTZ_TRIGGERS;

DECLARE @pkName Varchar(255)
Set @pkName = (Select 'ALTER TABLE SSQRTZ_CALENDARS DROP CONSTRAINT '+[name] FROM dbo.sysobjects WHERE [xtype] = 'PK' AND [parent_obj] = OBJECT_ID(N'[dbo].[SSQRTZ_CALENDARS]'))
EXECUTE ( @pkName );

ALTER TABLE SSQRTZ_CALENDARS ALTER COLUMN CALENDAR_NAME VARCHAR(200) NOT NULL;

DECLARE @pkName0 Varchar(255)
Set @pkName0 = (Select 'ALTER TABLE SSQRTZ_CRON_TRIGGERS DROP CONSTRAINT '+[name] FROM dbo.sysobjects WHERE [xtype] = 'PK' AND [parent_obj] = OBJECT_ID(N'[dbo].[SSQRTZ_CRON_TRIGGERS]'))
EXECUTE ( @pkName0 );

ALTER TABLE SSQRTZ_CRON_TRIGGERS ALTER COLUMN TRIGGER_NAME VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_CRON_TRIGGERS ALTER COLUMN TRIGGER_GROUP VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_CRON_TRIGGERS ALTER COLUMN CRON_EXPRESSION VARCHAR(120) NOT NULL;

DECLARE @pkName1 Varchar(255)
Set @pkName1 = (Select 'ALTER TABLE SSQRTZ_FIRED_TRIGGERS DROP CONSTRAINT '+[name] FROM dbo.sysobjects WHERE [xtype] = 'PK' AND [parent_obj] = OBJECT_ID(N'[dbo].[SSQRTZ_FIRED_TRIGGERS]'))
EXECUTE ( @pkName1 );

ALTER TABLE SSQRTZ_FIRED_TRIGGERS ALTER COLUMN TRIGGER_NAME VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_FIRED_TRIGGERS ALTER COLUMN TRIGGER_GROUP VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_FIRED_TRIGGERS ALTER COLUMN INSTANCE_NAME VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_FIRED_TRIGGERS ALTER COLUMN JOB_NAME VARCHAR(200) NULL;
ALTER TABLE SSQRTZ_FIRED_TRIGGERS ALTER COLUMN JOB_GROUP VARCHAR(200) NULL;

DECLARE @pkName2 Varchar(255)
Set @pkName2 = (Select 'ALTER TABLE SSQRTZ_PAUSED_TRIGGER_GRPS DROP CONSTRAINT '+[name] FROM dbo.sysobjects WHERE [xtype] = 'PK' AND [parent_obj] = OBJECT_ID(N'[dbo].[SSQRTZ_PAUSED_TRIGGER_GRPS]'))
EXECUTE ( @pkName2 );

ALTER TABLE SSQRTZ_PAUSED_TRIGGER_GRPS ALTER COLUMN TRIGGER_GROUP VARCHAR(200) NOT NULL;

DECLARE @pkName3 Varchar(255)
Set @pkName3 = (Select 'ALTER TABLE SSQRTZ_SCHEDULER_STATE DROP CONSTRAINT '+[name] FROM dbo.sysobjects WHERE [xtype] = 'PK' AND [parent_obj] = OBJECT_ID(N'[dbo].[SSQRTZ_SCHEDULER_STATE]'))
EXECUTE ( @pkName3 );

ALTER TABLE SSQRTZ_SCHEDULER_STATE ALTER COLUMN INSTANCE_NAME VARCHAR(200) NOT NULL;

DECLARE @pkName4 Varchar(255)
Set @pkName4 = (Select 'ALTER TABLE SSQRTZ_JOB_DETAILS DROP CONSTRAINT '+[name] FROM dbo.sysobjects WHERE [xtype] = 'PK' AND [parent_obj] = OBJECT_ID(N'[dbo].[SSQRTZ_JOB_DETAILS]'))
EXECUTE ( @pkName4 );

ALTER TABLE SSQRTZ_JOB_DETAILS ALTER COLUMN JOB_NAME VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_JOB_DETAILS ALTER COLUMN JOB_GROUP VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_JOB_DETAILS ALTER COLUMN DESCRIPTION VARCHAR(250) NOT NULL;
ALTER TABLE SSQRTZ_JOB_DETAILS ALTER COLUMN JOB_CLASS_NAME VARCHAR(250) NOT NULL;

DECLARE @pkName5 Varchar(255)
Set @pkName5 = (Select 'ALTER TABLE SSQRTZ_JOB_LISTENERS DROP CONSTRAINT '+[name] FROM dbo.sysobjects WHERE [xtype] = 'PK' AND [parent_obj] = OBJECT_ID(N'[dbo].[SSQRTZ_JOB_LISTENERS]'))
EXECUTE ( @pkName5 );

ALTER TABLE SSQRTZ_JOB_LISTENERS ALTER COLUMN JOB_NAME VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_JOB_LISTENERS ALTER COLUMN JOB_GROUP VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_JOB_LISTENERS ALTER COLUMN JOB_LISTENER VARCHAR(200) NOT NULL;

DECLARE @pkName6 Varchar(255)
Set @pkName6 = (Select 'ALTER TABLE SSQRTZ_SIMPLE_TRIGGERS DROP CONSTRAINT '+[name] FROM dbo.sysobjects WHERE [xtype] = 'PK' AND [parent_obj] = OBJECT_ID(N'[dbo].[SSQRTZ_SIMPLE_TRIGGERS]'))
EXECUTE ( @pkName6 );

ALTER TABLE SSQRTZ_SIMPLE_TRIGGERS ALTER COLUMN TRIGGER_NAME VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_SIMPLE_TRIGGERS ALTER COLUMN TRIGGER_GROUP VARCHAR(200) NOT NULL;

DECLARE @pkName7 Varchar(255)
Set @pkName7 = (Select 'ALTER TABLE SSQRTZ_BLOB_TRIGGERS DROP CONSTRAINT '+[name] FROM dbo.sysobjects WHERE [xtype] = 'PK' AND [parent_obj] = OBJECT_ID(N'[dbo].[SSQRTZ_BLOB_TRIGGERS]'))
EXECUTE ( @pkName7 );

ALTER TABLE SSQRTZ_BLOB_TRIGGERS ALTER COLUMN TRIGGER_NAME VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_BLOB_TRIGGERS ALTER COLUMN TRIGGER_GROUP VARCHAR(200) NOT NULL;

DECLARE @pkName8 Varchar(255)
Set @pkName8 = (Select 'ALTER TABLE SSQRTZ_TRIGGER_LISTENERS DROP CONSTRAINT '+[name] FROM dbo.sysobjects WHERE [xtype] = 'PK' AND [parent_obj] = OBJECT_ID(N'[dbo].[SSQRTZ_TRIGGER_LISTENERS]'))
EXECUTE ( @pkName8 );

ALTER TABLE SSQRTZ_TRIGGER_LISTENERS ALTER COLUMN TRIGGER_NAME VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_TRIGGER_LISTENERS ALTER COLUMN TRIGGER_GROUP VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_TRIGGER_LISTENERS ALTER COLUMN TRIGGER_LISTENER VARCHAR(200) NOT NULL;

DECLARE @pkName9 Varchar(255)
Set @pkName9 = (Select 'ALTER TABLE SSQRTZ_TRIGGERS DROP CONSTRAINT '+[name] FROM dbo.sysobjects WHERE [xtype] = 'PK' AND [parent_obj] = OBJECT_ID(N'[dbo].[SSQRTZ_TRIGGERS]'))
EXECUTE ( @pkName9 );

ALTER TABLE SSQRTZ_TRIGGERS ALTER COLUMN TRIGGER_NAME VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_TRIGGERS ALTER COLUMN TRIGGER_GROUP VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_TRIGGERS ALTER COLUMN JOB_NAME VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_TRIGGERS ALTER COLUMN JOB_GROUP VARCHAR(200) NOT NULL;
ALTER TABLE SSQRTZ_TRIGGERS ALTER COLUMN DESCRIPTION VARCHAR(250) NULL;
ALTER TABLE SSQRTZ_TRIGGERS ALTER COLUMN CALENDAR_NAME VARCHAR(200) NULL;

waitfor delay '0:0:2';

ALTER TABLE SSQRTZ_CALENDARS ADD CONSTRAINT PK__SSQRTZ_CALENDARS PRIMARY KEY (CALENDAR_NAME);
ALTER TABLE SSQRTZ_CRON_TRIGGERS ADD CONSTRAINT PK__SSQRTZ_CRON_TRIGGERS PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP);
ALTER TABLE SSQRTZ_FIRED_TRIGGERS ADD CONSTRAINT PK__SSQRTZ_FIRED_TRIGGERS PRIMARY KEY (ENTRY_ID);
ALTER TABLE SSQRTZ_PAUSED_TRIGGER_GRPS ADD CONSTRAINT PK__SSQRTZ_PAUSED_TRIGGER_GRPS PRIMARY KEY (TRIGGER_GROUP);
ALTER TABLE SSQRTZ_SCHEDULER_STATE ADD CONSTRAINT PK__SSQRTZ_SCHEDULER_STATE PRIMARY KEY (INSTANCE_NAME);
ALTER TABLE SSQRTZ_JOB_DETAILS ADD CONSTRAINT PK__SSQRTZ_JOB_DETAILS PRIMARY KEY (JOB_NAME,JOB_GROUP);
ALTER TABLE SSQRTZ_JOB_LISTENERS ADD CONSTRAINT PK__SSQRTZ_JOB_LISTENERS PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER);
ALTER TABLE SSQRTZ_SIMPLE_TRIGGERS ADD CONSTRAINT PK__SSQRTZ_SIMPLE_TRIGGERS PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP);
ALTER TABLE SSQRTZ_BLOB_TRIGGERS ADD CONSTRAINT PK__SSQRTZ_BLOB_TRIGGERS PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP);
ALTER TABLE SSQRTZ_TRIGGER_LISTENERS ADD CONSTRAINT PK__SSQRTZ_TRIGGER_LISTENERS PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER);
ALTER TABLE SSQRTZ_TRIGGERS ADD CONSTRAINT PK__SSQRTZ_TRIGGERS PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP);

ALTER TABLE SSQRTZ_CRON_TRIGGERS ADD
  CONSTRAINT FK_SSQRTZ_CRON_TRIGGERS_SSQRTZ_TRIGGERS FOREIGN KEY
  (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) REFERENCES SSQRTZ_TRIGGERS (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) ON DELETE CASCADE;

ALTER TABLE SSQRTZ_JOB_LISTENERS ADD
  CONSTRAINT FK_SSQRTZ_JOB_LISTENERS_SSQRTZ_JOB_DETAILS FOREIGN KEY
  (
    JOB_NAME,
    JOB_GROUP
  ) REFERENCES SSQRTZ_JOB_DETAILS (
    JOB_NAME,
    JOB_GROUP
  ) ON DELETE CASCADE;
ALTER TABLE SSQRTZ_SIMPLE_TRIGGERS ADD
  CONSTRAINT FK_SSQRTZ_SIMPLE_TRIGGERS_SSQRTZ_TRIGGERS FOREIGN KEY
  (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) REFERENCES SSQRTZ_TRIGGERS (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) ON DELETE CASCADE;
ALTER TABLE SSQRTZ_TRIGGER_LISTENERS ADD
  CONSTRAINT FK_SSQRTZ_TRIGGER_LISTENERS_SSQRTZ_TRIGGERS FOREIGN KEY
  (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) REFERENCES SSQRTZ_TRIGGERS (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) ON DELETE CASCADE;
ALTER TABLE SSQRTZ_TRIGGERS ADD
  CONSTRAINT FK_SSQRTZ_TRIGGERS_SSQRTZ_JOB_DETAILS FOREIGN KEY
  (
    JOB_NAME,
    JOB_GROUP
  ) REFERENCES SSQRTZ_JOB_DETAILS (
    JOB_NAME,
    JOB_GROUP
  );
 ALTER TABLE SSQRTZ_BLOB_TRIGGERS ADD CONSTRAINT FK_SSQRTZ_BLOB_TRIGGERS_SSQRTZ_TRIGGERS FOREIGN KEY
  (TRIGGER_NAME,TRIGGER_GROUP) REFERENCES SSQRTZ_TRIGGERS (TRIGGER_NAME,TRIGGER_GROUP);

alter table SS_Forums add brandingExt ntext null;
alter table SS_Functions add scope varchar(16);
alter table SS_FolderEntries add hasEntryAcl tinyint null;
alter table SS_FolderEntries add checkFolderAcl tinyint null;
alter table SS_Attachments add description_text ntext null;
alter table SS_Attachments add description_format int null;
alter table SS_Attachments add majorVersion int null;
alter table SS_Attachments add minorVersion int null;
alter table SS_Attachments add fileStatus int null;


INSERT INTO SS_SchemaInfo values (8);
