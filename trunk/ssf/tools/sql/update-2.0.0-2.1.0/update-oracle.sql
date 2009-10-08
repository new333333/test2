connect sitescape/sitescape;
alter table SS_ZoneConfig add mobileAccessEnabled(1,0);
alter table SS_AuditTrail add deletedFolderEntryFamily varchar2(32 char);
alter table SS_FolderEntries add preDeleted (1,0);
alter table SS_Forums add preDeleted (1,0);
alter table SS_Attachments add relevanceUUID varchar2(256 char);
