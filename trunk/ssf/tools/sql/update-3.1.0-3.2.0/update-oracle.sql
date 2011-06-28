connect sitescape/sitescape;

alter table SS_Forums add versionsEnabled number(1,0);
alter table SS_Forums add versionsToKeep number(19,0);
alter table SS_Forums add maxVersionAge number(19,0);
alter table SS_Forums add maxFileSize number(19,0);
alter table SS_Forums add fileEncryptionEnabled number(1,0);

alter table SS_Attachments add fileAgingDate timestamp;

INSERT INTO SS_SchemaInfo values (22);
