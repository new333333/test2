use sitescape;

alter table SS_Forums add column versionsEnabled bit;
alter table SS_Forums add column versionsToKeep bigint;
alter table SS_Forums add column maxVersionAge bigint;
alter table SS_Forums add column maxFileSize bigint;
alter table SS_Forums add column fileEncryptionEnabled bit;

INSERT INTO SS_SchemaInfo values (21);
