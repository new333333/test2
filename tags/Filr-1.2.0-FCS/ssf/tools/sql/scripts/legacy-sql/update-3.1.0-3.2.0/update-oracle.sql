connect sitescape/sitescape;

alter table SS_Forums add versionsEnabled number(1,0);
alter table SS_Forums add versionsToKeep number(19,0);
alter table SS_Forums add maxFileSize number(19,0);
alter table SS_Forums add fileEncryptionEnabled number(1,0);

alter table SS_Attachments add agingEnabled number(1,0);
alter table SS_ZoneConfig add fileVersionsMaxAge number(19,0);
alter table SS_Principals add fileSizeLimit number(19,0);
alter table SS_Principals add maxGroupsFileSizeLimit number(19,0);
alter table SS_ZoneConfig add fileSizeLimitUserDefault number(19,0);

alter table SS_Forums add versionAgingDays number(19,0);
alter table SS_Attachments add agingDate timestamp;
alter table SS_Forums add versionAgingEnabled number(1,0);

alter table SS_Attachments add encrypted number(1,0);
alter table SS_Attachments add encryptionKey raw(256);

alter table SS_Principals modify foreignName varchar2(255);

alter table SS_Forums drop column popularity;
create table SS_FolderEntryStats (id number(19,0) not null, zoneId number(19,0), popularity number(19,0), primary key (id));
alter table SS_FolderEntryStats add constraint FKC94AB27AF1E91E10 foreign key (id) references SS_FolderEntries;

alter table SS_ZoneConfig add fsaEnabled number(1,0);
alter table SS_ZoneConfig add fsaSynchInterval number(10,0);
alter table SS_ZoneConfig add fsaAutoUpdateUrl varchar2(255 char);

INSERT INTO SS_SchemaInfo values (31);
