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

INSERT INTO SS_SchemaInfo values (29);
