connect sitescape/sitescape;
drop index entityOwner_clog;
drop index entityOwner_audit;
drop index entityTransaction_audit;
drop index internalId_Definition;
drop index diskQuota_principal; 
drop index entityTransaction_wfhistory;
drop index access_shared;
alter table SS_Events MODIFY dtStart timestamp null;
alter table SS_Events add dtCalcStart timestamp;
alter table SS_Events add dtCalcEnd timestamp;
create table SS_EmailLog (id char(32) not null, zoneId number(19,0), sendDate timestamp not null, fromField varchar2(255 char), subj varchar2(255 char), comments clob, status varchar2(16 char) not null, type varchar2(32 char) not null, toEmailAddresses clob, fileAttachments clob, primary key (id));
create table SS_FunctionConditionMap (functionId number(19,0) not null, meet varchar2(16 char), conditionId number(19,0));
create table SS_FunctionConditions (id number(19,0) not null, type varchar2(32 char) not null, zoneId number(19,0) not null, encodedSpec clob, title varchar2(255 char) not null, description_text clob, description_format number(10,0), primary key (id));
alter table SS_FunctionConditionMap add constraint FK945D2AD8BCA364AE foreign key (functionId) references SS_Functions;
alter table SS_FunctionConditionMap add constraint FK945D2AD868DAC30E foreign key (conditionId) references SS_FunctionConditions;
create table SS_BinderQuota (binderId number(19,0) not null, zoneId number(19,0) not null, diskQuota number(19,0), diskSpaceUsed number(19,0), diskSpaceUsedCumulative number(19,0), primary key (binderId));
alter table SS_ZoneConfig add binderQuotasInitialized number(1,0);
alter table SS_ZoneConfig add binderQuotasEnabled number(1,0);
alter table SS_ZoneConfig add binderQuotasAllowOwner number(1,0);
alter table SS_ZoneConfig add holidays varchar2(4000);
alter table SS_ZoneConfig add weekendDays varchar2(128);
alter table SS_TokenInfo add requesterId number(19,0);
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
alter table SS_SeenMap add pruneDays number(19,0);

declare v_count integer;

begin

   select count(*) into v_count from user_indexes where lower(index_name) = 'ldapguid_principal';

   if v_count = 0 then

      execute immediate 'create index ldapGuid_principal on SS_Principals (ldapGuid)';

   end if;

end;

/

INSERT INTO SS_SchemaInfo values (20);