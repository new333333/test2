use sitescape;
drop index entityOwner_clog on SS_ChangeLogs;
drop index entityOwner_audit on SS_AuditTrail;
drop index entityTransaction_audit on SS_AuditTrail;
drop index internalId_Definition on SS_Definitions;
drop index diskQuota_principal on SS_Principals; 
drop index entityTransaction_wfhistory on SS_WorkflowHistory;
drop index access_shared on SS_SharedEntity;
alter table SS_Events MODIFY dtStart datetime null DEFAULT null;
alter table SS_Events add dtCalcStart datetime;
alter table SS_Events add dtCalcEnd datetime;
create table SS_EmailLog (id char(32) not null, zoneId bigint, sendDate datetime not null, fromField varchar(255), subj varchar(255), comments longtext, status varchar(16) not null, type varchar(32) not null, toEmailAddresses longtext, fileAttachments longtext, primary key (id)) ENGINE=InnoDB;
create table SS_FunctionConditionMap (functionId bigint not null, meet varchar(16), conditionId bigint) ENGINE=InnoDB;
create table SS_FunctionConditions (id bigint not null auto_increment, type varchar(32) not null, zoneId bigint not null, encodedSpec longtext, title varchar(255) not null, description_text longtext, description_format integer, primary key (id)) ENGINE=InnoDB;
alter table SS_FunctionConditionMap add constraint FK945D2AD8BCA364AE foreign key (functionId) references SS_Functions (id);
alter table SS_FunctionConditionMap add constraint FK945D2AD868DAC30E foreign key (conditionId) references SS_FunctionConditions (id);
create table SS_BinderQuota (binderId bigint not null, zoneId bigint not null, diskQuota bigint, diskSpaceUsed bigint, diskSpaceUsedCumulative bigint, primary key (binderId)) ENGINE=InnoDB;
alter table SS_ZoneConfig add column binderQuotasInitialized bit;
alter table SS_ZoneConfig add column binderQuotasEnabled bit;
alter table SS_ZoneConfig add column binderQuotasAllowOwner bit;
alter table SS_ZoneConfig add column holidays varchar(4000);
alter table SS_ZoneConfig add column weekendDays varchar(128);
alter table SS_TokenInfo add column requesterId bigint;
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
alter table SS_SeenMap add pruneDays bigint;
DELIMITER $$

CREATE PROCEDURE vopCreateIndex()
BEGIN
	DECLARE CONTINUE HANDLER FOR SQLSTATE '42000' BEGIN END;

	create index ldapGuid_principal on SS_Principals (ldapGuid);
END $$

DELIMITER ;
CALL vopCreateIndex();
DROP PROCEDURE vopCreateIndex;

INSERT INTO SS_SchemaInfo values (20);