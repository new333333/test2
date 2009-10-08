use sitescape;
alter table SS_ZoneConfig add column mobileAccessEnabled bit;
alter table SS_AuditTrail add column deletedFolderEntryFamily varchar(32);
alter table SS_FolderEntries add preDeleted bit;
alter table SS_Forums add preDeleted bit;
alter table SS_Attachments add column relevanceUUID varchar(256);
