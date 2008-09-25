alter table SS_Attachments drop constraint FKA1AD4C3193118767
alter table SS_Attachments drop constraint FKA1AD4C31DB0761E4
alter table SS_Attachments drop constraint FKA1AD4C31F93A11B4

alter table SS_Forums drop constraint FKDF668A5193118767
alter table SS_Forums drop constraint FKDF668A51DB0761E4

alter table SS_Notifications drop constraint FK9131F9296EADA262

alter table SS_Dashboards drop constraint FKFA9653BE93118767
alter table SS_Dashboards drop constraint FKFA9653BEDB0761E4

alter table SS_Definitions drop constraint FK7B56F60193118767
alter table SS_Definitions drop constraint FK7B56F601DB0761E4

alter table SS_Events drop constraint FKDE0E53F893118767
alter table SS_Events drop constraint FKDE0E53F8DB0761E4

alter table SS_FolderEntries drop constraint FKA6632C83F7719C70
alter table SS_FolderEntries drop constraint FKA6632C8393118767
alter table SS_FolderEntries drop constraint FKA6632C83DB0761E4
alter table SS_FolderEntries drop constraint FKA6632C83A3644438

alter table SS_WorkflowStates drop constraint FK8FA8AA80A3644438

alter table SS_Principals drop constraint FK7693816493118767
alter table SS_Principals drop constraint FK76938164DB0761E4

alter table SS_PrincipalMembership drop constraint FK176F6225AEB5AABF

create index owningBinder_audit on SS_AuditTrail (owningBinderId);
create index entityOwner_audit on SS_AuditTrail (entityId, entityType);
create index entityTransaction_audit on SS_AuditTrail (startDate, entityId, entityType, transactionType);
create index owningBinder_clog on SS_ChangeLogs (owningBinderId);
create index entityOwner_clog on SS_ChangeLogs (entityId, entityType);
create table SS_EmailAddresses (principal number(19,0) not null, type varchar2(64 char) not null, zoneId number(19,0) not null, address varchar2(256 char), primary key (principal, type));
alter table SS_EmailAddresses add constraint FKC706C3457488E8C7 foreign key (principal) references SS_Principals on delete cascade;
create index address_email on SS_EmailAddresses (address);
create table SS_NotifyStatus (ownerId number(19,0) not null, zoneId number(19,0), ownerType varchar2(16 char), owningBinderKey varchar2(255 char), owningBinderId number(19,0), lastModified timestamp, lastDigestSent timestamp, lastFullSent timestamp, primary key (ownerId));
create index notifyStatus_full on SS_NotifyStatus (zoneId, lastModified, lastFullSent);
create index notifyStatus_digest on SS_NotifyStatus (zoneId, lastModified, lastDigestSent);
alter table SS_Principals add postUrl varchar2(256 char) null;
alter table SS_Principals add timeout number(10,0) null;
alter table SS_Principals add trusted number(1,0) null;
alter table SS_Principals add status varchar2(256 char) null;
alter table SS_Principals add statusDate timestamp null;
alter table SS_Principals add skypeId varchar2(64 char) null;
alter table SS_Principals add twitterId varchar2(64 char) null;
alter table SS_Principals add miniBlogId number(19,0) null;
create table SS_ZoneInfo (id char(32) not null, zoneId number(19,0) not null unique, zoneName varchar2(128 char) not null unique, virtualHost varchar2(255 char) unique, primary key (id));
create table SS_TokenInfo (id char(32) not null, type char(1 char) not null, zoneId number(19,0), applicationId number(19,0), userId number(19,0), binderId number(19,0), binderAccessConstraints number(10,0), seed varchar2(128), primary key (id));
create index userId_tokenInfoSession on SS_TokenInfo (userId);
create table SS_SharedEntity (id char(32) not null, referer number(19,0), zoneId number(19,0), sharedDate timestamp, accessId number(19,0), accessType number(19,0), entityType varchar2(16 char), entityId number(19,0), primary key (id));
create index access_shared on SS_SharedEntity (sharedDate, accessId, accessType);
alter table SS_SharedEntity add constraint FK93426C47E6E76CBB foreign key (referer) references SS_Principals;
alter table SSQRTZ_triggers add PRIORITY NUMBER(13) null;
update SSQRTZ_TRIGGERS set PRIORITY=5 where PRIORITY is null;
alter table SSQRTZ_triggers add JOB_DATA blob null;
alter table SSQRTZ_fired_triggers add PRIORITY NUMBER(13) null;
update SSQRTZ_fired_triggers set PRIORITY=5 where PRIORITY is null;
alter table SSQRTZ_scheduler_state drop column RECOVERER;
create table SS_SimpleName (zoneId number(19,0) not null, name varchar2(128 char) not null, emailAddress varchar2(128 char) not null, emailAddress varchar(128) not null, binderId number(19,0), binderType varchar2(16 char), primary key (zoneId, name));
create index binderId_simpleName on SS_SimpleName (binderId);
create index name_simpleName on SS_SimpleName (name);
create index emailAddress_simpleName on SS_SimpleName (emailAddress);
alter table SS_Forums add branding clob;
alter table SS_Events add uid varchar2(255 char) null;
alter table SS_Events add freeBusy varchar2(32 char) null;
alter table SS_Definitions add binderId number(19,0);
create unique index definition_name on SS_Definitions (zoneId, name, binderId);
create table SS_IndexNode (id char(32) not null, nodeName varchar2(128 char), indexName varchar2(160 char), zoneId number(19,0), accessMode varchar2(16 char), inSynch number(1,0), primary key (id), unique (nodeName, indexName));
create table SS_AuthenticationConfig (zoneId number(19,0) not null, allowLocalLogin number(1,0), allowAnonymousAccess number(1,0), allowSelfRegistration number(1,0), lastUpdate number(19,0), primary key (zoneId));
create table SS_LdapConnectionConfig (id char(32) not null, zoneId number(19,0), url varchar2(255 char), userIdAttribute varchar2(255 char), mappings clob, userSearches clob, groupSearches clob, principal varchar2(255 char), credentials varchar2(255 char), position number(10,0), primary key (id));
create table SS_IndexingJournal (id number(19,0) not null, zoneId number(19,0), nodeName varchar2(128 char), indexName varchar2(160 char), operationName varchar2(32 char), operationArgs blob, primary key (id));
create index indexingJournal_nodeIndex on SS_IndexingJournal (nodeName, indexName);
create sequence ss_indexingjournal_id_sequence;
