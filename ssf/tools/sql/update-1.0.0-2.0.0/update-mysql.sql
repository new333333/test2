use sitescape;
alter table SS_Attachments drop foreign key FKA1AD4C3193118767;
alter table SS_Attachments drop foreign key FKA1AD4C31DB0761E4;
alter table SS_Attachments drop foreign key FKA1AD4C31F93A11B4;
alter table SS_Dashboards drop foreign key FKFA9653BE93118767;
alter table SS_Dashboards drop foreign key FKFA9653BEDB0761E4;
alter table SS_Definitions drop foreign key FK7B56F60193118767;
alter table SS_Definitions drop foreign key FK7B56F601DB0761E4;
alter table SS_Events drop foreign key FKDE0E53F893118767;
alter table SS_Events drop foreign key FKDE0E53F8DB0761E4;
alter table SS_FolderEntries drop foreign key FKA6632C83F7719C70;
alter table SS_FolderEntries drop foreign key FKA6632C8393118767;
alter table SS_FolderEntries drop foreign key FKA6632C83DB0761E4;
alter table SS_FolderEntries drop foreign key FKA6632C83A3644438;
alter table SS_Forums drop foreign key FKDF668A5193118767;
alter table SS_Forums drop foreign key FKDF668A51DB0761E4;
alter table SS_Notifications drop foreign key FK9131F9296EADA262;
alter table SS_PrincipalMembership drop foreign key FK176F6225AEB5AABF;
alter table SS_Principals drop foreign key FK7693816493118767;
alter table SS_Principals drop foreign key FK76938164DB0761E4;
alter table SS_WorkflowStates drop foreign key FK8FA8AA80A3644438;
alter table SS_Forums drop column upgradeVersion;
alter table SS_Attachments add column zoneId bigint;
alter table SS_AuditTrail add column zoneId bigint;
alter table SS_AuditTrail add column applicationId bigint;
alter table SS_CustomAttributes add column zoneId bigint;
alter table SS_Dashboards add column zoneId bigint;
alter table SS_Definitions add column binderId bigint;
create table SS_EmailAddresses (principal bigint not null, type varchar(64) not null, zoneId bigint not null, address varchar(256), primary key (principal, type)) ENGINE=InnoDB;
alter table SS_Events add column zoneId bigint;
alter table SS_Events add column calUid varchar(255);
alter table SS_Events add column freeBusy varchar(32);
alter table SS_FolderEntries add column zoneId bigint;
alter table SS_FolderEntries add column subscribed bit;
alter table SS_Forums add column branding mediumtext;
alter table SS_Forums add column postingEnabled bit;
alter table SS_Functions add column internalId varchar(32);
alter table SS_Functions add column zoneWide bit;
create table SS_IndexNode (id char(32) not null, nodeName varchar(128), indexName varchar(160), zoneId bigint, accessMode varchar(16), inSynch bit, primary key (id), unique (nodeName, indexName)) ENGINE=InnoDB;
create table SS_IndexingJournal (id bigint not null auto_increment, zoneId bigint, nodeName varchar(128), indexName varchar(160), operationName varchar(32), operationArgs longblob, primary key (id)) ENGINE=InnoDB;
create table SS_LdapConnectionConfig (id char(32) not null, zoneId bigint, url varchar(255), userIdAttribute varchar(255), mappings text, userSearches text, groupSearches text, principal varchar(255), credentials varchar(255), position integer, primary key (id)) ENGINE=InnoDB;
alter table SS_LibraryEntries add column zoneId bigint;
create table SS_NotifyStatus (ownerId bigint not null, zoneId bigint, ownerType varchar(16), owningBinderKey varchar(255), owningBinderId bigint, lastModified datetime, lastDigestSent datetime, lastFullSent datetime, primary key (ownerId)) ENGINE=InnoDB;
alter table SS_Postings add column credentials varchar(64);
alter table SS_Principals add column skypeId varchar(64);
alter table SS_Principals add column twitterId varchar(64);
alter table SS_Principals add column status varchar(256);
alter table SS_Principals add column statusDate datetime;
alter table SS_Principals add column miniBlogId bigint;
alter table SS_Principals add column postUrl varchar(256);
alter table SS_Principals add column timeout integer;
alter table SS_Principals add column trusted bit;
alter table SS_Principals add column maxIdleTime integer;
alter table SS_Principals add column sameAddrPolicy bit;
alter table SS_Ratings add column zoneId bigint;
alter table SS_SeenMap add column zoneId bigint;
create table SS_SharedEntity (id char(32) not null, referer bigint, zoneId bigint, sharedDate datetime, accessId bigint, accessType bigint, entityType varchar(16), entityId bigint, primary key (id)) ENGINE=InnoDB;
create table SS_SimpleName (zoneId bigint not null, name varchar(128) not null, emailAddress varchar(128), binderId bigint, binderType varchar(16), primary key (zoneId, name)) ENGINE=InnoDB;
alter table SS_Subscriptions add column zoneId bigint;
alter table SS_Subscriptions add column encodedStyles varchar(256);
alter table SS_Tags add column zoneId bigint;
create table SS_TokenInfo (id char(32) not null, type char(1) not null, zoneId bigint, seed varchar(128), userId bigint, applicationId bigint, binderId bigint, binderAccessConstraints integer, clientAddr varchar(128), lastAccessTime datetime, primary key (id)) ENGINE=InnoDB;
alter table SS_UserProperties add column zoneId bigint;
create table SS_WorkflowHistory (id char(32) not null, zoneId bigint, startBy bigint, startDate datetime, endBy bigint, endDate datetime, entityId bigint, entityType varchar(16), owningBinderId bigint, owningBinderKey varchar(255), tokenId bigint, state varchar(64), threadName varchar(64), definitionId varchar(32), ended bit, primary key (id)) ENGINE=InnoDB;
alter table SS_WorkflowResponses add column zoneId bigint;
alter table SS_WorkflowStates add column zoneId bigint;
create table SS_ZoneConfig (zoneId bigint not null, upgradeVersion integer, postingEnabled bit, simpleUrlPostingEnabled bit, sendMailEnabled bit, allowLocalLogin bit, allowAnonymousAccess bit, allowSelfRegistration bit, lastUpdate bigint, primary key (zoneId)) ENGINE=InnoDB;
create table SS_ZoneInfo (id char(32) not null, zoneId bigint not null unique, zoneName varchar(128) not null unique, virtualHost varchar(255) unique, primary key (id)) ENGINE=InnoDB;
alter table SS_Attachments add constraint FKA1AD4C3158C1A25C foreign key (creation_principal) references SS_Principals (id);
alter table SS_Attachments add constraint FKA1AD4C31A0B77CD9 foreign key (modification_principal) references SS_Principals (id);
alter table SS_Attachments add constraint FKA1AD4C31BEEA2CA9 foreign key (filelock_owner) references SS_Principals (id);
alter table SS_Dashboards add constraint FKFA9653BE58C1A25C foreign key (creation_principal) references SS_Principals (id);
alter table SS_Dashboards add constraint FKFA9653BEA0B77CD9 foreign key (modification_principal) references SS_Principals (id);
alter table SS_Definitions add constraint FK7B56F60158C1A25C foreign key (creation_principal) references SS_Principals (id);
alter table SS_Definitions add constraint FK7B56F601A0B77CD9 foreign key (modification_principal) references SS_Principals (id);
alter table SS_EmailAddresses add constraint FKC706C3457488E8C7 foreign key (principal) references SS_Principals (id) on delete cascade;
alter table SS_Events add constraint FKDE0E53F858C1A25C foreign key (creation_principal) references SS_Principals (id);
alter table SS_Events add constraint FKDE0E53F8A0B77CD9 foreign key (modification_principal) references SS_Principals (id);
alter table SS_FolderEntries add constraint FKA6632C83BD21B765 foreign key (reserved_principal) references SS_Principals (id);
alter table SS_FolderEntries add constraint FKA6632C8358C1A25C foreign key (creation_principal) references SS_Principals (id);
alter table SS_FolderEntries add constraint FKA6632C83A0B77CD9 foreign key (modification_principal) references SS_Principals (id);
alter table SS_FolderEntries add constraint FKA6632C8369145F2D foreign key (wrk_principal) references SS_Principals (id);
alter table SS_Forums add constraint FKDF668A5158C1A25C foreign key (creation_principal) references SS_Principals (id);
alter table SS_Forums add constraint FKDF668A51A0B77CD9 foreign key (modification_principal) references SS_Principals (id);
alter table SS_Notifications add constraint FK9131F929345DBD57 foreign key (principalId) references SS_Principals (id);
alter table SS_PrincipalMembership add constraint FK176F6225A3FFCD99 foreign key (userId) references SS_Principals (id);
alter table SS_PrincipalMembership add constraint FK176F622542AEEC1E foreign key (groupId) references SS_Principals (id);
alter table SS_PrincipalMembership add constraint FK176F62257465C5B4 foreign key (userId) references SS_Principals (id);
alter table SS_Principals add constraint FK7693816458C1A25C foreign key (creation_principal) references SS_Principals (id);
alter table SS_Principals add constraint FK76938164A0B77CD9 foreign key (modification_principal) references SS_Principals (id);
alter table SS_SharedEntity add constraint FK93426C47F68E5AD foreign key (referer) references SS_Principals (id);
alter table SS_WorkflowStates add constraint FK8FA8AA8069145F2D foreign key (wrk_principal) references SS_Principals (id);
alter table SSQRTZ_TRIGGERS  add PRIORITY integer null;
update SSQRTZ_TRIGGERS set PRIORITY=5 where PRIORITY is null;
alter table SSQRTZ_TRIGGERS add JOB_DATA blob null;
alter table SSQRTZ_FIRED_TRIGGERS add PRIORITY integer null;
update SSQRTZ_FIRED_TRIGGERS set PRIORITY=5 where PRIORITY is null;
alter table SSQRTZ_SCHEDULER_STATE drop RECOVERER;
alter table SS_Events modify column timeZone varchar(80);
create index owningBinder_audit on SS_AuditTrail (owningBinderId);
create index entityOwner_audit on SS_AuditTrail (entityId, entityType);
create index entityTransaction_audit on SS_AuditTrail (startDate, entityId, entityType, transactionType);
create index owningBinder_clog on SS_ChangeLogs (owningBinderId);
create index entityOwner_clog on SS_ChangeLogs (entityId, entityType);
create index address_email on SS_EmailAddresses (address);
create index indexingJournal_nodeIndex on SS_IndexingJournal (nodeName, indexName);
create index notifyStatus_full on SS_NotifyStatus (zoneId, lastModified, lastFullSent);
create index notifyStatus_digest on SS_NotifyStatus (zoneId, lastModified, lastDigestSent);
create index access_shared on SS_SharedEntity (sharedDate, accessId, accessType);
create index emailAddress_simpleName on SS_SimpleName (emailAddress);
create index binderId_simpleName on SS_SimpleName (binderId);
create index userId_tokenInfoSession on SS_TokenInfo (userId);
create index owningBinder_wfhistory on SS_WorkflowHistory (owningBinderId);
create index entityTransaction_wfhistory on SS_WorkflowHistory (startDate, entityId, entityType);
create index entityOwner_wfhistory on SS_WorkflowHistory (entityId, entityType);
alter table SS_Definitions add unique definition_name(zoneId, name, binderId);
alter table SS_FolderEntries change description_text description_text mediumtext;
