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
alter table SS_Definitions add unique definition_name(zoneId, name);
create table SS_EmailAddresses (principal bigint not null, type varchar(64) not null, zoneId bigint not null, address varchar(256), primary key (principal, type)) ENGINE=InnoDB;
alter table SS_EmailAddresses add constraint FKC706C3457488E8C7 foreign key (principal) references SS_Principals (id) on delete cascade;
create index address_email on SS_EmailAddresses (address);
create table SS_NotifyStatus (ownerId bigint not null, zoneId bigint, ownerType varchar(16), owningBinderKey varchar(255), owningBinderId bigint, lastModified datetime, lastDigestSent datetime, lastFullSent datetime, primary key (ownerId)) ENGINE=InnoDB;
create index notifyStatus_full on SS_NotifyStatus (zoneId, lastModified, lastFullSent);
create index notifyStatus_digest on SS_NotifyStatus (zoneId, lastModified, lastDigestSent);
alter table SS_Principals add postUrl varchar(256) null;
alter table SS_Principals add status varchar(256) null;
create table SS_ZoneInfo (id char(32) not null, zoneId bigint not null unique, zoneName varchar(128) not null unique, virtualHost varchar(255) unique, primary key (id)) ENGINE=InnoDB;
create table SS_TokenInfoBackground (applicationId bigint not null, userId bigint not null, binderId bigint not null, zoneId bigint, seed varchar(128), primary key (applicationId, userId, binderId)) ENGINE=InnoDB;
create table SS_TokenInfoInteractive (id char(32) not null, zoneId bigint, userId bigint, seed varchar(128), primary key (id)) ENGINE=InnoDB;
create index userId_tokenInfoInteractive on SS_TokenInfoInteractive (userId);
create table SS_SharedEntity (id char(32) not null, referer bigint, zoneId bigint, sharedDate datetime, accessId bigint, accessType bigint, entityType varchar(16), entityId bigint, primary key (id)) ENGINE=InnoDB;
create index access_shared on SS_SharedEntity (sharedDate, accessId, accessType);
alter table SS_SharedEntity add constraint FK93426C47E6E76CBB foreign key (referer) references SS_Principals (id);
