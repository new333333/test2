alter table SS_Attachments drop foreign key FKA1AD4C3193118767;
alter table SS_Attachments drop foreign key FKA1AD4C31DB0761E4;
alter table SS_Attachments drop foreign key FKA1AD4C31F93A11B4;

alter table SS_Forums drop foreign key FKDF668A5193118767;
alter table SS_Forums drop foreign key FKDF668A51DB0761E4;

alter table SS_Notifications drop foreign key FK9131F9296EADA262;

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

alter table SS_WorkflowStates drop foreign key FK8FA8AA80A3644438;

alter table SS_Principals drop foreign key FK7693816493118767;
alter table SS_Principals drop foreign key FK76938164DB0761E4;

alter table SS_PrincipalMembership drop foreign key FK176F6225AEB5AABF;

create index owningBinder_audit on SS_AuditTrail (owningBinderId);
create index entityOwner_audit on SS_AuditTrail (entityId, entityType);
create index entityTransaction_audit on SS_AuditTrail (startDate, entityId, entityType, transactionType);
create index owningBinder_clog on SS_ChangeLogs (owningBinderId);
create index entityOwner_clog on SS_ChangeLogs (entityId, entityType);
create table SS_EmailAddresses (principal bigint not null, type varchar(64) not null, zoneId bigint not null, address varchar(256), primary key (principal, type)) ENGINE=InnoDB;
alter table SS_EmailAddresses add constraint FKC706C3457488E8C7 foreign key (principal) references SS_Principals (id) on delete cascade;
create index address_email on SS_EmailAddresses (address);
create table SS_NotifyStatus (ownerId bigint not null, zoneId bigint, ownerType varchar(16), owningBinderKey varchar(255), owningBinderId bigint, lastModified datetime, lastDigestSent datetime, lastFullSent datetime, primary key (ownerId)) ENGINE=InnoDB;
create index notifyStatus_full on SS_NotifyStatus (zoneId, lastModified, lastFullSent);
create index notifyStatus_digest on SS_NotifyStatus (zoneId, lastModified, lastDigestSent);
alter table SS_Principals add postUrl varchar(256) null;
alter table SS_Principals add timeout integer null;
alter table SS_Principals add trusted bit null;
alter table SS_Principals add status varchar(256) null;
alter table SS_Principals add statusDate datetime null;
alter table SS_Principals add skypeId varchar(64) null;
alter table SS_Principals add twitterId varchar(64) null;
alter table SS_Principals add miniBlogId bigint null;
create table SS_ZoneInfo (id char(32) not null, zoneId bigint not null unique, zoneName varchar(128) not null unique, virtualHost varchar(255) unique, primary key (id)) ENGINE=InnoDB;
create table SS_TokenInfo (id char(32) not null, type char(1) not null, zoneId bigint, applicationId bigint, userId bigint, binderId bigint, binderAccessConstraints integer, seed varchar(128), primary key (id)) ENGINE=InnoDB;
create index userId_tokenInfoSession on SS_TokenInfo (userId);
create table SS_SharedEntity (id char(32) not null, referer bigint, zoneId bigint, sharedDate datetime, accessId bigint, accessType bigint, entityType varchar(16), entityId bigint, primary key (id)) ENGINE=InnoDB;
create index access_shared on SS_SharedEntity (sharedDate, accessId, accessType);
alter table SS_SharedEntity add constraint FK93426C47E6E76CBB foreign key (referer) references SS_Principals (id);
alter table SSQRTZ_TRIGGERS  add PRIORITY integer null;
update SSQRTZ_TRIGGERS set PRIORITY=5 where PRIORITY is null;
alter table SSQRTZ_TRIGGERS add JOB_DATA blob null;
alter table SSQRTZ_FIRED_TRIGGERS add PRIORITY integer null;
update SSQRTZ_FIRED_TRIGGERS set PRIORITY=5 where PRIORITY is null;
alter table SSQRTZ_SCHEDULER_STATE drop RECOVERER;
create table SS_SimpleName (zoneId bigint not null, name varchar(128) not null, emailAddress varchar(128) not null, binderId bigint, binderType varchar(16), primary key (zoneId, name)) ENGINE=InnoDB;
create index binderId_simpleName on SS_SimpleName (binderId);
create index name_simpleName on SS_SimpleName (name);
create index emailAddress_simpleName on SS_SimpleName (emailAddress);
alter table SS_Forums add branding mediumtext;
alter table SS_Events add uid varchar(255) null;
alter table SS_Events add freeBusy varchar(32) null;
alter table SS_Definitions add binderId bigint;
alter table SS_Definitions add unique definition_name(zoneId, name, binderId);
create table SS_IndexNode (id char(32) not null, nodeName varchar(128), indexName varchar(160), zoneId bigint, accessMode varchar(16), inSynch bit, primary key (id), unique (nodeName, indexName)) ENGINE=InnoDB;
create table SS_AuthenticationConfig (zoneId bigint not null, allowLocalLogin bit, allowAnonymousAccess bit, allowSelfRegistration bit, lastUpdate bigint, primary key (zoneId)) ENGINE=InnoDB;
create table SS_LdapConnectionConfig (id char(32) not null, zoneId bigint, url varchar(255), userIdAttribute varchar(255), mappings text, userSearches text, groupSearches text, principal varchar(255), credentials varchar(255), position integer, primary key (id)) ENGINE=InnoDB;
create table SS_IndexingJournal (id bigint not null auto_increment, zoneId bigint, nodeName varchar(128), indexName varchar(160), operationName varchar(32), operationArgs longblob, primary key (id)) ENGINE=InnoDB;
create index indexingJournal_nodeIndex on SS_IndexingJournal (nodeName, indexName);
