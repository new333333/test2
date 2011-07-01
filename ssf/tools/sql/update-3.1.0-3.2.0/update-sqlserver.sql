use sitescape;

alter table SS_Forums add versionsEnabled tinyint null;
alter table SS_Forums add versionsToKeep numeric(19,0);
alter table SS_Forums add maxFileSize numeric(19,0);
alter table SS_Forums add fileEncryptionEnabled tinyint null;

alter table SS_Attachments add agingEnabled tinyint null;
alter table SS_ZoneConfig add fileVersionsMaxAge int;

INSERT INTO SS_SchemaInfo values (23);
