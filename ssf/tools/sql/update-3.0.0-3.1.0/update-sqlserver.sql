use sitescape;
DROP INDEX entityOwner_clog on SS_ChangeLogs;
DROP INDEX entityOwner_audit ON SS_AuditTrail;
DROP INDEX entityTransaction_audit ON SS_AuditTrail;
DROP INDEX internalId_Definition ON SS_Definitions;
DROP INDEX diskQuota_principal ON SS_Principals; 
DROP INDEX entityTransaction_wfhistory ON SS_WorkflowHistory;
DROP INDEX access_shared ON SS_SharedEntity;
ALTER TABLE SS_Events ALTER COLUMN dtStart datetime null;
ALTER TABLE SS_Events add dtCalcStart datetime null;
ALTER TABLE SS_Events add dtCalcEnd datetime null;
create table SS_EmailLog (id char(32) not null, zoneId numeric(19,0) null, sendDate datetime not null, fromField nvarchar(255) null, subj nvarchar(255) null, comment ntext null, status varchar(16) not null, type varchar(32) not null, toEmailAddresses ntext null, fileAttachments ntext null, primary key (id));
create index index_emaillog on SS_EmailLog (sendDate, status, type);
create table SS_FunctionConditionMap (functionId numeric(19,0) not null, meet varchar(16) null, conditionId numeric(19,0) null);
create table SS_FunctionConditions (id numeric(19,0) identity not null, type varchar(32) not null, zoneId numeric(19,0) not null, encodedSpec ntext null, title nvarchar(255) not null, description_text ntext null, description_format int null, primary key (id));
alter table SS_FunctionConditionMap add constraint FK945D2AD8BCA364AE foreign key (functionId) references SS_Functions;
alter table SS_FunctionConditionMap add constraint FK945D2AD868DAC30E foreign key (conditionId) references SS_FunctionConditions;
create table SS_BinderQuota (binderId numeric(19,0) not null, zoneId numeric(19,0) not null, diskQuota numeric(19,0) null, diskSpaceUsed numeric(19,0) null, diskSpaceUsedCumulative numeric(19,0) null, primary key (binderId));
alter table SS_ZoneConfig add binderQuotasInitialized tinyint;
alter table SS_ZoneConfig add binderQuotasEnabled tinyint;
alter table SS_ZoneConfig add binderQuotasAllowOwner tinyint;
alter table SS_ZoneConfig add holidays varchar(4096) null;
alter table SS_ZoneConfig add weekendDays varchar(128) null;
CREATE INDEX entityOwner_clog on SS_ChangeLogs (entityType, entityId);
CREATE INDEX operationDate_clog on SS_ChangeLogs (zoneId, operationDate);
CREATE INDEX entityOwner_audit on SS_AuditTrail (entityType, entityId);
CREATE INDEX search_audit ON SS_AuditTrail (zoneId, startDate, startBy);
CREATE INDEX internal_Binder ON SS_Forums (zoneId, internalId);
CREATE INDEX binder_Dash ON SS_Dashboards (binderId);
CREATE INDEX internal_Definition ON SS_Definitions (zoneId, internalId, type);
CREATE INDEX sendDate_emaillog ON SS_EmailLog (zoneId, sendDate);
CREATE INDEX entryDef_fEntry ON SS_FolderEntries (entryDef);
CREATE INDEX snapshot_lstats ON SS_LicenseStats (zoneId, snapshotDate);
CREATE INDEX email_postings ON SS_Postings (zoneId, emailAddress);
CREATE INDEX internal_principal ON SS_Principals (zoneId,internalId);
CREATE INDEX type_principal ON SS_Principals (zoneId, type);
CREATE INDEX entryDef_principal ON SS_Principals (entryDef);
CREATE INDEX access_shared ON SS_SharedEntity (accessType, accessId, sharedDate);
CREATE INDEX entity_shared ON SS_SharedEntity (entityType, entityId);
CREATE INDEX entity_tag ON SS_Tags (entity_type, entity_id);
CREATE INDEX owner_tag ON SS_Tags (owner_type, owner_id);
CREATE INDEX startDate_wfhistory ON SS_WorkflowHistory (zoneId, startDate);
CREATE INDEX entity_Ratings ON SS_Ratings (entityId, entityType);
CREATE INDEX entity_Subscriptions ON SS_Subscriptions (entityId, entityType);
alter table SS_SeenMap add pruneDays numeric(19,0);
INSERT INTO SS_SchemaInfo values (20);