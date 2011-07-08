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

INSERT INTO SS_SchemaInfo values (25);
