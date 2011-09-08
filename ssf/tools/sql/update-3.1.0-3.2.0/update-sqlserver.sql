use sitescape;

alter table SS_Forums add versionsEnabled tinyint null;
alter table SS_Forums add versionsToKeep numeric(19,0);
alter table SS_Forums add maxFileSize numeric(19,0);
alter table SS_Forums add fileEncryptionEnabled tinyint null;

alter table SS_Attachments add agingEnabled tinyint null;
alter table SS_ZoneConfig add fileVersionsMaxAge numeric(19,0);
alter table SS_Principals add fileSizeLimit numeric(19,0);
alter table SS_Principals add maxGroupsFileSizeLimit numeric(19,0);
alter table SS_ZoneConfig add fileSizeLimitUserDefault numeric(19,0);

alter table SS_Forums add versionAgingDays numeric(19,0);
alter table SS_Attachments add agingDate datetime null;
alter table SS_Forums add versionAgingEnabled tinyint null;

alter table SS_Attachments add encrypted tinyint null;
alter table SS_Attachments add encryptionKey varbinary(256) null;

alter table SS_Principals alter column foreignName nvarchar(255); 

alter table SS_Forums drop column popularity;
create table SS_FolderEntryStats (id numeric(19,0) not null, zoneId numeric(19,0) null, popularity numeric(19,0) null, primary key (id));
alter table SS_FolderEntryStats add constraint FKC94AB27AF1E91E10 foreign key (id) references SS_FolderEntries;

alter table SS_ZoneConfig add fsaEnabled tinyint null;
alter table SS_ZoneConfig add fsaSynchInterval int null;
alter table SS_ZoneConfig add fsaAutoUpdateUrl nvarchar(255) null;

INSERT INTO SS_SchemaInfo values (31);
