use sitescape;

alter table SS_Forums add versionsEnabled tinyint null;
alter table SS_Forums add versionsToKeep numeric(19,0);
alter table SS_Forums add maxVersionAge numeric(19,0);
alter table SS_Forums add maxFileSize numeric(19,0);
alter table SS_Forums add fileEncryptionEnabled tinyint null;

alter table SS_Attachments add fileAgingDate datetime;

INSERT INTO SS_SchemaInfo values (22);
