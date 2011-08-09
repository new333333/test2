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
alter table SS_Attachments add encryptionKey varbinary(255) null;

INSERT INTO SS_SchemaInfo values (28);
