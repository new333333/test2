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
create unique index definition_name on SS_Definitions (zoneId, name);
create table SS_EmailAddresses (principal numeric(19,0) not null, type varchar(64) not null, zoneId numeric(19,0) not null, address nvarchar(256) null, primary key (principal, type));
alter table SS_EmailAddresses add constraint FKC706C3457488E8C7 foreign key (principal) references SS_Principals on delete cascade;
create index address_email on SS_EmailAddresses (address);
create table SS_NotifyStatus (ownerId numeric(19,0) not null, zoneId numeric(19,0) null, ownerType varchar(16) null, owningBinderKey varchar(255) null, owningBinderId numeric(19,0) null, lastModified datetime null, lastDigestSent datetime null, lastFullSent datetime null, primary key (ownerId));
create index notifyStatus_full on SS_NotifyStatus (zoneId, lastModified, lastFullSent);
create index notifyStatus_digest on SS_NotifyStatus (zoneId, lastModified, lastDigestSent);
alter table SS_Principals add postUrl nvarchar(256) null;
alter table SS_Principals add status nvarchar(256) null;
create table SS_ZoneInfo (id char(32) not null, zoneId numeric(19,0) not null unique, zoneName nvarchar(128) not null unique, virtualHost nvarchar(255) null unique, primary key (id));
create table SS_TokenInfoBackground (applicationId numeric(19,0) not null, userId numeric(19,0) not null, binderId numeric(19,0) not null, zoneId numeric(19,0) null, seed varchar(128) null, primary key (applicationId, userId, binderId));
create table SS_TokenInfoInteractive (id char(32) not null, zoneId numeric(19,0) null, userId numeric(19,0) null, seed varchar(128) null, primary key (id));
create index userId_tokenInfoInteractive on SS_TokenInfoInteractive (userId);
create table SS_SharedEntity (id char(32) not null, referer numeric(19,0) null, zoneId numeric(19,0) null, sharedDate datetime null, accessId numeric(19,0) null, accessType numeric(19,0) null, entityType varchar(16) null, entityId numeric(19,0) null, primary key (id));
create index access_shared on SS_SharedEntity (sharedDate, accessId, accessType);
alter table SS_SharedEntity add constraint FK93426C47E6E76CBB foreign key (referer) references SS_Principals;
