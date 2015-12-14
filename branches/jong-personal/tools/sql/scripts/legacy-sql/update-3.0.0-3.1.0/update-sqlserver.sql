CREATE PROCEDURE vopCreateIndex
AS
    BEGIN TRY
	    create index ldapGuid_principal on sitescape.dbo.SS_Principals (ldapGuid);
    END TRY
    BEGIN CATCH
    END CATCH;
GO

EXECUTE vopCreateIndex;

DROP PROCEDURE vopCreateIndex;


use sitescape;
drop index entityOwner_clog on SS_ChangeLogs;
drop index entityOwner_audit on SS_AuditTrail;
drop index entityTransaction_audit on SS_AuditTrail;
drop index internalId_Definition on SS_Definitions;
drop index diskQuota_principal on SS_Principals; 
drop index entityTransaction_wfhistory on SS_WorkflowHistory;
drop index access_shared on SS_SharedEntity;
alter table SS_Events ALTER COLUMN dtStart datetime null;
alter table SS_Events add dtCalcStart datetime null;
alter table SS_Events add dtCalcEnd datetime null;
create table SS_EmailLog (id char(32) not null, zoneId numeric(19,0) null, sendDate datetime not null, fromField nvarchar(255) null, subj nvarchar(255) null, comments ntext null, status varchar(16) not null, type varchar(32) not null, toEmailAddresses ntext null, fileAttachments ntext null, primary key (id));
create table SS_FunctionConditionMap (functionId numeric(19,0) not null, meet varchar(16) null, conditionId numeric(19,0) null);
create table SS_FunctionConditions (id numeric(19,0) identity not null, type varchar(32) not null, zoneId numeric(19,0) not null, encodedSpec ntext null, title nvarchar(255) not null, description_text ntext null, description_format int null, primary key (id));
alter table SS_FunctionConditionMap add constraint FK945D2AD8BCA364AE foreign key (functionId) references SS_Functions;
alter table SS_FunctionConditionMap add constraint FK945D2AD868DAC30E foreign key (conditionId) references SS_FunctionConditions;
create table SS_BinderQuota (binderId numeric(19,0) not null, zoneId numeric(19,0) not null, diskQuota numeric(19,0) null, diskSpaceUsed numeric(19,0) null, diskSpaceUsedCumulative numeric(19,0) null, primary key (binderId));
alter table SS_ZoneConfig add binderQuotasInitialized tinyint;
alter table SS_ZoneConfig add binderQuotasEnabled tinyint;
alter table SS_ZoneConfig add binderQuotasAllowOwner tinyint;
alter table SS_ZoneConfig add holidays varchar(4000) null;
alter table SS_ZoneConfig add weekendDays varchar(128) null;
alter table SS_TokenInfo add requesterId numeric(19,0) null;
create index entityOwner_clog on SS_ChangeLogs (entityType, entityId);
create index operationDate_clog on SS_ChangeLogs (zoneId, operationDate);
create index entityOwner_audit on SS_AuditTrail (entityType, entityId);
create index search_audit on SS_AuditTrail (zoneId, startDate, startBy);
create index internal_Binder on SS_Forums (zoneId, internalId);
create index binder_Dash on SS_Dashboards (binderId);
create index internal_Definition on SS_Definitions (zoneId, internalId, type);
create index sendDate_emaillog on SS_EmailLog (zoneId, sendDate);
create index entryDef_fEntry on SS_FolderEntries (entryDef);
create index snapshot_lstats on SS_LicenseStats (zoneId, snapshotDate);
create index email_postings on SS_Postings (zoneId, emailAddress);
create index internal_principal on SS_Principals (zoneId,internalId);
create index type_principal on SS_Principals (zoneId, type);
create index entryDef_principal on SS_Principals (entryDef);
create index access_shared on SS_SharedEntity (accessType, accessId, sharedDate);
create index entity_shared on SS_SharedEntity (entityType, entityId);
create index entity_tag on SS_Tags (entity_type, entity_id);
create index owner_tag on SS_Tags (owner_type, owner_id);
create index startDate_wfhistory on SS_WorkflowHistory (zoneId, startDate);
create index entity_Ratings on SS_Ratings (entityId, entityType);
create index entity_Subscriptions on SS_Subscriptions (entityId, entityType);
alter table SS_SeenMap add pruneDays numeric(19,0);

INSERT INTO SS_SchemaInfo values (20);