use sitescape;

alter table SS_Forums add column versionsEnabled bit;
alter table SS_Forums add column versionsToKeep bigint;
alter table SS_Forums add column maxFileSize bigint;
alter table SS_Forums add column fileEncryptionEnabled bit;

alter table SS_Attachments add column agingEnabled bit;
alter table SS_ZoneConfig add column fileVersionsMaxAge integer;

INSERT INTO SS_SchemaInfo values (23);
