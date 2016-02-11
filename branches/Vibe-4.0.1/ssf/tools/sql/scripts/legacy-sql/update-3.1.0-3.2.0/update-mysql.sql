use sitescape;

alter table SS_Forums add column versionsEnabled bit;
alter table SS_Forums add column versionsToKeep bigint;
alter table SS_Forums add column maxFileSize bigint;
alter table SS_Forums add column fileEncryptionEnabled bit;

alter table SS_Attachments add column agingEnabled bit;
alter table SS_ZoneConfig add column fileVersionsMaxAge bigint;
alter table SS_Principals add column fileSizeLimit bigint;
alter table SS_Principals add column maxGroupsFileSizeLimit bigint;
alter table SS_ZoneConfig add column fileSizeLimitUserDefault bigint;

alter table SS_Forums add column versionAgingDays bigint;
alter table SS_Attachments add column agingDate datetime;
alter table SS_Forums add column versionAgingEnabled bit;

alter table SS_Attachments add column encrypted bit;
alter table SS_Attachments add column encryptionKey blob;

alter table SS_Principals modify foreignName varchar(255); 

alter table SS_Forums drop column popularity;
create table SS_FolderEntryStats (id bigint not null, zoneId bigint, popularity bigint, primary key (id)) ENGINE=InnoDB;
alter table SS_FolderEntryStats add constraint FKC94AB27AF1E91E10 foreign key (id) references SS_FolderEntries (id);

alter table SS_ZoneConfig add column fsaEnabled bit;
alter table SS_ZoneConfig add column fsaSynchInterval integer; 
alter table SS_ZoneConfig add column fsaAutoUpdateUrl varchar(255);

INSERT INTO SS_SchemaInfo values (31);
